package cmsc335_project_4_elinkowski;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/*******************************************************************************
 * File name: Job class
 * Date: 2018102 0715L
 * Author: Keith R. Elinkowski
 * Purpose: Extends Thing class and implements Runnable.  Class holds various 
 * Jobs and gets their requirements, dispatches worker threads to work each job 
 * individually as well as displaying GUI elements to give visual representation 
 * of jobs being worked on via a JProgressBar.
*******************************************************************************/
public class Job extends Thing implements Runnable{
    //Project 1
    private double duration;
    private ArrayList<String> requirements;
    //Project 3
    private enum Status {WORKING, SUSPENDED, WAITING, COMPLETE}
    private Status status;
    private boolean suspendFlag;
    private boolean cancelFlag;
    private final JButton suspendButton;
    private final JButton cancelButton;
    private final JProgressBar progressBar;
    private JPanel buttonPanel;
    private final JLabel statusLabel;
    private boolean workDone;
    private final Thread workerThread;
    private boolean wait;
    // (*) Project 4
    private ArrayList<Person> workers;
    private String workerPort;
    
    /***************************************************************************
     * Job Constructor
     * @param scanner
    ***************************************************************************/
    public Job(Scanner scanner) {
        super(scanner);
        if(scanner.hasNextDouble()){
            duration = scanner.nextDouble();
        }
        else {
            duration = 0.0;
        }
        requirements = new ArrayList<>();
        while(scanner.hasNext()) {
            requirements.add(scanner.next());
        }
        //set flags, status's and create worker thread
        suspendFlag = false;
        cancelFlag = false;
        status = Status.SUSPENDED;
        workDone = false;
        workerThread = new Thread(this);
        
        //set up jlabel to display status
        statusLabel = new JLabel("Status", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Monospace", 1, 12));
        
        // set up a button to suspend work on job
        suspendButton = new JButton("Suspend");
        suspendButton.setPreferredSize(new Dimension(50,35));
        
        //set up a button to cancel jobs
        cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(50,35));
        
        //set up progress bar to display work being done on a job
        progressBar = new JProgressBar();
        progressBar.setPreferredSize(new Dimension(100,35));
        progressBar.setStringPainted(true);
        UIManager.put("ProgressBar.background", Color.BLACK);
        UIManager.put("ProgressBar.foreground", Color.GREEN);
        UIManager.put("ProgressBar.selectionBackground", Color.WHITE);
        UIManager.put("ProgressBar.selectionForeground", Color.BLACK);
        
        //set wait flag
        wait = false;
        
        //set up an array list of workers
        workers = new ArrayList<>(requirements.size());
    }
    
    /***************************************************************************
     * Getter for duration
    ***************************************************************************/    
    public double getDuration() {
        return duration;
    }
    
    /***************************************************************************
     * Setter for duration
     * @param jobDuration
    ***************************************************************************/
    public void setDuration(double jobDuration){
        if(jobDuration >= 0.0) {
            duration = jobDuration;
        }
    }
    
    /***************************************************************************
     * Getter for requirements
    ***************************************************************************/
    public ArrayList<String> getRequirements(){
        return requirements;
    }
    
    /***************************************************************************
     * Setter for requirements
     * @param jobRequirement
    ***************************************************************************/
    public void setRequirements(ArrayList<String> jobRequirement){
        requirements = jobRequirement;
    }
    
    /***************************************************************************
     * Getter for workers - Project 4
    ***************************************************************************/
    public ArrayList<Person> getWorkers() {
        return workers;
    }
    
    /***************************************************************************
     * Add person to workers ArrayList
     * @param person 
    (*) Project 4 
    ***************************************************************************/
    public void addWorker(Person person) {
        workers.add(person);
    }
    
    /***************************************************************************
     * Setter for workers
     * @param people
    (*) Project 4
    ***************************************************************************/
    public void setWorkers(ArrayList<Person> people) {
        workers = people;
    }
    
    /***************************************************************************
     * Getter for Port
    (*) Project 4
    ***************************************************************************/
    public String getWorkerPort() {
        return workerPort;
    }
    
    /***************************************************************************
     * Setter for Port
     * @param port
    (*) Project 4
    ***************************************************************************/
    public void setWorkerPort(String port) {
        workerPort = port;
    }
    
    /***************************************************************************
     * GUI display of Jobs being worked that will correlate to the job displayed 
     * in the JTable next to it
     * @param panel
    ***************************************************************************/    
    public void displayWork(JPanel panel){
        SwingUtilities.invokeLater(new Runnable () {
            public void run() {
                try {
                    buttonPanel = panel;
                    panel.add(progressBar);
                    panel.add(statusLabel);
                    panel.add(suspendButton);
                    panel.add(cancelButton);
                    suspendButton.addActionListener((ActionEvent e) -> {    setSuspend();   });
                    cancelButton.addActionListener((ActionEvent e) ->  {    setCancel();    });
                } catch(ArrayIndexOutOfBoundsException e) {
                    System.out.println(e);
                }
            }
        });
    }
    
