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
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;

/**
 *
 * @author Scott
 */
public class Edge extends Element {
    
    private Vertex endpoint1;
    private Vertex endpoint2;
    
    //Used to hold the postition of the control point of the curve
    private double ctrlX;
    private double ctrlY;
    
    /**
     * An initializer that allows you to define the endpoints immediately. This
     * also automatically adds itself to the two endpoints so that you don't have to.
     * (Note: endpoints are unordered. Numbered so we can tell them apart.)
     * @param endpoint1
     * @param endpoint2 
     */
    public Edge (Vertex endpoint1, Vertex endpoint2) {
        this.endpoint1 = endpoint1;
        this.endpoint2 = endpoint2;
        
        //Default stroke width
        this.strokeWidth = Values.EDGE_STROKE_WIDTH;
        
        //Set the default control point:
        //get the endpoint coordinates
        double x1 = endpoint1.getCenter().getX();
        double y1 = endpoint1.getCenter().getY();
        double x2 = endpoint2.getCenter().getX();
        double y2 = endpoint2.getCenter().getY();
        //set the control point
        ctrlX = (x1 + x2)/2; //find the mid-x
        ctrlY = (y1 + y2)/2; //find the mid-y
        
        //Connect this edge to the two vertices
        this.endpoint1.addEdge(this);
        this.endpoint2.addEdge(this);
    }
    
    /**
     * A no-argument constructor used only when loading graphs from JSON
     * serialization. This constructor does not use the 
     * {@link Vertex.addEdge(Edge)} method (because each endpoint vertex already
     * has this edge added to it, technically. We assign endpoints and control
     * point manually, after this constructor is called. 
     */
    public Edge () {
        //Default stroke width
        this.strokeWidth = Values.EDGE_STROKE_WIDTH;
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
        
        if (isPressed) {
            //change the current properties to the highlighted mode
            currentStroke = new BasicStroke(Values.EDGE_HIGHLIGHT_STROKE_WIDTH);
            currentStrokeColor = Values.EDGE_PRESSED_COLOR;
            //if it's pressed, it can't be highlighted or walk colored
        } else if (isHighlighted) {
            //change the current properties to the highlighted mode
            currentStroke = new BasicStroke(Values.EDGE_HIGHLIGHT_STROKE_WIDTH);
            currentStrokeColor = Values.EDGE_HIGHLIGHT_COLOR;
            //if it's highlighted, it can't be walk colored
        } else if (isWalkColored) {
            //change the current properties to the shown walk mode
            currentStroke = new BasicStroke(Values.WALK_EDGE_STROKE_WIDTH);
            currentStrokeColor = Values.WALK_EDGE_STROKE_COLOR;
        }
        /*
        If this edge is neither highlighted nor part of a shown walk,
        then leave the colors as the default (chosen by the user or 
        default)
        */
        
        //Actually draw:
        g2.setStroke(currentStroke);
        g2.setColor(currentStrokeColor);
        //Convert the center points of the two endpoints to ints
        int x1 = (int) endpoint1.getCenter().getX();
        int y1 = (int) endpoint1.getCenter().getY();
        int x2 = (int) endpoint2.getCenter().getX();
        int y2 = (int) endpoint2.getCenter().getY();
        //Define a new quad curve from the endpoints and the control point:
        QuadCurve2D qCurve = new QuadCurve2D.Double(); //instantiate a curve
        qCurve.setCurve(x1, y1, ctrlX, ctrlY, x2, y2); //assign the values
        g2.draw(qCurve); //draw the curve
    }
    
    public Vertex getEndpoint1() {
        return endpoint1;
    }

    public void setEndpoint1(Vertex endpoint1) {
        this.endpoint1 = endpoint1;
    }

    public Vertex getEndpoint2() {
        return endpoint2;
    }

    public void setEndpoint2(Vertex endpoint2) {
        this.endpoint2 = endpoint2;
    }
    
    /**
     * Given an endpoint that is attached to this edge, this method returns
     * the other endpoint that is attached to this edge.
     * @param firstEndpoint The vertex that we know is attached to this edge
     * @return The other vertex attached to this edge
     */
    public Vertex getOtherEndpoint(Vertex firstEndpoint) {
        //if we already know that endpoint1 is attached to this edge
        if (endpoint1.equals(firstEndpoint)) {
            //the other endpoint must be endpoint2
            return endpoint2;
        } else { //if endpoint1 is not firstEndpoint
            //the other endpoint must be endpoint1
            return endpoint1;
        }
    }
    
