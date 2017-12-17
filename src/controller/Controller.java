/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import element.Graph;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ListSelectionModel;
import views.Canvas;
import views.GraphFrame;

/**
 *
 * @author Scott
 */
public class Controller {

    private final GraphFrame frame = new GraphFrame();
    private final Canvas canvas = frame.getCanvas();

    private final Graph graph = new Graph("Simple Graph");

    public Controller() {
        frame.setTitle("Graph Helper");
        frame.setLocationRelativeTo(null);
        frame.setSize(800, 500);

        canvas.setGraph(graph); //pass the graph to the canvas

        //Set up list models:
        //set them to their respective JLists
        frame.getVerticesList().setModel(graph.getVerticesListModel());
        frame.getEdgesList().setModel(graph.getEdgesListModel());
        //set their selection modes
        frame.getVerticesList().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        frame.getEdgesList().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        //Action listeners:
        frame.getLoadSamplesMenuItem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Helpers.setSampleElements(graph);
                canvas.repaint();
            }
        });
        
        //Drag to move vertices:
        graph.addEventHandlers(canvas);
        
    }

    public static void main(String[] args) {
        Controller app = new Controller();
        app.frame.setVisible(true);
    }
}
