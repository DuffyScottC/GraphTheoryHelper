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
    
    private transient boolean isHidden = false;
    private transient boolean isSelected = false;
    
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
    
    public void setEdges(List<Edge> edges) {
        this.edges = edges;
    }
    
    public List<SimpleEdge> getSimpleEdges() {
        return simpleEdges;
    }
    
    /**
     * Add an edge to the walk (adds the edge and a matching simpleEdge)
     * @param edge
     */
    public void addEdge(Edge edge) {
        //if this element is selected
        if (isSelected) {
            //set the edge to be walk colored
            edge.setIsWalkColored(true);
            //get the edge's endpoints
            Vertex ep1 = edge.getEndpoint1();
            Vertex ep2 = edge.getEndpoint2();
            //set the vertices to be walk colored
            ep1.setIsWalkColored(true);
            ep2.setIsWalkColored(true);
        }
        edges.add(edge);
        simpleEdges.add(new SimpleEdge(edge));
    }
    
    /**
     * Removes both the given Edge and its matching SimpleEdge from the walk.
     * Sets isWalkColored to false and isHidden to false for the given edge.
     * @param e The Edge to be removed
     */
    public boolean removeEdge(Edge e) {
        int index = edges.indexOf(e);
        return removeEdge(index);
    }
    
    /**
     * Removes the edge at the given index from the walk. Sets isWalkColored
     * to false and isHidden to false for the given edge.
     * @param index The index of the edge to be removed
     * @return True if the given edge was in the walk and was successfully
     * removed, false if the edge was not contained in the walk at all
     */
    public boolean removeEdge(int index) {
        if (index == -1) {
            //the edge does not exist in this walk
            return false;
        }
        //get the edge to be removed
        Edge e = edges.get(index);
        //remove the edge from the walk
        edges.remove(index);
        simpleEdges.remove(index);
        //Handle coloring:
        //set isWalkColored to false
        e.setIsWalkColored(false);
        //get the edge's endpoints
        Vertex ep1 = e.getEndpoint1();
        Vertex ep2 = e.getEndpoint2();
        //if ep1 does NOT have even one edge in this walk
        if (!vertexHasEdgesInWalk(ep1)) {
            //set isWalkColored to false
            ep1.setIsWalkColored(false);
            ep1.setIsHidden(false);
        }
        //if ep2 does NOT have even one edge in this walk
        if (!vertexHasEdgesInWalk(ep2)) {
            //set isWalkColored to false
            ep2.setIsWalkColored(false);
            ep2.setIsHidden(false);
        }
        //we successfully deleted the edge
        return true;
    }
    
    public boolean isEdgeConnected(Edge givenEdge) {
        for (Edge currentEdge : edges) {
            //if the given edge shares an endpoint with the current edge
            if (currentEdge.sharesEndpointWith(givenEdge)) {
                //the given edge is connected to the walk
                return true;
            }
        }
        //if none of the edges in this walk shares an endpoint with givenEdge
        //then the given edge is not connected to the walk
        return false;
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
     * Checks to see if the Walk contains the given SimpleEdge
     * @param se The SimpleEdge in question
     * @return 
     */
    public boolean contains(SimpleEdge se) {
        return simpleEdges.contains(se);
    }
    
    /**
     * When a walk is selected, it sets all its elements' 
     * {@link Element.isWalkColored} values to true.
     */
    public void select() {
        isSelected = true;
        setIsWalkColoredForAllElements(true);
    }
    
    /**
     * When a walk is selected, it sets all its elements' 
     * {@link Element.isWalkColored} values to false.
     */
    public void deselect() {
        isSelected = false;
        setIsWalkColoredForAllElements(false);
    }
    
    /**
     * Assign the given boolean value to all the walk's elements'
     * {@link Element.isWalkColored} variable.
     * @param isWalkColored 
     */
    private void setIsWalkColoredForAllElements(boolean isWalkColored) {
        for (Edge edge : edges) {
            //set the edge to be walk colored
            edge.setIsWalkColored(isWalkColored);
            //get the edge's endpoints
            Vertex ep1 = edge.getEndpoint1();
            Vertex ep2 = edge.getEndpoint2();
            //set the vertices to be walk colored
            ep1.setIsWalkColored(isWalkColored);
            ep2.setIsWalkColored(isWalkColored);
        }
    }
    
    public void hide() {
        setIsHiddenForAllElements(true);
    }
    
    public void unhide() {
        setIsHiddenForAllElements(false);
    }
    
    private void setIsHiddenForAllElements(boolean isHidden) {
        this.isHidden = isHidden;
        //loop through all of the edges
        for (Edge edge : edges) {
            //hide the edge
            edge.setIsHidden(isHidden);
            //get the edge's endpoints
            Vertex ep1 = edge.getEndpoint1();
            Vertex ep2 = edge.getEndpoint2();
            //if we should hide this endpoint
            if (shouldHideVertex(ep1)) {
                ep1.setIsHidden(isHidden);
            }
            //if we should hide this endpoint
            if (shouldHideVertex(ep2)) {
                ep2.setIsHidden(isHidden);
            }
        }
    }
    
    public boolean isHidden() {
        return isHidden;
    }
    
    /**
     * Checks to see whether any of the given vertex's SimpleEdges are NOT
     * contained within this walk. If even a single edge connected to the
     * given vertex is not within this walk, then we should not hide the
     * vertex. If this walk contains all of the given vertex's SimpleEdges,
     * then we should hide the vertex.
     * @param v The given vertex
     * @return True if we should hide the vertex, false if not. 
     */
    private boolean shouldHideVertex(Vertex v) {
        //loop through the vertex's SimpleEdges
        for (SimpleEdge se : v.edgeNames) {
            //if this walk does NOT contain se
            if (!this.contains(se)) {
                //we should not hide the vertex
                return false;
            }
        }
        //if this walk contains all of v's SimpleEdges, we should hide v
        return true;
    }
    
    /**
     * Checks to see whether the walk contains any of the given vertex's edges.
     * This is not to be confused with {@link shouldHideVertex}. 
     * @param v The given vertex
     * @return True if at least one of the given vertex's edges in contained
     * in the walk.
     */
    private boolean vertexHasEdgesInWalk(Vertex v) {
        //loop through the vertex's SimpleEdges
        for (SimpleEdge se : v.edgeNames) {
            //if this walk contains se
            if (this.contains(se)) {
                //this walk has at least one vertex edge
                return true;
            }
        }
        //this walk contains NONE of v's SimpleEdges
        return false;
    }
    
    /**
     * If this walk has no edges, then it is empty.
     * @return 
     */
    public boolean isEmpty() {
        return edges.isEmpty();
    }
    
    @Override
    public String toString() {
        //if the path is empty
        if (this.isEmpty()) {
            //provide only an indicator
            return "<Empty>";
        }
        StringBuilder strB = new StringBuilder();
        if (isHidden) {
            strB.append("- ");
        }
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
