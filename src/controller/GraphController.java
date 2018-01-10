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
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.AbstractAction;
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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
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
     * This is used for moving all vertices, not to be confused with selectedVertices,
     * which is used for deleting vertices and changing titles.
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
    private final JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
    private File saveFile;

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

        SampleCanvas sampleCanvas = graphColorChooserDialog.getSampleCanvas();
        sampleCanvas.setUp(graph); //Set up the sample canvas in the dialog

        addKeyboardShortcuts();

        enterSelectionState();

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

                    //if the user did not click any vertices (or is not moving any vertices)
                    if (clickedVertices.isEmpty()) {
                        //if the user did not click any edges (or is not moving any edges)
                        if (clickedEdges.isEmpty()) {
                            //update the endpoint of the selection box
                            endX = mx;
                            endY = my;
                            canvas.setEndPosition(mx, my);
                            //select the appropriate vertices
                            multipleSelection(mx, my);
                        } else { //if the user clicked any edges
                            //cycle through all clicked edges
                            for (Edge clickedEdge : clickedEdges) {
                                //move both nodes attached to this edge
                                clickedEdge.getEndpoint1().incLocation(incX, incY);
                                clickedEdge.getEndpoint2().incLocation(incX, incY);
                            }
                            setIsModified(true);
                        }
                    } else { //if the user clicked any vertices
                        //Move all the clicked vertices:
                        //cycle through all clicked vertices
                        for (Vertex clickedVertex : clickedVertices) {
                            //move each vertex
                            clickedVertex.incLocation(incX, incY);
                        }
                        setIsModified(true);
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
                        System.out.println("Do not close the window.");
                    } else { //if the user does want to close the window
                        System.exit(0); //the window should close
                    }
                } else { //if the user has closed the program after saving
                    System.exit(0); //the window should close
                }
            }

        });

        frame.getRotate90MenuItem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rotateVertices90();
                canvas.repaint();
            }
        });

        frame.getShowVertexNamesMenuItem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Toggle the showTitles boolean
                if (showTitles) {
                    showTitles = false;
                    canvas.setShowTitles(false);
                } else {
                    showTitles = true;
                    canvas.setShowTitles(true);
                }

                canvas.repaint();
            }
        });

        //Add vertices
        addVerticesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addVertices();
            }
        });
        addVerticesMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addVertices();
            }
        });

        //Selection
        selectionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selection();
            }
        });
        selectionMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selection();
            }
        });

        //Add edges
        addEdgesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addEdges();
            }
        });
        addEdgesMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addEdges();
            }
        });

        //Delete
        frame.getDeleteButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedElements();
            }
        });
        frame.getDeleteMenuItem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedElements();
            }
        });

        verticesList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //Deselect all edges (if any were selected)
                selectedEdgeIndices.clear();
                setSelectedEdges();

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
                //(which would redundantly run this again)
                //Deselect the vertices (if any were selected)
                selectedVertexIndices.clear();
                setSelectedVertices();
                
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

        titleTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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
                        return; //leave (without renaming the vertex)
                    }
                }
                //If the name is unique, rename the title
                selectedVertices.get(0).setTitle(newTitle);
                verticesList.repaint();
                canvas.repaint();
                setIsModified(true);
            }
        });

        //Set up list models:
        //set them to their respective JLists
        frame.getVerticesList().setModel(verticesListModel);
        frame.getEdgesList().setModel(edgesListModel);
        //set their selection modes
        frame.getVerticesList().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        frame.getEdgesList().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        frame.getSaveAsMenuItem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveGraphAs();
            }
        });

        frame.getOpenMenuItem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isModified) {
                    if (!shouldContinue("OK to discard changes?")) {
                        return;
                    }
                }

                setIsModified(false);

                chooser.setDialogTitle("Open");
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

                            canvas.repaint();
                        }
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(frame, "Unable to read selected file.\n"
                                + ex.getMessage(), "Oops!", JOptionPane.ERROR_MESSAGE);
                    } catch (ClassNotFoundException ex) {
                        JOptionPane.showMessageDialog(frame, "File is not a figures file.\n"
                                + ex.getMessage(), "Oops!", JOptionPane.ERROR_MESSAGE);
                    }

                    saveFile = loadFile; //update the save file

                }
            }
        });

        frame.getSaveMenuItem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (saveFile == null) {
                    saveGraphAs();
                } else {
                    saveGraph();
                }
            }
        });

        frame.getNewMenuItem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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
            }
        });

        frame.getAddGraphMenuItem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addGraphDialog.setLocationRelativeTo(null);
                addGraphDialog.setTitle("Add Vertices");

                addGraphDialog.setFocusToTextField();

                //Make it so that the user can press enter to press Add
                addGraphDialog.getRootPane().setDefaultButton(addGraphDialog.getAddButton());

                addGraphDialog.setVisible(true);
            }
        });

        frame.getChangeColorsMenuItem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                graphColorChooserDialog.setLocationRelativeTo(null);
                graphColorChooserDialog.setTitle("Choose Colors");

                //Make it so that the user can press enter to press OK
                graphColorChooserDialog.getRootPane().setDefaultButton(graphColorChooserDialog.getOKButton());

                //Initialize the dialog with the graph's current colors
                graphColorChooserDialog.setVertexFillColor(graph.getVertexFillColor());
                graphColorChooserDialog.setVertexStrokeColor(graph.getVertexStrokeColor());
                graphColorChooserDialog.setEdgeStrokeColor(graph.getEdgeStrokeColor());

                graphColorChooserDialog.setVisible(true);
            }
        });

        frame.getFormatVerticesMenuItem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                formatAllVertices();
                if (vertices != null) {
                    if (!vertices.isEmpty()) {
                        setIsModified(true);
                    }
                }
                canvas.repaint();
            }
        });

        //MARK: addGraphDialog event handlers:
        //The add button
        addGraphDialog.getAddButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<Vertex> toBeFormatted = new ArrayList();
                List<Vertex> vertices = graph.getVertices();
                List<Edge> edges = graph.getEdges();

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
                    int index1 = vertices.indexOf(newVertex1);
                    int index2 = vertices.indexOf(newVertex2);

                    if (index1 == -1) { //if this is a new vertex
                        vertices.add(newVertex1); //add this vertex to the list
                        toBeFormatted.add(newVertex1);
                        wasModified = true;
                    } else { //if this vertex is already contained in the graph
                        //reassign the reference newVertex1 to the vertex that
                        //is already in the graph but has the same name:
                        newVertex1 = vertices.get(index1);
                    }

                    if (index2 == -1) { //if this is a new vertex
                        vertices.add(newVertex2); //add this vertex to the list
                        toBeFormatted.add(newVertex2);
                        wasModified = true;
                    } else { //if this vertex is already contained in the graph
                        //reassign the reference newVertex2 to the vertex that
                        //is already in the graph but has the same name:
                        newVertex2 = vertices.get(index2);
                    }

                    //At this point, newVertex1 and newVertex2 are the vertices
                    //in the graph that we want to work with (whether their new
                    //or already existed in the graph).
                    //Check if the edge already exists:
                    //If newVertex1 is NOT already connected to newVertex2
                    if (!newVertex1.isAdjacentTo(newVertex2)) {
                        //create a new edge between newVertex1 and newVertex2
                        Edge newEdge = new Edge(newVertex1, newVertex2);
                        edges.add(newEdge); //add the edge to the list
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
            }
        });

        addGraphDialog.getCancelButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addGraphDialog.setVisible(false); //close the dialog
            }
        });

        //MARK: Color choosing dialog
        //Choose buttons:
        graphColorChooserDialog.getVertexFillColorChooseButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color newColor = JColorChooser.showDialog(frame, "Choose color",
                        graphColorChooserDialog.getVertexFillColor()); //get the color chosen by the user
                graphColorChooserDialog.setVertexFillColor(newColor); //set the sample fill color
                sampleCanvas.repaint(); //repaint the canvas
            }
        });
        graphColorChooserDialog.getVertexStrokeColorChooseButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color newColor = JColorChooser.showDialog(frame, "Choose color",
                        graphColorChooserDialog.getVertexStrokeColor()); //get the color chosen by the user
                graphColorChooserDialog.setVertexStrokeColor(newColor); //set the sample stroke color
                sampleCanvas.repaint(); //repaint the canvas
            }
        });
        graphColorChooserDialog.getEdgeStrokeColorChooseButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color newColor = JColorChooser.showDialog(frame, "Choose color",
                        graphColorChooserDialog.getEdgeStrokeColor()); //get the color chosen by the user
                graphColorChooserDialog.setEdgeStrokeColor(newColor); //set the sample fill color
                sampleCanvas.repaint(); //repaint the canvas
            }
        });

        //cancel button
        graphColorChooserDialog.getCancelButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                graphColorChooserDialog.setVisible(false);
            }
        });

        //ok button
        graphColorChooserDialog.getOKButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Get the colors from the dialog
                Color newVertexFillColor = graphColorChooserDialog.getVertexFillColor();
                Color newVertexStrokeColor = graphColorChooserDialog.getVertexStrokeColor();
                Color newEdgeStrokeColor = graphColorChooserDialog.getEdgeStrokeColor();

                //Check if the new colors are different from the old colors
                if (newVertexFillColor == graph.getVertexFillColor()
                        || newVertexStrokeColor == graph.getVertexStrokeColor()
                        || newEdgeStrokeColor == graph.getEdgeStrokeColor()) {
                    setIsModified(true); //label the graph as modified
                }

                //set the graph's colors
                graph.setColors(newVertexFillColor, newVertexStrokeColor, newEdgeStrokeColor);

                //Set the colors of the current vertices and edges
                colorAllElements(newVertexFillColor, newVertexStrokeColor, newEdgeStrokeColor);

                //dismiss the dialog
                graphColorChooserDialog.setVisible(false);

                canvas.repaint(); //repaint the canvas
            }
        });
    }

    //MARK: Other methods--------------------
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
                unhighlightVertex(selectedVertex);
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
        StringBuffer ret = new StringBuffer();
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
        this.formatVertices(this.vertices);
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
        final double delta = (2 * Math.PI) / vs.size(); //the change in angle between each vertex
        double angle = 0.0; //the angle that changes to position each vertex

        for (Vertex v : vs) {
            //calculate the positions
            double x = xCent + radius * Math.cos(angle);
            double y = yCent + radius * Math.sin(angle);
            v.setLocation(x, y); //position the vertex
            angle += delta; //increment the angle
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

        //MARK: Update the list selection
        int newIndex = vertices.size() - 1;
        verticesList.setSelectedIndex(newIndex);
        selectedVertexIndices.clear(); //clear all elements
        selectedVertexIndices.add(newIndex); //set the selected index
        setSelectedVertices();
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
        //MARK: Select vertex

        //Find the topmost vertex that 
        //contains the mouse click (if any):
        //if vertices is null, then there are definitely no edges and we can 
        //stop here. (A graph can't have edges without vertices.)
        if (vertices == null) {
            return;
        }
        //If vertices is null, then edges is definitly null, and we don't have
        //to worry about clicking the canvas because nothing could have been
        //clicked in the first place. 

        boolean clickedBlankSpace = true;
        boolean didSelectVertex = false;

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
                    }
                } else { //if the user clicked a new, unselected vertex
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
                clickedBlankSpace = false; //user didn't click blank space
                didSelectVertex = true; //the user did click a vertex
                break; //exit the loop (we don't need to check the rest)
            }
        }

        //MARK: Select edge
        //if edges is not null and the user did NOT select a vertex 
        //(which have priority over edges when it comes to selecting)
        if (edges != null && didSelectVertex == false) {
            //true if edge "e" was clicked (in loop below), false if no edge was clicked
            boolean clickedAnEdge = false;
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
                        }
                    } else { //if the user clicked an entirely new edge
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
                    clickedBlankSpace = false; //the user didn't click blank space
                    break; //exit the loop (we don't need to check the rest)
                }
                //otherwise check next edge
            }
        }
        //If edges is null, then the user might still be clicking blank canvas

        //MARK: Select canvas
        if (clickedBlankSpace) { //if the user clicked the canvas, not a vertex/edge
            //Deselect the vertex
            verticesList.clearSelection(); //deselect vertex in the list
            selectedVertexIndices.clear();
            setSelectedVertices();

            //Deselect the edge
            edgesList.clearSelection();; //deselect edge in the list
            selectedEdgeIndices.clear();
            setSelectedEdges();

            canvas.repaint();
        }

        //update the last position
        lastX = mx;
        lastY = my;
    }
    
    /**
     * Convenience method to improve readability in the selectVertexOrEdge()
     * method. This method checks if the given click position (mx,my) is in the
     * click area of the given edge. 
     * @param e The edge to be checked
     * @param mx The x coordinate of the click position
     * @param my The y coordinate of the click position
     * @return True of (mx,my) is in the click area of edge e.
     */
    private boolean isEdgeClicked(Edge e, int mx, int my) {
        boolean clickedAnEdge = false;
        if (isEdgeVertical(e)) { //if the edge is verticle
            //All we need to do is check if mx is close enough to e's x-position:

            //get an endpoint (arbitrary)
            Point2D.Double ep1 = e.getEndpoint1().getCenter();
            //the difference between mx and e's x-position
            double diff = ep1.x - mx;
            //find the absolute value
            if (diff < 0) {
                diff *= -1;
            }
            if (diff <= Values.LINE_SELECTION_DISTANCE) {
                //check if the point is on the edge (which is much simpler if the
                //edge is vertical (only have to check y-values)

                //get the other endpoint (we already have ep1 from above)
                Point2D.Double ep2 = e.getEndpoint2().getCenter();
                //Note: greater y is lower on canvas, smaller y is higher on canvas
                if (ep1.y < ep2.y) { //if ep1 is higher than ep2
                    //ep1.y<my<ep2.y
                    if (ep1.y < my && my < ep2.y) { //if my is between ep1.y and ep2.y
                        clickedAnEdge = true; //we clicked edge e
                    }
                } else //if ep2 is higher than ep1
                //ep2.y<my<ep1.y
                 if (ep2.y < my && my < ep1.y) { //if my is between ep2.y and ep1.y
                        clickedAnEdge = true; //we clicked edge e
                    }
            }
        } else { //if the edge is not verticle
            //Find the point on edge e that is closest to (mx,my) (the intersection, I)
            Point2D.Double I = getClosestPointOnEdge(mx, my, e);

            //Find the distance between I and (mx,my)
            double d = distance(I.x, I.y, mx, my);
            //(mx,my) is close enough to the line formed by e
            if (d <= Values.LINE_SELECTION_DISTANCE) {
                //If the intersection, I, is on the edge (not beyond it)
                if (isPointOnEdge(I.x, I.y, e)) {
                    clickedAnEdge = true; //we clicked edge e
                }
            }
        }
        return clickedAnEdge;
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
     * @param selectedIndices Either selectedVertexIndices or selectedEdgeIndices
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

    private Point2D.Double getClosestPointOnEdge(int Mx, int My, Edge e) {
        //Get the endpoints of the edge
        Vertex A = e.getEndpoint1();
        Vertex B = e.getEndpoint2();

        //get the positions of the endpoints
        double Ax = A.getCenter().x;
        double Ay = A.getCenter().y;
        double Bx = B.getCenter().x;
        double By = B.getCenter().y;

        //Handle undefined slope 
        //(if their exactly the same, make one slightly different)
        if (Ax == Bx) {
            Bx += 0.000001;
        }
        double m = (Ay - By) / (Ax - Bx); //the slope
        double m2 = m * m; //the slope squared

        //The position of the closest point on the edge to the point (Mx,My) = M
        //(Ix,Iy) = I
        double Ix = (m * My + Mx - m * Ay + m2 * Ax) / (m2 + 1);
        double Iy = m * (Ix - Ax) + Ay;

        return new Point2D.Double(Ix, Iy);
    }

    /**
     * Checks to see if a given point is on the given edge (not beyond it).
     *
     * @param x The x-value of the given point
     * @param y The y-value of the given point
     * @param e The edge
     * @return true of the point is on the edge, false if the point is beyond it
     */
    private boolean isPointOnEdge(double x, double y, Edge e) {
        //Check if the point is actually within the bounds of e:
        //The distance between the intersection, I, and endpoint1
        double ep1Dist = distance(x, y, e.getEndpoint1());
        //The distance between the intersection, I, and endpoint2
        double ep2Dist = distance(x, y, e.getEndpoint2());
        //The sum of the two distances
        double sumOfDist = ep1Dist + ep2Dist;
        //The distance between endpoint1 and endpoint2
        double epDist = distance(e.getEndpoint1(), e.getEndpoint2());

        //If the distance between the endpoints equals to sum of the distances
        //between each endpoint and the point in question, then the point in
        //question is on the edge (not beyond it)
        //To allow for small errors in rounding, the difference between the two
        //should be less than marginOfError
        double diff = sumOfDist - epDist;
        //find the absolute value
        if (diff < 0) {
            diff *= -1;
        }
        double marginOfError = 0.00001;
        if (diff < marginOfError) {
            return true;
        }
        return false;
    }

    /**
     * Checks to see if a given edge is perfectly vertical.
     *
     * @param e The edge to be checked
     * @return True if the edge is vertical, false if not
     */
    private boolean isEdgeVertical(Edge e) {
        //Use location not center, because center takes more calculation and
        //if the centers are perfectly aligned then the locations are as well:
        Point2D.Double a = e.getEndpoint1().getLocation();
        Point2D.Double b = e.getEndpoint2().getLocation();
        //A line is vertical if its slope is undefined, ?/0, where x1-x2=0
        double diff = a.x - b.x;
        //get the absolute value
        if (diff < 0) {
            diff *= -1;
        }
        double marginOfError = 0.00001; //allows for rounding errors
        //if the diff is 0, then the slope is undefined and the edge is verical:
        return (diff <= marginOfError);
    }

    /**
     * The distance between two points
     *
     * @param x1 The x-value of the first point
     * @param y1 The y-value of the first point
     * @param x2 The x-value of the second point
     * @param y2 The y-value of the second point
     * @return The distance between the two points
     */
    private double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    /**
     * The distance between a given point and a vertex
     *
     * @param x The x-value of the given point
     * @param y The y-value of the given point
     * @param v The vertex
     * @return The distance between the point and the vertex location
     */
    private double distance(double x, double y, Vertex v) {
        Point2D.Double p = v.getCenter();
        return distance(x, y, p.x, p.y);
    }

    /**
     * The distance between two vertices
     *
     * @param v1 The first vertex
     * @param v2 The second vertex
     * @return The distance between the two vertices locations
     */
    private double distance(Vertex v1, Vertex v2) {
        Point2D.Double p1 = v1.getCenter();
        Point2D.Double p2 = v2.getCenter();
        return distance(p1.x, p1.y, p2.x, p2.y);
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
            return;
        }
        if (vertices.isEmpty() || vertices.size() == 1) {
            JOptionPane.showMessageDialog(frame, "Need at least two vertices to add an edge.");
            setSelectedEdges(false);
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
            selectFirstVertex(mx, my);
        } else { //The user has already chosen their first vertex
            selectSecondVertex(mx, my);
        }
    }

    /**
     * Part I of the edge adding process. The user must click a vertex to start
     * from.
     *
     * @param mx
     * @param my
     */
    private void selectFirstVertex(int mx, int my) {
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
                    return; //we've assigned the first selected vertex and we're done
                }
            }
        }
        //if we reach this point, the user hasn't selected and vertex.
        //and we want to stay in the add edge state so that they can choose another vertex
    }

    /**
     * Part II of the edge adding process. The user must choose a second vertex
     * to draw an edge to.
     *
     * @param mx
     * @param my
     */
    private void selectSecondVertex(int mx, int my) {
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

                    //Update selection
                    int lastIndex = edges.size() - 1; //last index in edges
                    edgesList.setSelectedIndex(lastIndex);
                    selectedEdgeIndices.clear();
                    selectedEdgeIndices.add(lastIndex);
                    setSelectedEdges();

                    setIsModified(true);

                    return; //we don't need to check anymore
                }
            }
        }
        //If we reach this point, we want to cancel the edge
        exitAddEdgesState();
        //reenter the add edge state (allow user to add more edges)
        enterAddEdgeState();
        canvas.repaint();
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
        edgesList.clearSelection(); //clear the visual selection in the JList
        //deselect the edge
        selectedEdgeIndices.clear();
        setSelectedEdges();

        //Assign the canAddEdges values of all the vertices and get the number of vertices
        //that can't have edges added to them
        int numberOfFalses = assignCanAddEdges();

        if (numberOfFalses == vertices.size()) { //if none of the vertices can have edges added to them
            exitAddEdgesState(); //exit the state because there are no available edges
            enterSelectionState();
            return; //do not continue
        }

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
            v.setStrokeColor(graph.getVertexStrokeColor());
            v.setStrokeWidth(Values.VERTEX_STROKE_WIDTH);
        }
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
                highlightVertex(v);
            } else { //if this vertex is completely full
                unhighlightVertex(v);
            }
        }
    }

    private void highlightVertex(Vertex vertex) {
        vertex.setStrokeColor(Values.EDGE_HIGHLIGHT_COLOR);
        vertex.setStrokeWidth(Values.VERTEX_HIGHLIGHT_STROKE_WIDTH);
    }

    private void unhighlightVertex(Vertex vertex) {
        vertex.setStrokeColor(graph.getVertexStrokeColor());
        vertex.setStrokeWidth(Values.VERTEX_STROKE_WIDTH);
    }
    
    private void highlightEdge(Edge edge) {
        edge.setStrokeWidth(Values.EDGE_STROKE_WIDTH);
        edge.setStrokeColor(graph.getEdgeStrokeColor());
    }
    
    private void unHighlightEdge(Edge edge) {
        edge.setStrokeWidth(Values.EDGE_HIGHLIGHT_STROKE_WIDTH);
        edge.setStrokeColor(Values.EDGE_HIGHLIGHT_COLOR);
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
        int selection = JOptionPane.showConfirmDialog(frame, message); //ask the user if they want to continue

        if (selection != JOptionPane.YES_OPTION) { //if the user did not choose "yes"
            return false; //cancel the operation
        }

        //if the user did choose yes, then we should continue the operation
        //if the file has been saved, then we can just return true
        return true;

    }

    private void saveGraphAs() {
        if (graph.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Cannot save an empty graph.");
            return;
        }

        chooser.setDialogTitle("Save");

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

        try {
            //Create an output stream from the file
            FileOutputStream ostr = new FileOutputStream(saveFile);

            ObjectOutputStream oostr = new ObjectOutputStream(ostr);

            //Save the graph
            oostr.writeObject(graph);
            oostr.close(); //must do this to ensure completion
        } catch (IOException ex) {

            JOptionPane.showMessageDialog(frame, "Unable to save file.\n"
                    + ex.getMessage(), "Oops!", JOptionPane.ERROR_MESSAGE);

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
