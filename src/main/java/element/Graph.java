/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package element;

import controller.Values;
import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Holds the list of vertices and edges that are contained in the graph.
 * This will be stored in the binary file. 
 * @author Scott
 */
public class Graph implements Serializable {
    //the vertices which appear in canvas and the vertices JList
    private final List<Vertex> vertices = new ArrayList<>();
    //the edges which appear in canvas and the edges JList
    private transient final List<Edge> edges = new ArrayList<>();
    /**
     * a list of SimpleEdges which will be used for saving and loading the edges
     * from JSON serialization. Edge objects are not good for storing because
     * they do not store references to the vertices (instead they create
     * identical instances stored in a different memory location). 
     */
    private final List<SimpleEdge> simpleEdges = new ArrayList<>();
    
    //Start out default
    private Color vertexFillColor = Values.VERTEX_FILL_COLOR;
    private Color vertexStrokeColor = Values.VERTEX_STROKE_COLOR;
    private Color edgeStrokeColor = Values.EDGE_STROKE_COLOR;
    
    /**
     * If this is not null, we want to start drawing an edge between this vertex
     * and the mouse. Not to be confused with selectedVertex, which is used for
     * deleting vertices and changing titles.
     */
    private transient Vertex firstSelectedVertex = null;
    
    /**
     * Adds an edge to the graph's edges list and to its simpleEdges list.
     * (The edges list does not de-serialize well, so we store simpleEdges
     * and convert them to real Edges when loading from files). 
     * @param edge 
     */
    public void addEdge(Edge edge) {
        //add edges to the edges list
        edges.add(edge);
        //add a SimpleEdge version of the edge to the simpleEdges list
        simpleEdges.add(new SimpleEdge(edge));
    }
    
    /**
     * Removes an edge from the graph's edges list and from its simpleEdges 
     * list.
     * @param edge The edge to be removed
     */
    public void removeEdge(Edge edge) {
        //find the index of the edge to be removed
        int index = edges.indexOf(edge);
        //remove the edge at that index
        edges.remove(index);
        //remove the simple edge at that index
        simpleEdges.remove(index);
    }
    
    /**
     * Removes edges from the graph's edges list and from its simpleEdges list.
     * @param allEdges The edges to be removed
     */
    public void removeAllEdges(List<Edge> allEdges) {
        //cycle through all the edges to be removed
        for (Edge e : allEdges) {
            //remove each edge
            this.removeEdge(e);
        }
    }
    
    public void setEdgeCtrlPoint(int edgeIndex, double ctrlX, double ctrlY) {
        edges.get(edgeIndex).setCtrlPoint(ctrlX, ctrlY);
        simpleEdges.get(edgeIndex).setCtrlPoint(ctrlX, ctrlY);
    }
    
    public void incEdgeCtrlPoint(int edgeIndex, double incX, double incY) {
        edges.get(edgeIndex).incCtrlPoint(incX, incY);
        simpleEdges.get(edgeIndex).incCtrlPoint(incX, incY);
    }
    
    /**
     * Clear all elements from edges list and from simpleEdges list.
     */
    public void clearEdges() {
        edges.clear();
        simpleEdges.clear();
    }
    
    //MARK: Getters and setters
    public List<Vertex> getVertices() {
        return vertices;
    }
    
    public List<Edge> getEdges() {
        return edges;
    }
    
    public List<SimpleEdge> getSimpleEdges() {
        return simpleEdges;
    }
    
    public Color getVertexFillColor() {
        return vertexFillColor;
    }

    public void setVertexFillColor(Color vertexFillColor) {
        this.vertexFillColor = vertexFillColor;
    }

    public Color getVertexStrokeColor() {
        return vertexStrokeColor;
    }

    public void setVertexStrokeColor(Color vertexStrokeColor) {
        this.vertexStrokeColor = vertexStrokeColor;
    }
    
    public Color getEdgeStrokeColor() {
        return edgeStrokeColor;
    }
    
    public void setEdgeStrokeColor(Color edgeStrokeColor) {
        this.edgeStrokeColor = edgeStrokeColor;
    }
    
    /**
     * Convenience method to improve readability inside GraphController's
     * constructor (the part where we add an ActionListener to addButton). 
     * This will find the vertex within this graph's vertices list whose title
     * matches the passed in title. 
     * @param title The title of the vertex we wish to find
     * @return The vertex whose title matches the passed in title
     */
    public Vertex getVertexNamed(String title) {
        for (Vertex v : vertices) {
            //if v.title matches title
            if (v.getTitle().equals(title)) {
                return v;
            }
            //otherwise keep searching
        }
        //this will never happen - if we don't find a vertex named "title"
        return null;
    }
    
    /**
     * Convenience method to set the three colors all at once.
     * @param vertexFillColor
     * @param vertexStrokeColor
     * @param edgeStrokeColor 
     */
    public void setColors(Color vertexFillColor, Color vertexStrokeColor, Color edgeStrokeColor) {
        this.vertexFillColor = vertexFillColor;
        this.vertexStrokeColor = vertexStrokeColor;
        this.edgeStrokeColor = edgeStrokeColor;
    }
    
    public Vertex getFirstSelectedVertex() {
        return firstSelectedVertex;
    }
    
    public void setFirstSelectedVertex(Vertex firstSelectedVertex) {
        this.firstSelectedVertex = firstSelectedVertex;
    }
    
    /**
     * Highlights all vertices that are available to have an edge added to them
     * when the user enters the addEdgeState.
     */
    public void highlightAvailableVertices() {
        for (Vertex v : vertices) {
            //if this vertex is available to add edges to
            if (v.canAddEdges()) {
                v.setStrokeColor(Values.VERTEX_AVAILABLE_STROKE_COLOR);
                v.setStrokeWidth(Values.VERTEX_HIGHLIGHT_STROKE_WIDTH);
            } else { //if this vertex is completely full
                v.setStrokeColor(vertexStrokeColor);
                v.setStrokeWidth(Values.VERTEX_STROKE_WIDTH);
            }
        }
    }
    
    public void highlightVertex(Vertex vertex) {
        vertex.setStrokeColor(Values.EDGE_HIGHLIGHT_COLOR);
        vertex.setStrokeWidth(Values.VERTEX_HIGHLIGHT_STROKE_WIDTH);
    }

    public void unHighlightVertex(Vertex vertex) {
        vertex.setStrokeColor(vertexStrokeColor);
        vertex.setStrokeWidth(Values.VERTEX_STROKE_WIDTH);
    }

    public void highlightEdge(Edge edge) {
        edge.setStrokeWidth(Values.EDGE_HIGHLIGHT_STROKE_WIDTH);
        edge.setStrokeColor(Values.EDGE_HIGHLIGHT_COLOR);
    }

    public void unHighlightEdge(Edge edge) {
        edge.setStrokeWidth(Values.EDGE_STROKE_WIDTH);
        edge.setStrokeColor(edgeStrokeColor);
    }
    
    //MARK: Other methods
    
    /**
     * The graph is empty if vertices is empty (doesn't matter whether edges is
     * full or not)
     *
     * @return
     */
    public boolean isEmpty() {
        return vertices.isEmpty();
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
