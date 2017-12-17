/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package element;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Shape;

/**
 *
 * @author Scott
 */
public class Edge extends Element {
    
    private Vertex endpoint1;
    private Vertex endpoint2;
    
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
        int x1 = (int) endpoint1.getCenter().getX();
        int y1 = (int) endpoint1.getCenter().getY();
        int x2 = (int) endpoint2.getCenter().getX();
        int y2 = (int) endpoint2.getCenter().getY();
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

    @Override
    public Shape getPositionShape() {
        throw new UnsupportedOperationException("Not supported yet."
                + "Not sure what this is for."); 
    }
    //contains a pair of vertices
}
