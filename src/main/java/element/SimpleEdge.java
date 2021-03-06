/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package element;

import java.awt.geom.Point2D;

/**
 * This object helps vertices keep track of the edges that they are connected
 * to. If we used actual Edge objects, then JSON Serialization would be
 * impossible (because there would be Edge-seption, meaning that edges
 * would be saved within vertices which would again contain the same
 * edges which contain the same vertices etc.). This class only stores the names
 * of the edge endpoints, because that is all we need. 
 *
 * @author Scott
 */
public class SimpleEdge {

    String endpoint1;
    String endpoint2;
    
    //The coordinates of the control point
    double ctrlX;
    double ctrlY;

    public SimpleEdge(Edge edge) {
        this.endpoint1 = edge.getEndpoint1().getTitle();
        this.endpoint2 = edge.getEndpoint2().getTitle();
        this.ctrlX = edge.getCtrlPoint().x;
        this.ctrlY = edge.getCtrlPoint().y;
    }
    
    public void setCtrlPoint(double ctrlX, double ctrlY) {
        this.ctrlX = ctrlX;
        this.ctrlY = ctrlY;
    }
    
    public void incCtrlPoint(double incX, double incY) {
        this.setCtrlPoint(this.ctrlX + incX, this.ctrlY + incY);
    }
    
    public Point2D.Double getCtrlPoint() {
        return new Point2D.Double(ctrlX, ctrlY);
    }
    
    public String getEndpoint1() {
        return endpoint1;
    }
    
    public String getEndpoint2() {
        return endpoint2;
    }
    
    /**
     * Checks to see if the given vertex is an endpoint of this edge.
     * @param v The vertex that we are asking whether it is an endpoint
     * of the edge or not.
     * @return True if the edge has v as the endpoint, false if not. 
     */
    public boolean hasEndpoint(Vertex v) {
        if (endpoint1.equals(v.getTitle())) { //if v == endpoint1
            return true;
        }
        if (endpoint2.equals(v.getTitle())) { //if v == endpoint2
            return true;
        }
        //if neither endpoint equals v
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        //if this is a SimpleEdge
        if (obj instanceof SimpleEdge) {
            //convert the Object to a SimpleEdge
            SimpleEdge se = (SimpleEdge) obj;
            if (this.endpoint1.equals(se.endpoint1)) { //If e.enpoint1 equals either endpoint1
                //check if e.endpoint2 equals endpoint2
                if (this.endpoint2.equals(se.endpoint2)) { //if e.endpoint2 equals endpoint2
                    return true; //Both are equal and they are the same
                } else {
                    return false; //Only e.endpoint1 equals endpoint1 and they are different
                }
            } else if (this.endpoint2.equals(se.endpoint1)) { //If e.endpoint1 equals endpoint2
                //check if e.endpoint2 equals endpoint1
                if (this.endpoint1.equals(se.endpoint2)) { //If e.ednpoint2 equals endpoint1
                    return true; //both are equal and they are the same
                } else {
                    return false; //onlye e.endpoint1 equals endpoint2, so they are different
                }
            } else { //If e.endpoint1 equals neither endpoint1 nor endpoint2
                //then they are not equal (no need to check the other endpoint)
                return false;
            }
        } else { //if this is neither an Edge nor a SimpleEdge
            return false; //it is not equal
        }
    }
    
    @Override
    public String toString() {
        return "(" + endpoint1 + "," + endpoint2 + ")";
    }
    
}
