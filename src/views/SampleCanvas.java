/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package views;

import controller.Values;
import element.Edge;
import element.Graph;
import element.Vertex;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JTextArea;

/**
 * Shows the user two sample vertices with a sample edge between them of the
 * colors they choose.
 * @author Scott
 */
public class SampleCanvas extends JTextArea {
    
    private Vertex v1;
    private Vertex v2;
    private Edge e;
    
    public void setUp(Graph graph) {
        v1 = new Vertex(Values.DIAMETER);
        v1.setLocation(50, 50);
        v1.setStrokeColor(graph.getVertexStrokeColor());
        v1.setFillColor(graph.getVertexFillColor());
        v1.setStrokeWidth(Values.VERTEX_STROKE_WIDTH);
        v1.setTitle("");
        
        v2 = new Vertex(Values.DIAMETER);
        v2.setLocation(150, 50);
        v2.setStrokeColor(graph.getVertexStrokeColor());
        v2.setFillColor(graph.getVertexFillColor());
        v2.setStrokeWidth(Values.VERTEX_STROKE_WIDTH);
        v2.setTitle("");
        
        e = new Edge(v1, v2);
        e.setStrokeWidth(Values.EDGE_STROKE_WIDTH);
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );
        
        setVertexPositions(); //position the vertices
        
        v1.draw(g2);
        v2.draw(g2);
        e.draw(g2);
    }
    
    private void setVertexPositions() {
        //get the height and width of the canvas
        int h = this.getHeight();
        int w = this.getWidth();
        int distFromSide = 50; //the distance of both vertices from both sides
        
        v1.setLocation(distFromSide, h/2);
        v2.setLocation(w - distFromSide, h/2);
    }
    
    public void setVertexFillColor(Color vertexFillColor) {
        v1.setFillColor(vertexFillColor);
        v2.setFillColor(vertexFillColor);
    }
    
    public void setVertexStrokeColor(Color vertexStrokeColor) {
        v1.setStrokeColor(vertexStrokeColor);
        v2.setStrokeColor(vertexStrokeColor);
    }
    
    public void setEdgeStrokeColor(Color edgeStrokeColor) {
        e.setStrokeColor(edgeStrokeColor);
    }
    
}
