/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import static controller.Values.DIAMETER;
import element.Edge;
import element.Graph;
import element.Vertex;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.DefaultListModel;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.filechooser.FileFilter;
import views.AddGraphDialog;
import views.Canvas;
import views.GraphColorChooserDialog;
import views.GraphFrame;
import views.SampleCanvas;

/**
 *
 * @author Scott
 */
public class GraphController {

    /**
     * The edge that the user can edit, if they are in the edge adding state and
     * click an already-placed edge. Set to null if there is no edge to be
     * edited.
     */
    private Edge editingEdge = null;
    /**
     * the last selected vertex in the vertices JList (Used for things like
     * setting the title text field, updating the title, changing the color,
     * etc.)
     */
    private List<Vertex> selectedVertices = new ArrayList();
    /**
     * The last selected edge in the edges JList
     */
    private List<Edge> selectedEdges = new ArrayList();
    /**
     * A list of the selected indices in the vertices JList. null if there are
     * no selected vertices. (Used for things like setting the title text field
     * or updating the title)
     */
    private List<Integer> selectedVertexIndices = new ArrayList();
    /**
     * The last selected index in the edges JList
     */
    private List<Integer> selectedEdgeIndices = new ArrayList();
    private JTextField titleTextField;
    private JList verticesList;
    private JList edgesList;
    private JToggleButton addVerticesButton;
    private JToggleButton addEdgesButton;
    private JToggleButton selectionButton;
    private JMenuItem addVerticesMenuItem;
    private JMenuItem addEdgesMenuItem;
    private JMenuItem selectionMenuItem;

    //MARK: Adding edge state:
    /**
     * Only true if the user is in the edge adding state.
     */
    private boolean addingEdges = false;
    /**
     * Only true if the user has clicked inside of an edge's control point
     * and is holding down the mouse.
     */
    private boolean movingControlPoint = false;
    /**
     * If this is not null, we want to start drawing an edge between this vertex
     * and the mouse. Not to be confused with selectedVertex, which is used for
     * deleting vertices and changing titles.
     */
    private Vertex firstSelectedVertex;
    //MARK: Adding vertex state
    /**
     * Only true if the user is in the vertex adding state.
     */
    private boolean addingVertices = false;
    //MARK: Selection state
    /**
     * Only true if the user is in the selection state.
     */
    private boolean selecting = false;
    /**
     * Only true of the command key is pressed
     */
    private boolean isCommandPressed = false;

    // models for vertex and edge selection lists
    private final DefaultListModel verticesListModel = new DefaultListModel();
    private final DefaultListModel edgesListModel = new DefaultListModel();

    /**
     * The x-coordinate of the start point of the multiple-selection box.
     */
    private int startX;
    /**
     * The y-coordinate of the start point of the multiple-selection box.
     */
    private int startY;
    /**
     * The x-coordinate of the end point of the multiple-selection box.
     */
    private int endX;
    /**
     * The y-coordinate of the end point of the multiple-selection box.
     */
    private int endY;

    //Used for moving objects. Holds the last point the mouse was at.
    private int lastX;
    private int lastY;
    /**
     * This is used for moving all vertices, not to be confused with
     * selectedVertices, which is used for deleting vertices and changing
     * titles.
     */
    private List<Vertex> clickedVertices = new ArrayList();
    /**
     * This is used for moving all edges, not to be confused with selectedEdges,
     * which is used for deleting edges.
     */
    private List<Edge> clickedEdges = new ArrayList();

    //MARK: Booleans
    /**
     * Used to make sure clearSelection() or setSelectedIndex() do not
     * redundantly call the change listeners on the JLists.
     */
    private boolean showTitles = false;
    private boolean isModified = false;
    private JTextField modifiedTextField;

    //MARK: From controller
    private final GraphFrame frame = new GraphFrame();
    private final Canvas canvas = frame.getCanvas();
    private final AddGraphDialog addGraphDialog = new AddGraphDialog(frame, true);
    private final GraphColorChooserDialog graphColorChooserDialog = new GraphColorChooserDialog(frame, true);

    private final Graph graph = new Graph();

    private final List<Vertex> vertices = graph.getVertices();
    private final List<Edge> edges = graph.getEdges();

    //MARK: File I/O:
    /**
     * Holds all user preferences for this application
     */
    private Preferences prefs;
    /**
     * initialized with user.dir just in case something goes wrong with loading
     * preferences
     */
    private final JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
    /**
     * Allows only .graph files to be chosen by the user. (Allows user to open
     * directories, but not choose them).
     */
    private FileFilter filter;
    /**
     * Used to save files (or have the user create a new file if its null).
     */
    private File saveFile;
    /**
     * Used to tell what directory to open the chooser into.
     */
    private File currentDirectory;

    public GraphController() {
        frame.setTitle("Graph Theory Helper");
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); //this is neccessary to prevent the user from closing without saving
        frame.setSize(600, 500);

        canvas.setGraph(graph); //pass the graph to the canvas
        canvas.setGraphOutputTextField(frame.getGraphOutputTextField());

        //Get the two JLists
        verticesList = frame.getVerticesList(); //the visual JList that the user sees and interacts with
        edgesList = frame.getEdgesList(); //the visual JList that the user sees and interacts with

        //Remove the up/down arrow key action from both JLists (too hard to deal with for now)
        verticesList.getInputMap().put(KeyStroke.getKeyStroke("DOWN"), "none"); //make it do nothing
        verticesList.getInputMap().put(KeyStroke.getKeyStroke("UP"), "none"); //make it do nothing
        edgesList.getInputMap().put(KeyStroke.getKeyStroke("DOWN"), "none"); //make it do nothing
        edgesList.getInputMap().put(KeyStroke.getKeyStroke("UP"), "none"); //make it do nothing

        //remove the backspace action from canvas to prevent error beep
        canvas.getInputMap().put(KeyStroke.getKeyStroke("BACK_SPACE"), "none");

        titleTextField = frame.getTitleTextField();
        modifiedTextField = frame.getModifiedTextField();
        addVerticesButton = frame.getAddVerticesButton();
        addEdgesButton = frame.getAddEdgesButton();
        selectionButton = frame.getSelectionButton();
        addVerticesMenuItem = frame.getAddVerticesMenuItem();
        addEdgesMenuItem = frame.getAddEdgesMenuItem();
        selectionMenuItem = frame.getSelectionMenuItem();

        loadPreferences();

        SampleCanvas sampleCanvas = graphColorChooserDialog.getSampleCanvas();
        sampleCanvas.setUp(graph); //Set up the sample canvas in the dialog

        addKeyboardShortcuts();

        enterAddVerticesState();

