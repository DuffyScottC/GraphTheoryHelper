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
import javax.swing.DefaultListModel;

/**
 *
 * @author Scott
 */
public class Graph {
    
    //the vertices which appear in canvas and the vertices JList
    private final List<Vertex> vertices = new ArrayList<>();
    //the edges which appear in canvas and the edges JList
    private final List<Edge> edges = new ArrayList<>();
    
    // models for vertex and edge selection lists
    private final DefaultListModel verticesListModel = new DefaultListModel();
    private final DefaultListModel edgesListModel = new DefaultListModel();
    
    private String title = "Simple Graph";
    
    public Graph (String title) {
        this.title = title;
    }
    
    
    public void drawVertices(Graphics2D g2) {
        if (vertices == null) {
            return;
        }
        
        AffineTransform t = g2.getTransform(); // save the transform settings
        
    }

    public void drawEdges(Graphics2D g2) {
        
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
    
}
