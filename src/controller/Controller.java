/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import element.Graph;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
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
    
    //File I/O:
    JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));

    public Controller() {
        frame.setTitle("Graph Helper");
        frame.setLocationRelativeTo(null);
        frame.setSize(800, 500);
        
        canvas.setGraph(graph); //pass the graph to the canvas
        graph.setCanvas(canvas); //pass the canvas to the graph

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
        
        frame.getSaveAsMenuItem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (graph.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Cannot save an empty graph.");
                    return;
                }
                
                chooser.setDialogTitle("Save");
                
                //Open the save dialogue and let the user choose 
                //where to save the file:
                int chooserResult = chooser.showSaveDialog(frame);
                //if the user successfully saved the file
                if (chooserResult == JFileChooser.APPROVE_OPTION) {
                    
                    //get the path of the file that the user selected
                    Path path = chooser.getSelectedFile().toPath();
                    
                    //check if the file has an extension already
                    String fileName = path.getFileName().toString(); //the name of the file
                    if (!fileName.matches(".*\\.\\w+")) { //if the file name has NO extension
                        //add .fig
                        String fileNameWithExtension = fileName + ".graph";
                        //use the resolveSibling method to change the old, 
                        //extensionless file name to the new filename created above
                        path = path.resolveSibling(fileNameWithExtension); 
                        //e.g. this will replace "curdir/sample2" with "curdir/sample2.graph"
                    }
                    
                    File saveFile = path.toFile(); //convert the path object to a file object
                    
                    //Save the graph
                    try {
                        //Create an output stream from the file
                        FileOutputStream ostr = new FileOutputStream(saveFile);
                        
                        ObjectOutputStream oostr = new ObjectOutputStream(ostr);
                        
                        //Save the graph
                        oostr.writeObject(graph);
                        oostr.close(); //must do this to ensure completion
                    } catch (IOException ex) {
                        
                        JOptionPane.showMessageDialog(frame, "Unable to save file.\n" + ex.getMessage(), "Oops!", JOptionPane.ERROR_MESSAGE);
                        
                    }
                    
                }
                
            }
        });
        
        frame.getOpenMenuItem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooser.setDialogTitle("Open");
                int chooserResult = chooser.showOpenDialog(frame);
                if (chooserResult == JFileChooser.APPROVE_OPTION) {
                    File loadFile = chooser.getSelectedFile();
                    
                    try {
                        //create an input stream from the selected file
                        FileInputStream  istr = new FileInputStream(loadFile); 
                        ObjectInputStream oistr = new ObjectInputStream( istr );
                        
                        //load the object from the serialized file
                        Object theObject = oistr.readObject();
                        oistr.close();
                        
                        //if this object is a graph
                        if (theObject instanceof Graph) {
                            //cast the loaded object to a graph
                            Graph loadedGraph = (Graph) theObject;
                            
                            //replace the old graph with the new one
                            graph.replace(loadedGraph);
                            
                            canvas.repaint();
                        }
                    } catch (IOException ex) {
                        
                    } catch (ClassNotFoundException ex) {
                        
                    }
                    
                }
            }
        });
        
        //Drag to move vertices:
        graph.addEventHandlers(frame);
        
    }

    public static void main(String[] args) {
        Controller app = new Controller();
        app.frame.setVisible(true);
    }
}
