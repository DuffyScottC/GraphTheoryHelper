/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package element;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Scott
 */
public class Graph {

    private final List<Vertex> vertices = new ArrayList<>();
    private final List<Edge> edges = new ArrayList<>();
    private String title = "Simple Graph";
    
    public Graph (String title) {
        this.title = title;
    }
    
    
    public void drawVertices() {
        
    }

    public void drawEdges() {
        
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
}
