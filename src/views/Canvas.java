/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package views;

import element.Graph;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JTextArea;

/**
 *
 * @author Scott
 */
public class Canvas extends JTextArea {

    private Graph graph = null;
    /**
     * True if we are in the edge adding state
     */
    private boolean addingEdge = false;

    public void setGraph(Graph graph) {
        this.graph = graph;
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
        
        graph.drawEdges(g2);
        graph.drawVertices(g2);
        
        graph.drawLiveEdge(g2); //used for when we're adding an edge
        
        this.setText(graph.toString());
        
    }

}
