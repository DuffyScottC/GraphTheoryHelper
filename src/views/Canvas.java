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
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author Scott
 */
public class Canvas extends JTextArea {

    private Graph graph = null;
    private JTextField graphOutputTextField;
    /**
     * True if we are in the edge adding state
     */
    private boolean showTitles = false;
    
    private List<Vertex> vertices;
    private List<Edge> edges;
    
    /**
     * used for drawing a live edge
     */
    Vertex firstSelectedVertex = null;
    Vertex liveVertex;
    
    //used for calculating the position of the endpoint of a live edge
    int lastX;
    int lastY;
    
    public void setGraph(Graph graph) {
        this.graph = graph;
        this.vertices = graph.getVertices();
        this.edges = graph.getEdges();
        
        //initialize the live vertex
        liveVertex = new Vertex("", Values.DIAMETER);
        liveVertex.setFillColor(Values.VERTEX_FILL_COLOR);
        liveVertex.setStrokeColor(Values.VERTEX_STROKE_COLOR);
        liveVertex.setStrokeWidth(Values.VERTEX_STROKE_WIDTH);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (graph == null) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );
        
        drawLiveEdge(g2); //used for when we're adding an edge
        drawEdges(g2);
        drawVertices(g2);
        drawLiveVertex(g2); //used for when we're adding a vertex
        
        graphOutputTextField.setText(graph.toString());
        
    }
    
    public void drawVertices(Graphics2D g2) {
        if (vertices == null) {
            return;
        }

        AffineTransform t = g2.getTransform(); // save the transform settings

        //loop from back to front so that the "top" vertext gets chosen
        //first when the user clicks on it.
        for (Vertex vertex : vertices) {
            double x = vertex.getLocation().x;
            double y = vertex.getLocation().y;
            g2.translate(x, y);
            vertex.draw(g2); //actually draw the vertex
            g2.setTransform(t); //restore each after drawing

            if (showTitles) { //If the user wants to show the titles
                vertex.drawTitle(g2);
            }
        }

    }

    public void drawEdges(Graphics2D g2) {
        if (edges == null) {
            return;
        }

//        AffineTransform t = g2.getTransform(); // save the transform settings
        //loop from back to front so that the "top" edge gets chosen
        //first when the user clicks on it.
        for (Edge edge : edges) {
//            double x = edge.getLocation().x;
//            double y = edge.getLocation().y;
//            g2.translate(x, y);
            edge.draw(g2); //actually draw the edge
//            g2.setTransform(t); //restore each after drawing
        }
    }
    
    /**
     * We want to draw a vertex under the mouse
     * @param g2 
     */
    public void drawLiveVertex(Graphics2D g2) {
        AffineTransform t = g2.getTransform(); // save the transform settings
        
        liveVertex.setLocation(lastX, lastY);
        g2.translate(lastX, lastY);
        liveVertex.draw(g2);
        g2.setTransform(t); //restore each after drawing
    }
    
    /**
     * We want to draw an edge between the first vertex and the current mouse
     * position
     *
     * @param g2
     */
    public void drawLiveEdge(Graphics2D g2) {
        //If this is null, then we are not in the adding a vertex state
        //or the user has not yet selected their first vertex
        if (firstSelectedVertex == null) {
            return;
        }
        int x1 = (int) firstSelectedVertex.getCenter().getX();
        int y1 = (int) firstSelectedVertex.getCenter().getY();
        int x2 = lastX;
        int y2 = lastY;
        g2.setStroke(new BasicStroke(Values.EDGE_STROKE_WIDTH));
        g2.setColor(Color.BLACK);
        g2.drawLine(x1, y1, x2, y2); //draw the line
    }
    
    //MARK: Getters and Setters
    public void setShowTitles(boolean showTitles) {
        this.showTitles = showTitles;
    }
    
    public void setLastPosition(int lastX, int lastY) {
        this.lastX = lastX;
        this.lastY = lastY;
    }
    
    public void setFirstSelectedVertex(Vertex firstSelectedVertex) {
        this.firstSelectedVertex = firstSelectedVertex;
    }
    
    public void setGraphOutputTextField(JTextField graphOutputTextField) {
        this.graphOutputTextField = graphOutputTextField;
    }
    
}
