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
public class GPath {
    
    /**
     * This holds the edges in the path
     */
    private transient List<Edge> edges = new ArrayList();
    
    /**
     * This gets saved in the JSON serialization and is used to retrieve
     * the proper list of edges when a graph is opened. 
     */
    private List<SimpleEdge> simpleEdges = new ArrayList();
    
    public GPath() {
        
    }
    
    /**
     * Add an edge to the path (adds the edge and a matching simpleEdge)
     */
    public void addEdge(Edge edge) {
        edges.add(edge);
        simpleEdges.add(new SimpleEdge(edge));
    }
    
    /**
     * Checks if the given vertex is contained within the GPath.
     * @param v The vertex in question.
     * @return True if v is in the GPath, false if not. 
     */
    public boolean contains(Vertex v) {
        //loop through the path's edges
        for (Edge e : edges) {
            //if e.endpoint1 equals v
            if (e.getEndpoint1().equals(v)) {
                return true;
            }
            //if e.endpoint2 equals v
            if (e.getEndpoint2().equals(v)) {
                return true;
            }
        }
        //if we get here, then none of the edge endpoints were equal to v
        return false;
    }
    
}
