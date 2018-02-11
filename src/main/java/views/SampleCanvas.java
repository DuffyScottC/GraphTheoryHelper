/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package views;

import controller.Values;
import static controller.Values.choosableColors;
import element.Edge;
import element.Graph;
import element.Vertex;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import javax.swing.JTextArea;

/**
 * Shows the user two sample vertices with a sample edge between them of the
 * colors they choose.
 * @author Scott
 */
public class SampleCanvas extends JTextArea {
    
    private Vertex v1 = new Vertex(Values.DIAMETER);
    private Vertex v2 = new Vertex(Values.DIAMETER);
    private Edge e;
    
    public void setUp(Graph graph) {
        Color vertexStrokeColor = choosableColors[graph.getVertexStrokeColorIndex()];
        Color vertexFillColor = choosableColors[graph.getVertexStrokeColorIndex()];
        
        v1 = new Vertex(Values.DIAMETER);
        v1.setLocation(50, 50);
        v1.setStrokeColor(vertexStrokeColor);
        v1.setFillColor(vertexFillColor);
        v1.setStrokeWidth(Values.VERTEX_STROKE_WIDTH);
        v1.setTitle("");
        
        v2 = new Vertex(Values.DIAMETER);
        v2.setLocation(150, 50);
        v2.setStrokeColor(vertexStrokeColor);
        v2.setFillColor(vertexFillColor);
        v2.setStrokeWidth(Values.VERTEX_STROKE_WIDTH);
        v2.setTitle("");
        
        e = new Edge(v1, v2);
        e.setStrokeWidth(Values.EDGE_STROKE_WIDTH);
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (v1 == null) {
            return;
        }
        if (v2 == null) {
            return;
        }
        
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );
        
        setVertexPositions(); //position the vertices
        
        e.draw(g2);
        drawVertex(v1, g2);
        drawVertex(v2, g2);
    }
    
    /**
     * Convenience method to improve readability. 
     * @param vertex
     * @param g2 
     */
    private void drawVertex(Vertex vertex, Graphics2D g2) {
        AffineTransform t = g2.getTransform(); // save the transform settings
        //loop from back to front so that the "top" vertext gets chosen
        //first when the user clicks on it.
        double x = vertex.getLocation().x;
        double y = vertex.getLocation().y;
        g2.translate(x, y);
        vertex.draw(g2); //actually draw the vertex
        g2.setTransform(t); //restore each after drawing
    }
    
    private void setVertexPositions() {
        //get the height and width of the canvas
        int h = this.getHeight();
        int w = this.getWidth();
        int distFromSide = 50 - Values.DIAMETER/2; //the distance of both vertices from both sides
        
        v1.setLocation(distFromSide, h/2);
        v2.setLocation(w - distFromSide, h/2);
        //straighten the edge
        //set the control point
        double ctrlX = w/2; //find the mid-x
        double ctrlY = h/2 + Values.DIAMETER/2; //find the mid-y
        e.setCtrlPoint(ctrlX, ctrlY);
    }
    
    public void setVertexFillColorIndex(int vertexFillColorIndex) {
        Color vertexFillColor = choosableColors[vertexFillColorIndex];
        v1.setFillColor(vertexFillColor);
        v2.setFillColor(vertexFillColor);
    }
    
    public void setVertexStrokeColorIndex(int vertexStrokeColorIndex) {
        Color vertexStrokeColor = choosableColors[vertexStrokeColorIndex];
        v1.setStrokeColor(vertexStrokeColor);
        v2.setStrokeColor(vertexStrokeColor);
    }
    
    public void setEdgeStrokeColorIndex(int edgeStrokeColorIndex) {
        Color edgeStrokeColor = choosableColors[edgeStrokeColorIndex];
        e.setStrokeColor(edgeStrokeColor);
    }
    
}
