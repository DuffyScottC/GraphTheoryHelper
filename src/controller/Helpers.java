/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import element.Edge;
import element.Vertex;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.DefaultListModel;
import views.AddGraphDialog;
import views.Canvas;
import views.GraphFrame;

/**
 *
 * @author Scott
 */
public class Helpers {
    
    public static final float EDGE_STROKE_WIDTH = 2.0f;
    public static final float VERTEX_STROKE_WIDTH = 1.0f;
    public static final float HIGHLIGHT_STROKE_WIDTH = 2.0f;
    public static final Color VERTEX_FILL_COLOR = Color.RED;
    public static final Color VERTEX_STROKE_COLOR = Color.BLACK;
    public static final Color HIGHLIGHT_COLOR = Color.GREEN;
    public static final int DIAMETER = 15;
    public static final double FORMAT_RADIUS = 150;

    /**
     * Pass in the graph and this will add two sample vertices and one sample
     * edge between them.
     *
     * @param vertices
     * @param edges
     */
    public static void setSampleElements(GraphController graph) {
        List<Vertex> vertices = graph.getVertices();
        List<Edge> edges = graph.getEdges();
        Vertex vertex1 = new Vertex(DIAMETER);
        vertex1.setLocation(220, 140);
        vertex1.setStrokeColor(VERTEX_STROKE_COLOR);
        vertex1.setFillColor(VERTEX_FILL_COLOR);
        vertex1.setStrokeWidth(VERTEX_STROKE_WIDTH);
        vertex1.setTitle("A");
        vertices.add(vertex1);

        Vertex vertex2 = new Vertex(DIAMETER);
        vertex2.setLocation(100, 100);
        vertex2.setStrokeColor(VERTEX_STROKE_COLOR);
        vertex2.setFillColor(VERTEX_FILL_COLOR);
        vertex2.setStrokeWidth(VERTEX_STROKE_WIDTH);
        vertex2.setTitle("B");
        vertices.add(vertex2);

        Edge edgeElement = new Edge(vertex1, vertex2);
        edgeElement.setStrokeColor(Color.BLACK);
        edgeElement.setFillColor(Color.BLACK);
        edgeElement.setStrokeWidth(EDGE_STROKE_WIDTH);
        edgeElement.setTitle("A,B");
        edges.add(edgeElement);

        DefaultListModel verticesList = graph.getVerticesListModel();
        verticesList.removeAllElements();
        for (Vertex v : vertices) {
            verticesList.addElement(v);
        }

        DefaultListModel edgesList = graph.getEdgesListModel();
        edgesList.removeAllElements();
        for (Edge e : edges) {
            edgesList.addElement(e);
        }

    }

    public static void addGraphDialogEventHandlers(
            AddGraphDialog addGraphDialog,
            GraphController graph,
            GraphFrame frame,
            Canvas canvas
    ) {
        
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
                    
                    if (wasModified) {
                        graph.setIsModified(true);
                    }
                    
                    graph.formatVertices(toBeFormatted);
                    graph.updateListModels();
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

}
