/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package element;

import controller.Helpers;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import views.Canvas;
import views.GraphFrame;

/**
 *
 * @author Scott
 */
public class Graph implements Serializable {

    private Canvas canvas;

    //the vertices which appear in canvas and the vertices JList
    private final List<Vertex> vertices = new ArrayList<>();
    //the edges which appear in canvas and the edges JList
    private final List<Edge> edges = new ArrayList<>();

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
    private final DefaultListModel verticesListModel;
    private final DefaultListModel edgesListModel;

    private String title = "Simple Graph";

    //Used for moving objects. Holds the last point the mouse was at.
    private int lastX;
    private int lastY;
    /**
     * This is used for moving a vertex, not to be confused with selectedVertex,
     * which is used for deleting and changing titles.
     */
    private Vertex clickedVertex;

    private boolean showTitles; //is not serializable
    private boolean isSaved; //is not serializable

    public Graph(String title) {
        showTitles = false;
        isSaved = true;
        verticesListModel = new DefaultListModel();
        edgesListModel = new DefaultListModel();
        this.title = title;
    }

    public void addEventHandlers(GraphFrame frame) {

        titleTextField = frame.getTitleTextField();
        verticesList = frame.getVerticesList(); //the visual JList that the user sees and interacts with

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
                                    //Make it so that user can't add edge from a vertex to itself:
                                    firstSelectedVertex.setCanAddEdges(false);
                                    //Make it so that user can't add an edge to vertices that are already
                                    //connected to the firstSelectedVertex:
                                    firstSelectedVertex.assignCanAddEdgesToConnectedVertices();
                                    //Reset the highlights
                                    highlightAvailableVertices();
                                    lastX = mx;
                                    lastY = my;
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
                                    
                                    isSaved = false; //Note that this is not saved

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
                isSaved = false;

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

                canvas.repaint();
            }

        });

        frame.getShowVertexNamesMenuItem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Toggle the showTitles boolean
                if (showTitles) {
                    showTitles = false;
                } else {
                    showTitles = true;
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
                isSaved = false;

                //Set the focus to be in the titleTextField
                titleTextField.requestFocus();
                titleTextField.setSelectionStart(0);
                titleTextField.setSelectionEnd(newTitle.length());
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
                isSaved = false;
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

                if (selectedIndex == -1) { //if the user is deselecting something, do nothing
                    return;
                }

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
                isSaved = false;
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
     * This will add the given list of vertices and list of edges to the graph's
     * own list, and will assign positions to the entirely new vertices (in a
     * circle that tries not to interfere with the existing elements).
     *
     * @param vs A list of vertices to be added to the graph
     * @param es A list of edges to be added to the graph
     */
    public void appendElements(List<Vertex> vs, List<Edge> es) {
        List<Vertex> toBeFormatted = new ArrayList(); //holds the new vertices that must be formatted

        //Add the vertices
        for (Vertex v : vs) { //loop through the new vertices
            if (!vertices.contains(v)) { //if vertices does NOT already contain this vertex
                vertices.add(v); //add the new vertex to the graph
                toBeFormatted.add(v); //assign the new vertex to be formatted
            }
        }

        //Add the edges
        for (Edge e : es) {
            if (!edges.contains(e)) { //if edges does NOT already contain this edge
                edges.add(e); //add the new edge to the graph
            }
        }

        //Format the new vertices that weren't already in the graph
        this.formatVertices(toBeFormatted);

        canvas.repaint();
    }

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
        //For now, it just puts the vertices into a line at the top
        int xPosition = Helpers.DIAMETER; //initialize the x position
        for (Vertex v : vs) { //loop through the vertices to be moved
            v.setLocation(xPosition, Helpers.DIAMETER * 2); //place the vertex in the line
            xPosition += Helpers.DIAMETER * 2; //increment the x position
        }
    }

    public void drawVertices(Graphics2D g2) {
        if (vertices == null) {
            return;
        }

        AffineTransform t = g2.getTransform(); // save the transform settings

        //loop from back to front so that the "top" vertext gets chosen
        //first when the user clicks on it.
        for (Vertex vertex : vertices) {
            double x = vertex.getLocation().x;
            double y = vertex.getLocation().y;
            g2.translate(x, y);
            vertex.draw(g2); //actually draw the vertex
            g2.setTransform(t); //restore each after drawing

            if (showTitles) { //If the user wants to show the titles
                vertex.drawTitle(g2);
            }
        }

    }

    public void drawEdges(Graphics2D g2) {
        if (edges == null) {
            return;
        }

//        AffineTransform t = g2.getTransform(); // save the transform settings
        //loop from back to front so that the "top" edge gets chosen
        //first when the user clicks on it.
        for (Edge edge : edges) {
//            double x = edge.getLocation().x;
//            double y = edge.getLocation().y;
//            g2.translate(x, y);
            edge.draw(g2); //actually draw the edge
//            g2.setTransform(t); //restore each after drawing
        }
    }

    /**
     * We want to draw an edge between the first vertex and the current mouse
     * position
     *
     * @param g2
     */
    public void drawLiveEdge(Graphics2D g2) {
        //If this is null, then we are not in the adding a vertex state
        //or the user has not yet selected their first vertex
        if (firstSelectedVertex == null) {
            return;
        }
        int x1 = (int) firstSelectedVertex.getCenter().getX();
        int y1 = (int) firstSelectedVertex.getCenter().getY();
        int x2 = lastX;
        int y2 = lastY;
        g2.setStroke(new BasicStroke(Helpers.EDGE_STROKE_WIDTH));
        g2.setColor(Color.BLACK);
        g2.drawLine(x1, y1, x2, y2); //draw the line
    }

    /**
     * Used for loading a graph from a file
     *
     * @param g
     */
    public void replace(Graph g) {
        this.title = g.title;

        List<Vertex> newVertices = g.getVertices();
        List<Edge> newEdges = g.getEdges();

        vertices.clear(); //remove all elements from vertices
        for (Vertex v : newVertices) { //loop through new list
            vertices.add(v); //add each vertex to the vertices list
        }

        updateVerticesListModel();

        edges.clear(); //remove all elements from edges
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
     * @return The number of vertexes that were assigned a canAddEdges value
     * of false
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

    public void setTitle(String title) {
        this.title = title;
    }

    public void setShowTitles(boolean showTitles) {
        this.showTitles = showTitles;
    }

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }

    /**
     * The graph is empty if vertices is empty (doesn't matter whether edges is
     * full or not)
     *
     * @return
     */
    public boolean isEmpty() {
        return vertices.isEmpty();
    }

    public boolean isSaved() {
        return isSaved;
    }

    public void setIsSaved(boolean isSaved) {
        this.isSaved = isSaved;
    }

    @Override
    public String toString() {
        StringBuilder strB = new StringBuilder();
        strB.append("{");
        for (int i = 0; i < edges.size(); i++) {
            strB.append("{");
            strB.append(edges.get(i).toString());
            strB.append("}");
            if (i < edges.size() - 1) { //if this is not the last one
                strB.append(",");
            }
        }
        strB.append("}");
        return strB.toString();
    }

}
