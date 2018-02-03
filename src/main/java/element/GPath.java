/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package element;

import controller.Values;
import java.awt.Graphics2D;
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
     * Checks to see if the GPath contains the given SimpleEdge
     * @param se
     * @return 
     */
    public boolean contains(SimpleEdge se) {
        return simpleEdges.contains(se);
    }
    
    /**
     * Removes both the given SimpleEdge and its matching Edge from the path.
     * @param se The SimpleEdge to be removed
     */
    public void removeSimpleEdge(SimpleEdge se) {
        int index = simpleEdges.indexOf(se);
        edges.remove(index);
        simpleEdges.remove(index);
    }
    
    /**
     * Removes the edge at the given index from the path.
     * @param index The index of the edge to be removed
     */
    public void removeEdge(int index) {
        edges.remove(index);
        simpleEdges.remove(index);
    }
    
    /**
     * Removes the given vertex from the path (if the vertex exists);
     * @param v 
     */
    public void removeVertex(Vertex v) {
        //loop through the path's edges
        for (int i = 0; i < edges.size(); i++) {
            //get the next edge
            Edge e = edges.get(i);
            //if e.endpoint1 equals v
            if (e.getEndpoint1().equals(v)) {
                //remove the current edge from the path
                this.removeEdge(i);
                //exit the function because our job is done
                return;
            }
            //if e.endpoint2 equals v
            if (e.getEndpoint2().equals(v)) {
                //remove the current edge from the path
                this.removeEdge(i);
                //exit the function because our job is done
                return;
            }
        }
    }
    
    public void draw(Graphics2D g2) {
        for (Edge e : edges) {
            e.draw(g2, Values.PATH_STROKE_COLOR);
            e.getEndpoint1().draw(g2, Values.PATH_VERTEX_STROKE_COLOR, Values.PATH_VERTEX_FILL_COLOR);
            e.getEndpoint2().draw(g2, Values.PATH_VERTEX_STROKE_COLOR, Values.PATH_VERTEX_FILL_COLOR);
        }
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
