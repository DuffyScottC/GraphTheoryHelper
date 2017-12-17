/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import element.Edge;
import element.Vertex;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Scott
 */
public class Helpers {
    
    /**
     * Pass in the elements you want to use as the "vertices" and "edges" lists
     * and this will add two sample vertices and one sample edge between them.
     * @param vertices
     * @param edges 
     */
    static void setSampleElements(List<Vertex> vertices, List<Edge> edges) {
        Vertex vertex1 = new Vertex(10);
        vertex1.setLocation(220, 140);
        vertex1.setStrokeColor(Color.magenta);
        vertex1.setFillColor(Color.yellow);
        vertex1.setStrokeWidth(3f);
        vertex1.setTitle("A");
        vertices.add(vertex1);

        Vertex vertex2 = new Vertex(10);
        vertex2.setLocation(100, 100);
        vertex1.setStrokeColor(Color.magenta);
        vertex1.setFillColor(Color.yellow);
        vertex2.setStrokeWidth(3f);
        vertex2.setTitle("B");
        vertices.add(vertex2);

        Edge edgeElement = new Edge(vertex1, vertex2);
        edgeElement.setLocation(40, 40);
        edgeElement.setStrokeColor(Color.blue);
        edgeElement.setFillColor(Color.red);
        edgeElement.setStrokeWidth(4.2f);
        edgeElement.setTitle("A,B");
        edges.add(edgeElement);
    }
}
