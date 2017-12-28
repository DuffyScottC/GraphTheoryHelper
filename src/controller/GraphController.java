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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
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
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import views.AddGraphDialog;
import views.Canvas;
import views.GraphFrame;

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
    private Vertex selectedVertex = null;
    /**
     * The last selected edge in the edges JList
     */
    private Edge selectedEdge = null;
    /**
     * the last selected index in the vertices JList (Used for things like
     * setting the title text field, updating the title, changing the color,
     * etc.)
     */
    private int selectedVertexIndex = -1;
    /**
     * The last selected index in the edges JList
     */
    private int selectedEdgeIndex = -1;
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

    // models for vertex and edge selection lists
    private final DefaultListModel verticesListModel = new DefaultListModel();
    private final DefaultListModel edgesListModel = new DefaultListModel();

    //Used for moving objects. Holds the last point the mouse was at.
    private int lastX;
    private int lastY;
    /**
     * This is used for moving a vertex, not to be confused with selectedVertex,
     * which is used for deleting vertices and changing titles.
     */
    private Vertex clickedVertex;
    /**
     * This is used for moving an edge, not to be confused with selectedEdge,
     * which is used for deleting edges.
     */
    private Edge clickedEdge;

    /**
     * Used to make sure clearSelection() or setSelectedIndex() do not
     * redundantly call the change listeners on the JLists.
     */
    private boolean shouldChange = true;
    private boolean showTitles = false;
    private boolean isModified = false;
    private JTextField modifiedTextField;

    //MARK: From controller
    private final GraphFrame frame = new GraphFrame();
    private final Canvas canvas = frame.getCanvas();
    private final AddGraphDialog addGraphDialog = new AddGraphDialog(frame, true);

    private final Graph graph = new Graph();

    private final List<Vertex> vertices = graph.getVertices();
    private final List<Edge> edges = graph.getEdges();

    //File I/O:
    private final JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
    private File saveFile;

    public GraphController() {
        frame.setTitle("Graph Theory Helper");
        frame.setLocationRelativeTo(null);
        frame.setSize(600, 500);

        canvas.setGraph(graph); //pass the graph to the canvas
        canvas.setGraphOutputTextField(frame.getGraphOutputTextField());

        titleTextField = frame.getTitleTextField();
        verticesList = frame.getVerticesList(); //the visual JList that the user sees and interacts with
        edgesList = frame.getEdgesList(); //the visual JList that the user sees and interacts with
        modifiedTextField = frame.getModifiedTextField();
        addVerticesButton = frame.getAddVerticesButton();
        addEdgesButton = frame.getAddEdgesButton();
        selectionButton = frame.getSelectionButton();
        addVerticesMenuItem = frame.getAddVerticesMenuItem();
        addEdgesMenuItem = frame.getAddEdgesMenuItem();
        selectionMenuItem = frame.getSelectionMenuItem();

        selectionButton.setSelected(true);
        selectionMenuItem.setSelected(true);
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
                }
                pressVertex();
                pressEdge();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (clickedVertex != null) {
                    unpressVertex();
                    clickedVertex = null; //we don't want to move a vertex after the user lets go
                    canvas.repaint();
                }
                if (clickedEdge != null) {
                    unpressEdge();
                    clickedEdge = null; //we don't want to move an edge after the user lets go
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

                if (selecting) { //if we're not in the selection state
                    setIsModified(true);

                    if (clickedVertex == null) { //if the user did not click a vertex
                        if (clickedEdge == null) { //if the user did not click an edge
                            //Then the user clicked open space
                            //Move all nodes at once
                            for (Vertex v : vertices) {
                                v.incLocation(incX, incY);
                            }
                        } else { //if the user clicked an edge
                            //move both nodes attached to the edge
                            clickedEdge.getEndpoint1().incLocation(incX, incY);
                            clickedEdge.getEndpoint2().incLocation(incX, incY);
                        }
                    } else { //if the user clicked a vertex
                        //move the chosen node
                        clickedVertex.incLocation(incX, incY);
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
//                    hoverOverVertex();
                    canvas.repaint();
                }
                if (addingVertices) { //if we are in the adding vertex state
                    canvas.repaint();
                }
                //don't want to repaint canvas if nothing happened
            }

        });

        canvas.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    deleteSelectedElement();
                }
            }
        });
        verticesList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    deleteSelectedElement();
                }
            }
        });
        edgesList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    deleteSelectedElement();
                }
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
        
        frame.getDeleteButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedElement();
            }
        });
        frame.getDeleteMenuItem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedElement();
            }
        });

        verticesList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (shouldChange) { //if we did not just call clearSelection() 
                    //(which would redundantly run this again)
                    //Deselect the edge (if it was selected)
                    selectedEdgeIndex = -1;
                    setSelectedEdge();

                    //Select (or deselect) the vertex
                    selectedVertexIndex = verticesList.getSelectedIndex(); //get the index of the selected item
                    setSelectedVertex();
                    canvas.repaint();
                }
                shouldChange = true;
            }
        });

        edgesList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (shouldChange) { //if we did not just call clearSelection() 
                    //(which would redundantly run this again)
                    //Deselect the vertex (if it was selected)
                    selectedVertexIndex = -1;
                    setSelectedVertex();

                    //Select (or deselect) the edge
                    selectedEdgeIndex = edgesList.getSelectedIndex(); //get the index of the selected item
                    setSelectedEdge();
                    canvas.repaint();
                }
                shouldChange = true;
            }
        }
        );

        titleTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //The title of the vertex should be updated and the JList should be repainted
                if (selectedVertex == null) {
                    return;
                }
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
                selectedVertex.setTitle(newTitle);
                verticesList.repaint();
                canvas.repaint();
                setIsModified(true);
            }
        });

        //Set up list models:
        //set them to their respective JLists
        frame.getVerticesList()
                .setModel(verticesListModel);
        frame.getEdgesList()
                .setModel(edgesListModel);
        //set their selection modes
        frame.getVerticesList()
                .setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        frame.getEdgesList()
                .setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

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

                setIsModified(false);
            }
        });

        frame.getAddGraphMenuItem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addGraphDialog.setLocationRelativeTo(null);
                addGraphDialog.setTitle("Add Vertices");

                addGraphDialog.setFocusToTextField();

                addGraphDialog.getRootPane().setDefaultButton(addGraphDialog.getAddButton());

                addGraphDialog.setVisible(true);
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

                    //Formate the new vertices
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

    }

    //MARK: Other methods--------------------
    /**
     * Uses selectedIndex (a member variable) to set selectedVertex, highlight
     * selected vertex, un-highlights previously selected vertex set the
     * titleTextField content, (If selectedIndex = -1, then it deselects all).
     * This also repaints the canvas.
     */
    private void setSelectedVertex() {
        //Visually deselect the old selectedVertex
        if (selectedVertex != null) { //if there was a previously selected vertex
            selectedVertex.setStrokeColor(Values.VERTEX_STROKE_COLOR);
            selectedVertex.setStrokeWidth(Values.VERTEX_STROKE_WIDTH);
        }

        //Programattically select the new selectedVertex (or deselect entirely)
        if (selectedVertexIndex == -1) { //if the user deselected a vertex
            selectedVertex = null;
            titleTextField.setText("");
            titleTextField.setEditable(false);
            shouldChange = false;
            verticesList.clearSelection(); //unselect the vertex in the JList
        } else { //if the user selected a vertex
            selectedVertex = vertices.get(selectedVertexIndex); //store the selected vertex
            selectedVertex.setStrokeColor(Values.EDGE_HIGHLIGHT_COLOR); //highlight the vertex
            selectedVertex.setStrokeWidth(Values.VERTEX_HIGHLIGHT_STROKE_WIDTH);
            titleTextField.setText(selectedVertex.getTitle());
            titleTextField.setEditable(true);
            //Set the focus to be in the titleTextField
//            titleTextField.requestFocus();
//            titleTextField.setSelectionStart(0);
//            titleTextField.setSelectionEnd(titleTextField.getText().length());
        }
    }

    private void setSelectedEdge() {
        //Visually deselect the old selected edge
        if (selectedEdge != null) {
            selectedEdge.setStrokeWidth(Values.EDGE_STROKE_WIDTH);
            selectedEdge.setStrokeColor(Values.EDGE_STROKE_COLOR);
        }

        //Programatically and visually select the new edge (or deselect entirely)
        if (selectedEdgeIndex == -1) { //if the user deselected the edge
            selectedEdge = null;
            shouldChange = false;
            edgesList.clearSelection(); //unselect the edge in the JList
        } else { //if the user selected an edge
            selectedEdge = edges.get(selectedEdgeIndex);
            selectedEdge.setStrokeWidth(Values.EDGE_HIGHLIGHT_STROKE_WIDTH);
            selectedEdge.setStrokeColor(Values.EDGE_HIGHLIGHT_COLOR);
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
     * Used for loading a graph from a file
     *
     * @param g
     */
    public void replace(Graph g) {
        //Get a reverence to the new graph's vertices and edges
        List<Vertex> newVertices = g.getVertices();
        List<Edge> newEdges = g.getEdges();

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

        //Update the list selection
        int newIndex = vertices.size() - 1;
        shouldChange = false;
        verticesList.setSelectedIndex(newIndex);
        selectedVertexIndex = newIndex;
        setSelectedVertex();
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
        selectedVertexIndex = -1;
        setSelectedVertex();
        canvas.repaint();
    }

    private void pressVertex() {
        if (clickedVertex == null) {
            return;
        }
        clickedVertex.setFillColor(Values.VERTEX_PRESSED_COLOR);
        clickedVertex.setStrokeColor(Values.EDGE_PRESSED_COLOR);
    }

    private void pressEdge() {
        if (clickedEdge == null) {
            return;
        }
        clickedEdge.setStrokeColor(Values.EDGE_PRESSED_COLOR);
    }

    private void unpressVertex() {
        if (clickedVertex == null) {
            return;
        }
        clickedVertex.setFillColor(Values.VERTEX_FILL_COLOR);
        clickedVertex.setStrokeColor(Values.EDGE_HIGHLIGHT_COLOR);
    }

    private void unpressEdge() {
        if (clickedEdge == null) {
            return;
        }
        clickedEdge.setStrokeColor(Values.EDGE_HIGHLIGHT_COLOR);
    }

    /**
     * Holds the code that checks what vertex/edge the user clicked (if any) and
     * updates the lastX and lastY variables in preparation for moving the
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
                //store the clicked vertex (for moving)
                clickedVertex = currentVertex;
                //Update the selection:
                //deselect the edge
                selectedEdgeIndex = -1;
                setSelectedEdge();
                //select the vertex
                shouldChange = false;
                verticesList.setSelectedIndex(i);
                selectedVertexIndex = i;
                setSelectedVertex();
                canvas.repaint();
                clickedBlankSpace = false; //user didn't click blank space
                didSelectVertex = true; //the user did click a vertex
                break; //exit the loop (we don't need to check the rest)
            }
        }

        //MARK: Select edge
        //if edges is not null and the user did NOT select a vertex
        if (edges != null && didSelectVertex == false) {
            //true if edge "e" was clicked in loop, false if no edge was clicked
            boolean clickedAnEdge = false;
            for (int i = edges.size() - 1; i >= 0; --i) {
                Edge e = edges.get(i);
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

                //if we clicked edge e
                if (clickedAnEdge) {
                    //store the clicked edge (for moving)
                    clickedEdge = e;
                    //Update the selection:
                    //deselect the vertex
                    selectedVertexIndex = -1;
                    setSelectedVertex();
                    //select the edge
                    shouldChange = false;
                    edgesList.setSelectedIndex(i);
                    selectedEdgeIndex = i;
                    setSelectedEdge();
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
            shouldChange = false; //don't allow clearSelection to run setSelectedVertex again
            verticesList.clearSelection(); //deselect vertex in the list
            selectedVertexIndex = -1;
            setSelectedVertex();

            //Deselect the edge
            shouldChange = false; //don't allow clearSelection to run setSelectedEdge again
            edgesList.clearSelection();; //deselect edge in the list
            selectedEdgeIndex = -1;
            setSelectedEdge();

            canvas.repaint();
        }

        //update the last position
        lastX = mx;
        lastY = my;
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

    //MARK: States
    /**
     * The code that runs in both the selectionButton and the selectionMenuItem
     */
    private void selection() {
        addVerticesButton.setSelected(false);
        addVerticesMenuItem.setSelected(false);
        addEdgesButton.setSelected(false);
        addEdgesMenuItem.setSelected(false);
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
        selecting = true;
    }

    private void exitSelectionState() {
        selecting = false;
    }

    private void deleteSelectedElement() {
        if (selectedVertexIndex != -1) {
            removeVertex();
        }
        if (selectedEdgeIndex != -1) {
            removeEdge();
        }
    }

    /**
     * The code that runs in both the addVerticesButton and the
     * addVerticesMenuItem
     */
    private void addVertices() {
        selectionButton.setSelected(false);
        selectionMenuItem.setSelected(false);
        addEdgesButton.setSelected(false);
        addEdgesMenuItem.setSelected(false);
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
        newVertex.setStrokeColor(Values.VERTEX_STROKE_COLOR);
        newVertex.setFillColor(Values.VERTEX_FILL_COLOR);
        newVertex.setStrokeWidth(Values.VERTEX_STROKE_WIDTH);
        String newTitle = generateVertexTitle();
        newVertex.setTitle(newTitle);
        vertices.add(newVertex);

        updateVerticesListModel();

        //Update selection:
        int bottomIndex = vertices.size() - 1;
        //Set the selection of the visual JList to the bottom
        shouldChange = false;
        verticesList.setSelectedIndex(bottomIndex);
        selectedVertexIndex = bottomIndex;
        setSelectedVertex();
        canvas.repaint();
        setIsModified(true);
    }

    private void removeVertex() {
        if (selectedVertexIndex == -1) {
            return;
        }

        //Get the list of edges to remove
        List<Edge> removeEdges = vertices.get(selectedVertexIndex).getEdges();

        //remove the edges that were attached to this vertex from the list of edges
        edges.removeAll(removeEdges);

        vertices.remove(selectedVertexIndex); //remove the vertex

        //Remove the edges that were attached to this vertex 
        //from all the other vertices associated with them
        for (Edge eg : removeEdges) { //cycle through all the edges to remove
            for (Vertex v : vertices) { //cycle through all the vertices
                v.removeEdge(eg); //remove each edge from each vertex
            }
        }

        updateVerticesListModel();
        updateEdgesListModel();
        //Deselect the vertex:
        selectedVertexIndex = -1;
        setSelectedVertex();

        canvas.repaint();
        setIsModified(true);
    }

    /**
     * Used to enter the state in which a user can add vertices to the canvas by
     * clicking anywhere as many times as they want.
     */
    private void enterAddVerticesState() {
        addingVertices = true; //enter the vertex adding state
        canvas.setAddingVertex(true);
    }

    /**
     * Used to exit the state in which a user can add vertices to the canvas by
     * clicking anywhere. Called when the user enters selection state or add
     * edge state.
     */
    private void exitAddVerticesState() {
        addingVertices = false; //exit the state
        canvas.setAddingVertex(false);
    }
    
    /**
     * The cod that runs in both the addEdgesButton and the addEdgesMenuItem
     */
    private void addEdges() {
        if (vertices == null) {
            JOptionPane.showMessageDialog(frame, "Need at least two vertices to add an edge.");
            addEdgesButton.setSelected(false);
            addEdgesMenuItem.setSelected(false);
            return;
        }
        if (vertices.isEmpty() || vertices.size() == 1) {
            JOptionPane.showMessageDialog(frame, "Need at least two vertices to add an edge.");
            addEdgesButton.setSelected(false);
            addEdgesMenuItem.setSelected(false);
            return;
        }
        addVerticesButton.setSelected(false);
        addVerticesMenuItem.setSelected(false);
        selectionButton.setSelected(false);
        selectionButton.setSelected(false);
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
                    shouldChange = false;
                    edgesList.setSelectedIndex(lastIndex);
                    selectedEdgeIndex = lastIndex;
                    setSelectedEdge();

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

    private void removeEdge() {
        //If the user did not choose an edge
        if (selectedEdgeIndex == -1) {
            return;
        }
        //Get a reference to the edge
        Edge edgeToRemove = edges.get(selectedEdgeIndex);

        //Remove the edge from the vertices that the edges were attached to
        edgeToRemove.getEndpoint1().removeEdge(edgeToRemove);
        edgeToRemove.getEndpoint2().removeEdge(edgeToRemove);

        edges.remove(selectedEdgeIndex);

        updateEdgesListModel();

//        exitAddEdgesState();
//        //reenter the add edge state (allow user to add more edges)
//        enterAddEdgeState();
        canvas.repaint();
    }

    private void exitAddEdgesState() {
        addingEdges = false;
        firstSelectedVertex = null; //prepare for the next edge
        canvas.setFirstSelectedVertex(null);
        //Unhighlight all vertices
        for (Vertex v : vertices) {
            v.setStrokeColor(Values.VERTEX_STROKE_COLOR);
            v.setStrokeWidth(Values.VERTEX_STROKE_WIDTH);
        }
    }

    private void enterAddEdgeState() {
        addingEdges = true; //enter the edge adding state
        //highlight all of the vertexes to provide a visual cue that the user is supposed
        //to click one to add the edge

        //Update vertex selection
        shouldChange = false; //don't allow clearSelection to run setSelectedVertex again
        verticesList.clearSelection(); //clear the visual selection in the JList
        //deselect the vertex
        selectedVertexIndex = -1;
        setSelectedVertex();

        //Update edge selection
        shouldChange = false; //don't allow clearSelection to run setSelectedVertex again
        edgesList.clearSelection(); //clear the visual selection in the JList
        //deselect the edge
        selectedEdgeIndex = -1;
        setSelectedEdge();

        //Assign the canAddEdges values of all the vertices and get the number of vertices
        //that can't have edges added to them
        int numberOfFalses = assignCanAddEdges();

        if (numberOfFalses == vertices.size()) { //if none of the vertices can have edges added to them
            addEdgesButton.setSelected(false);
            addEdgesMenuItem.setSelected(false);
            selectionButton.setSelected(true);
            selectionMenuItem.setSelected(true);
            exitAddEdgesState(); //exit the state because there are no available edges
            enterSelectionState();
            return; //do not continue
        }

        //Highglight appropriate vertices
        highlightAvailableVertices();
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
                v.setStrokeColor(Values.EDGE_HIGHLIGHT_COLOR);
                v.setStrokeWidth(Values.VERTEX_HIGHLIGHT_STROKE_WIDTH);
            } else { //if this vertex is completely full
                v.setStrokeColor(Values.VERTEX_STROKE_COLOR);
                v.setStrokeWidth(Values.VERTEX_STROKE_WIDTH);
            }
        }
    }

//    private void hoverOverVertex() {
//        //loop through all the vertices
//        for (int i = vertices.size() - 1; i >= 0; i--) {
//            Vertex v = vertices.get(i);
//            //(Note: it's faster to check if canAddEdges before contains(x,y))
//            if (v.canAddEdges()) { //if this vertex can have edges added to it
//                //if the mouse is hovering over this vertex
//                if (v.getPositionShape().contains(lastX, lastY)) {
//                    //highlight it
//                    v.setStrokeWidth(Values.EDGE_HIGHLIGHT_STROKE_WIDTH);
//                } else { //if the mouse is not hovering over this vertex
//                    //unhighlight it
//                    v.setStrokeWidth(Values.VERTEX_HIGHLIGHT_STROKE_WIDTH);
//                }
//            }
//        }
//    }
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
        //ask the user if they want to continue
        int selection = JOptionPane.showConfirmDialog(frame, message);

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
        GraphController app = new GraphController();
        app.frame.setVisible(true);
    }

}
