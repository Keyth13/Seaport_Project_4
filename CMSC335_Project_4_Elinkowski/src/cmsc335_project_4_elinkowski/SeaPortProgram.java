package cmsc335_project_4_elinkowski;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.text.DefaultCaret;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

/******************************************************************************
 * File name: SeaPortProgram class
 * Date: 20181024 1417L
 * Author: Keith R. Elinkowski
 * Purpose: Primary program, contains the Main method.  It initializes the 
 * program, creates the GUI and instantiates a World.  It also includes 
 * actionListeners for the "Read", "Display", and "Search" buttons as well 
 * as a ComboBox that allows the User to select their search target. Contains
 * a method to generate a JTree object based off of the User input simulation
 * file.
 ******************************************************************************/
public class SeaPortProgram extends JFrame {
    //Project 1
    private World world;
    private Scanner scanner;
    private JPanel structurePanel;
    private JTextPane console;
    //private JTextArea console;
    private JComboBox<String> searchComboBox;
    private JTextField searchField;
    private JTree root;
    private Dimension screenSize;
    //Project 2
    private JComboBox<String> sortTypeComboBox;
    private JComboBox<String> sortTargetComboBox;
    private HashMap<Integer, Thing> structureMap;
    //Project 3
    private JobTableTemplate workTableModel;
    private JTable workTable;
    private JPanel workTablePanel;
    private JPanel workButtonPanel;
    public boolean running;
    public boolean ready;
    //Project 4
    private HashMap<String, ArrayList<Person>> resourcePool;
    private JTextArea resourceConsole;
    private JPanel resourcePanel;
    private JPanel workPanel;
    private JPanel consolePanel;
    private JScrollPane scrollPane;
      
