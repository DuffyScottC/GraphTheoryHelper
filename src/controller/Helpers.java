/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import element.Edge;
import element.Graph;
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

    public static final float VERTEX_STROKE_WIDTH = 1.0f;
    public static final float HIGHLIGHT_STROKE_WIDTH = 2.0f;
    public static final Color VERTEX_FILL_COLOR = Color.RED;
    public static final Color VERTEX_STROKE_COLOR = Color.BLACK;
    public static final Color HIGHLIGHT_COLOR = Color.GREEN;
    public static final int DIAMETER = 15;

    /**
     * Pass in the graph and this will add two sample vertices and one sample
     * edge between them.
     *
     * @param vertices
     * @param edges
     */
    public static void setSampleElements(Graph graph) {
        List<Vertex> vertices = graph.getVertices();
        List<Edge> edges = graph.getEdges();
        Vertex vertex1 = new Vertex(DIAMETER);
        vertex1.setLocation(220, 140);
        vertex1.setStrokeColor(VERTEX_FILL_COLOR);
        vertex1.setFillColor(VERTEX_STROKE_COLOR);
        vertex1.setStrokeWidth(3f);
        vertex1.setTitle("A");
        vertices.add(vertex1);

        Vertex vertex2 = new Vertex(DIAMETER);
        vertex2.setLocation(100, 100);
        vertex2.setStrokeColor(VERTEX_FILL_COLOR);
        vertex2.setFillColor(VERTEX_STROKE_COLOR);
        vertex2.setStrokeWidth(3f);
        vertex2.setTitle("B");
        vertices.add(vertex2);

        Edge edgeElement = new Edge(vertex1, vertex2);
        edgeElement.setStrokeColor(Color.BLACK);
        edgeElement.setFillColor(Color.BLACK);
        edgeElement.setStrokeWidth(VERTEX_STROKE_WIDTH);
        edgeElement.setTitle("A,B");
        edges.add(edgeElement);

        vertex1.addEdge(edgeElement);
        vertex2.addEdge(edgeElement);

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
            Graph graph,
            GraphFrame frame,
            Canvas canvas
    ) {

        addGraphDialog.getAddButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<Vertex> vertices = new ArrayList();
                List<Edge> edges = new ArrayList();
                
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
                
                //this will grab the titles of the vertices
                String titleRegex = "(\\w+),(\\w+)";
                
                Pattern p = Pattern.compile(titleRegex);
                Matcher m = p.matcher(input);
                
                //cycle through all of the 
                while (m.find()) {
                    //Get the two titles
                    String title1 = m.group(1); //the first title
                    String title2 = m.group(2); //the second title
                    
                    //Form two new vertices from the titles
                    Vertex v1 = new Vertex(title1, DIAMETER);
                    Vertex v2 = new Vertex(title2, DIAMETER);
                    
                    //Add the two vertices to the list
                    //(unless their repeats)
                    if (!vertices.contains(v1)) {
                        vertices.add(v1);
                    }
                    if (!vertices.contains(v2)) {
                        vertices.add(v2);
                    }
                    
                    //Create an edge from the two vertices
                    Edge eg = new Edge(v1, v2);
                    
                    //Add the edge to the list of edges
                    if (!edges.contains(eg)) {
                        edges.add(eg);
                    }
                }
                
                graph.appendElements(vertices, edges);
                
                //Add the new edges and vertices to the graph
                
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
