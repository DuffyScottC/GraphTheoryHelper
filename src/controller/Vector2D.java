/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.awt.geom.Point2D;

/**
 * All code in this particular vector class assumes that the vector is from the
 * origin (0,0) to whatever points you insert. This helps find the angle of the 
 * vector.<>
 * Important: Point2D and Vector2D are not interchangeable! Vector2D is not a
 * position. It conveys only a magnitude and a direction from the origin. To
 * get a position use the {@link #add(Point2D.Double)} method.
 *
 * @author Scott
 */
public class Vector2D {

    public double x;
    public double y;
    private int quadrant = 1;
    
    /**
     * Creates a vector from the origin (0,0) to the given point (x,y).
     * @param x The x-value of this vector from the origin
     * @param y The y-value of this vector from the origin
     */
    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
        findQuadrant();
    }
    
    /**
     * Creates a vector from the origin (0,0) that has the same magnitude and
     * direction as the vector from points a to b.
     * @param a
     * @param b 
     */
    public Vector2D(Point2D.Double a, Point2D.Double b) {
        this.x = b.x - a.x;
        this.y = b.y - a.y;
        findQuadrant();
    }
    
    /**
     * Creates a normal vector (with magnitude 1) from the origin (0,0) with
     * the passed-in direction angle.
     * @param angle The angle of the new normal vector
     */
    public Vector2D(double angle) {
        x = Math.cos(angle);
        y = Math.sin(angle);
    }
    
    private void findQuadrant() {
        //determine what quadrant we are in
        if (y > 0) { //y is positive
            if (x > 0) { //x is positive
                quadrant = 1;
            } else { //x is negative
                quadrant = 2;
            }
        } else //y is negative
        if (x > 0) { //x is positive
            quadrant = 4;
        } else { //x is negative
            quadrant = 3;
        }
    }

    public double getMagnitude() {
        return Math.sqrt(x * x + y * y);
    }
    
    /**
     * Returns the angle from the x-axis to this vector.
     * @return An angle from -pi/2 to 3pi/2 not inclusive (in radians)
     */
    public double getAngle() {
        if (x == 0) { //if the vector is completely vertical
            if (y > 0) { //if y is positive
                return Math.PI/4; //90 degrees, or pi/4 radians
            } else { //if y is negative
                return 3*Math.PI/2; //270 degrees, or 3pi/2 radians
            }
        } 
        //if the vector is not completely vertical (x != 0)
        //find the result of arctan(y/x)
        double angle = Math.atan2(y, x);
        //add 180 degrees (pi radians) to angle if this vector is in Q2 or Q3
        switch (quadrant) {
            case 1:
                return angle;
            case 2:
                return angle + Math.PI;
            case 3:
                return angle + Math.PI;
            default: //quadrant 4
                return angle + Math.PI*2;
        }
    }
    
    /**
     * Normalizes this vector (gives it a magnitude of 1).
     */
    public void normalize() {
        double mag = this.getMagnitude();
        x = x/mag;
        y = y/mag;
    }
    
    /**
     * Multiply this vector by the given scalar quantity. This changes the
     * vector's magnitude. If the vector is normal (has a magnitude of 1),
     * then this operation effectively sets the magnitude of this vector
     * to the scalar quantity.
     * @param scalar The scalar quanity to multiply the vector by. 
     */
    public void multiplyBy(double scalar) {
        x = x*scalar;
        y = y*scalar;
    }
    
    /**
     * Add this vector to the given point.
     * @param point The point to add this vector to
     * @return A Point2D.Double object that is this vector's magnitude and
     * direction from the passed in point.
     */
    public Point2D.Double add(Point2D.Double point) {
        double xPos = point.x + this.x;
        double yPos = point.y + this.y;
        return new Point2D.Double(xPos, yPos);
    }
    
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("(");
        double rX = Math.round(x * 10000.0) / 10000.0;
        s.append(rX);
        s.append(",");
        double rY = Math.round(y * 10000.0) / 10000.0;
        s.append(rY);
        s.append("), ");
        s.append(this.getMagnitude());
        s.append(", ");
        s.append(this.getAngle());
        return s.toString();
    }
}
