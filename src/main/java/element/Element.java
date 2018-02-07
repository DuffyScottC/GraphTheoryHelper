/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package element;

import controller.Values;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.io.Serializable;

/**
 *
 * @author Scott
 */
public abstract class Element implements Serializable {

    protected transient float strokeWidth = Values.VERTEX_STROKE_WIDTH;
    protected transient Color strokeColor = Color.BLACK;
    protected transient Color fillColor = Color.RED;
    protected double xLoc = 0;
    protected double yLoc = 0;
    protected String title = "A";
    
    /**
     * Is only true if this element is highlighted (selected, etc)
     */
    protected transient boolean isHighlighted = false;
    /**
     * Is only true if this element is pressed (clicked on and held)
     */
    protected transient boolean isPressed = false;
    /**
     * Is only true if this element is part of a hidden walk (meaning the user
     * wants it to not be shown at all)
     */
    protected transient boolean isWalkHidden = false;
    /**
     * Is only true if this element is part of a walk that is highlighted
     * (meaning the user has the walk selected and this element should be
     * orange)
     */
    protected transient boolean isWalkShown = false;

    // the stroke is created from strokeWidth + BasicStroke
    // but BasicStroke is not serializable, so the actual
    // stroke used is created "on the fly"
    protected transient Stroke stroke = null;

    // the actual drawing is done by the subclass
    public abstract void draw(Graphics2D g2);
    
//    public abstract Shape getPositionShape();

    public final void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
        stroke = null;
    }

    public void setStrokeColor(Color color) {
        this.strokeColor = color;
    }

    public void setFillColor(Color color) {
        this.fillColor = color;
    }

    public String getTitle() {
        return title;
    }
    
    public final void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }

    public void setIsHighlighted(boolean isHighlighted) {
        this.isHighlighted = isHighlighted;
    }
    
    public void setIsPressed(boolean isPressed) {
        this.isPressed = isPressed;
    }
    
    public void setIsWalkHidden(boolean isWalkHidden) {
        this.isWalkHidden = isWalkHidden;
    }

    public void setIsWalkShown(boolean isWalkShown) {
        this.isWalkShown = isWalkShown;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof Element) { //if this is an Element object (or some subclass of Element)
            Element el = (Element) obj; //cast it to an Element
            return this.title.equals(el.title); //compare the titles
        } else { //if this is not an Element, then they are not equal
            return false;
        }
    }

}