    /***************************************************************************
     * Synchronized method to start a new thread that will work on a new job
    ***************************************************************************/
    public synchronized void startWork(){
        workDone = false;
        wait = false;
        workerThread.start();
    }
    
    /***************************************************************************
     * Method to break down GUI elements of completed or canceled jobs
    ***************************************************************************/
    public void endWork(){
        SwingUtilities.invokeLater(new Runnable () {
            public void run() {
                //delete progress bar
                progressBar.setVisible(false);

                //delete button panel
                buttonPanel.remove(progressBar);

                //status lable
                statusLabel.setVisible(false);
                buttonPanel.remove(statusLabel);

                //suspend button
                suspendButton.setVisible(false);
                buttonPanel.remove(suspendButton);

                //cancel button
                cancelButton.setVisible(false);
                buttonPanel.remove(cancelButton);

                //set flags
                workDone = true;
                wait = false;
            }
        });
    }
    
    /***************************************************************************
     * required method for implementing Runnable interface.  The duration for 
     * the progress bar is also set up here by getting PortTime and using a random
     * seed to mix things up a bit.
    ***************************************************************************/
    @Override
    public void run() {
        //Random rand = new Random();
        PortTime portTime = new PortTime();
        int time = portTime.getPortTime();
        double startTime = time;
        double stopTime = time + ThreadLocalRandom.current().nextInt(100, 300) * duration;
        //double stopTime = time + (rand.nextInt(300) +100) * duration;
        double timeNeeded = stopTime - time;
        while (time < stopTime && !cancelFlag) {
            try {
                Thread.sleep(100);
            } 
            catch (InterruptedException e) {
                System.out.println(e);
            }
            if(wait) {
                displayStatus(Status.WAITING);
                time += 100;
                progressBar.setValue((int) (((time - startTime) / timeNeeded) * 100));
            }
            else if (!suspendFlag) {
                displayStatus(Status.WORKING);
                time += 100;
                progressBar.setValue((int) (((time - startTime) / timeNeeded) * 100));
            } 
            else if(suspendFlag) {
                displayStatus(Status.SUSPENDED);
            }
        }
        progressBar.setValue(100);
        if(!wait) {
            displayStatus(Status.COMPLETE);
        }
        workDone = true;   
    }
    
    /***************************************************************************
     * Simple helper method to toggle the suspense flag
    ***************************************************************************/
    public void setSuspend(){
        suspendFlag = !suspendFlag;
    }
    
    /***************************************************************************
     * Simple helper method to toggle the cancel flag
    ***************************************************************************/
    public void setCancel(){
        cancelFlag = true;
        workDone = true;
    }
    
    /***************************************************************************
     * Simple helper method to return the finished flag
    ***************************************************************************/
    public boolean finished(){
        return workDone;
    }
    
    /***************************************************************************
     * Simple helper method set the JLabel status.  Uses different colors to 
     * differentiate between the status
    ***************************************************************************/
    private void displayStatus(Status st) {
        status = st;
        switch(status) {
            case WORKING:
                statusLabel.setOpaque(true);
                statusLabel.setBackground(Color.BLUE);
                statusLabel.setForeground(Color.WHITE);
                statusLabel.setText("Working");
                break;
            case SUSPENDED:
                statusLabel.setOpaque(true);
                statusLabel.setBackground(Color.RED);
                statusLabel.setForeground(Color.WHITE);
                statusLabel.setText("Suspended");
                break;
            case COMPLETE:
                statusLabel.setOpaque(true);
                statusLabel.setBackground(Color.GREEN);
                statusLabel.setForeground(Color.BLACK);
                statusLabel.setText("Complete");
                break;
            case WAITING:
                statusLabel.setOpaque(true);
                statusLabel.setBackground(Color.YELLOW);
                statusLabel.setForeground(Color.BLACK);
                statusLabel.setText("Waiting");
        }
    }
    
    /***************************************************************************
     * Verify if all requirements for a job have been met
    (*) Project 4
    ***************************************************************************/
    public boolean requirementsMet() {
        for(Person worker : workers) {
            if(worker == null) {
                displayStatus(Status.WAITING);
                return false;
            }
        }
        return true;
    }
    
    /***************************************************************************
     * Used to cancel jobs.  Since a canceled job is also marked as complete I 
     * used this to quickly remove the job.
    (*) Project 4 
    ***************************************************************************/
    public void resourcesNotAvailable() {
        wait = true;
        duration = 2;
        workerThread.start();
    }
    
    /***************************************************************************
     * @Override toString method
    ***************************************************************************/
    @Override
    public String toString() {
        String outJob = String.format("%s ", super.toString());
        outJob += String.format("%.2f Hours, ", duration);
        outJob += String.format("Requirements: ");
        for(String string : requirements) {
            outJob += String.format("%s, ", string);
        }
        outJob += String.format(" ");
        return outJob;
    }
}