        //Define the filter
        filter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (pathname.isDirectory()) {
                    return true;
                } else {
                    String name = pathname.getName();
                    //file must be "something.graph"
                    return name.matches(".*\\.graph");
                }
            }

            @Override
            public String getDescription() {
                return ".graph files";
            }
        };
        //Set the current directory to the user's preference of the last openned 
        //path, which was set when we ran loadPreferences()
        chooser.setCurrentDirectory(currentDirectory);

        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int mx = e.getX(); //x-coord of mouse click
                int my = e.getY(); //y-coord of mouse click
                if (addingEdges) { //if we are in the edge adding state, we don't want to be able to move any vertices
                    addEdge(mx, my);
                }

                if (addingVertices) {
                    addVertex(mx - Values.DIAMETER / 2, my - Values.DIAMETER / 2);
                }

                if (selecting) { //if we are not in the edge adding state, then we can move the vertices
                    selectVertexOrEdge(mx, my);
                    //Set up the start position for multiple selection
                    startX = mx;
                    startY = my;
                    canvas.setStartPosition(mx, my);
                    endX = mx;
                    endY = my;
                    canvas.setEndPosition(endX, endY);
                }
                pressVertices();
                pressEdges();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (!clickedVertices.isEmpty()) {
                    unpressVertices();
                    clickedVertices.clear(); //we don't want to move a vertex after the user lets go
                    canvas.repaint();
                }
                if (!clickedEdges.isEmpty()) {
                    unpressEdges();
                    clickedEdges.clear(); //we don't want to move an edge after the user lets go
                    canvas.repaint();
                }
                if (selecting) {
                    canvas.setMultipleSelecting(false);
                    canvas.repaint();
                }
                if (addingEdges) {
                    //stop the user from being able to edit the selected edge's control point
                    movingControlPoint = false;
                }
                //Don't want to repaint canvas if nothing happenned
            }

        });

        canvas.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int mx = e.getX(); //x-coord of mouse click
                int my = e.getY(); //y-coord of mouse click

                //Find the difference between the last position and the current 
                //position (used for moving the figure)
                int incX = mx - lastX;
                int incY = my - lastY;

                //update the last position
                lastX = mx;
                lastY = my;
                canvas.setLastPosition(lastX, lastY);

                if (selecting) { //if we're in the selection state
                    //if the user did not click any edges or vertices (only canvas)
                    if (clickedVertices.isEmpty() && clickedEdges.isEmpty()) {
                        //update the endpoint of the selection box
                        endX = mx;
                        endY = my;
                        canvas.setEndPosition(mx, my);
                        //select the appropriate vertices
                        multipleSelection(mx, my);
                    } else { //if the user clicked any vertices or edges
                        moveElements(incX, incY);
                    }
                }
                
                if (addingEdges) { //if we're in the edge adding state
                    //if the user's mouse is held down on the selected edge's
                    //control point
                    if (movingControlPoint) {
                        //increment the control point's location
                        editingEdge.incCtrlPoint(incX, incY);
                    }
                }
                canvas.repaint();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                //Update position for drawing live edge
                lastX = e.getX();
                lastY = e.getY();
                canvas.setLastPosition(lastX, lastY);
                if (addingEdges) { //if we are in the edge adding state
                    canvas.repaint();
                }
                if (addingVertices) { //if we are in the adding vertex state
                    canvas.repaint();
                }
                //don't want to repaint canvas if nothing happened
            }
        });

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (isModified) { //if the user tried to close without saving
                    if (!shouldContinue("OK to discard changes?")) { //if the user does not want to close the window without saving
                        isCommandPressed = false; //unpress command
                    } else { //if the user does want to close the window
                        System.exit(0); //the window should close
                    }
                } else { //if the user has closed the program after saving
                    System.exit(0); //the window should close
                }
            }
        });

        frame.getRotate90MenuItem().addActionListener((ActionEvent e) -> {
            rotateVertices90();
            canvas.repaint();
        });

        frame.getShowVertexNamesMenuItem().addActionListener((ActionEvent e) -> {
            //Toggle the showTitles boolean
            if (showTitles) {
                showTitles = false;
                canvas.setShowTitles(false);
                prefs.putBoolean(Values.SHOW_VERTEX_NAMES, false);
            } else {
                showTitles = true;
                canvas.setShowTitles(true);
                prefs.putBoolean(Values.SHOW_VERTEX_NAMES, true);
            }
            
            canvas.repaint();
        });

        //Add vertices
        addVerticesButton.addActionListener((ActionEvent e) -> {
            addVertices();
        });
        addVerticesMenuItem.addActionListener((ActionEvent e) -> {
            addVertices();
        });

        //Selection
        selectionButton.addActionListener((ActionEvent e) -> {
            selection();
        });
        selectionMenuItem.addActionListener((ActionEvent e) -> {
            selection();
        });

        //Add edges
        addEdgesButton.addActionListener((ActionEvent e) -> {
            addEdges();
        });
        addEdgesMenuItem.addActionListener((ActionEvent e) -> {
            addEdges();
        });

        //Delete
        frame.getDeleteButton().addActionListener((ActionEvent e) -> {
            deleteSelectedElements();
        });
        frame.getDeleteMenuItem().addActionListener((ActionEvent e) -> {
            deleteSelectedElements();
        });

        verticesList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //if command is not held down
                if (!isCommandPressed) {
                    //deselect all edges (if any were selected)
                    selectedEdgeIndices.clear();
                    setSelectedEdges();
                }

                //Select (or deselect) the vertices:
                //remove all previous selected vertices 
                selectedVertexIndices.clear();
                //get the list of selected vertices
                int[] tempIndices = verticesList.getSelectedIndices();
                //loop through the selected indices
                for (int i : tempIndices) {
                    //add each one to the main ArrayList
                    selectedVertexIndices.add(i);
                }
                setSelectedVertices();
                canvas.repaint();
            }
        });

        edgesList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //if command is not held down
                if (!isCommandPressed) {
                    //Deselect the vertices (if any were selected)
                    selectedVertexIndices.clear();
                    setSelectedVertices();
                }

                //Select (or deselect) the edges:
                //remove all previous selected edges
                selectedEdgeIndices.clear();
                //get the list of selected edges
                int[] tempIndices = edgesList.getSelectedIndices();
                //loop through the selected indices
                for (int i : tempIndices) {
                    //add each one to the main ArrayList
                    selectedEdgeIndices.add(i);
                }
                setSelectedEdges();
                canvas.repaint();
            }
        });

        titleTextField.addActionListener((ActionEvent e) -> {
            //The title of the vertex should be updated and the JList should be repainted
            if (selectedVertices.size() != 1) { //if there is not exactly one vertex selected
                return; //we can't allow the text field to edit the name of any vertices
            }
            //If there is exacly one vertex selected
            String newTitle = titleTextField.getText();
            //Check if the name is unique:
            for (Vertex v : vertices) { //cycle through all elements
                if (v.getTitle().equals(newTitle)) {
                    //Throw a dialogue telling the user that they can't name two vertexes the same thing
                    JOptionPane.showMessageDialog(frame, "Title \"" + newTitle + "\" has already been used in this graph.");
                    isCommandPressed = false; //unpress command
                    return; //leave (without renaming the vertex)
                }
            }
            //If the name is unique, rename the title
            selectedVertices.get(0).setTitle(newTitle);
            verticesList.repaint();
            canvas.repaint();
            setIsModified(true);
        });

        //Set up list models:
        //set them to their respective JLists
        frame.getVerticesList().setModel(verticesListModel);
        frame.getEdgesList().setModel(edgesListModel);
        //set their selection modes
        frame.getVerticesList().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        frame.getEdgesList().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        frame.getSaveAsMenuItem().addActionListener((ActionEvent e) -> {
            saveGraphAs();
        });

        frame.getOpenMenuItem().addActionListener((ActionEvent e) -> {
            if (isModified) {
                if (!shouldContinue("OK to discard changes?")) {
                    return;
                }
            }
            
            chooser.setFileFilter(filter);
            chooser.setDialogTitle("Open");
            chooser.setAcceptAllFileFilterUsed(false);
            
            int chooserResult = chooser.showOpenDialog(frame);
            if (chooserResult == JFileChooser.APPROVE_OPTION) {
                File loadFile = chooser.getSelectedFile();
                
                try {
                    //create an input stream from the selected file
                    FileInputStream istr = new FileInputStream(loadFile);
                    ObjectInputStream oistr = new ObjectInputStream(istr);
                    
                    //load the object from the serialized file
                    Object theObject = oistr.readObject();
                    oistr.close();
                    
                    //if this object is a graph
                    if (theObject instanceof Graph) {
                        //cast the loaded object to a graph
                        Graph loadedGraph = (Graph) theObject;
                        
                        //replace the old graph with the new one
                        replace(loadedGraph);
                        
                        setIsModified(false);
                        
                        canvas.repaint();
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(frame, "Unable to read selected file.\n"
                            + ex.getMessage(), "Oops!", JOptionPane.ERROR_MESSAGE);
                    isCommandPressed = false; //unpress command
                    return;
                } catch (ClassNotFoundException ex) {
                    JOptionPane.showMessageDialog(frame, "File is not a figures file.\n"
                            + ex.getMessage(), "Oops!", JOptionPane.ERROR_MESSAGE);
                    isCommandPressed = false; //unpress command
                    return;
                }
                
                //Update the save file:
                saveFile = loadFile;
                currentDirectory = loadFile; //update the current directory
                //Set the file chooser's directory
                chooser.setCurrentDirectory(currentDirectory);
                //Update the user's preference for the current directory
                prefs.put(Values.LAST_FILE_PATH, currentDirectory.toString());
                //debug print
                System.out.println("currentDirectory.toString(): " + currentDirectory.toString());
                
            }
        });

        frame.getSaveMenuItem().addActionListener((ActionEvent e) -> {
            if (saveFile == null) { //if there is no save file
                saveGraphAs();
                //if there is a save file specified programatically
            } else {
                System.out.print("");
                if (saveFile.exists()) { //if the file exists
                    saveGraph(); //save the file
                } else { //if the file does not exist
                    saveGraphAs(); //have the user save as
                }
            }
        });

        frame.getNewMenuItem().addActionListener((ActionEvent e) -> {
            if (isModified) {
                if (!shouldContinue("OK to discard changes?")) {
                    return;
                }
            }
            
            saveFile = null; //we no longer have a file to save
            
            clear();
            
            colorAllElements(Values.VERTEX_FILL_COLOR, Values.VERTEX_STROKE_COLOR, Values.EDGE_STROKE_COLOR);
            graph.setColors(Values.VERTEX_FILL_COLOR, Values.VERTEX_STROKE_COLOR, Values.EDGE_STROKE_COLOR);
            
            if (addingEdges) {
                exitAddEdgesState();
            }
            if (addingVertices) {
                exitAddVerticesState();
            }
            if (!selecting) {
                enterSelectionState();
            }
            
            setIsModified(false);
        });

        frame.getAddGraphMenuItem().addActionListener((ActionEvent e) -> {
            addGraphDialog.setLocationRelativeTo(null);
            addGraphDialog.setTitle("Add Vertices");
            
            addGraphDialog.setFocusToTextField();
            
            //Make it so that the user can press enter to press Add
            addGraphDialog.getRootPane().setDefaultButton(addGraphDialog.getAddButton());
            
            isCommandPressed = false;
            
            addGraphDialog.setVisible(true);
        });

        frame.getChangeColorsMenuItem().addActionListener((ActionEvent e) -> {
            graphColorChooserDialog.setLocationRelativeTo(null);
            graphColorChooserDialog.setTitle("Choose Colors");
            
            //Make it so that the user can press enter to press OK
            graphColorChooserDialog.getRootPane().setDefaultButton(graphColorChooserDialog.getOKButton());
            
            //Initialize the dialog with the graph's current colors
            graphColorChooserDialog.setVertexFillColor(graph.getVertexFillColor());
            graphColorChooserDialog.setVertexStrokeColor(graph.getVertexStrokeColor());
            graphColorChooserDialog.setEdgeStrokeColor(graph.getEdgeStrokeColor());
            
            isCommandPressed = false;
            
            graphColorChooserDialog.setVisible(true);
        });

        frame.getFormatVerticesMenuItem().addActionListener((ActionEvent e) -> {
            formatAllVertices();
            if (vertices != null) {
                if (!vertices.isEmpty()) {
                    setIsModified(true);
                }
            }
            canvas.repaint();
        });

        //MARK: addGraphDialog event handlers:
        //The add button
        addGraphDialog.getAddButton().addActionListener((ActionEvent e) -> {
            List<Vertex> toBeFormatted = new ArrayList();
            List<Vertex> vertices1 = graph.getVertices();
            List<Edge> edges1 = graph.getEdges();
            //This regex checks if the graph is properly formatted
            //(e.g. {{A,B},{B,A},{C,D}} )
            String properFormat = "\\{(\\{\\w+,\\w+\\})(,\\s*\\{\\w+,\\w+\\})*\\}";
            //Get the user's input
            String input = addGraphDialog.getGraphTextField().getText();
            //Check if it's properly formatted:
            if (!input.matches(properFormat)) { //if it's NOT properly formatted
                addGraphDialog.getErrorLabel().setText("Incorrect format. "
                        + "Please add one or more edges sepparated by commas.");
                return; //cancel the adding and let the user try again
            }
            //If it is properly formatted
            //Convert the input to a list of edges:
            //will be set to true if anything was actually changed
            boolean wasModified = false;
            //this will grab the titles of the vertices
            String titleRegex = "(\\w+),(\\w+)";
            Pattern p = Pattern.compile(titleRegex);
            Matcher m = p.matcher(input);
            //cycle through all of the edges entered by the user
            while (m.find()) {
                //Get the two titles of the vertices of the next edge
                String title1 = m.group(1); //the first title
                String title2 = m.group(2); //the second title
                //Create new vertex objects using the titles entered
                //(these are used to 1. Search the vertices list to check if
                //they exist in the graph or not and 2. Add a new vertex to
                //the list if this is a new vertex)
                Vertex newVertex1 = new Vertex(title1, DIAMETER);
                Vertex newVertex2 = new Vertex(title2, DIAMETER);
                //Get the indexes of the vertexes named title1 and title2
                //(if they exist):
                int index1 = vertices1.indexOf(newVertex1);
                int index2 = vertices1.indexOf(newVertex2);
                if (index1 == -1) {
                    //if this is a new vertex
                    vertices1.add(newVertex1); //add this vertex to the list
                    toBeFormatted.add(newVertex1);
                    wasModified = true;
                } else {
                    //if this vertex is already contained in the graph
                    //reassign the reference newVertex1 to the vertex that
                    //is already in the graph but has the same name:
                    newVertex1 = vertices1.get(index1);
                }
                if (index2 == -1) {
                    //if this is a new vertex
                    vertices1.add(newVertex2); //add this vertex to the list
                    toBeFormatted.add(newVertex2);
                    wasModified = true;
                } else {
                    //if this vertex is already contained in the graph
                    //reassign the reference newVertex2 to the vertex that
                    //is already in the graph but has the same name:
                    newVertex2 = vertices1.get(index2);
                }
                //At this point, newVertex1 and newVertex2 are the vertices
                //in the graph that we want to work with (whether their new
                //or already existed in the graph).
                //Check if the edge already exists:
                //If newVertex1 is NOT already connected to newVertex2
                if (!newVertex1.isAdjacentTo(newVertex2)) {
                    //create a new edge between newVertex1 and newVertex2
                    Edge newEdge = new Edge(newVertex1, newVertex2);
                    edges1.add(newEdge); //add the edge to the list
                    wasModified = true;
                }
                //If newVertex1 is already adjacent to newVertex2, then it
                //must already be in edges and we don't need to do anything else
                //If there was at least one new vertex or edge
                if (wasModified) {
                    setIsModified(true);
                }
                //update the list models
                updateVerticesListModel();
                updateEdgesListModel();
                //Format the new vertices
                formatVertices(toBeFormatted);
                canvas.repaint();
            }
            addGraphDialog.setVisible(false); //close the dialog
        });

        addGraphDialog.getCancelButton().addActionListener((ActionEvent e) -> {
            addGraphDialog.setVisible(false); //close the dialog
        });

        //MARK: Color choosing dialog
        //Choose buttons:
        graphColorChooserDialog.getVertexFillColorChooseButton().addActionListener((ActionEvent e) -> {
            Color newColor = JColorChooser.showDialog(frame, "Choose color",
                    graphColorChooserDialog.getVertexFillColor()); //get the color chosen by the user
            graphColorChooserDialog.setVertexFillColor(newColor); //set the sample fill color
            sampleCanvas.repaint(); //repaint the canvas
        });
        graphColorChooserDialog.getVertexStrokeColorChooseButton().addActionListener((ActionEvent e) -> {
            Color newColor = JColorChooser.showDialog(frame, "Choose color",
                    graphColorChooserDialog.getVertexStrokeColor()); //get the color chosen by the user
            graphColorChooserDialog.setVertexStrokeColor(newColor); //set the sample stroke color
            sampleCanvas.repaint(); //repaint the canvas
        });
        graphColorChooserDialog.getEdgeStrokeColorChooseButton().addActionListener((ActionEvent e) -> {
            Color newColor = JColorChooser.showDialog(frame, "Choose color",
                    graphColorChooserDialog.getEdgeStrokeColor()); //get the color chosen by the user
            graphColorChooserDialog.setEdgeStrokeColor(newColor); //set the sample fill color
            sampleCanvas.repaint(); //repaint the canvas
        });

        //cancel button
        graphColorChooserDialog.getCancelButton().addActionListener((ActionEvent e) -> {
            graphColorChooserDialog.setVisible(false);
        });

        //ok button
        graphColorChooserDialog.getOKButton().addActionListener((ActionEvent e) -> {
            //Get the colors from the dialog
            Color newVertexFillColor = graphColorChooserDialog.getVertexFillColor();
            Color newVertexStrokeColor = graphColorChooserDialog.getVertexStrokeColor();
            Color newEdgeStrokeColor = graphColorChooserDialog.getEdgeStrokeColor();
            
            //Check if the new colors are different from the old colors
            if (newVertexFillColor == graph.getVertexFillColor()) {
                setIsModified(true); //label the graph as modified
            }
            if (newVertexStrokeColor == graph.getVertexStrokeColor()) {
                
            }
            if (newEdgeStrokeColor == graph.getEdgeStrokeColor()) {
                
            }
            
            //set the graph's colors
            graph.setColors(newVertexFillColor, newVertexStrokeColor, newEdgeStrokeColor);
            
            //Set the colors of the current vertices and edges
            colorAllElements(newVertexFillColor, newVertexStrokeColor, newEdgeStrokeColor);
            
            //dismiss the dialog
            graphColorChooserDialog.setVisible(false);
            
            canvas.repaint(); //repaint the canvas
        });
    }

    //MARK: Other methods--------------------
    /**
     * Loads all user preferences from the system
     */
    private void loadPreferences() {
        // This will define a node in which the preferences can be stored
        prefs = Preferences.userRoot().node(this.getClass().getName());

        //Get the file path from user preferences (return the current directory if 
        //no preference was set yet):
        String filePath = prefs.get(Values.LAST_FILE_PATH, System.getProperty("user.dir"));
        currentDirectory = new File(filePath);

        //Get the user preference for showTitles menu item (return true as default)
        boolean showVertexNames = prefs.getBoolean(Values.SHOW_VERTEX_NAMES, true);
        //update the appropriate values
        frame.getShowVertexNamesMenuItem().setSelected(showVertexNames);
        showTitles = showVertexNames;
        canvas.setShowTitles(showVertexNames);
    }

    /**
     * Convenience method - adds the same KeyListener (KeyboardShortcuts) to
     * every focusable element in the entire frame. This may be a temporary
     * solution, but it works for now. Check out the following link if you want
     * a better solution:<>
     * https://stackoverflow.com/questions/1231622/setting-up-application-wide-key-listeners
     */
    private void addKeyboardShortcuts() {
        KeyAdapter keyboardShortcuts = new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                //if the user pressed backspace
                if (keyCode == KeyEvent.VK_BACK_SPACE) {
                    deleteSelectedElements();
                }
                //if the user is holding down the command key
                if (e.isMetaDown() || e.isControlDown()) {
                    isCommandPressed = true;
                    if (keyCode == KeyEvent.VK_A) { //if A was pressed
                        selectAllVertices();
                    }
                }

            }

            @Override
            public void keyReleased(KeyEvent e) {
                isCommandPressed = false;
            }
        };
        canvas.addKeyListener(keyboardShortcuts);
        verticesList.addKeyListener(keyboardShortcuts);
        edgesList.addKeyListener(keyboardShortcuts);
        addVerticesButton.addKeyListener(keyboardShortcuts);
        addEdgesButton.addKeyListener(keyboardShortcuts);
        selectionButton.addKeyListener(keyboardShortcuts);
        titleTextField.addKeyListener(keyboardShortcuts);
        frame.getDeleteButton().addKeyListener(keyboardShortcuts);
        frame.getGraphOutputTextField().addKeyListener(keyboardShortcuts);
    }

    private void selectAllVertices() {
        //Clear the selected indices
        selectedVertexIndices.clear();
        //Initialize a new primitive array of ints to hold all indices
        int[] allIndices = new int[vertices.size()];
        //loop through all the indices in vertices ArrayList
        for (int i = 0; i < vertices.size(); i++) {
            //add each one to the primitive array
            allIndices[i] = i;
            //add each one to the selected indices
            selectedVertexIndices.add(i);
        }
        //select all the indices in the JList
        verticesList.setSelectedIndices(allIndices);
        setSelectedVertices();
        canvas.repaint();
    }

    /**
     * Uses selectedIndex (a member variable) to set selectedVertex, highlight
     * selected vertex, un-highlights previously selected vertex set the
     * titleTextField content, (If selectedIndex = -1, then it deselects all).
     * This also repaints the canvas.
     */
    private void setSelectedVertices() {
        //Visually deselect the old selectedVertices
        if (!selectedVertices.isEmpty()) { //if there were previously selected vertices
            //loop through the old vertices
            for (Vertex selectedVertex : selectedVertices) {
                //unhighlight each one
                unHighlightVertex(selectedVertex);
            }
        }

        //Programattically select the new selectedVertices (or deselect all)
        if (selectedVertexIndices.isEmpty()) { //if the user deselected all vertices
            selectedVertices.clear(); //remove all elements from selectedVertices
            titleTextField.setText("");
            titleTextField.setEditable(false);
            verticesList.clearSelection(); //unselect the vertex in the JList
        } else { //if the user selected vertices
            selectedVertices.clear(); //clear the old selected vertices
            //store the new selected vertices:
            for (int i : selectedVertexIndices) { //loop through the selected indices
                Vertex selectedVertex = vertices.get(i); //store this selected vertex
                highlightVertex(selectedVertex);
                selectedVertices.add(selectedVertex); //add the new selection
            }
            if (selectedVertices.size() == 1) { //if exactly one vertex was selected
                //Get the title and put it in the titleTextField
                titleTextField.setText(selectedVertices.get(0).getTitle());
                titleTextField.setEditable(true); //enable editing of the title
            } else { //if not exactly one vertex was selected
                titleTextField.setText(""); //empty the titleTextField
                titleTextField.setEditable(false); //disable editing of titles
            }
        }
    }

    private void setSelectedEdges() {
        //Visually deselect the old selected edge
        if (!selectedEdges.isEmpty()) { //if there were previously selected edges
            //loop through the old edges
            for (Edge selectedEdge : selectedEdges) {
                //unhighlight each one
                unHighlightEdge(selectedEdge);
            }
        }

        //Programatically and visually select the new edge (or deselect entirely)
        if (selectedEdgeIndices.isEmpty()) { //if the user deselected all edges
            selectedEdges.clear();
            edgesList.clearSelection(); //unselect the edge in the JList
        } else { //if the user selected edges
            selectedEdges.clear(); //clear the old selected edges
            //store the new selected edges
            for (int i : selectedEdgeIndices) { //loop through the selected indices
                Edge selectedEdge = edges.get(i); //store this selected edge
                highlightEdge(selectedEdge);
                selectedEdges.add(selectedEdge); //add the new selection
            }
        }
    }

    private String generateVertexTitle() {
        String newName = "A"; //start with A
        if (vertices.isEmpty()) { //if names is empty
            return newName;
        }
        boolean matchFound = true;
        int i = 1;
        while (matchFound) {
            matchFound = false;
            newName = toBase26(i);
            for (Vertex v : vertices) {
                if (v.getTitle().equals(newName)) {
                    matchFound = true;
                    break;
                }
            }
            i++;
        }
        return newName;
    }

    /**
     * From
     * https://en.wikipedia.org/w/index.php?title=Hexavigesimal&oldid=578218059#Bijective_base-26
     *
     * @param n
     * @return The number n + 65 in base 26 ((int) 'A' = 65 )
     */
    public static String toBase26(int n) {
        StringBuilder ret = new StringBuilder();
        while (n > 0) {
            --n;
            ret.append((char) ('A' + n % 26));
            n /= 26;
        }
        // reverse the result, since its
        // digits are in the wrong order
        return ret.reverse().toString();
    }

    /**
     * Positions all vertices in the graph in an evenly spaced circle
     */
    public void formatAllVertices() {
        formatVertices(vertices);
    }

    /**
     * Positions all vertices passed to this function in an evenly spaced circle
     *
     * @param vs A list of vertices (Must contain only vertices that already
     * exist in the graph)
     */
    private void formatVertices(List<Vertex> vs) {
        int xCent = canvas.getWidth() / 2;
        int yCent = canvas.getHeight() / 2;
        final double radius = Values.FORMAT_RADIUS;
        //the change in angle between each vertex
        final double delta = (2 * Math.PI) / vs.size();
        //this angle changes to position each vertex (start at 360 degrees)
        double angle = (3 * Math.PI) / 2;

        for (Vertex v : vs) {
            //calculate the positions
            double x = xCent + radius * Math.cos(angle);
            double y = yCent + radius * Math.sin(angle);
            v.setLocation(x, y); //position the vertex
            angle -= delta; //decrement the angle
        }
    }

    /**
     * Rotates all vertices by 90 degrees counter-clockwise about the origin
     */
    private void rotateVertices90() {
        for (Vertex v : vertices) {
            double oy = canvas.getHeight() / 2;
            double x = v.getLocation().x;
            double y = v.getLocation().y;
            v.setLocation(2 * oy - y, x);
        }
    }

    /**
     * Used for loading a graph from a file
     *
     * @param newGraph The new graph that will replace the old graph (the graph
     * we're calling this function on is the old graph)
     */
    public void replace(Graph newGraph) {
        //MARK: Vertices and edges
        //Get a reverence to the new graph's vertices and edges
        List<Vertex> newVertices = newGraph.getVertices();
        List<Edge> newEdges = newGraph.getEdges();

        vertices.clear(); //remove all elements from the current vertices
        for (Vertex v : newVertices) { //loop through new list
            vertices.add(v); //add each vertex to the vertices list
        }

        updateVerticesListModel();

        edges.clear(); //remove all elements from the current edges
        for (Edge e : newEdges) { //loop through new list
            edges.add(e); //add each edge to the edges list
        }

        updateEdgesListModel();

        //MARK: Colors
        //get the new graph's colors
        Color newVertexFillColor = newGraph.getVertexFillColor();
        Color newVertexStrokeColor = newGraph.getVertexStrokeColor();
        Color newEdgeStrokeColor = newGraph.getEdgeStrokeColor();

        //update the graph's colors
        graph.setVertexFillColor(newVertexFillColor);
        graph.setVertexStrokeColor(newVertexStrokeColor);
        graph.setEdgeStrokeColor(newEdgeStrokeColor);

        //MARK: Update the list selection:
        //deselect all vertices
        verticesList.clearSelection();
        selectedVertexIndices.clear(); //clear all elements
        setSelectedVertices();

        //deselect all edges
        edgesList.clearSelection();
        selectedEdges.clear();
        setSelectedEdges();
        canvas.repaint();
    }

    /**
     * Clears the graph of all elements
     */
    public void clear() {
        vertices.clear();
        edges.clear();

        updateVerticesListModel();
        updateEdgesListModel();

        //deselect the vertices
        selectedVertexIndices.clear();
        setSelectedVertices();
        canvas.repaint();
    }

    private void pressVertices() {
        if (clickedVertices.isEmpty()) {
            return;
        }
        //cycle through all the clicked vertices
        for (Vertex clickedVertex : clickedVertices) {
            clickedVertex.setFillColor(graph.getVertexFillColor().darker());
            clickedVertex.setStrokeColor(Values.EDGE_PRESSED_COLOR);
        }
    }

    private void pressEdges() {
        if (clickedEdges.isEmpty()) {
            return;
        }
        //cycle through all the clicked edges
        for (Edge clickedEdge : clickedEdges) {
            clickedEdge.setStrokeColor(Values.EDGE_PRESSED_COLOR);
        }
    }

    private void unpressVertices() {
        if (clickedVertices.isEmpty()) {
            return;
        }
        //cycle through all the clicked vertices
        for (Vertex clickedVertex : clickedVertices) {
            clickedVertex.setFillColor(graph.getVertexFillColor());
            clickedVertex.setStrokeColor(Values.EDGE_HIGHLIGHT_COLOR);
        }
    }

    private void unpressEdges() {
        if (clickedEdges.isEmpty()) {
            return;
        }
        //cycle through all the clicked edges
        for (Edge clickedEdge : clickedEdges) {
            clickedEdge.setStrokeColor(Values.EDGE_HIGHLIGHT_COLOR);
        }
    }

    /**
     * Holds the code that checks which vertices/edges the user clicked (if any)
     * and updates the lastX and lastY variables in preparation for moving the
     * vertex (which happens in the mouseMotionListener).
     *
     * @param mx
     * @param my
     */
    private void selectVertexOrEdge(int mx, int my) {
        boolean didSelectElement = false;

        //MARK: Select vertex
        //Find the topmost vertex that contains the mouse click (if any):
        //if vertices is empty, then there are definitely no edges and we can 
        //stop here. (A graph can't have edges without vertices.)
        if (vertices.isEmpty()) {
            return;
        }
        //If vertices is empty, then edges is definitly empty, and we don't have
        //to worry about clicking the canvas because nothing could have been
        //clicked in the first place.

        //click the vertex (if any)
        didSelectElement = selectVertex(mx, my);

        //MARK: Select edge
        if (didSelectElement == false) { //if no vertex was selected
            didSelectElement = selectEdge(mx, my);
        }

        //MARK: Select canvas
        if (didSelectElement == false) { //if no edge and no vertex was selected
            selectCanvas();
        }

        //update the last position
        lastX = mx;
        lastY = my;
    }

    /**
     * Convenience method to improve readability of
     * {@link selectVertexOrEdge(int,int)}. Checks if there is a vertex at
     * (mx,my) and selects it if it exists.
     *
     * @param mx The x-position of the user click
     * @param my The y-position of the user click
     */
    private boolean selectVertex(int mx, int my) {
        //if vertices is empty, we don't need to do anything
        if (vertices.isEmpty()) {
            boolean didSelectElement = false;
            return didSelectElement;
        }
        for (int i = vertices.size() - 1; i >= 0; --i) {
            Vertex currentVertex = vertices.get(i);
            //if this vertex contains the mouse click:
            if (currentVertex.getPositionShape().contains(mx, my)) {
                //If the clicked vertex is one of multiple already selected vertices
                if (selectedVertices.contains(currentVertex)) {
                    //If the command button is held down
                    if (isCommandPressed) {
                        //Remove the clicked vertex from the selection:
                        //remove the selected vertex's index
                        selectedVertexIndices.remove(Integer.valueOf(i));
                        //Convert the selected indices to an array
                        int[] tempIndices = selectedIndicesToArray(selectedVertexIndices);
                        //Set selected indices of the verticesList to the array
                        //version of selectedVertexIndices
                        verticesList.setSelectedIndices(tempIndices);
                        setSelectedVertices();
                    } else { //if the command button is not held down
                        //add the selected vertices to clickedVertices (for moving)
                        clickedVertices.addAll(selectedVertices);
                        clickedEdges.addAll(selectedEdges);
                    }
                    //if the user clicked a new, unselected vertex
                } else {
                    System.out.print("");
                    if (isCommandPressed) { //if the command key is held down
                        //Add the new vertex to the selection:
                        //append the index of this clicked vertex to the selection
                        selectedVertexIndices.add(i);
                        //Convert the selected indices to an array
                        int[] tempIndices = selectedIndicesToArray(selectedVertexIndices);
                        //Set selected indices of the verticesList to the array
                        //version of selectedVertexIndices
                        verticesList.setSelectedIndices(tempIndices);
                        setSelectedVertices();
                        //add the selected vertices to clickedVertices (for moving)
                        clickedVertices.addAll(selectedVertices);
                        clickedEdges.addAll(selectedEdges);
                    } else { //if the command key is not held down
                        //store the clicked vertex (for moving)
                        clickedVertices.add(currentVertex);
                        //Update the selection:
                        //deselect any selected edges
                        selectedEdgeIndices.clear();
                        setSelectedEdges();
                        //select the vertex
                        verticesList.setSelectedIndex(i);
                        selectedVertexIndices.clear(); //empty the old selected indices
                        selectedVertexIndices.add(i); //update selected indices
                        setSelectedVertices();
                    }
                }
                //Whether the user clicked a selected or unselected vertex:
                canvas.repaint(); //repaint the canvas
                boolean didSelectElement = true; //the user did click a vertex
                return didSelectElement; //exit the loop (we don't need to check the rest)
            }
            //move on to the next vertex
        }
        //if no vertices were selected
        boolean didSelectElement = false;
        return didSelectElement;
    }

    /**
     * Convenience method to improve readability of
     * {@link selectVertexOrEdge(int,int)}. Checks if there is an edge at
     * (mx,my) and selects it if it exists.
     *
     * @param mx The x-position of the user click
     * @param my The y-position of the user click clickedBlankSpace and
     * didSelectVertex
     */
    private boolean selectEdge(int mx, int my) {
        if (edges.isEmpty()) {
            boolean didSelectElement = false;
            return didSelectElement;
        }
        //true if edge "e" was clicked (in loop below), false if no edge was clicked
        boolean clickedAnEdge;
        for (int i = edges.size() - 1; i >= 0; --i) { //loop through edges
            Edge e = edges.get(i); //get the next edge in the list
            //Check if the current edge was clicked
            clickedAnEdge = isEdgeClicked(e, mx, my);
            //If we clicked edge e
            if (clickedAnEdge) {
                //if the clicked edge is one of multiple already selected edges
                if (selectedEdges.contains(e)) {
                    if (isCommandPressed) {  //if command is held down
                        //We want to deselect this edge:
                        //Remove the clicked edge from the selection:
                        //remove the selected edge's index
                        selectedEdgeIndices.remove(Integer.valueOf(i));
                        //Convert the selected indices to an array
                        int[] tempIndices = selectedIndicesToArray(selectedEdgeIndices);
                        //Set selected indices of the edgesList to the array
                        //version of selectedEdgeIndices
                        edgesList.setSelectedIndices(tempIndices);
                        setSelectedEdges();
                    } else { //if command is not held down
                        //We want to allow the user to move all selected edges:
                        //add the selected edges to clickedEdges (for moving)
                        clickedEdges.addAll(selectedEdges);
                        clickedVertices.addAll(selectedVertices);
                    }
                    //if the user clicked an entirely new edge
                } else {
                    System.out.print("");
                    if (isCommandPressed) { //if command is held down
                        //We want to add the new edge to the current set of 
                        //already selected edges:
                        //append the index of this clicked edge to the selection
                        selectedEdgeIndices.add(i);
                        //Convert the selected indices to an array
                        int[] tempIndices = selectedIndicesToArray(selectedEdgeIndices);
                        //Set selected indices of the edgesList to the array
                        //version of selectedEdgeIndices
                        edgesList.setSelectedIndices(tempIndices);
                        setSelectedEdges();
                        //add the selected edges to clickedEdges (for moving)
                        clickedEdges.addAll(selectedEdges);
                        clickedVertices.addAll(selectedVertices);
                    } else { //if command is not held down
                        //We want to make this the only selected edge:
                        //store the clicked edge (for moving)
                        clickedEdges.add(e);
                        //Update the selection:
                        //deselect all vertices
                        selectedVertexIndices.clear();
                        setSelectedVertices();
                        //select the edge
                        edgesList.setSelectedIndex(i);
                        //clear the previous selection
                        selectedEdgeIndices.clear();
                        //add this index to the selection
                        selectedEdgeIndices.add(i);
                        setSelectedEdges();
                    }
                }
                //Whether we clicked a selected or unselected edge:
                canvas.repaint();
                boolean didSelectElement = true;
                return didSelectElement; //exit the loop (we don't need to check the rest)
            }
            //otherwise check next edge
        }
        //if no edges were clicked
        boolean didSelectElement = false;
        return didSelectElement;
    }

    /**
     * Convenience method to improve readability of
     * {@link selectVertexOrEdge(int,int)}. Deselects all vertices and edges.
     */
    private void selectCanvas() {
        //Deselect the vertex
        verticesList.clearSelection(); //deselect vertex in the list
        selectedVertexIndices.clear();
        setSelectedVertices();

        //Deselect the edge
        edgesList.clearSelection(); //deselect edge in the list
        selectedEdgeIndices.clear();
        setSelectedEdges();

        canvas.repaint();
    }

    /**
     * Convenience method to improve readability in the selectVertexOrEdge()
     * method. Checks to see if a point is close enough to the given edge to be
     * selected.
     *
     * @param qCurve The Quadratic Bezier Curve
     * @param mx The x value of the user's click point
     * @param my The y value of the user's click point
     * @return True if the given click point is within
     * Values.LINE_SELECTION_DISTANCE pixels of the given edge. False if the
     * click is too far.
     */
    private boolean isEdgeClicked(Edge e, int mx, int my) {
        QuadCurve2D edgeQCurve = e.getPositionShape();
        Point2D.Double p0 = (Point2D.Double) edgeQCurve.getP1();

        //get an ArrayList of all the points on the given curve
        List<Point2D.Double> pointsOnCurve = getPointsOnCurve(edgeQCurve);

        //cycle through all the points on the curve (except the first point)
        for (int i = 0; i < pointsOnCurve.size(); i++) {
            //get the current point
            Point2D.Double point = pointsOnCurve.get(i);
            //find the distance between the click and the current point
            double distance = Point2D.distance(mx, my, point.x, point.y);
            //if the distance is close enough to be selected
            if (distance <= Values.LINE_SELECTION_DISTANCE) {
                //signal that the point is close enough to the edge
                return true;
            }
        }
        //signal that the point is NOT close enough to the edge
        return false;
    }

    /**
     * This function finds all the points on a the given Quadratic Bezier Curve
     * and puts them in an ArrayList.
     *
     * @param qCurve the Quadratic Bezier Curve
     * @return An ArrayList of all the points on the given Quadratic Bezier
     * Curve
     */
    private List<Point2D.Double> getPointsOnCurve(QuadCurve2D qCurve) {
        Point2D.Double p0 = (Point2D.Double) qCurve.getP1();
        Point2D.Double p1 = (Point2D.Double) qCurve.getCtrlPt();
        Point2D.Double p2 = (Point2D.Double) qCurve.getP2();

        //MARK: Figure out what the percentage of the t-increment value should be to
        //make the points along the curve close together enough:
        Rectangle2D bounds = qCurve.getBounds2D();
        double rectArea = bounds.getWidth() * bounds.getHeight();
        //what percentage of rectArea is 2 pixels?
        //increase the numberator to decrease the number of points along the 
        //curve and vice versa
        double tInc = 3 / Math.sqrt(rectArea);

        List<Point2D.Double> points = new ArrayList();

        //MARK: Add the points along the curve to the array list:
        points.add(p0); //add the first point (no need to calculate this; t=0)
        double t = tInc;
        while (t < 1) { //only continue while t is less than 1 (t=1 means p2)
            //Find the x and y on the curve at this t value:
            //the +2 at the end of the equation for y is there to make the
            //points more in line with the visual stroke of the curve
            double x = (1 - t) * (1 - t) * p0.x + 2 * (1 - t) * t * p1.x + t * t * p2.x;
            double y = (1 - t) * (1 - t) * p0.y + 2 * (1 - t) * t * p1.y + t * t * p2.y + Values.EDGE_STROKE_WIDTH;
            //form a point from these two values to be added to the points array
            Point2D.Double point = new Point2D.Double(x, y);
            //add the new point to the points array
            points.add(point);
            //increment the t value for the next point on the curve
            t += tInc;
        }
        points.add(p2); //add the last point (no need to calculate this; t=1)
        return points;
    }

    private void moveElements(int incX, int incY) {
        /*
        This will hold the vertices that are to be moved in the move-vertices
        section. Unclicked vertices that are attached to clicked edges must
        be added to this list so that they can be moved later.
         */
        List<Vertex> verticesToMove = new ArrayList();
        //add all the clicked vertices first
        verticesToMove.addAll(clickedVertices);

        //MARK: Add edge endpoints to clickedVertices
        //Add all the edge endpoints to clickedVertices so that they will
        //be moved appropriately in the move-vertices section:
        //cycle through all clicked edges
        for (Edge clickedEdge : clickedEdges) {
            //Add both vertices attached to this edge:
            //get the first endpoint
            Vertex ep1 = clickedEdge.getEndpoint1();
            //if clickedVertices does NOT already contain this vertex
            if (!verticesToMove.contains(ep1)) {
                //add this vertex to be moved later
                verticesToMove.add(ep1);
            }
            //get the second endpoint
            Vertex ep2 = clickedEdge.getEndpoint2();
            //if clickedVertices does NOT already contain this vertex
            if (!verticesToMove.contains(ep2)) {
                //add this vertex to be moved later
                verticesToMove.add(ep2);
            }
            //increment the edge control point's location
            clickedEdge.incCtrlPoint(incX, incY);
        }

        //A set of edges whose control points have already been incremented
        //because, even though they were not selected, both of their
        //vertices are selected so they might as well be.
        List<Edge> incedEdges = new ArrayList();

        //MARK: Move all the selected vertices:
        //cycle through all clicked vertices
        for (Vertex clickedVertex : verticesToMove) {
            //if the vertex has any edges
            if (!clickedVertex.getEdges().isEmpty()) {
                for (Edge edge : clickedVertex.getEdges()) {
                    //if this edge was NOT already moved above
                    if (!clickedEdges.contains(edge)) {
                        /*
                            From here to the end of this loop, "old" means before 
                            clickedVertex is moved/incremented and "new" means after.
                         */
                        //Get the elements of this edge (p2 is the vertex that
                        //is moving, p1 is ctrl, p1 is staying still)
                        Point2D.Double p2 = clickedVertex.getCenter();
                        Point2D.Double p1 = edge.getCtrlPoint();
                        Vertex otherVertex = edge.getOtherEndpoint(clickedVertex);
                        Point2D.Double p0 = otherVertex.getCenter();

                        //if otherVertex is NOT in clickedVertices, then it will
                        //not be moving later and we need to move this edge's
                        //control point accordingly with angles and vectors
                        if (!verticesToMove.contains(otherVertex)) {
                            //the old vector from p2 to p0
                            Vector2D A1 = new Vector2D(p2, p0);
                            //the old vector from p2 to p1
                            Vector2D B1 = new Vector2D(p2, p1);

                            //the new p2 (after the move)
                            Point2D.Double newP2 = new Point2D.Double(p2.x + incX, p2.y + incY);
                            //the new vector from newP2 to p0
                            Vector2D A2 = new Vector2D(newP2, p0);

                            //get the ratio of the magnitude change from old to new
                            double magRatio = A2.getMagnitude() / A1.getMagnitude();
                            //get the difference of the angle change from old to new
                            double angleDiff = A1.getAngle() - A2.getAngle();

                            //find B2's new angle
                            double B2Angle = B1.getAngle() - angleDiff;
                            //the new vector from newP2 to newP1 with magnitude 1
                            Vector2D B2 = new Vector2D(B2Angle);
                            //find B2's new magnitude
                            double B2Mag = magRatio * B1.getMagnitude();
                            //multiply B2 by B2Mag to get the right size vector
                            B2.multiplyBy(B2Mag);

                            //add B2 to newP2 to get the newP1, the new control point
                            Point2D.Double newP1 = B2.add(newP2);

                            //set the new control point
                            edge.setCtrlPoint(newP1.x, newP1.y);
                        } else /*
                                If, however, the other vertex was clicked, then we 
                                simply need to increment the edge's control point with 
                                incX and incY and mark it so that we don't do it twice
                                (when we run into the other vertex)
                         */ if (!incedEdges.contains(edge)) {
                            //mark this so that it is not incremented when
                            //we run into the other vertex
                            incedEdges.add(edge);
                            edge.incCtrlPoint(incX, incY);
                        }
                    }
                }
            }
            //increment this vertex's position
            clickedVertex.incLocation(incX, incY);
        }

        setIsModified(true);
    }

    /**
     * Contains the code that allows the user to click and drag to select
     * multiple vertices at once. Constantly updates.
     */
    private void multipleSelection(int mx, int my) {
        //If we get to this point, we already are multiple-selecting and dragging
        //the mouse around the screen and startX and startY are already set for
        //both this and the canvas.
        canvas.setMultipleSelecting(true);

        //MARK: Select vertices
        //start by clearing out the old selected vertices (if needed)
        selectedVertexIndices.clear();
        //cycle through the vertices
        for (int i = vertices.size() - 1; i >= 0; i--) {
            //get the current vertex in the loop
            Vertex vertex = vertices.get(i);
            //get the center position of the vertex
            Point2D.Double pos = vertex.getCenter();
            int px = (int) pos.x;
            int py = (int) pos.y;
            //if the center is within the boundingBox
            if (ltlt(endX, px, startX)) { //a or d
                if (ltlt(endY, py, startY)) { //a
                    selectedVertexIndices.add(i);
                } else if (ltlt(startY, py, endY)) { //d
                    selectedVertexIndices.add(i);
                } //not in bounding box
            } else if (ltlt(startX, px, endX)) { //b or c
                if (ltlt(endY, py, startY)) { //b
                    selectedVertexIndices.add(i);
                } else if (ltlt(startY, py, endY)) { //c
                    selectedVertexIndices.add(i);
                } //not in bounding box
            } //not in bounding box
        }
        //now we have a list of selected vertices

        int[] tempIndices = selectedIndicesToArray(selectedVertexIndices);
        //set the selection to the indices of the selected vertices
        verticesList.setSelectedIndices(tempIndices);
        setSelectedVertices();
    }

    /**
     * Convenience method - converts an ArrayList of Integer objects to a
     * primitive array of ints.
     *
     * @param selectedIndices Either selectedVertexIndices or
     * selectedEdgeIndices
     * @return
     */
    private int[] selectedIndicesToArray(List<Integer> selectedIndices) {
        //initialize an array with the right number of elements
        int[] tempIndices = new int[selectedIndices.size()];
        int i = 0; //array index iterator
        //cycle through the selectedEdges
        for (int index : selectedIndices) {
            //add this edge index
            tempIndices[i] = index;
            //increment the array index
            i++;
        }
        return tempIndices;
    }

    /**
     * Convenience method (ltlt stands for less-than-less-than). Checks if (a is
     * less than b is less than c)
     *
     * @param a
     * @param b
     * @param c
     * @return
     */
    private boolean ltlt(int a, int b, int c) {
        return (a < b && b < c);
    }

    //MARK: States methods
    private void setSelectedVertices(boolean selected) {
        addVerticesButton.setSelected(selected);
        addVerticesMenuItem.setSelected(selected);
    }

    private void setSelectedEdges(boolean selected) {
        addEdgesButton.setSelected(selected);
        addEdgesMenuItem.setSelected(selected);
    }

    private void setSelectedSelection(boolean selected) {
        selectionButton.setSelected(selected);
        selectionMenuItem.setSelected(selected);
    }

    /**
     * The code that runs in both the selectionButton and the selectionMenuItem
     */
    private void selection() {
        if (addingVertices) {
            exitAddVerticesState();
            canvas.repaint();
        }
        if (addingEdges) {
            exitAddEdgesState();
            canvas.repaint();
        }
        enterSelectionState();
    }

    private void enterSelectionState() {
        setSelectedSelection(true);
        selecting = true;
    }

    private void exitSelectionState() {
        setSelectedSelection(false);
        selecting = false;
    }

    private void deleteSelectedElements() {
        if (!selectedVertexIndices.isEmpty()) {
            removeVertices();
        }
        if (!selectedEdgeIndices.isEmpty()) {
            removeEdges();
        }
    }

    /**
     * The code that runs in both the addVerticesButton and the
     * addVerticesMenuItem
     */
    private void addVertices() {
        exitAddEdgesState(); //leave the add edge state
        exitSelectionState();
        enterAddVerticesState(); //enter the add vertices state
        canvas.repaint();
    }

    /**
     * Adds a vertex to the canvas at the specified location.
     *
     * @param x The x-position of the new vertex
     * @param y The y-position of the new vertex
     */
    private void addVertex(int x, int y) {
        //Create a new vertex object
        Vertex newVertex = new Vertex(Values.DIAMETER);
        newVertex.setLocation(x, y);
        newVertex.setFillColor(graph.getVertexFillColor());
        newVertex.setStrokeColor(graph.getVertexStrokeColor());
        newVertex.setStrokeWidth(Values.VERTEX_STROKE_WIDTH);
        String newTitle = generateVertexTitle();
        newVertex.setTitle(newTitle);
        vertices.add(newVertex);

        updateVerticesListModel();

        //Update selection:
        int bottomIndex = vertices.size() - 1;
        //Set the selection of the visual JList to the bottom
        verticesList.setSelectedIndex(bottomIndex);
        selectedVertexIndices.clear(); //clear the selection
        selectedVertexIndices.add(bottomIndex); //select the new index
        setSelectedVertices();
        canvas.repaint();
        setIsModified(true);
    }

    private void removeVertices() {
        //The list of edges to remove
        List<Edge> removeEdges = new ArrayList();
        //Get all the edges from all the selected vertices:
        //first loop through all selected vertices
        for (Vertex v : selectedVertices) {
            //then add the list of edges from each selected vertices
            removeEdges.addAll(v.getEdges());
            //finally, remove the vertex from the vertices list
        }

        //remove the edges that were attached to this vertex from the list of edges
        edges.removeAll(removeEdges);

        //Cycle trhough the vertices to remove
        //Note: can't remove vertices by index, 
        //since indices change with each removal
        for (Vertex v : selectedVertices) {
            vertices.remove(v); //remove the matching vertex from the vertices
        }

        //Remove the edges that were attached to this vertex 
        //from all the other vertices associated with them
        for (Edge eg : removeEdges) { //cycle through all the edges to remove
            for (Vertex v : vertices) { //cycle through all the vertices
                v.removeEdge(eg); //remove each edge from each vertex
            }
        }

        updateVerticesListModel();
        updateEdgesListModel();
        //Deselect the vertices:
        selectedVertexIndices.clear();
        setSelectedVertices();
        //Deselect the edges:
        selectedEdgeIndices.clear();
        setSelectedEdges();

        canvas.repaint();
        setIsModified(true);
    }

    /**
     * Used to enter the state in which a user can add vertices to the canvas by
     * clicking anywhere as many times as they want.
     */
    private void enterAddVerticesState() {
        setSelectedVertices(true);
        addingVertices = true; //enter the vertex adding state
        canvas.setAddingVertex(true);
    }

    /**
     * Used to exit the state in which a user can add vertices to the canvas by
     * clicking anywhere. Called when the user enters selection state or add
     * edge state.
     */
    private void exitAddVerticesState() {
        setSelectedVertices(false);
        addingVertices = false; //exit the state
        canvas.setAddingVertex(false);
    }

    /**
     * The code that runs in both the addEdgesButton and the addEdgesMenuItem
     */
    private void addEdges() {
        if (vertices == null) {
            JOptionPane.showMessageDialog(frame, "Need at least two vertices to add an edge.");
            setSelectedEdges(false);
            isCommandPressed = false; //unpress command
            return;
        }
        if (vertices.isEmpty() || vertices.size() == 1) {
            JOptionPane.showMessageDialog(frame, "Need at least two vertices to add an edge.");
            setSelectedEdges(false);
            isCommandPressed = false; //unpress command
            return;
        }
        exitAddVerticesState();
        exitSelectionState();
        enterAddEdgeState();
        canvas.repaint();
    }

    private void addEdge(int mx, int my) {
        //Find out which vertex was clicked (if any):
        if (firstSelectedVertex == null) { //if this is null, the user hasn't chosen their first vertex
            /*
            if there is an edge in edit mode, we want to provide priority to the
            edge's control point (in case it's on top of a vertex)
            */
            if (editingEdge != null) {
                //if the user clicked the control point
                if (editingEdge.getCtrlPointPositionShape().contains(mx, my)) {
                    //signal to the mouseDragged function in canvas's mouse motion
                    //listener that the user is moving a control point
                    movingControlPoint = true;
                } else { //if the user did not click the control point
                    editingEdge = null;
                    canvas.setEditingEdge(null);
                    edgesList.clearSelection(); //deselect edge in the list
                    selectedEdgeIndices.clear();
                    setSelectedEdges();
                    canvas.repaint();
                }
            }
            //if editingEdge is null, then there is not an edge in edit mode yet
            
            /*
            select the first vertex of the edge (because selecting a vertex
            should have priority over selecting an edge to edit
            */
            boolean vertexWasSelected = selectFirstVertex(mx, my);

            //if no vertex was selected
            if (!vertexWasSelected) {
                //Check if the user selected an edge:
                if (!edges.isEmpty()) { //if there are edges to be chosen
                    //cycle through all the edges
                    for (Edge edge : edges) {
                        //if this edge was clicked
                        if (isEdgeClicked(edge, mx, my)) {
                            //set the editingEdge
                            editingEdge = edge;
                            canvas.setEditingEdge(edge);
                            //find the index of the editingEdge
                            int index = edges.indexOf(editingEdge);
                            //select the editingEdge
                            selectedEdgeIndices.add(index);
                            edgesList.setSelectedIndex(index);
                            setSelectedEdges();
                            canvas.repaint();
                        }
                    }
                }
            }
        } else { //The user has already chosen their first vertex
            //add the second vertex or cancel the active edge
            selectSecondVertex(mx, my);
        }
    }

    /**
     * Part I of the edge adding process. The user must click a vertex to start
     * from.
     *
     * @param mx
     * @param my
     * @return True if a vertex was selected, false if no vertex was selected
     */
    private boolean selectFirstVertex(int mx, int my) {
        //(If we reach this point, vertices.size() is at least 2)
        for (Vertex currentVertex : vertices) { //loop through the vertices
            //if we can add edges to this vertex in the first place
            //(don't bother checking if shape contains mouse position if not):
            if (currentVertex.canAddEdges()) {
                //Check if this vertex contains the mouse click:
                if (currentVertex.getPositionShape().contains(mx, my)) {
                    firstSelectedVertex = currentVertex; //assign the first vertex
                    canvas.setFirstSelectedVertex(firstSelectedVertex);
                    //Make it so that user can't add edge from a vertex to itself:
                    firstSelectedVertex.setCanAddEdges(false);
                    //Make it so that user can't add an edge to vertices that are already
                    //connected to the firstSelectedVertex:
                    firstSelectedVertex.assignCanAddEdgesToConnectedVertices();
                    //Reset the highlights
                    highlightAvailableVertices();
                    lastX = mx;
                    lastY = my;
                    canvas.setLastPosition(lastX, lastY);
                    canvas.repaint();
                    return true; //we've assigned the first selected vertex and we're done
                }
            }
        }
        //if we reach this point, the user hasn't selected and vertex.
        return false;
    }

    /**
     * Part II of the edge adding process. The user must choose a second vertex
     * to draw an edge to.
     *
     * @param mx
     * @param my
     * @return True if the user selected a second vertex, false if the user
     * just clicked the canvas and canceled the active edge.
     */
    private boolean selectSecondVertex(int mx, int my) {
        //(If we reach this point, vertices.size() is at least 2)
        for (Vertex currentVertex : vertices) { //loop through the vertices
            //If this vertex can have edges added to it (no use checking if
            //its shape contains the mouse click if not):
            if (currentVertex.canAddEdges()) {
                //If this figure contains the mouse click:
                if (currentVertex.getPositionShape().contains(mx, my)) {
                    //Create a new edge with the two vertices
                    Edge newEdge = new Edge(firstSelectedVertex, currentVertex);
                    newEdge.setStrokeWidth(Values.EDGE_STROKE_WIDTH);

                    edges.add(newEdge); //Add the edge to the graph

                    updateEdgesListModel(); //update the visual JList

                    exitAddEdgesState(); //exit the add edge state
                    //reenter the add edge state (allow user to add more edges)
                    enterAddEdgeState();
                    canvas.repaint();
                    
                    //set the editingEdge
                    editingEdge = newEdge;
                    canvas.setEditingEdge(newEdge);
                    
                    //Update selection
                    int lastIndex = edges.size() - 1; //last index in edges
                    edgesList.setSelectedIndex(lastIndex);
                    selectedEdgeIndices.clear();
                    selectedEdgeIndices.add(lastIndex);
                    setSelectedEdges();

                    setIsModified(true);

                    return true; //we don't need to check anymore
                }
            }
        }
        //If we reach this point, we want to cancel the edge
        exitAddEdgesState();
        //reenter the add edge state (allow user to add more edges)
        enterAddEdgeState();
        canvas.repaint();
        return false;
    }

    private void removeEdges() {
        //If the user did not choose an edge
        if (selectedEdgeIndices.isEmpty()) {
            return;
        }
        //Get a reference to the selected edges:
        //create a temporary ArrayList to hold the edges to be removed
        List<Edge> edgesToRemove = new ArrayList();
        for (Edge e : selectedEdges) { //loop through all selected edges
            edgesToRemove.add(e); //mark this edge to be removed
        }

        //Remove the edges from the vertices that they are attached to
        for (Edge e : edgesToRemove) {
            //Remove this edge from the vertices that the edge is attached to
            e.getEndpoint1().removeEdge(e);
            e.getEndpoint2().removeEdge(e);
        }

        //remove all the edges from the edges list
        edges.removeAll(selectedEdges);

        updateEdgesListModel();

        //update selection
        selectedEdgeIndices.clear();
        setSelectedEdges();
        
        //set the editingEdge to null
        editingEdge = null;
        canvas.setEditingEdge(null);
        //in case the user was holding down the mouse when they switched states
        movingControlPoint = false;

        canvas.repaint();
    }

    private void enterAddEdgeState() {
        setSelectedEdges(true);

        addingEdges = true; //enter the edge adding state
        //highlight all of the vertexes to provide a visual cue that the user is supposed
        //to click one to add the edge

        //Update vertex selection
        verticesList.clearSelection(); //clear the visual selection in the JList
        //deselect the vertex
        selectedVertexIndices.clear();
        setSelectedVertices();

        //Update edge selection
        //if selectedEdgeIndeces is not empty
        if (!selectedEdgeIndices.isEmpty()) {
            //find the last selected index
            int lastIndex = selectedEdgeIndices.get(selectedEdgeIndices.size() - 1);
            //set the last index to be the only one selected
            edgesList.setSelectedIndex(lastIndex);
            //deselect all edges
            selectedEdgeIndices.clear();
            //add the last index
            selectedEdgeIndices.add(lastIndex);
            setSelectedEdges();
        }

        //Assign the canAddEdges values of all the vertices and get the number of vertices
        //that can't have edges added to them
        int numberOfFalses = assignCanAddEdges();

        //Highglight appropriate vertices
        highlightAvailableVertices();
    }

    private void exitAddEdgesState() {
        setSelectedEdges(false);
        addingEdges = false;
        firstSelectedVertex = null; //prepare for the next edge
        canvas.setFirstSelectedVertex(null);
        //Unhighlight all vertices
        for (Vertex v : vertices) {
            unHighlightVertex(v);
        }
        //set the editingEdge to null
        editingEdge = null;
        canvas.setEditingEdge(null);
        //in case the user was holding down the mouse when they switched states
        movingControlPoint = false;
    }

    /**
     * Determines whether all vertices are available to add edges to (and
     * assigns their canAddEdges value) when the user enters the addEdgeState.
     * (A vertex is available if its degree is less than (order-1), where order
     * is the number of vertices in the graph.
     *
     * @return The number of vertexes that were assigned a canAddEdges value of
     * false
     */
    private int assignCanAddEdges() {
        int numberOfFalses = 0;
        for (Vertex v : vertices) {
            //if this vertex is available to add edges to:
            if (v.getDegree() < vertices.size() - 1) {
                v.setCanAddEdges(true);
            } else { //if this vertex is completely full
                v.setCanAddEdges(false);
                numberOfFalses++; //increment the number of falses
            }
        }
        return numberOfFalses;
    }

    /**
     * Highlights all vertices that are available to have an edge added to them
     * when the user enters the addEdgeState.
     */
    private void highlightAvailableVertices() {
        for (Vertex v : vertices) {
            //if this vertex is available to add edges to
            if (v.canAddEdges()) {
                v.setStrokeColor(Values.VERTEX_AVAILABLE_STROKE_COLOR);
                v.setStrokeWidth(Values.VERTEX_HIGHLIGHT_STROKE_WIDTH);
            } else { //if this vertex is completely full
                v.setStrokeColor(graph.getVertexStrokeColor());
                v.setStrokeWidth(Values.VERTEX_STROKE_WIDTH);
            }
        }
    }

    private void highlightVertex(Vertex vertex) {
        vertex.setStrokeColor(Values.EDGE_HIGHLIGHT_COLOR);
        vertex.setStrokeWidth(Values.VERTEX_HIGHLIGHT_STROKE_WIDTH);
    }

    private void unHighlightVertex(Vertex vertex) {
        vertex.setStrokeColor(graph.getVertexStrokeColor());
        vertex.setStrokeWidth(Values.VERTEX_STROKE_WIDTH);
    }

    private void highlightEdge(Edge edge) {
        edge.setStrokeWidth(Values.EDGE_HIGHLIGHT_STROKE_WIDTH);
        edge.setStrokeColor(Values.EDGE_HIGHLIGHT_COLOR);
    }

    private void unHighlightEdge(Edge edge) {
        edge.setStrokeWidth(Values.EDGE_STROKE_WIDTH);
        edge.setStrokeColor(graph.getEdgeStrokeColor());
    }

    /**
     * Changes the colors of the vertices and edges after the user chooses new
     * ones.
     *
     * @param newVertexFillColor
     * @param newVertexStrokeColor
     * @param newEdgeStrokeColor
     */
    private void colorAllElements(Color newVertexFillColor, Color newVertexStrokeColor, Color newEdgeStrokeColor) {
        for (Vertex v : vertices) {
            v.setFillColor(newVertexFillColor);
            v.setStrokeColor(newVertexStrokeColor);
        }

        for (Edge e : edges) {
            e.setStrokeColor(newEdgeStrokeColor);
        }
    }

    private void updateVerticesListModel() {
        verticesListModel.removeAllElements();
        for (Vertex v : vertices) {
            verticesListModel.addElement(v);
        }
    }

    private void updateEdgesListModel() {
        edgesListModel.removeAllElements();
        for (Edge eg : edges) {
            edgesListModel.addElement(eg);
        }
    }

    /**
     * Convenience method that asks a user if they want to continue if they try
     * to 1) open a file without saving 2) create a new file without saving 3)
     * close the program without saving
     *
     * @return true if the process should continue, false if you should return
     * out of the method
     */
    private boolean shouldContinue(String message) {
        isCommandPressed = false; //unpress command
        int selection = JOptionPane.showConfirmDialog(frame, message); //ask the user if they want to continue
        //if the user did not choose "yes", then we should cancel the operation
        //if the user did choose yes, then we should continue the operation
        //if the file has been saved, then we can just return true
        return selection == JOptionPane.YES_OPTION;

    }

    private void saveGraphAs() {
        if (graph.isEmpty()) {
            isCommandPressed = false; //unpress command
            JOptionPane.showMessageDialog(frame, "Cannot save an empty graph.");
            return;
        }

        chooser.setDialogTitle("Save");
        chooser.resetChoosableFileFilters(); //remove the .graph specification
        chooser.setAcceptAllFileFilterUsed(true);

        //Open the save dialogue and let the user choose 
        //where to save the file:
        int chooserResult = chooser.showSaveDialog(frame);
        //if the user successfully saved the file
        if (chooserResult == JFileChooser.APPROVE_OPTION) {

            //get the path of the file that the user selected
            Path path = chooser.getSelectedFile().toPath();

            //check if the file has an extension already
            String fileName = path.getFileName().toString(); //the name of the file
            if (!fileName.matches(".*\\.\\w+")) { //if the file name has NO extension
                //add .fig
                String fileNameWithExtension = fileName + ".graph";
                //use the resolveSibling method to change the old, 
                //extensionless file name to the new filename created above
                path = path.resolveSibling(fileNameWithExtension);
                //e.g. this will replace "curdir/sample2" with "curdir/sample2.graph"
            }

            saveFile = path.toFile(); //convert the path object to a file object
            currentDirectory = saveFile; //update the current directory
            //Set the file chooser's directory
            chooser.setCurrentDirectory(currentDirectory);
            //Update the user's preference for the current directory
            prefs.put(Values.LAST_FILE_PATH, currentDirectory.toString());

            //check if the file already exists
            if (Files.exists(path)) { //if the file already exists
                //ask the user if they want to continue
                if (!shouldContinue("OK to overwrite existing file?")) {
                    //if the user does not want to overwrite a pre-existing file
                    return;
                }
            }

            //Save the graph
            saveGraph();

        }
    }

    private void saveGraph() {
        if (saveFile == null) {
            return;
        }

        //Unhighlight all selected vertices and edges:
        for (Vertex v : selectedVertices) { //cycle through all the vertices
            unHighlightVertex(v); //unhighlight this vertex
        }
        for (Edge e : selectedEdges) { //cycle through all the vertices
            unHighlightEdge(e); //unhighlight this vertex
        }
        //Now colors won't be saved as "highlighted" accedentally

        try {
            //Create an output stream from the file
            FileOutputStream ostr = new FileOutputStream(saveFile);

            ObjectOutputStream oostr = new ObjectOutputStream(ostr);

            //Save the graph
            oostr.writeObject(graph);
            oostr.close(); //must do this to ensure completion
        } catch (IOException ex) {

            isCommandPressed = false; //unpress command
            JOptionPane.showMessageDialog(frame, "Unable to save file.\n"
                    + ex.getMessage(), "Oops!", JOptionPane.ERROR_MESSAGE);

        }

        //Re-highlight all selected vertices and edges:
        for (Vertex v : selectedVertices) { //cycle through all the vertices
            highlightVertex(v); //unhighlight this vertex
        }
        for (Edge e : selectedEdges) { //cycle through all the vertices
            highlightEdge(e); //unhighlight this vertex
        }

        setIsModified(false);
    }

    /**
     * Handles everything required to mark the graph as modified or not. This
     * includes setting the isModified boolean, setting the text of the
     * modifiedTextField, etc.
     *
     * @param isModified True if the graph has been modified, false if the graph
     * is not modified (saved or empty)
     */
    private void setIsModified(boolean isModified) {
        this.isModified = isModified;
        if (isModified) {
            modifiedTextField.setText("*");
        } else {
            modifiedTextField.setText("");
        }
    }

    //MARK: Getters and Setters
    public List<Vertex> getVertices() {
        return vertices;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public DefaultListModel getVerticesListModel() {
        return verticesListModel;
    }

    public DefaultListModel getEdgesListModel() {
        return edgesListModel;
    }

    public static void main(String[] args) {

        //handle Command-Q on Mac through window-closing
        System.setProperty("apple.eawt.quitStrategy", "CLOSE_ALL_WINDOWS");

        GraphController app = new GraphController();
        app.frame.setVisible(true);
    }

}
