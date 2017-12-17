/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package element;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

/**
 *
 * @author Scott
 */
public class Vertex extends Element {

    private int degree = 0;
    private double diameter = 10;
    //the center of the sphere 
    //(used to tell edges where to place their endpoints):
    private double xCent = 5;
    private double yCent = 5;

    public Vertex(String title) {
        this.title = title;
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

    /**
     * 
     * @return The center of the vertex
     */
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
        return degree;
    }

    @Override
    public Shape getPositionShape() {
        throw new UnsupportedOperationException("Not "
                + "supported yet. Not sure what this does");
    }

}
