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
    public static void setSampleElements(Graph graph, DefaultListModel verticesListModel, DefaultListModel edgesListModel) {
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

        verticesListModel.removeAllElements();
        for (Vertex v : vertices) {
            verticesListModel.addElement(v);
        }

        edgesListModel.removeAllElements();
        for (Edge e : edges) {
            edgesListModel.addElement(e);
        }

    }

}
