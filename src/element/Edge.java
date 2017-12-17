/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package element;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Line2D;

/**
 *
 * @author Scott
 */
public class Edge extends Element {
    
    private Vertex endpoint1;
    private Vertex endpoint2;
    
    //Used to hold the positions of the endpoints of the line
    private int x1;
    private int y1;
    private int x2;
    private int y2;
    
    /**
     * An initializer that allows you to define the endpoints immediately
     * (Note: endpoints are unordered. Numbered so we can tell them apart.)
     * @param endpoint1
     * @param endpoint2 
     */
    public Edge (Vertex endpoint1, Vertex endpoint2) {
        this.endpoint1 = endpoint1;
        this.endpoint2 = endpoint2;
    }
    
    @Override
    public void draw(Graphics2D g2) {
        if (stroke == null) {
            stroke = new BasicStroke(strokeWidth);
        }
        g2.setStroke(stroke);
        
        g2.setColor(strokeColor);
        
        //Convert the center points of the two endpoints to ints
        x1 = (int) endpoint1.getCenter().getX();
        y1 = (int) endpoint1.getCenter().getY();
        x2 = (int) endpoint2.getCenter().getX();
        y2 = (int) endpoint2.getCenter().getY();
        g2.drawLine(x1, y1, x2, y2); //draw the line
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
     * This is used to determine if the mouse click is contained by
     * the shape of this line (called in Graph)
     * @return 
     */
    @Override
    public Shape getPositionShape() {
        return new Line2D.Double(x1, y1, x2, y2);
    }
    
    /**
     * The string is a combination of the names of the endpoints
     * (Order is arbitrary). 
     * @return 
     */
    @Override
    public String toString() {
        return endpoint1.title + "," + endpoint2.title;
    }
}
