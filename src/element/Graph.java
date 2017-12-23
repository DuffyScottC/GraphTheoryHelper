/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package element;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Scott
 */
public class Graph {
    //the vertices which appear in canvas and the vertices JList
    private final List<Vertex> vertices = new ArrayList<>();
    //the edges which appear in canvas and the edges JList
    private final List<Edge> edges = new ArrayList<>();
    
    public List<Vertex> getVertices() {
        return vertices;
    }
    
    public List<Edge> getEdges() {
        return edges;
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
}
