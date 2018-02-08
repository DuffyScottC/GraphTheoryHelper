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
public class Walk {
    
    /**
     * This holds the edges in the walk
     */
    private transient List<Edge> edges = new ArrayList();
    
    /**
     * This gets saved in the JSON serialization and is used to retrieve
     * the proper list of edges when a graph is opened. 
     */
    private List<SimpleEdge> simpleEdges = new ArrayList();
    
    /**
     * Creates a new Walk object with the specified edge as the first edge.
     * @param edge The first edge in the walk.
     */
    public Walk(Edge edge) {
        addEdge(edge);
    }
    
    /**
     * Add an edge to the walk (adds the edge and a matching simpleEdge)
     * @param edge
     */
    public void addEdge(Edge edge) {
        edges.add(edge);
        simpleEdges.add(new SimpleEdge(edge));
    }
    
    /**
     * Checks to see if the Walk contains the given edge
     * @param e The edge in question
     * @return 
     */
    public boolean contains(Edge e) {
        return edges.contains(e);
    }
    
    /**
     * Removes both the given Edge and its matching SimpleEdge from the walk.
     * @param e The Edge to be removed
     */
    public void removeEdge(Edge e) {
        int index = edges.indexOf(e);
        edges.remove(index);
        simpleEdges.remove(index);
    }
    
    /**
     * Removes the edge at the given index from the walk.
     * @param index The index of the edge to be removed
     */
    public void removeEdge(int index) {
        edges.remove(index);
        simpleEdges.remove(index);
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
