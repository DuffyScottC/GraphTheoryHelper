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
import java.awt.Stroke;
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
    private transient Shape shape = new Ellipse2D.Double(0, 0, diameter, diameter);
    
    //(used to tell edges where to place their endpoints):
    private double xCent = 5;
    private double yCent = 5;
    
    /**
     * Used to determine whether a vertex is available to add an edge to
     * (whether from a given edge or from the first selected edge).
     * Used in addEdgeState operations. Defaults to true when you
     * create a new vertex (not serializable, so set in constructor)
     */
    private transient boolean canAddEdges = true;
    
    /**
     * A list of edges associated with this vertex.
     * Used to delete all associated edges when this vertex
     * is deleted.
     */
    List<SimpleEdge> simpleEdges = new ArrayList<>();

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
        if (isHidden) {
            return;
        }
        
        //set up stroke if neccessary
        if (stroke == null) {
            stroke = new BasicStroke(strokeWidth);
        }
        
        //initialize the stroke and strokeColor
        Stroke currentStroke = stroke;
        Color currentStrokeColor = strokeColor;
        Color currentFillColor = fillColor;
        
        if (isPressed) {
            currentStroke = new BasicStroke(Values.VERTEX_HIGHLIGHT_STROKE_WIDTH);
            currentStrokeColor = Values.EDGE_PRESSED_COLOR;
            //if this is pressed AND walkColored
            if (isWalkColored) {
                currentFillColor = Values.WALK_VERTEX_PRESSED_COLOR;
            } else { //if this is pressed and NOT walkColored
                currentFillColor = Values.VERTEX_PRESSED_COLOR;
            }
            //if it's pressed, it can't be highlighted or walk colored
        } else if (isHighlighted) {
            currentStroke = new BasicStroke(Values.VERTEX_HIGHLIGHT_STROKE_WIDTH);
            currentStrokeColor = Values.EDGE_HIGHLIGHT_COLOR;
            //if this is highlighted AND walkColored
            if (isWalkColored) {
                currentFillColor = Values.WALK_VERTEX_FILL_COLOR;
            } //otherwise leave as default
        } else if (isWalkColored) {
            currentStroke = new BasicStroke(Values.WALK_VERTEX_STROKE_WIDTH);
            currentStrokeColor = Values.WALK_VERTEX_STROKE_COLOR;
            currentFillColor = Values.WALK_VERTEX_FILL_COLOR;
        }
        /*
        If this vertex is neither highlighted nor part of a shown walk,
        then leave the colors as the default (chosen by the user or 
        default)
        */
         
        //Actually draw:
        g2.setStroke(currentStroke);
        g2.setColor(currentFillColor); //set the circle's color
        //initialize the shape object
        shape = new Ellipse2D.Double(0, 0, diameter, diameter);
        g2.fill(shape); //fill in the circle in that color
        
        //if the strokeColor is null, we want NO outline.
        if (currentStrokeColor != null) {
            g2.setColor(currentStrokeColor); //set the circle's color
            g2.draw(shape); //draw the outline in that color
        }
    }
    
    public void drawTitle(Graphics2D g2) {
        //if this vertex is NOT hidden
        if (!isHidden) {
            //draw the title
            g2.setColor(Color.BLACK);
            g2.drawString(title, (int) xLoc, (int) yLoc - 8);
        }
    }
    
    /**
     * Used to move the vertex vertically by xInc and horizontally
     * by yInc.
     * (the new location) =
     * (the old location vector) + 
     * (the increment vector)
     * @param xInc
     * @param yInc 
     */
    public void incLocation(double xInc, double yInc) {
        //to consolidate code, this simply calls setLocation after incrementing
        this.setLocation(this.xLoc + xInc, this.yLoc + yInc);
    }
    
    /**
     * Used to set the location of this vertex to the given
     * set of coordinates.
     * @param xLoc
     * @param yLoc 
     */
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
        return simpleEdges.size();
    }
    
    public void addEdge(Edge e) {
        //Convert this edge to a SimpleEdge
        SimpleEdge se = new SimpleEdge(e);
        //if the new edge is already connected to this vertex:
        if (simpleEdges.contains(se)) {
            //throw an exception
            throw new RuntimeException("Attempted to add edge (" +
                    e.toString() +
                    ") same to vertex (" +
                    this.toString() +
                    ") twice.");
        }
        //Otherwise just add it
        simpleEdges.add(se);
    }
    
    public void addAllEdges(List<Edge> es) {
        for (Edge e : es) { //loop through the edges to be added
            this.addEdge(e); //add the edge to this vertex
        }
    }
    
    public void removeEdge(Edge e) {
        //convert this edge to a SimpleEdge
        SimpleEdge se = new SimpleEdge(e);
        //remove the SimpleEdge to this vertex
        simpleEdges.remove(se);
    }
    
    public List<SimpleEdge> getSimpleEdges() {
        return simpleEdges;
    }
    
    public void setSimpleEdges (List<SimpleEdge> simpleEdges) {
        this.simpleEdges = simpleEdges;
    }
    
    public void setCanAddEdges(boolean canAddEdges) {
        this.canAddEdges = canAddEdges;
    }
    
    public boolean canAddEdges() {
        return canAddEdges;
    }
    
    public boolean isAdjacentTo(Vertex v) {
        for (SimpleEdge se : simpleEdges) { //cycle through all the SimpleEdges
            if (se.hasEndpoint(v)) { //if v is an enpoint of se
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
    
    @Override
    public String toString() {
        return title;
    }
}