    /***************************************************************************
     * Starts program 
     * @param args
     **************************************************************************/
    public static void main(String[] args) {
        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
        SeaPortProgram simulation = new SeaPortProgram();
        simulation.seaPortProgramDisplay();
        simulation.addWindowListener(new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent e) {
            simulation.running = false;
            if(JOptionPane.showConfirmDialog(simulation, "Quit?", "Exiting.", JOptionPane.OK_OPTION, 0, new ImageIcon(""))!= 0){
                return;
            }
            System.exit(0);
        }});
        while(simulation.running){
            simulation.monitorWork();
        }
    }
    
    /***************************************************************************
     * Method to be called when the User hits the Read button. Uses an
     * instance of JFileChooser to select a simulation file to read. Returns
     * a Scanner object that is then used by the World Class to populate
     * the structure of the World.
    ***************************************************************************/
    private Scanner readSimulation() {
        //console.append(">>> You pressed the \"Read\" Button.\n");
        appendToConsole(console, "You pressed the \"Read\" Button.\n", Color.WHITE);
        JFileChooser fileChooser = new JFileChooser(".");
        try {
            if(fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                scanner = new Scanner(fileChooser.getSelectedFile());
                //console.append(">>> Reading simulation file [" + fileChooser.getSelectedFile().getName() + "]\n");
                appendToConsole(console, ">>> Reading simulation file [" + fileChooser.getSelectedFile().getName() + "]\n", Color.BLUE);
                TimeUnit.MILLISECONDS.sleep(500);
                //console.append(">>> . \n");
                appendToConsole(console, ">>> . \n", Color.BLUE);
                TimeUnit.MILLISECONDS.sleep(500);
                //console.append(">>> . .\n");
                appendToConsole(console, ">>> . .\n", Color.BLUE);
                TimeUnit.MILLISECONDS.sleep(500);
                //console.append(">>> . . .\n");
                appendToConsole(console, ">>> . . .\n", Color.BLUE);
                //console.append(">>> Simulation [" + fileChooser.getSelectedFile().getName() + "] successfully loaded.\n");
                appendToConsole(console, ">>> Simulation [" + fileChooser.getSelectedFile().getName() + "] successfully loaded.\n", Color.BLUE);
            }
        } catch (InterruptedException | FileNotFoundException e) {
            System.out.println(e);
        }
        //console.append(">>> Error occurred while loading the simulation. Please try again!\n");
        //console.append(">>> Structure is ready to be display.\n");
        appendToConsole(console, "<<< Please hit the \"Display\" button!\n", Color.BLUE);
        //console.append("<<< Please hit the \"Display\" button!\n");
        appendToConsole(console, "<<< Please hit the \"Display\" button!\n", Color.BLUE);
        return null;
    }
    
    /***************************************************************************
     * Method used to populate the World structure.  Uses HashMaps and method 
     * calls of each Type to store objects.
    ***************************************************************************/ 
    private void buildStructure(Scanner scanner){
        world = new World(scanner);
        structureMap = new HashMap<>();
        HashMap<Integer, SeaPort> portMap = new HashMap<>();
        HashMap<Integer, Dock> dockMap = new HashMap<>();
        HashMap<Integer, Ship> shipMap = new HashMap<>();
        resourcePool = new HashMap<>();
        while(scanner.hasNextLine()){
            String lineScanner = scanner.nextLine().trim();
            if(lineScanner.length() == 0) continue;
            Scanner thingScanner = new Scanner(lineScanner);
            if(!thingScanner.hasNext()) return;
            switch(thingScanner.next()){
                case "port":
                    SeaPort port = new SeaPort(thingScanner);
                    portMap.put(port.getIndex(), port);
                    structureMap.put(port.getIndex(), port);
                    world.assignSeaPort(port);
                    //console.append(">>> Added new Port - ["+port.getName()+"]\n");
                    appendToConsole(console, ">>> Added new Port - ["+port.getName()+"]\n", Color.YELLOW);
                    break;
                case "dock":
                    Dock dock = new Dock(thingScanner);
                    dockMap.put(dock.getIndex(), dock);
                    structureMap.put(dock.getIndex(), dock);
                    world.assignDock(dock, portMap.get(dock.getParent()));
                    //console.append(">>> Added new Pier - ["+dock.getName()+"]\n");
                    appendToConsole(console, ">>> Added new Pier - ["+dock.getName()+"]\n", Color. YELLOW);
                    break;
                case "pship":
                    PassengerShip passengerShip = new PassengerShip(thingScanner);
                    shipMap.put(passengerShip.getIndex(), passengerShip);
                    structureMap.put(passengerShip.getIndex(), passengerShip);
                    SeaPort passengerPort = portMap.get(passengerShip.getParent());
                    Dock passengerDock = dockMap.get(passengerShip.getParent());
                    if(passengerPort == null) {
                        passengerPort = portMap.get(passengerDock.getParent());
                    }                    
                    world.assignShip(passengerShip, passengerPort, passengerDock);
                    //console.append(">>> Added new PassengerShip - ["+passengerShip.getName()+"]\n");
                    appendToConsole(console, ">>> Added new PassengerShip - ["+passengerShip.getName()+"]\n", Color.YELLOW);
                    break;
                case "cship":
                    CargoShip cargoShip = new CargoShip(thingScanner);
                    shipMap.put(cargoShip.getIndex(), cargoShip);
                    structureMap.put(cargoShip.getIndex(), cargoShip);
                    SeaPort cargoPort = portMap.get(cargoShip.getParent());
                    Dock cargoDock = dockMap.get(cargoShip.getParent());
                    if(cargoPort == null) {
                        cargoPort = portMap.get(cargoDock.getParent());
                    }
                    world.assignShip(cargoShip, cargoPort, cargoDock);
                    //console.append(">>> Added new CargoShip - ["+cargoShip.getName()+"]\n");
                    appendToConsole(console, ">>> Added new CargoShip - ["+cargoShip.getName()+"]\n", Color.YELLOW);
                    break;
                case "person":
                    Person person = new Person(thingScanner);
                    structureMap.put(person.getIndex(), person);
                    world.assignPerson(person, portMap.get(person.getParent()));
                    //console.append(">>> Added new Person - ["+person.getName()+"]\n");
                    appendToConsole(console, ">>> Added new Person - ["+person.getName()+"]\n", Color.YELLOW);
                    break;
                case "job":
                    Job job = new Job(thingScanner);
                    structureMap.put(job.getIndex(), job);
                    world.assignJob(job, structureMap.get(job.getParent()));
                    //console.append(">>> Added new Job - ["+job.getName()+"]\n");
                    appendToConsole(console, ">>> Added new Job - ["+job.getName()+"]\n", Color.YELLOW);
                    break;
                default:
                    break;
            }
        }
        //Populate the Resource pool during initial ingestion of simulation
        for(SeaPort port : portMap.values()) {
            for(Person person : port.getPersons()) {
                String key = (port.getName() + " " + person.getSkill());
                try {
                    if(resourcePool.get(key) == null) {
                        resourcePool.put(key, new ArrayList<>());
                        resourcePool.get(key).add(person);
                    }
                    else {
                        resourcePool.get(key).add(person);
                    }
                } catch(NullPointerException e) {
                    System.out.println(e);
                }
            }
        }
        //initial docking of ships and start of work on any open jobs they have
        for(Ship ship : shipMap.values()){
            if(!ship.getJobs().isEmpty() && structureMap.get(ship.getParent()) instanceof Dock){
                //console.append(String.format(">>> SHIP DOCKING: SS %s docking in %s at Port of %s\n", ship.getName(), dockMap.get(ship.getParent()).getName(), portMap.get(dockMap.get(ship.getParent()).getParent()).getName()));
                appendToConsole(console, String.format(">>> SHIP DOCKING: SS %s docking in %s at Port of %s\n", ship.getName(), dockMap.get(ship.getParent()).getName(), portMap.get(dockMap.get(ship.getParent()).getParent()).getName()), Color.BLUE);
                for(Job job : ship.getJobs()){
                    workTableModel.add(ship, structureMap, job);
                    job.displayWork(workButtonPanel);
                    allocateWorkers(job, ship);     //replace job.startJob() with a new method to handle resources
                }
            }
        }
        workButtonPanel.setLayout(new GridLayout(workTableModel.getRowCount(),3,3,3));
        workButtonPanel.setBorder(new EmptyBorder(0,3,0,3));
        workButtonPanel.setPreferredSize(new Dimension(400, workTableModel.getRowCount() * 25));
        updateResources();      //update the reourcePane 
        ready = true;
    }
    
    /***************************************************************************
     * Draws the GUI and handles all of the buttons, panels and comboBoxes.
     * Uses BorderLayout to display everything in a neat, but non-intuitive 
     * manner
    ***************************************************************************/
    private void seaPortProgramDisplay() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                /* GUI setup */
                running = true;
                Toolkit toolkit = Toolkit.getDefaultToolkit();
                screenSize = toolkit.getScreenSize();
                setTitle ("Keith R. Elinkowski Seaport Simulation 4");
                setSize((screenSize.width), 900);
                setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
                setVisible(true);
                setLayout(new BorderLayout());

                 /* Console Panel */
                consolePanel = new JPanel();
                consolePanel.setLayout(new BorderLayout());
                add(consolePanel, BorderLayout.CENTER);

                /* Console TextArea */
                //console = new JTextArea();
                console = new JTextPane();
                DefaultCaret caret = (DefaultCaret)console.getCaret();
                caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
                console.setFont(new Font("Monospaced", 0, 12));
                console.setBackground(Color.BLACK);
                //console.setEditable(false);

                /* Console ScrollPanel */
                scrollPane = new JScrollPane(console);
                scrollPane.setPreferredSize(new Dimension(400, 400));
                consolePanel.setBorder(new EmptyBorder(10,0,25,25));
                consolePanel.add(scrollPane, BorderLayout.CENTER);

                /* Read Button */
                JButton readButton = new JButton("Read");
                readButton.addActionListener((ActionEvent e)->readSimulation());

                /* Display Button */
                JButton displayButton = new JButton("Display");
                displayButton.addActionListener((ActionEvent e)->displayStructure());

                /* Sort Button */
                JButton sortButton = new JButton("Sort");
                sortButton.addActionListener((ActionEvent e)->sort());

                /* Clear Button */
                JButton clearButton = new JButton("Clear");
                clearButton.addActionListener((ActionEvent e)->clearConsole());
                
                /* Sort Target ComboBox */
                JLabel sortTargetLabel = new JLabel("Sort Target");
                sortTargetComboBox = new JComboBox<>();
                sortTargetComboBox.addItem("Queue");
                sortTargetComboBox.addItem("Ports");
                sortTargetComboBox.addItem("Piers");
                sortTargetComboBox.addItem("Ships");
                sortTargetComboBox.addItem("Cargo Ships");
                sortTargetComboBox.addItem("Passenger Ships");
                sortTargetComboBox.addItem("People");
                sortTargetComboBox.addItem("Jobs");
                sortTargetComboBox.addItem("World");
                sortTargetComboBox.addActionListener(e->updateSortTypeComboBox());

                /* Sort Type ComboBox */
                JLabel sortTypeLabel = new JLabel("Sort Type");
                sortTypeComboBox = new JComboBox<>();
                updateSortTypeComboBox();

                /* Search ComboBox */
                JLabel searchLable = new JLabel("Search Target");
                searchField = new JTextField(15);
                searchComboBox = new JComboBox<>();
                searchComboBox.addItem("Index");
                searchComboBox.addItem("Type");
                searchComboBox.addItem("Name");
                searchComboBox.addItem("Skill");

                /* Search Button */
                JButton searchButton = new JButton("Search");
                searchButton.addActionListener((ActionEvent e)->search((String)(searchComboBox.getSelectedItem()), searchField.getText()));

                /* Action Panel */
                JPanel topPanel = new JPanel(new BorderLayout());
                topPanel.setBorder(new EmptyBorder(10,25,0,25));
                JPanel actionPanel = new JPanel();
                actionPanel.setFont(new Font("Monospaced", 0, 12));
                actionPanel.add(readButton);
                actionPanel.add(displayButton);
                actionPanel.add(searchLable);
                actionPanel.add(searchField);
                actionPanel.add(searchComboBox);
                actionPanel.add(searchButton);
                actionPanel.add(sortTargetLabel);
                actionPanel.add(sortTargetComboBox);
                actionPanel.add(sortTypeLabel);
                actionPanel.add(sortTypeComboBox);
                actionPanel.add(sortButton);
                actionPanel.add(clearButton);
                topPanel.add(actionPanel,BorderLayout.WEST);
                add(topPanel, BorderLayout.NORTH);

                /* Structure Panel */
                structurePanel = new JPanel(new BorderLayout());
                structurePanel.setFont(new Font("Monospaced", 0, 12));
                structurePanel.setPreferredSize(new Dimension(350, screenSize.height / 2));
                structurePanel.setBorder(new EmptyBorder(10,25,25,25));
                add(structurePanel, BorderLayout.WEST);

                /* Work Panel */
                workPanel = new JPanel();
                workPanel.setLayout(new BorderLayout());
                add(workPanel, BorderLayout.EAST);

                /* Job Table */
                workTablePanel = new JPanel(new BorderLayout());
                workTablePanel.setPreferredSize(new Dimension(750, screenSize.height / 2));
                workTablePanel.setBorder(new EmptyBorder(10, 0, 25, 25));
                workPanel.add(workTablePanel, BorderLayout.EAST);

                /* Resource Panel */
                resourcePanel = new JPanel(new BorderLayout());
                resourcePanel.setPreferredSize(new Dimension(350, screenSize.height / 2));
                resourcePanel.setBorder(new EmptyBorder(10,0,25,25));
                workPanel.add(resourcePanel, BorderLayout.CENTER);
                validate();
            }
        });
    }
    
    /***************************************************************************
     * Search helper method called when user hits search button.  Uses the
     * combobox input to determine what the target of the search is.  dumps 
     * results into the console window.
     **************************************************************************/
    private void search(String searchType, String searchTarget) {
        //console.append(">>> You pressed the \"Search\" button!\n");
        appendToConsole(console, ">>> You pressed the \"Search\" button!\n", Color.PINK);
        if(scanner == null) {
            displayStructure();
        }
        if(searchTarget.equals("")) {
            //console.append(">>> Please try again!\n");
            appendToConsole(console, ">>> Please try again!\n", Color.PINK);
            return;
        }
        //console.append(">>> You selected the following \"Type\": [" + searchType + "], and are searching for, [" + searchTarget + "]\n\n");
        appendToConsole(console, ">>> You selected the following \"Type\": [" + searchType + "], and are searching for, [" + searchTarget + "]\n\n", Color.PINK);
        ArrayList<Thing> searchResults = new ArrayList<>();
        ArrayList<String> skillSearchResults = new ArrayList<>();
         switch(searchType) {
            case "Index":
                try {
                    int requestedIndex = Integer.parseInt(searchTarget);
                    searchResults.add(structureMap.get(requestedIndex));
                }
                catch(NumberFormatException e) {
                    //console.append(">>> Invalid \"Index\" input, please try again!");
                    appendToConsole(console, ">>> Invalid \"Index\" input, please try again!", Color.PINK);
                }
                break;
            case "Type":
                try {
                    searchResults = world.searchByType(searchTarget);
                    if("SKILL".equals(searchTarget.toUpperCase())){
                        for(Thing thing : searchResults) {
                            if(thing instanceof Person) {
                                if(((Person)thing).getSkill() != null && !skillSearchResults.contains(((Person)thing).getSkill())){
                                    skillSearchResults.add(((Person)thing).getSkill());
                                }
                            }
                        }
                    }
                    else {
                    if(searchResults == null) {
                            //console.append(">>> Type not found!\n");
                            appendToConsole(console, ">>> Type not found!\n", Color.PINK);
                            return;
                        }
                    }
                } catch (NullPointerException e) {
                    //console.append(">>> Invalid \"Type\" input, please try again!");
                    appendToConsole(console, ">>> Invalid \"Type\" input, please try again!", Color.PINK);
                }
                break;
            case "Name":
                try {
                searchResults = world.searchByName(searchTarget);
                if(searchResults.size() <= 0) {
                    //console.append(">>> Name not found!\n");
                    appendToConsole(console, ">>> Name not found!\n", Color.PINK);
                    return;
                    }
                } catch (NullPointerException e) {
                    //console.append(">>> Invalid \"Name\" input, please try again!");
                    appendToConsole(console, ">>> Invalid \"Name\" input, please try again!", Color.PINK);
                }
                break;
            case "Skill":
                try {
                    searchResults = world.findSkill(searchTarget);
                    if(searchResults.size() <= 0) {
                        //console.append(">>> Skill not found!\n");
                        appendToConsole(console, ">>> Skill not found!\n", Color.PINK);
                        return;
                    }
                } catch (NullPointerException e) {
                    //console.append(">>> Invalid \"Skill\" input, please try again!");
                    appendToConsole(console, ">>> Invalid \"Skill\" input, please try again!", Color.PINK);
                }
                break;
        }
        if(searchResults.size() > 0 && !"SKILL".equals(searchTarget.toUpperCase())) {
            for(Thing thing : searchResults) {
                if(thing != null) {
                    //console.append(thing + "\n");
                    appendToConsole(console, thing + "\n", Color.PINK);
                }
                else {
                    //console.append("Your search returned ZERO results.\n");
                    appendToConsole(console, "Your search returned ZERO results.\n", Color.PINK);
                }
            }
        }
        else if(skillSearchResults.size() > 0 && "SKILL".equals(searchTarget.toUpperCase())) {
            for(String string : skillSearchResults) {
                //console.append(string+"\n");
                appendToConsole(console, string+"\n", Color.PINK);
            }
        }
    }
    
    /***************************************************************************
     * Simple method used to change the contents of the sortTypeComboBox based 
     * which target selected. 
    ***************************************************************************/
    private void updateSortTypeComboBox(){
        sortTypeComboBox.removeAllItems();
        if(sortTargetComboBox.getSelectedItem().equals("Queue")){
            sortTypeComboBox.addItem("Weight");
            sortTypeComboBox.addItem("Width");
            sortTypeComboBox.addItem("Length");
            sortTypeComboBox.addItem("Draft");
            sortTypeComboBox.addItem("Queued Ship Name");
        }
        else if(sortTargetComboBox.getSelectedItem().equals("Cargo Ships")) {
            sortTypeComboBox.addItem("Cargo Weight");
            sortTypeComboBox.addItem("Cargo Volume");
            sortTypeComboBox.addItem("Cargo Value");
            sortTypeComboBox.addItem("Cargo Ship Name");
        }
        else if(sortTargetComboBox.getSelectedItem().equals("Passenger Ships")) {
            sortTypeComboBox.addItem("Passengers");
            sortTypeComboBox.addItem("Rooms");
            sortTypeComboBox.addItem("Occupied");
            sortTypeComboBox.addItem("Passenger Ship Name");
        }
        else {
            sortTypeComboBox.addItem("Name");
        }
        validate();
    }
    
    /***************************************************************************
     * simple helper method used to determine the target of the sort and what 
     * type of sort to do. 
    ***************************************************************************/
    private void sort(){
        //console.append("\nYou pressed the \"Sort\" Button\n");
        appendToConsole(console, "\nYou pressed the \"Sort\" Button\n", Color.ORANGE);
        if(scanner == null) {
            displayStructure();
        }
        String sortType = sortTypeComboBox.getSelectedItem().toString();
        String sortTarget = sortTargetComboBox.getSelectedItem().toString();
        sortThings(sortTarget, sortType);
    }
    
    /***************************************************************************
     * method called by sort() and is passed via sort() the target of the sort 
     * and the type of sort to do.  Using Collections sort method an ArrayList of 
     * objects and the Comparator generated by the Thing class implementation of 
     * Comparator interface and its compare() method are passed.  Sorting then 
     * happens on the original ArrayList of objects. 
    ***************************************************************************/
    private void sortThings(String target, String sortBy) {
        ArrayList<?> things = new ArrayList<>();
        try {
            switch (target) {
                case "World":
                    sortThings("Ports", sortBy);
                    sortThings("Piers", sortBy);
                    sortThings("Ships", sortBy);
                    sortThings("Jobs", sortBy);
                    sortThings("People", sortBy);
                    return;
                case "Ports":
                    ArrayList<SeaPort> ports = world.getPorts();
                    Collections.sort(ports, new Thing(sortBy));
                    world.setPorts(ports);
                    things = world.getPorts();
                    //console.append(String.format("\nKeith's SeaPorts sorted by Name:\n"));
                        appendToConsole(console, String.format("\nKeith's SeaPorts sorted by Name:\n"), Color.ORANGE);
                    for(Object obj : things) {
                        //console.append(String.format("Port %s\n", ((SeaPort)obj).getName()));
                        appendToConsole(console, String.format("Port %s\n", ((SeaPort)obj).getName()), Color.ORANGE);
                    }
                    break;
                default:
                    for(SeaPort port : world.getPorts()) {
                        switch(target) {
                            case "Queue":
                                Collections.sort(port.getQueue(), new Thing(sortBy));
                                things = port.getQueue();
                                //console.append(String.format("\n%s's Queued Ship's sorted by %s:\n", port.getName(), sortBy));
                                appendToConsole(console, String.format("\n%s's Queued Ship's sorted by %s:\n", port.getName(), sortBy), Color.ORANGE);
                                switch(sortBy){
                                    case "Queued Ship Name":
                                        for(Object obj : things) {
                                            //console.append(String.format("SS %s\n", ((Ship)obj).getName()));
                                            appendToConsole(console, String.format("SS %s\n", ((Ship)obj).getName()), Color.ORANGE);
                                        }
                                        break;
                                    case "Draft":
                                        for(Object obj : things) {
                                            //console.append(String.format("SS %s's %s: %.2f m\n", ((Ship)obj).getName(), sortBy, ((Ship)obj).getDraft()));
                                            appendToConsole(console, String.format("SS %s's %s: %.2f m\n", ((Ship)obj).getName(), sortBy, ((Ship)obj).getDraft()), Color.ORANGE);
                                        }
                                        break;
                                    case "Length":
                                        for(Object obj : things) {
                                            //console.append(String.format("SS %s's %s: %.2f m\n", ((Ship)obj).getName(), sortBy, ((Ship)obj).getLength()));
                                            appendToConsole(console, String.format("SS %s's %s: %.2f m\n", ((Ship)obj).getName(), sortBy, ((Ship)obj).getLength()), Color.ORANGE);
                                        }
                                        break;
                                    case "Weight":
                                        for(Object obj : things) {
                                            //console.append(String.format("SS %s's %s: %.2f MT\n", ((Ship)obj).getName(), sortBy, ((Ship)obj).getWeight()));
                                            appendToConsole(console, String.format("SS %s's %s: %.2f MT\n", ((Ship)obj).getName(), sortBy, ((Ship)obj).getWeight()), Color.ORANGE);
                                        }
                                        break;
                                    case "Width":
                                        for(Object obj : things) {
                                            //console.append(String.format("SS %s's %s: %.2f m\n", ((Ship)obj).getName(), sortBy, ((Ship)obj).getWidth()));
                                            appendToConsole(console, String.format("SS %s's %s: %.2f m\n", ((Ship)obj).getName(), sortBy, ((Ship)obj).getWidth()), Color.ORANGE);
                                        }
                                        break;
                                    default:
                                        break;
                                }
                                break;
                            case "Piers":
                                Collections.sort(port.getDocks(), new Thing(sortBy));
                                things = port.getDocks();
                                //console.append(String.format("\nPort %s's %s sorted by %s:\n", port.getName(), target, sortBy));
                                appendToConsole(console, String.format("\nPort %s's %s sorted by %s:\n", port.getName(), target, sortBy), Color.ORANGE);
                                for(Object obj : things) {
                                    //console.append(String.format("%s\n", ((Dock)obj).getName()));
                                    appendToConsole(console, String.format("%s\n", ((Dock)obj).getName()), Color.ORANGE);
                                }
                                break;
                            case "Ships":
                                Collections.sort(port.getShips(), new Thing(sortBy));
                                things = port.getShips();
                                //console.append(String.format("\nPort %s's %s sorted by %s:\n", port.getName(), target, sortBy));
                                appendToConsole(console, String.format("\nPort %s's %s sorted by %s:\n", port.getName(), target, sortBy), Color.ORANGE);
                                for(Object obj : things) {
                                    //console.append(String.format("%s\n", ((Ship)obj).getName()));
                                    appendToConsole(console, String.format("%s\n", ((Ship)obj).getName()), Color.ORANGE);
                                }
                                break;
                            case "Cargo Ships":
                                Collections.sort(port.getShips(), new Thing(sortBy));
                                things = port.getShips();
                                //console.append(String.format("\nPort %s's %s sorted by %s:\n",  port.getName(), target, sortBy));
                                appendToConsole(console, String.format("\nPort %s's %s sorted by %s:\n",  port.getName(), target, sortBy), Color.ORANGE);
                                switch(sortBy) {
                                    case "Cargo Ship Name":
                                        for(Object obj : things) {
                                            if(obj instanceof CargoShip) {
                                                //console.append(String.format("SS %s\n", ((CargoShip)obj).getName()));
                                                appendToConsole(console, String.format("SS %s\n", ((CargoShip)obj).getName()), Color.ORANGE);
                                            }
                                        }
                                        break;
                                    case "Cargo Weight":
                                        for(Object obj : things) {
                                            if(obj instanceof CargoShip) {
                                                //console.append(String.format("Cargo Ship %s's %s: %.2f MT\n", ((CargoShip)obj).getName(), sortBy, ((CargoShip)obj).getCargoWeight()));
                                                appendToConsole(console, String.format("Cargo Ship %s's %s: %.2f MT\n", ((CargoShip)obj).getName(), sortBy, ((CargoShip)obj).getCargoWeight()), Color.ORANGE);
                                            }
                                        }
                                        break;
                                    case "Cargo Volume":
                                        for(Object obj : things) {
                                            if(obj instanceof CargoShip) {
                                                //console.append(String.format("Cargo Ship %s's %s: %.2f m^3\n", ((CargoShip)obj).getName(), sortBy, ((CargoShip)obj).getCargoVolume()));
                                                appendToConsole(console, String.format("Cargo Ship %s's %s: %.2f m^3\n", ((CargoShip)obj).getName(), sortBy, ((CargoShip)obj).getCargoVolume()), Color.ORANGE);
                                            }
                                        }
                                        break;
                                    case "Cargo Value":
                                        for(Object obj : things) {
                                            if(obj instanceof CargoShip) {
                                                //console.append(String.format("Cargo Ship %s's %s: $%.2f million\n", ((CargoShip)obj).getName(), sortBy, ((CargoShip)obj).getCargoValue()));
                                                appendToConsole(console, String.format("Cargo Ship %s's %s: $%.2f million\n", ((CargoShip)obj).getName(), sortBy, ((CargoShip)obj).getCargoValue()), Color.ORANGE);
                                            }
                                        } 
                                        break;
                                    default:
                                        break;
                                }   
                                break;                       
                            case "Passenger Ships":
                                Collections.sort(port.getShips(), new Thing(sortBy));
                                things = port.getShips();
                                //console.append(String.format("\nPort %s's %s sorted by %s:\n",  port.getName(), target, sortBy));
                                appendToConsole(console, String.format("\nPort %s's %s sorted by %s:\n",  port.getName(), target, sortBy), Color.ORANGE);
                                switch(sortBy) {
                                    case "Passenger Ship Name":
                                        for(Object obj : things) {
                                            if(obj instanceof PassengerShip) {
                                                //console.append(String.format("SS %s\n", ((PassengerShip)obj).getName()));
                                                appendToConsole(console, String.format("SS %s\n", ((PassengerShip)obj).getName()), Color.ORANGE);
                                            }
                                        }
                                        break;
                                    case "Passengers":
                                        for(Object obj : things) {
                                            if(obj instanceof PassengerShip) {
                                                //console.append(String.format("Passenger Ship %s's %s: %d people\n", ((PassengerShip)obj).getName(), sortBy, ((PassengerShip)obj).getNumberOfPassengers()));
                                                appendToConsole(console, String.format("Passenger Ship %s's %s: %d people\n", ((PassengerShip)obj).getName(), sortBy, ((PassengerShip)obj).getNumberOfPassengers()), Color.ORANGE);
                                            }
                                        }
                                        break;
                                    case "Rooms":
                                        for(Object obj : things) {
                                            if(obj instanceof PassengerShip) {
                                                //console.append(String.format("Cargo Ship %s's %s: %d rooms\n", ((PassengerShip)obj).getName(), sortBy, ((PassengerShip)obj).getNumberOfRooms()));
                                                appendToConsole(console, String.format("Cargo Ship %s's %s: %d rooms\n", ((PassengerShip)obj).getName(), sortBy, ((PassengerShip)obj).getNumberOfRooms()), Color.ORANGE);
                                            }
                                        }
                                        break;
                                    case "Occupied":
                                        for(Object obj : things) {
                                            if(obj instanceof PassengerShip) {
                                                //console.append(String.format("Passenger Ship %s's has %d rooms %s\n",((PassengerShip)obj).getName(), ((PassengerShip)obj).getNumberOfOccupiedRooms(), sortBy));
                                                appendToConsole(console, String.format("Passenger Ship %s's has %d rooms %s\n",((PassengerShip)obj).getName(), ((PassengerShip)obj).getNumberOfOccupiedRooms(), sortBy), Color.ORANGE);
                                            }
                                        }
                                        break;
                                    default:
                                        break;
                                }   
                                break;
                            case "Jobs":
                                for(Ship ship : port.getShips()) {
                                    Collections.sort(ship.getJobs(), new Thing(sortBy));
                                    things = ship.getJobs();
                                    //console.append(String.format("\nSS %s's %s sorted by %s:\n",  ship.getName(), target, sortBy));
                                    appendToConsole(console, String.format("\nSS %s's %s sorted by %s:\n",  ship.getName(), target, sortBy), Color.ORANGE);
                                    for(Object obj : things) {
                                        //console.append(String.format("%s\n", ((Job)obj).getName()));
                                        appendToConsole(console, String.format("%s\n", ((Job)obj).getName()), Color.ORANGE);
                                    }
                                }
                                break;
                            case "People":
                                Collections.sort(port.getPersons(), new Thing(sortBy));
                                things = port.getPersons();
                                //console.append(String.format("\nPort %s's %s sorted by %s:\n", port.getName(), target, sortBy));
                                appendToConsole(console, String.format("\nPort %s's %s sorted by %s:\n", port.getName(), target, sortBy), Color.ORANGE);
                                for(Object obj : things) {
                                    //console.append(String.format("%s\n", ((Person)obj).getName()));
                                    appendToConsole(console, String.format("%s\n", ((Person)obj).getName()), Color.ORANGE);
                                }
                                break;
                            default:
                                break;
                        }
                    }   
                    break;
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e);
        }
    }
    
    /***************************************************************************
     * Simple method used to display the world structure in the left most pane
     * of the gui.
    ***************************************************************************/
    private void drawStructure() {
        root = new JTree(createBranch("Root"));
        JScrollPane structurePane = new JScrollPane(root);
        JButton structureCollapseButton = new JButton("Collaspe");
        JButton structureExpandButton = new JButton("Expand");
        JPanel structureButtonPanel = new JPanel();
        structureButtonPanel.setBorder(new EmptyBorder(10,0,0,0));
        structureButtonPanel.add(structureCollapseButton);
        structureButtonPanel.add(structureExpandButton);
        structureCollapseButton.addActionListener(e -> collapseStructure());
        structureExpandButton.addActionListener(e -> expandStructure());
        structurePanel.add(structureButtonPanel, BorderLayout.SOUTH);
        structurePanel.add(structurePane, BorderLayout.CENTER);
        
        //added functionality to search for node clicked in sturcuture pane
        root.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        root.getSelectionModel().addTreeSelectionListener((TreeSelectionEvent e) -> {
            String selected = e.getPath().toString();
            String[] bits = selected.split(" ");
            selected = bits[bits.length-1].replace("]", "");
            search("Name", selected);
        });
        validate();
    }
    
    /***************************************************************************
     * Method used to display the structure of the simulation world.  Calls
     * the buildStructure method to do the work of populating the HashMap
     * and the drawStructure method to display it in the left most pane of the
     * GUI.
     **************************************************************************/ 
    private void displayStructure(){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if(scanner == null){
                    //console.append(">>> File not loaded. Loading file before display.\n");
                    appendToConsole(console, ">>> File not loaded. Loading file before display.\n", Color.WHITE);
                    readSimulation();
                }
                //console.append(">>> Display World Button pressed.\n");
                appendToConsole(console, ">>> Display World Button pressed.\n", Color.WHITE);
                drawWorkTable();
                drawResources();        //Draw resources pane
                buildStructure(scanner);
                drawStructure();
                ready = true;
            }
        });
    }
    
    /***************************************************************************
     * Helper method to build world structure, using basic tree, branch, 
     * leaf(node) structure.  This method adds a branch for each thing of a type.
    ***************************************************************************/
    private DefaultMutableTreeNode createBranch(String title) {
        DefaultMutableTreeNode greatGrandParentNode = new DefaultMutableTreeNode(title);
        DefaultMutableTreeNode grandParentNode;
        DefaultMutableTreeNode parentNode;
        DefaultMutableTreeNode childNode;
        DefaultMutableTreeNode subChildNode;
        // Ports
        for(SeaPort port : world.getPorts()) {
            grandParentNode = new DefaultMutableTreeNode("Port of " + port.getName());
            greatGrandParentNode.add(grandParentNode);
            //Docks
            parentNode = new DefaultMutableTreeNode("Piers");
            grandParentNode.add(parentNode);
            for(Dock dock : port.getDocks()) {
                childNode = new DefaultMutableTreeNode(dock.getName());
                parentNode.add(childNode);
                subChildNode = new DefaultMutableTreeNode(dock.getShip().getName());
                childNode.add(subChildNode);
            }
            //Ships
            grandParentNode.add(addNode(port.getShips(), "Ships"));
            //Cargo Ships
            parentNode = new DefaultMutableTreeNode("Cargo Ships");
            subChildNode = new DefaultMutableTreeNode("Passenger Ships");
            for(Ship ship : port.getShips()) {
                if(ship instanceof CargoShip) {
                    grandParentNode.add(parentNode);
                    childNode = new DefaultMutableTreeNode("SS "+ship.getName());
                    parentNode.add(childNode);
                }
                //Passenger Ships
                else {
                    grandParentNode.add(subChildNode);
                    childNode = new DefaultMutableTreeNode("SS "+ship.getName());
                    subChildNode.add(childNode);
                }
            }
            //Jobs
            parentNode = new DefaultMutableTreeNode("Jobs");
            for(Ship ship : port.getShips()) {
                grandParentNode.add(parentNode);
                parentNode.add(addNode(ship.getJobs(), "SS "+ship.getName() + " Jobs"));
            }
            //Queue
            grandParentNode.add(addNode(port.getQueue(), "Queue"));
            //People
            grandParentNode.add(addNode(port.getPersons(), "People"));
            //Skills
            parentNode = new DefaultMutableTreeNode("Skills");
            for(Person person : port.getPersons()) {
               grandParentNode.add(parentNode);
               childNode = new DefaultMutableTreeNode(person.getSkill());
               parentNode.add(childNode);
            }
        }
        return greatGrandParentNode;
    }
    
    /***************************************************************************
     * Creates a branch for each Type of thing. 
    ***************************************************************************/ 
    private <T extends Thing> DefaultMutableTreeNode addNode(ArrayList<T> things, String name){
        DefaultMutableTreeNode leaf = new DefaultMutableTreeNode(name);
        try {
            for(Thing thing : things) { 
                String displayString = "";
                if(thing instanceof Dock) {
                    displayString = ((Dock)thing).getName();
                }
                if(thing instanceof Ship) {
                    if(thing instanceof CargoShip) {
                        displayString = "SS "+((Ship)thing).getName();
                    }
                    else if(thing instanceof PassengerShip) {
                        displayString = "SS "+((Ship)thing).getName();
                    }
                }
                if(thing instanceof Person) {
                    displayString = ((Person)thing).getName();
                }
                if(thing instanceof Job) {
                    displayString = ((Job)thing).getName();
                }
                leaf.add(new DefaultMutableTreeNode(displayString));
            } 
        } catch(NullPointerException e) {
            System.out.println(e);
        }
        return leaf;
    }
    
    /***************************************************************************
     * Collapses all nodes in JTree structure
     **************************************************************************/
    private void collapseStructure() {
        for(int i = 1; i < root.getRowCount()-1; i++) {
            root.collapseRow(i);
        }
    }
    
    /***************************************************************************
     * Expands all nodes in JTree structure
     **************************************************************************/
    private void expandStructure() {
        for(int i = 0; i <= root.getRowCount()-1; i++) {
            root.expandRow(i);
        }
    }
    
    /***************************************************************************
     * After the initial ingestion of the simulation file in my buildStructure 
     * method, this method monitors a ships' work on jobs.  When all of a ships 
     * Jobs are complete, the ship will undock from its pier and another ship in
     * the Queue will be allowed to dock and begin work on its Jobs.  I started 
     * to use two global "Flags", ready and running, and one internal "Flag", 
     * workComplete to avoid having "Forever" or infinite loops.
    ***************************************************************************/
    public void monitorWork() {   
        if(ready){
            for(SeaPort port : world.getPorts()) {
                for(Dock dock : port.getDocks()) {
                    synchronized(dock) {
                        boolean workComplete = true;
                        if(dock.getShip() == null){
                            continue;
                        }
                        for(Job job : dock.getShip().getJobs()) {
                            if(dock.getShip().getJobs().isEmpty()) {
                                workComplete = true;
                            }
                            if(!job.finished()) {
                                workComplete = false;
                            }
                            else if(job.finished()) {
                                returnWorkers(job.getWorkers(), port.getName());
                                job.setWorkers(new ArrayList<>());
                            }
                        }
                        if(workComplete) {
                            //console.append(String.format(">>> SHIP DEPARTING: SS %s leaving %s at Port of %s\n", dock.getShip().getName(), dock.getName(), port.getName()));
                            appendToConsole(console, String.format(">>> SHIP DEPARTING: SS %s leaving %s at Port of %s\n", dock.getShip().getName(), dock.getName(), port.getName()), Color.WHITE);
                            for(Job job : dock.getShip().getJobs()) {
                                if(job.finished()) {
                                    //console.append(String.format(">>> JOB DONE: Work order %s finished on SS %s at %s in Port of %s\n", job.getName(), dock.getShip().getName(), dock.getName(), port.getName()));
                                    appendToConsole(console, String.format(">>> JOB DONE: Work order %s finished on SS %s at %s in Port of %s\n", job.getName(), dock.getShip().getName(), dock.getName(), port.getName()), Color.WHITE);

                                }
                                job.endWork();
                                workTableModel.remove(job.getName());
                                workTable.validate();
                            }
                            dock.setShip(null);
                            if(port.getQueue().isEmpty()){
                                return; 
                            }
                            else {
                                dock.setShip(port.getQueue().remove(0));
                                //console.append(String.format(">>> SHIP DOCKING: SS %s docking in %s at Port of %s\n", dock.getShip().getName(), dock.getName(), port.getName()));
                                appendToConsole(console, String.format(">>> SHIP DOCKING: SS %s docking in %s at Port of %s\n", dock.getShip().getName(), dock.getName(), port.getName()), Color.WHITE);
                                for(Job job : dock.getShip().getJobs()) {
                                    job.displayWork(workButtonPanel);
                                    dock.getShip().setParent(dock.getIndex());
                                    workTableModel.add(dock.getShip(), structureMap, job);
                                    workTable.validate();
                                    allocateWorkers(job, dock.getShip());  //updated to allow for resource tracking replaced jobStart()
                                }
                            }
                        }
                        if(workTableModel.getRowCount() > 0) {
                            workButtonPanel.setLayout(new GridLayout(workTableModel.getRowCount(), 2, 3, 3));
                            workButtonPanel.setPreferredSize(new Dimension(400, workTableModel.getRowCount() * 25)); 
                            updateResources();
                        }
                    }
                }
            } 
        }  
    }
    
    /***************************************************************************
     * Simple method used to display the work table in the right most pane
     * of the gui.
    ***************************************************************************/
    private void drawWorkTable(){
        String[] header = {"Ship", "Location", "Work Order", "Requirements"};
        workTableModel = new JobTableTemplate(header);
        workTable = new JTable(workTableModel);
        workTable.setRowHeight(35);
        JPanel tablePanel = new JPanel(new BorderLayout());
        workButtonPanel = new JPanel();
        tablePanel.add(workTable, BorderLayout.CENTER);
        tablePanel.add(workButtonPanel, BorderLayout.EAST);
        tablePanel.add(workTable.getTableHeader(), BorderLayout.NORTH);
        JScrollPane tableScroll = new JScrollPane(tablePanel);
        workTablePanel.add(tableScroll);
        validate();
    }
    
    /***************************************************************************
     * Simple helper method that clears console textArea
    ***************************************************************************/
    public void clearConsole() {
        console.setText(null);
    }
    
    /***************************************************************************
     * Method to manage the allocation of workers to jobs.  Starts and stops 
     * jobs according to workers availability.  If no workers present in the 
     * resource pool with the required skills to meet job requirements then all 
     * resources will be release back into the resource pool.
     * @param job
     * @param ship
    ***************************************************************************/
    public void allocateWorkers(Job job, Ship ship) {
        boolean allocateWorkers = true;
        for(String requirement : job.getRequirements()) {
            SeaPort port = (SeaPort)structureMap.get(structureMap.get(ship.getParent()).getParent());
            job.setWorkerPort(port.getName());
            ArrayList<Person> workers = resourcePool.get(port.getName() + " " + requirement);
            if(workers != null && !workers.isEmpty()) {
                Person worker = workers.remove(0);
                job.addWorker(worker);
                //console.append(">>> ADDED WORKER: " + worker.toString() + " to worker order " + job.getName() + " \n");
                appendToConsole(console, ">>> ADDED WORKER: " + worker.toString() + " to worker order " + job.getName() + "at the Port of " + port.getName() + "\n", Color.BLUE);
            }
            else if(workers == null) {
                //console.append(">>> JOB CANCELLED: Unable to locate " + requirement + " at the Port of " + port.getName() + " \n");
                appendToConsole(console, ">>> JOB CANCELLED: Unable to locate " + requirement + " at the Port of " + port.getName() + " \n", Color.RED);
                job.resourcesNotAvailable();
                allocateWorkers = false;
                break;
            }
        }
        if(allocateWorkers && job.requirementsMet()) {
            //console.append(">>> REQUIREMENTS MET: all requirements for work order " + job.getName() + " have been found.\n");
            appendToConsole(console, ">>> REQUIREMENTS MET: all requirements for work order " + job.getName() + " have been found.\n", Color.CYAN);
            job.startWork();
        }
    }
    
    /***************************************************************************
     * Returns workers back to the Resource pool when they have completed their
     * work orders
     * @param allocatedWorkers
     * @param port
    ***************************************************************************/
    public void returnWorkers(ArrayList<Person> allocatedWorkers, String homePort) {
        for(Person worker : allocatedWorkers) {
            resourcePool.get((homePort + " " + worker.getSkill())).add(worker);
            //console.append(">>> WORKER RETURNED: " + worker + worker.getSkill() + "at the Port of " + port + " \n");
            appendToConsole(console, ">>> WORKER RETURNED: " + worker.getSkill() + " at the Port of " + homePort + " \n", Color.GREEN);
        }
        updateResources();  //update resource pane
    }
    
    /***************************************************************************
     * Updates the Resource Pool textarea of the GUI. Uses setText, which makes 
     * resource names appear and disappear without being scrolling or jarring.
    ***************************************************************************/
    public void updateResources() {
        String resources = "<<<------------RESOURCE POOL------------>>>";
        for(String string : resourcePool.keySet()) {
            resources += ("\n " + string + ": ");
            for(Person worker : resourcePool.get(string)) {
                resources += (worker.getName().toUpperCase()+" ");
            }
        }
        resourceConsole.setText(resources);
    }
    
    /***************************************************************************
     * Simple method used to draw the resource pool in the center-right pane of 
     * the GUI.
    ***************************************************************************/
    private void drawResources() {
        resourceConsole = new JTextArea();
        resourceConsole.setBackground(Color.BLACK);
        resourceConsole.setForeground(Color.GREEN);
        resourceConsole.setFont(new Font("Monospaced", 0, 12));
        resourceConsole.setEditable(false);
        JScrollPane resourceScrollPane = new JScrollPane(resourceConsole);
        resourceScrollPane.setPreferredSize(new Dimension(300, 375));
        resourcePanel.add(resourceScrollPane, BorderLayout.CENTER);
        validate();
    }
    
    /***************************************************************************
     * Simple method to allow for colored text in the console pane.  This 
     * required far more work than I had initially anticipated, however it does
     * looks nice and makes it easier to pick out what is happening in the very 
     * busy console window.
     * @param console
     * @param string
     * @param color 
    ***************************************************************************/
    private void appendToConsole(JTextPane console, String string, Color color) {
        StyleContext styleContext = StyleContext.getDefaultStyleContext();
        AttributeSet attributeSet = styleContext.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, color);
        attributeSet = styleContext.addAttribute(attributeSet, StyleConstants.FontFamily, "Monospaced");
        attributeSet = styleContext.addAttribute(attributeSet, StyleConstants.FontSize, 16);
        attributeSet = styleContext.addAttribute(attributeSet, StyleConstants.Bold, true);
        attributeSet = styleContext.addAttribute(attributeSet, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);
        int length = console.getDocument().getLength();
        console.setCaretPosition(length);
        console.setCharacterAttributes(attributeSet, false);
        console.replaceSelection(string);
    }
}