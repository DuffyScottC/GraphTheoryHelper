/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import static controller.Helpers.DIAMETER;
import element.Edge;
import element.Graph;
import element.Vertex;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
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
    private Vertex selectedVertex;
    /**
     * the last selected index in the vertices JList (Used for things like
     * setting the title text field, updating the title, changing the color,
     * etc.)
     */
    private int selectedIndex;
    private JTextField titleTextField;
    private JList verticesList;
    private JList edgesList;

    //MARK: Adding edge state:
    /**
     * Only true if the user is in the edge adding state.
     */
    private boolean addingEdge = false;
    /**
     * If this is not null, we want to start drawing an edge between this vertex
     * and the mouse
     */
    private Vertex firstSelectedVertex;

    // models for vertex and edge selection lists
    private final DefaultListModel verticesListModel = new DefaultListModel();
    private final DefaultListModel edgesListModel = new DefaultListModel();

    //Used for moving objects. Holds the last point the mouse was at.
    private int lastX;
    private int lastY;
    /**
     * This is used for moving a vertex, not to be confused with selectedVertex,
     * which is used for deleting and changing titles.
     */
    private Vertex clickedVertex;
    
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
//        graph.setCanvas(canvas); //pass the canvas to the graph

        titleTextField = frame.getTitleTextField();
        verticesList = frame.getVerticesList(); //the visual JList that the user sees and interacts with
        edgesList = frame.getEdgesList(); //the visual JList that the user sees and interacts with
        modifiedTextField = frame.getModifiedTextField();
        
        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int mx = e.getX(); //x-coord of mouse click
                int my = e.getY(); //y-coord of mouse click
                if (addingEdge) { //if we are in the edge adding state, we don't want to be able to move any vertices

                    //Find out which vertex was clicked (if any):
                    if (firstSelectedVertex == null) { //if this is null, the user hasn't chosen their first vertex
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
                        //Instead, they clicked empty space. We should cancel the process
                        exitAddEdgeState();
                    } else { //The user has already chosen their first vertex
                        //(If we reach this point, vertices.size() is at least 2)
                        for (Vertex currentVertex : vertices) { //loop through the vertices
                            //If this vertex can have edges added to it (no use checking if
                            //its shape contains the mouse click if not):
                            if (currentVertex.canAddEdges()) {
                                //If this figure contains the mouse click:
                                if (currentVertex.getPositionShape().contains(mx, my)) {
                                    //Create a new edge with the two vertices
                                    Edge newEdge = new Edge(firstSelectedVertex, currentVertex);
                                    newEdge.setStrokeWidth(Helpers.EDGE_STROKE_WIDTH);

                                    edges.add(newEdge); //Add the edge to the graph

                                    updateEdgesListModel(); //update the visual JList

                                    exitAddEdgeState(); //exit the add edge state

                                    isModified = true; //Note that this is not saved
                                    modifiedTextField.setText("*");
                                    

                                    return; //we don't need to check anymore
                                }
                            }

                        }
                        //If we reach this point, we want to cancel the edge
                        exitAddEdgeState();

                    }
                } else { //if we are not in the edge adding state, then we can move the vertices
                    //Find the topmost vertex that 
                    //contains the mouse click (if any):

                    //if vertices is null, then there are definitely no
                    //edges and we can stop here. (A graph can't have
                    //edges without vertices.)
                    if (vertices == null) {
                        return;
                    }

                    boolean clickedBlankSpace = true;

                    for (int i = vertices.size() - 1; i >= 0; --i) {
                        Vertex currentVertex = vertices.get(i);
                        //if this figure contains the mouse click:
                        if (currentVertex.getPositionShape().contains(mx, my)) {
                            //store the clicked vertex (for moving)
                            clickedVertex = currentVertex;
                            //Update the selection
                            verticesList.setSelectedIndex(i);
                            selectedIndex = i;
                            setSelectedVertex();
                            clickedBlankSpace = false; //user didn't click blank space
                            break; //exit the loop (we don't need to check the rest)
                        }
                    }

                    if (clickedBlankSpace) {
                        verticesList.clearSelection(); //deselect vertex in the list
                        selectedIndex = -1;
                        setSelectedVertex();
                    }

                    //update the last position
                    lastX = mx;
                    lastY = my;
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                clickedVertex = null; //we don't want to move a figure after the user lets go
            }

        });

        canvas.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                isModified = true;
                modifiedTextField.setText("*");

                int mx = e.getX(); //x-coord of mouse click
                int my = e.getY(); //y-coord of mouse click

                // find the difference between the last position and the current position (used for moving the figure)
                int incX = mx - lastX;
                int incY = my - lastY;

                //update the last position
                lastX = mx;
                lastY = my;

                if (clickedVertex == null) { //if the user clicked open space
                    //move all nodes
                    for (Vertex v : vertices) {
                        v.incLocation(incX, incY);
                    }
                    canvas.repaint();
                } else { //if the user clicked a vertex
                    //move the chosen node
                    clickedVertex.incLocation(incX, incY);
                    canvas.repaint();
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                //If null, user hasn't selected first vertex
                //(or we're not in the adding edge state
                if (firstSelectedVertex == null) {
                    return;
                }
                //If firstSelectedVertex is not null, we are in the edge adding state
                //and the user has clicked the first edge.
                //The actual drawing happends in this.drawLiveEdge(Graphics2D g2), which
                //gets called in canvas's paint method

                //I use these variables to store the 
                //location of the mouse for drawing the lines
                lastX = e.getX();
                lastY = e.getY();
                canvas.setLastPosition(lastX, lastY);

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

        frame.getAddVertexButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //For now, each new vertex is simply added to the center
                int xPos = canvas.getWidth() / 2;
                int yPos = canvas.getHeight() / 2;

                Vertex newVertex = new Vertex(Helpers.DIAMETER);
                newVertex.setLocation(xPos, yPos);
                newVertex.setStrokeColor(Helpers.VERTEX_STROKE_COLOR);
                newVertex.setFillColor(Helpers.VERTEX_FILL_COLOR);
                newVertex.setStrokeWidth(Helpers.VERTEX_STROKE_WIDTH);
                String newTitle = generateVertexTitle();
                newVertex.setTitle(newTitle);
                vertices.add(newVertex);

                updateVerticesListModel();

                //Update selection:
                int bottomIndex = vertices.size() - 1;
                //Set the selection of the visual JList to the bottom
                verticesList.setSelectedIndex(bottomIndex);
                selectedIndex = bottomIndex;
                setSelectedVertex();
                isModified = true;
                modifiedTextField.setText("*");
            }
        });

        frame.getRemoveVertexButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedIndex == -1) {
                    return;
                }

                //Get the list of edges to remove
                List<Edge> removeEdges = vertices.get(selectedIndex).getEdges();

                //remove the edges that were attached to this vertex from the list of edges
                edges.removeAll(removeEdges);

                vertices.remove(selectedIndex); //remove the vertex

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
                selectedIndex = -1;
                setSelectedVertex();

                canvas.repaint();
                isModified = true;
                modifiedTextField.setText("*");
            }
        });

        frame.getAddEdgeButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (vertices == null) {
                    JOptionPane.showMessageDialog(frame, "Need at least two vertices to add an edge.");
                    return;
                }
                if (vertices.isEmpty() || vertices.size() == 1) {
                    JOptionPane.showMessageDialog(frame, "Need at least two vertices to add an edge.");
                    return;
                }
                enterAddEdgeState();
            }
        });

        verticesList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                selectedIndex = verticesList.getSelectedIndex(); //get the index of the selected item

                setSelectedVertex();
            }
        });
        
        edgesList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                selectedIndex = -1;
                setSelectedVertex();
            }
        });

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
                isModified = true;
                modifiedTextField.setText("*");
            }
        });

        //Set up list models:
        //set them to their respective JLists
        frame.getVerticesList().setModel(verticesListModel);
        frame.getEdgesList().setModel(edgesListModel);
        //set their selection modes
        frame.getVerticesList().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        frame.getEdgesList().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        //Action listeners:
        frame.getLoadSamplesMenuItem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Helpers.setSampleElements(graph, verticesListModel, edgesListModel);
                canvas.repaint();
            }
        });

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

                isModified= false;
                modifiedTextField.setText("");

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

                isModified = true; //we have not yet saved the new file
                modifiedTextField.setText("*");
            }
        });

        frame.getAddVerticesMenuItem().addActionListener(new ActionListener() {
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
                    
                    //if newVertex1 not already connected to newVertex2, then
                    //it must already be in edges and we don't need to do anything else
                    
                    //if there was at least one new vertex or edge
                    if (wasModified) {
                        isModified = true;
                        modifiedTextField.setText("*");
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
            selectedVertex.setStrokeColor(Helpers.VERTEX_STROKE_COLOR);
            selectedVertex.setStrokeWidth(Helpers.VERTEX_STROKE_WIDTH);
        }

        //Programattically select the new selectedVertex (or deselect entirely)
        if (selectedIndex == -1) { //if the user deselected a vertex
            selectedVertex = null;
            titleTextField.setText("");
            titleTextField.setEditable(false);
            verticesList.clearSelection(); //unselect the element in the JList
        } else { //if the user selected a vertex
            selectedVertex = vertices.get(selectedIndex); //store the selected vertex
            selectedVertex.setStrokeColor(Helpers.HIGHLIGHT_COLOR); //highlight the vertex
            selectedVertex.setStrokeWidth(Helpers.HIGHLIGHT_STROKE_WIDTH);
            titleTextField.setText(selectedVertex.getTitle());
            titleTextField.setEditable(true);
            //Set the focus to be in the titleTextField
            titleTextField.requestFocus();
            titleTextField.setSelectionStart(0);
            titleTextField.setSelectionEnd(titleTextField.getText().length());
        }
        canvas.repaint();
    }

    private String generateVertexTitle() {
        if (vertices == null) {
            return "V1";
        }
        /*
        Loop through 1 to n = vertices.size(). Worst comes to worst, 
        the all the current vertices are named V1, V2,...,Vn
        And we have to name it V(n+1). If they are named V1, V2,...,V(n-1), V(n+1) 
        (skipping Vn) then we will name it Vn automattically.
         */
        int i = 1;
        String newTitle = "V"; //Holds the auto-generated name for the new vertex (will never be V)
        boolean matchFound = true; //true when newTitle is already in the vertices list
        while (matchFound == true) { //We want to loop again if we found that newTitle is already in the vertices list
            newTitle = "V" + i; //Combination of the current index and V
            matchFound = false; //We start by assuming there is no match
            for (Vertex v : vertices) { //loop through all of the vertices
                if (v.getTitle().equals(newTitle)) { //If even a single vertex is named the same thing
                    matchFound = true; //we want to continue the outermost loop and find a different number
                    break; //and we don't need to keep looking for one that matches this particular name
                }
            }
            i++;
        }
        return newTitle; //by this point, we've found a unique vertex name (be it V or V+(some number)). 
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
        final double radius = Helpers.FORMAT_RADIUS;
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
        verticesList.setSelectedIndex(newIndex);
        selectedIndex = newIndex;
        setSelectedVertex();
        //^ Already has canvas.repaint();

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
        selectedIndex = -1;
        setSelectedVertex();
        //^ has canvas.repaint() in it already
    }

    private void enterAddEdgeState() {
        addingEdge = true; //enter the edge adding state
        //highlight all of the vertexes to provide a visual cue that the user is supposed
        //to click one to add the edge

        //Update vertex selection
        verticesList.clearSelection(); //clear the visual selection
        //deselect the vertex
        selectedIndex = -1;
        setSelectedVertex();

        //Assign the canAddEdges values of all the vertices and get the number of vertices
        //that can't have edges added to them
        int numberOfFalses = assignCanAddEdges();

        if (numberOfFalses == vertices.size()) { //if none of the vertices can have edges added to them
            exitAddEdgeState(); //exit the state
            return; //do not continue
        }

        //Highglight appropriate vertices
        highlightAvailableVertices();

        canvas.repaint();
    }

    private void exitAddEdgeState() {
        addingEdge = false;
        firstSelectedVertex = null; //prepare for the next edge
        canvas.setFirstSelectedVertex(null);
        //Unhighlight all vertices
        for (Vertex v : vertices) {
            v.setStrokeColor(Helpers.VERTEX_STROKE_COLOR);
            v.setStrokeWidth(Helpers.VERTEX_STROKE_WIDTH);
        }
        canvas.repaint();
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
                v.setStrokeColor(Helpers.HIGHLIGHT_COLOR);
                v.setStrokeWidth(Helpers.HIGHLIGHT_STROKE_WIDTH);
            } else { //if this vertex is completely full
                v.setStrokeColor(Helpers.VERTEX_STROKE_COLOR);
                v.setStrokeWidth(Helpers.VERTEX_STROKE_WIDTH);
            }
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
        
        isModified = false;
        modifiedTextField.setText("");
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
