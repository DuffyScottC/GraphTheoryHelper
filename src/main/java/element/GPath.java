/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package element;

import controller.Values;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.QuadCurve2D;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Scott
 */
public class GPath {
    
    /**
     * This holds the edges in the path
     */
    private transient List<Edge> edges = new ArrayList();
    
    /**
     * This gets saved in the JSON serialization and is used to retrieve
     * the proper list of edges when a graph is opened. 
     */
    private List<SimpleEdge> simpleEdges = new ArrayList();
    
    public GPath() {
        
    }
    
    /**
     * Add an edge to the path (adds the edge and a matching simpleEdge)
     */
    public void addEdge(Edge edge) {
        edges.add(edge);
        simpleEdges.add(new SimpleEdge(edge));
    }
    
    /**
     * Checks to see if the GPath contains the given SimpleEdge
     * @param se
     * @return 
     */
    public boolean contains(SimpleEdge se) {
        return simpleEdges.contains(se);
    }
    
    /**
     * Removes both the given SimpleEdge and its matching Edge from the path.
     * @param se The SimpleEdge to be removed
     */
    public void removeSimpleEdge(SimpleEdge se) {
        int index = simpleEdges.indexOf(se);
        edges.remove(index);
        simpleEdges.remove(index);
    }
    
    /**
     * Removes the edge at the given index from the path.
     * @param index The index of the edge to be removed
     */
    public void removeEdge(int index) {
        edges.remove(index);
        simpleEdges.remove(index);
    }
    
    /**
     * Removes the given vertex from the path (if the vertex exists);
     * @param v 
     */
    public void removeVertex(Vertex v) {
        //loop through the path's edges
        for (int i = 0; i < edges.size(); i++) {
            //get the next edge
            Edge e = edges.get(i);
            //if e.endpoint1 equals v
            if (e.getEndpoint1().equals(v)) {
                //remove the current edge from the path
                this.removeEdge(i);
                //exit the function because our job is done
                return;
            }
            //if e.endpoint2 equals v
            if (e.getEndpoint2().equals(v)) {
                //remove the current edge from the path
                this.removeEdge(i);
                //exit the function because our job is done
                return;
            }
        }
    }
    
    public void draw(Graphics2D g2) {
        for (Edge e : edges) {
            this.drawEdge(g2, e);
            this.drawVertex(g2, e.getEndpoint1());
            this.drawVertex(g2, e.getEndpoint2());
        }
    }
    
    /**
     * A private helper method that draws an "edge" manually (rather than using
     * the {@link Edge.draw(Graphics2D)} method) using path colors and stroke
     * widths.
     * @param g2
     * @param edge 
     */
    private void drawEdge(Graphics2D g2, Edge edge) {
        g2.setStroke(new BasicStroke(Values.PATH_EDGE_STROKE_WIDTH));
        
        g2.setColor(Values.PATH_EDGE_STROKE_COLOR);
        
        //Convert the center points of the two endpoints to ints
        int x1 = (int) edge.getEndpoint1().getCenter().getX();
        int y1 = (int) edge.getEndpoint1().getCenter().getY();
        int x2 = (int) edge.getEndpoint2().getCenter().getX();
        int y2 = (int) edge.getEndpoint2().getCenter().getY();
        double ctrlX = edge.getCtrlPoint().x;
        double ctrlY = edge.getCtrlPoint().y;
        //Define a new quad curve from the endpoints and the control point:
        QuadCurve2D qCurve = new QuadCurve2D.Double(); //instantiate a curve
        qCurve.setCurve(x1, y1, ctrlX, ctrlY, x2, y2); //assign the values
        g2.draw(qCurve); //draw the curve
    }
    
    /**
     * A private helper method that draws a "vertex" manually (rather than using
     * the {@link Vertex.draw(Graphics2D)} method) using path colors and stroke
     * widths. 
     * @param g2
     * @param vertex The vertex to draw
     */
    private void drawVertex(Graphics2D g2, Vertex vertex) {
        AffineTransform t = g2.getTransform(); // save the transform settings
        double x = vertex.getLocation().x;
        double y = vertex.getLocation().y;
        g2.translate(x, y); //translate the canvas
        
        //Actually draw the vertex:
        //set the stroke width
        g2.setStroke(new BasicStroke(Values.PATH_VERTEX_STROKE_WIDTH));
        //Draw fill circle:
        g2.setColor(Values.PATH_VERTEX_FILL_COLOR); //set the circle's color
        //initialize the shape object
        Ellipse2D.Double ellipse = new Ellipse2D.Double(0, 0, 
                Values.DIAMETER, Values.DIAMETER);
        g2.fill(ellipse); //fill in the circle in that color
        //Draw outline circle:
        g2.setColor(Values.PATH_VERTEX_STROKE_COLOR); //set the circle's color
        g2.draw(ellipse); //draw the outline in that color
        
        g2.setTransform(t); //restore each after drawing
    }
    
    @Override
    public String toString() {
        StringBuilder strB = new StringBuilder();
        strB.append("{");
        for (int i = 0; i < edges.size(); i++) {
            strB.append("{");
            strB.append(edges.get(i).toString());
            strB.append("}");
            if (i < edges.size() - 1) { //if this is not the last one
                strB.append(",");
            }
        }
        strB.append("}");
        return strB.toString();
    }
}
