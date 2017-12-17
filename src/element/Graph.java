/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package element;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import views.Canvas;
import views.GraphFrame;

/**
 *
 * @author Scott
 */
public class Graph implements Serializable {
    
    //the vertices which appear in canvas and the vertices JList
    private final List<Vertex> vertices = new ArrayList<>();
    //the edges which appear in canvas and the edges JList
    private final List<Edge> edges = new ArrayList<>();
    
    // models for vertex and edge selection lists
    private final DefaultListModel verticesListModel = new DefaultListModel();
    private final DefaultListModel edgesListModel = new DefaultListModel();
    
    private String title = "Simple Graph";
    
    //Used for moving objects. Holds the last point the mouse was at.
    private int lastX;
    private int lastY;
    
    //Used for moving objects
    private Vertex selectedVertex;
    
    public Graph (String title) {
        this.title = title;
    }
    
    public void addEventHandlers(Canvas canvas, GraphFrame frame) {
        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                //Find the topmost vertex that 
                //contains the mouse click (if any):
                
                //if vertices is null, then there are definitely no
                //edges and we can stop here. (A graph can't have
                //edges without vertices.)
                if (vertices == null) {
                    return;
                }
                
                int mx = e.getX(); //x-coord of mouse click
                int my = e.getY(); //y-coord of mouse click
                
                for (Vertex currentVertex : vertices) {
                    //if this figure contains the mouse click:
                    if (currentVertex.getPositionShape().contains(mx, my)) {
                        selectedVertex = currentVertex;
                        break; //exit the loop (we don't need to check the rest)
                    }
                }
                
                //update the last position
                lastX = mx;
                lastY = my;
                
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                selectedVertex = null; //we don'e want to move a figure after the user lets go
            }
            
        });
        
        canvas.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                
                if (selectedVertex == null) {
                    return; //we don't have a vertex to move, so just stop here
                }
                
                int mx = e.getX(); //x-coord of mouse click
                int my = e.getY(); //y-coord of mouse click

                // find the difference between the last position and the current position (used for moving the figure)
                int incX = mx - lastX;
                int incY = my - lastY;
                
                //update the last position
                lastX = mx;
                lastY = my;
                
                selectedVertex.incLocation(incX, incY);
                canvas.repaint();
                
            }
        });
        
        frame.getAddVertexButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
            }
        });
        
    }
    
    public void drawVertices(Graphics2D g2) {
        if (vertices == null) {
            return;
        }
        
        AffineTransform t = g2.getTransform(); // save the transform settings
        
        //loop from back to front so that the "top" vertext gets chosen
        //first when the user clicks on it.
        for (int i = vertices.size() - 1; i >= 0; --i) {
            Vertex vertex = vertices.get(i);
            double x = vertex.getLocation().x;
            double y = vertex.getLocation().y;
            g2.translate(x, y);
            vertex.draw(g2); //actually draw the vertex
            g2.setTransform(t); //restore each after drawing
        }
        
    }

    public void drawEdges(Graphics2D g2) {
        if (edges == null) {
            return;
        }
        
        AffineTransform t = g2.getTransform(); // save the transform settings
        
        //loop from back to front so that the "top" edge gets chosen
        //first when the user clicks on it.
        for (int i = edges.size() - 1; i >= 0; --i) {
            Edge edge = edges.get(i);
//            double x = edge.getLocation().x;
//            double y = edge.getLocation().y;
//            g2.translate(x, y);
            edge.draw(g2); //actually draw the edge
            g2.setTransform(t); //restore each after drawing
        }
        
    }
    
    public List<Vertex> getVertices() {
        return vertices;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public DefaultListModel getVerticesListModel() {
        return verticesListModel;
    }

    public DefaultListModel getEdgesListModel() {
        return edgesListModel;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
}