    public Point2D.Double getCtrlPoint() {
        return new Point2D.Double(ctrlX, ctrlY);
    }
    
    /**
     * Get the position shape of this edge's control point. (Used for checking
     * if the user's click is inside of it and for drawing.)
     * @return Ellipse2D.Double object 
     */
    public Ellipse2D.Double getCtrlPointPositionShape() {
        int dim = Values.EDGE_CTRL_POINT_DIMESION;
        //subtract dim/2 to place the center of the circle in the visually
        //appropriate location instead of the top left corner
        return new Ellipse2D.Double(ctrlX - dim/2, ctrlY - dim/2, dim, dim);
    }
    
    public void setCtrlPoint(double ctrlX, double ctrlY) {
        this.ctrlX = ctrlX;
        this.ctrlY = ctrlY;
    }
    
    public void incCtrlPoint(double incX, double incY) {
        this.setCtrlPoint(this.ctrlX + incX, this.ctrlY + incY);
    }
    
    /**
     * Checks to see if the given vertex is an endpoint of this edge.
     * @param v The vertex that we are asking whether it is an endpoint
     * of the edge or not.
     * @return True if the edge has v as the endpoint, false if not. 
     */
    public boolean hasEndpoint(Vertex v) {
        if (endpoint1.equals(v)) { //if v == endpoint1
            return true;
        }
        if (endpoint2.equals(v)) { //if v == endpoint2
            return true;
        }
        //if neither endpoint equals v
        return false;
    }
    
    /**
     * Checks to see whether the given edge has an endpoint in common with
     * this edge.
     * @param givenEdge The given edge
     * @return True if this edge shares one or more endpoints with the given
     * edge, false if this edge shares no endpoints with the given edge.
     */
    public boolean sharesEndpointWith(Edge givenEdge) {
        Vertex ep1 = givenEdge.getEndpoint1();
        Vertex ep2 = givenEdge.getEndpoint2();
        //return true if this has ep1 or ep2, false if this has neither of them
        return this.hasEndpoint(ep1) || this.hasEndpoint(ep2);
    }
    
    /**
     * This is used to determine if the mouse click is contained by
     * the shape of this line (called in Graph)
     * @return A QuadCurve2D.Double object defining the edge's curve
     */
    public QuadCurve2D getPositionShape() {
        //Convert the center points of the two endpoints to ints
        int x1 = (int) endpoint1.getCenter().getX();
        int y1 = (int) endpoint1.getCenter().getY();
        int x2 = (int) endpoint2.getCenter().getX();
        int y2 = (int) endpoint2.getCenter().getY();
        //convert this into a QuadCurve2D.Double
        return new QuadCurve2D.Double(x1, y1, ctrlX, ctrlY, x2, y2);
    }
    
    /**
     * The string is a combination of the names of the endpoints
     * (Order is arbitrary). 
     * @return 
     */
    @Override
    public String toString() {
        //if this edge is hidden
        if (isHidden) {
            //add the - in front of it
            return "- " + endpoint1.title + "," + endpoint2.title;
        } else { //if this edge is NOT hidden
            //just add the titles of the endpoint
            return endpoint1.title + "," + endpoint2.title;
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof Edge) {
            Edge e = (Edge) obj;
            if (this.endpoint1.equals(e.endpoint1)) { //If e.enpoint1 equals either endpoint1
                //check if e.endpoint2 equals endpoint2
                if (this.endpoint2.equals(e.endpoint2)) { //if e.endpoint2 equals endpoint2
                    return true; //Both are equal and they are the same
                } else {
                    return false; //Only e.endpoint1 equals endpoint1 and they are different
                }
            } else if (this.endpoint2.equals(e.endpoint1)) { //If e.endpoint1 equals endpoint2
                //check if e.endpoint2 equals endpoint1
                if (this.endpoint1.equals(e.endpoint2)) { //If e.ednpoint2 equals endpoint1
                    return true; //both are equal and they are the same
                } else {
                    return false; //onlye e.endpoint1 equals endpoint2, so they are different
                }
            } else { //If e.endpoint1 equals neither endpoint1 nor endpoint2
                //then they are not equal (no need to check the other endpoint)
                return false;
            }
        } else {
            return false;
        }
    }
    
}
