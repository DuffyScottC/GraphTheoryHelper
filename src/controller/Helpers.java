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
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;

/**
 *
 * @author Scott
 */
public class Helpers {
    
    public static float STROKE_WIDTH = 2.0f;
    /**
     * The default vertex color
     */
    public static Color VERTEX_COLOR = Color.RED;
    public static Color HIGHLIGHT_COLOR = Color.GREEN;
    public static int DIAMETER = 15;
    
    /**
     * Pass in the graph and this will add two sample 
     * vertices and one sample edge between them.
     * @param vertices
     * @param edges 
     */
    public static void setSampleElements(Graph graph) {
        List<Vertex> vertices = graph.getVertices();
        List<Edge> edges = graph.getEdges();
        Vertex vertex1 = new Vertex(DIAMETER);
        vertex1.setLocation(220, 140);
        vertex1.setStrokeColor(VERTEX_COLOR);
        vertex1.setFillColor(VERTEX_COLOR);
        vertex1.setStrokeWidth(3f);
        vertex1.setTitle("A");
        vertices.add(vertex1);
        
        Vertex vertex2 = new Vertex(DIAMETER);
        vertex2.setLocation(100, 100);
        vertex2.setStrokeColor(VERTEX_COLOR);
        vertex2.setFillColor(VERTEX_COLOR);
        vertex2.setStrokeWidth(3f);
        vertex2.setTitle("B");
        vertices.add(vertex2);
        
        Edge edgeElement = new Edge(vertex1, vertex2);
        edgeElement.setStrokeColor(Color.BLACK);
        edgeElement.setFillColor(Color.BLACK);
        edgeElement.setStrokeWidth(STROKE_WIDTH);
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
}
