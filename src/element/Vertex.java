/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package element;

import controller.Values;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Scott
 */
public class Vertex extends Element {
    
    private double diameter = 10;
    
    //(used to tell edges where to place their endpoints):
    private double xCent = 5;
    private double yCent = 5;
    
    /**
     * Used to determine whether a vertex is available to add an edge to
     * (whether from a given edge or from the first selected edge).
     * Used in addEdgeState operations. Defaults to true when you
     * create a new vertex (not serializable, so set in constructor)
     */
    private boolean canAddEdges;
    
    /**
     * A list of edges associated with this vertex.
     * Used to delete all associated edges when this vertex
     * is deleted.
     */
    List<Edge> edgeNames = new ArrayList<>();

    public Vertex (double diameter) {
        this.diameter = diameter;
        canAddEdges = true;
        shape = new Ellipse2D.Double(0, 0, diameter, diameter);
    }
    
    public Vertex(String title, double diameter) {
        this.title = title;
        this.diameter = diameter;
        canAddEdges = true;
        shape = new Ellipse2D.Double(0, 0, diameter, diameter);
    }
    
    @Override
    public void draw(Graphics2D g2) {
        if (stroke == null) {
            stroke = new BasicStroke(strokeWidth);
        }
        g2.setStroke(stroke);
        
        g2.setColor(fillColor); //set the circle's color
        g2.fill(shape); //fill in the circle in that color
        
        //if the strokeColor is null, we want NO outline.
        if (strokeColor != null) {
            g2.setColor(strokeColor); //set the circle's color
            g2.draw(shape); //draw the outline in that color
        }
    }
    
    public void drawTitle(Graphics2D g2) {
        g2.setColor(Color.BLACK);
        g2.drawString(title, (int) xLoc, (int) yLoc - 8);
    }
    
    public void incLocation(double xInc, double yInc) {
        //to consolidate code, this simply calls setLocation after incrementing
        this.setLocation(this.xLoc + xInc, this.yLoc + yInc);
    }
    
    public void setLocation(double xLoc, double yLoc) {
        //Update the location for next time
        this.xLoc = xLoc;
        this.yLoc = yLoc;
    }

    public Point2D.Double getLocation() {
        return new Point2D.Double(xLoc, yLoc);
    }
    
    public Point2D.Double getCenter() {
        //We want to calculate the center when a given edge
        this.calculateCenter(); //find the center
        return new Point2D.Double(xCent, yCent);
    }
    
    /**
     * The center is half the diameter from the top right corner.
     * This is a helper method to calculate it (for readability).
     */
    private void calculateCenter() {
        xCent = xLoc + diameter / 2;
        yCent = yLoc + diameter / 2;
    }
    
    public int getDegree() {
        return edgeNames.size();
    }
    
    public void addEdge(Edge e) {
        //if the new edge is already connected to this vertex:
        if (edgeNames.contains(e)) {
            throw new RuntimeException("Attempted to add edge (" +
                    e.toString() +
                    ") same to vertex (" +
                    this.toString() +
                    ") twice.");
        }
        //Otherwise just add it
        edgeNames.add(e);
    }
    
    public void addAllEdges(List<Edge> es) {
        for (Edge e : es) { //loop through the edges to be added
            this.addEdge(e); //add the edge to this vertex
        }
    }
    
    /**
     * Make it so that edges cannot be added to the vertices that are already
     * connected to this vertex.
     */
    public void assignCanAddEdgesToConnectedVertices() {
        //Loop through all edges
        for (Edge e : edgeNames) {
            //Disable both endpoints (It's not worth checking
            //if each endpoint is the current vertex or not)
            e.getEndpoint1().setCanAddEdges(false);
            e.getEndpoint2().setCanAddEdges(false);
        }
    }
    
    public void removeEdge(Edge e) {
        edgeNames.remove(e);
    }
    
    public List<Edge> getEdgeNames() {
        return edgeNames;
    }
    
    public void setCanAddEdges(boolean canAddEdges) {
        this.canAddEdges = canAddEdges;
    }
    
    public boolean canAddEdges() {
        return canAddEdges;
    }
    
    public boolean isAdjacentTo(Vertex v) {
        for (Edge e : edgeNames) { //cycle through all the edges
            if (e.hasEndpoint(v)) { //if v is an enpoint of e
                return true;
            }
        }
        //if none of the edges has v as an endpoint
        return false;
    }
    
    /**
     * This is used to determine if the mouse click is contained by
     * the shape of this vertex (called in Graph)
     * @return And Ellipse2D.Double object that defines the vertex's position
     * shape.
     */
    public Shape getPositionShape() {
        double strokeBuffer = Values.VERTEX_STROKE_WIDTH/2;
        return new Ellipse2D.Double(xLoc, yLoc, diameter + strokeBuffer, diameter + strokeBuffer);
    }

}
