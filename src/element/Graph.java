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

    private String title = "Simple Graph";

    //Used for moving objects. Holds the last point the mouse was at.
    private int lastX;
    private int lastY;
    private Vertex clickedVertex;

    private boolean showTitles = false;

    public Graph(String title) {
        this.title = title;
    }

    public void addEventHandlers(Canvas canvas, GraphFrame frame) {

        JTextField titleTextField = frame.getTitleTextField();
        JList verticesList = frame.getVerticesList(); //the visual JList that the user sees and interacts with

        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (addingEdge) { //if we are in the edge adding state, we don't want to be able to move any vertices
                    int mx = e.getX(); //x-coord of mouse click
                    int my = e.getY(); //y-coord of mouse click

                    //Find out which vertex was clicked (if any):
                    if (firstSelectedVertex == null) { //if this is null, the user hasn't chosen their first vertex
                        //(If we reach this point, vertices.size() is at least 2)
                        for (Vertex currentVertex : vertices) { //loop through the vertices
                            //if this figure contains the mouse click:
                            if (currentVertex.getPositionShape().contains(mx, my)) {
                                firstSelectedVertex = currentVertex; //assign the first vertex
                                return; //we've assigned the first selected vertex and we're done
                            }
                        }
                        //if we reach this point, the user hasn't selected and vertex.
                        //Instead, they clicked empty space. We should cancel the process
                        exitAddEdgeState();
                    } else { //The user has already chosen their first vertex
                        //(If we reach this point, vertices.size() is at least 2)
                        for (Vertex currentVertex : vertices) { //loop through the vertices
                            //if this figure contains the mouse click:
                            if (currentVertex.getPositionShape().contains(mx, my)) {
                                //Create a new edge with the two vertices
                                Edge newEdge = new Edge(firstSelectedVertex, currentVertex);
                                
                                edges.add(newEdge);
                                
                                edgesListModel.removeAllElements();
                                for (Edge eg : edges) {
                                    edgesListModel.addElement(eg);
                                }
                                
                                exitAddEdgeState();
                                
                                canvas.repaint();
                                
                                return; //we don't need to check anymore
                            }
                        }
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

                    int mx = e.getX(); //x-coord of mouse click
                    int my = e.getY(); //y-coord of mouse click

                    for (Vertex currentVertex : vertices) {
                        //if this figure contains the mouse click:
                        if (currentVertex.getPositionShape().contains(mx, my)) {
                            clickedVertex = currentVertex;
                            break; //exit the loop (we don't need to check the rest)
                        }
                    }

                    //update the last position
                    lastX = mx;
                    lastY = my;
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                clickedVertex = null; //we don'e want to move a figure after the user lets go
            }

        });

        canvas.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (clickedVertex == null) {
                    return; //we don't have a vertex to move, so just stop here
                }

                int mx = e.getX(); //x-coord of mouse click
                int my = e.getY(); //y-coord of mouse click

                // find the difference between the last position and the current position (used for moving the figure)
                int incX = mx - lastX;
                int incY = my - lastY;

                //update the last position
                lastX = mx;
                lastY = my;

                clickedVertex.incLocation(incX, incY);
                canvas.repaint();
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

                Vertex newVertex = new Vertex(10);
                newVertex.setLocation(xPos, yPos);
                newVertex.setStrokeColor(Helpers.VERTEX_COLOR);
                newVertex.setFillColor(Helpers.VERTEX_COLOR);
                newVertex.setStrokeWidth(3f);
                newVertex.setTitle(generateVertexName());
                vertices.add(newVertex);

                //Update the list model
                verticesListModel.removeAllElements();
                for (Vertex v : vertices) {
                    verticesListModel.addElement(v);
                }

                canvas.repaint();

            }
        });

        frame.getRemoveVertexButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedIndex == -1) {
                    return;
                }

                vertices.remove(selectedIndex);

                //Update the list model
                verticesListModel.removeAllElements();
                for (Vertex v : vertices) {
                    verticesListModel.addElement(v);
                }

                //Deselect the vertex:
                selectedIndex = -1;
                selectedVertex = null;
                titleTextField.setText("");

                canvas.repaint();
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
                addingEdge = true; //enter the edge adding state
                //highlight all of the vertexes to provide a visual cue that the user is supposed
                //to click one to add the edge
                for (Vertex v : vertices) {
                    v.setStrokeColor(Helpers.VERTEX_COLOR);
                }
            }
        });

        verticesList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                selectedIndex = verticesList.getSelectedIndex(); //get the index of the selected item

                if (selectedIndex == -1) { //if the user is deselecting something, do nothing
                    return;
                }

                selectedVertex = vertices.get(selectedIndex); //store the selected vertex
                titleTextField.setText(selectedVertex.getTitle());

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
            }
        });

    }

    //MARK: Other methods--------------------
    private String generateVertexName() {
        if (vertices == null) {
            return "V";
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

    public void drawVertices(Graphics2D g2) {
        if (vertices == null) {
            return;
        }

        AffineTransform t = g2.getTransform(); // save the transform settings

        //loop from back to front so that the "top" vertext gets chosen
        //first when the user clicks on it.
        for (int i = vertices.size() - 1; i >= 0; --i) {
            Vertex vertex = vertices.get(i);
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

        AffineTransform t = g2.getTransform(); // save the transform settings

        //loop from back to front so that the "top" edge gets chosen
        //first when the user clicks on it.
        for (int i = edges.size() - 1; i >= 0; --i) {
            Edge edge = edges.get(i);
//            double x = edge.getLocation().x;
//            double y = edge.getLocation().y;
//            g2.translate(x, y);
            edge.draw(g2); //actually draw the edge
            g2.setTransform(t); //restore each after drawing
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
        g2.setStroke(new BasicStroke(Helpers.STROKE_WIDTH));
        g2.setColor(Color.BLACK);
        g2.drawLine(x1, y1, x2, y2); //draw the line
    }

    private void exitAddEdgeState() {
        addingEdge = false;
        firstSelectedVertex = null;
        for (Vertex v : vertices) {
            v.setStrokeColor(Helpers.VERTEX_COLOR);
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
