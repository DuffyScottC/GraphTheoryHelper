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

    @Override
    public Shape getPositionShape() {
        throw new UnsupportedOperationException("Not supported yet."
                + "Not sure what this is for."); 
    }
    //contains a pair of vertices
}
