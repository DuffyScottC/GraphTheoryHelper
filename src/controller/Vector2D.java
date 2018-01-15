/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

/**
 * All code in this particular vector class assumes that the vector is from the
 * origin (0,0) to whatever points you insert. This helps you
 *
 * @author Scott
 */
public class Vector2D {

    public double x;
    public double y;
    private int quadrant = 1;

    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;

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
                return angle;
        }
    }
}
