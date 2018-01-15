/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

/**
 * All code in this particular vector class assumes that the vector is from 
 * the origin (0,0) to whatever points you insert. This helps you 
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
        } else { //y is negative
            if (x > 0) { //x is positive
                quadrant = 4;
            } else { //x is negative
                quadrant = 3;
            }
        }
    }
}
