/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import element.Vertex;
import javax.swing.JOptionPane;
import controller.Values.States;
import element.Edge;
import element.Graph;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JToggleButton;
import views.Canvas;
import views.GraphFrame;

/**
 *
 * @author Scott
 */
public class GraphStateMachine {
    
    /**
     * An integer used to keep track of which state the user is in.
     * VERTEX_ADDING - the user is in the vertex adding state.
     * EDGE_ADDING - the user is in the edge adding state.
     * SELECTION - the user is in the selection state.
     * PATHADDING - the user is in the path adding state.
     */
    private States state = States.SELECTION;
    private final JToggleButton addVerticesButton;
    private final JToggleButton addEdgesButton;
    private final JToggleButton selectionButton;
    private final JToggleButton addPathsButton;
    private final JCheckBoxMenuItem addVerticesMenuItem;
    private final JCheckBoxMenuItem addEdgesMenuItem;
    private final JCheckBoxMenuItem selectionMenuItem;
    private final JCheckBoxMenuItem addPathsMenuItem;
    private final GraphSelectionHandler graphSelectionHandeler;
    private final Canvas canvas;
    private final Graph graph;
    
    public GraphStateMachine(GraphFrame frame,
            Graph graph,
            Canvas canvas, 
            List<Vertex> vertices, 
            GraphSelectionHandler graphSelectionHandeler) {
        addVerticesButton = frame.getAddVerticesButton();
        addEdgesButton = frame.getAddEdgesButton();
        selectionButton = frame.getSelectionButton();
        addPathsButton = frame.getAddPathsButton();
        addVerticesMenuItem = frame.getAddVerticesMenuItem();
        addEdgesMenuItem = frame.getAddEdgesMenuItem();
        selectionMenuItem = frame.getSelectionMenuItem();
        addPathsMenuItem = frame.getAddPathsMenuItem();
        this.graphSelectionHandeler = graphSelectionHandeler;
        this.canvas = canvas;
        this.graph = graph;
        
        //Add vertices
        ActionListener addVertices = (ActionEvent e) -> {
            enterState(States.VERTEX_ADDING);
            canvas.repaint();
        };
        addVerticesButton.addActionListener(addVertices);
        addVerticesMenuItem.addActionListener(addVertices);

        //Selection
        ActionListener selection = (ActionEvent e) -> {
            enterState(States.SELECTION);
            canvas.repaint();
        };
        selectionButton.addActionListener(selection);
        selectionMenuItem.addActionListener(selection);

        //Add edges
        ActionListener addEdges = (ActionEvent e) -> {
            if (vertices == null) {
                JOptionPane.showMessageDialog(frame, "Need at least two vertices "
                        + "to add an edge.");
                setSelectedEdges(false);
//                isCommandPressed = false; //unpress command
                return;
            }
            if (vertices.isEmpty() || vertices.size() == 1) {
                JOptionPane.showMessageDialog(frame, "Need at least two vertices "
                        + "to add an edge.");
                setSelectedEdges(false);
//                isCommandPressed = false; //unpress command
                return;
            }
            enterState(States.EDGE_ADDING);
            canvas.repaint();
        };
        addEdgesButton.addActionListener(addEdges);
        addEdgesMenuItem.addActionListener(addEdges);
        
        //Add paths
        ActionListener addPaths = (ActionEvent e) -> {
            element.GPath debugPath = new element.GPath();
            debugPath.addEdge(graph.getEdges().get(0));
            debugPath.addEdge(graph.getEdges().get(1));
            graph.getPaths().add(debugPath);
            System.out.println("debugPath: " + debugPath);
            
            enterState(States.PATH_ADDING);
            //if no path is selected
            if (graphSelectionHandeler.getSelectedPath() == null) {
                //if there is at least one graph
                if (!graph.getPaths().isEmpty()) {
                    //set the selected path to the first element in the list
                    graphSelectionHandeler.setSelectedPath(graph.getPaths().get(0));
                }
            }
            canvas.repaint();
        };
        addPathsButton.addActionListener(addPaths);
        addPathsMenuItem.addActionListener(addPaths);
    }
    
    int debugCount = 0;
    /**
     * Exit the current state and enter the given state
     * @param newState The state to enter
     */
    public void enterState(States newState) {
        debugCount += 1;
        System.out.print("Switch states: ");
        //Exit the old state
        switch (state) {
            case VERTEX_ADDING:
                exitAddVerticesState();
                System.out.print("exitAddVertices -> ");
                break;
            case EDGE_ADDING:
                exitAddEdgesState();
                System.out.print("exitAddEdges -> ");
                break;
            case SELECTION:
                exitSelectionState();
                System.out.print("exitSelection -> ");
                break;
            case PATH_ADDING:
                exitAddPathsState();
                System.out.print("exitAddPaths -> ");
                break;
            default:
        }
        //Enter the new state
        switch (newState) {
            case VERTEX_ADDING:
                enterAddVerticesState();
                System.out.print("enterAddVertices");
                break;
            case EDGE_ADDING:
                enterAddEdgesState();
                System.out.print("enterAddEdges");
                break;
            case SELECTION:
                enterSelectionState();
                System.out.print("enterSelection");
                break;
            case PATH_ADDING:
                enterAddPathsState();
                System.out.print("enterAddPaths");
                break;
            default:
        }
        System.out.println("(" + debugCount + ")");
    }
    
    //SUBMARK: Enter/Exit States
    private void enterSelectionState() {
        setSelectedSelection(true);
        state = States.SELECTION;
    }

    private void exitSelectionState() {
        setSelectedSelection(false);
    }
    
    /**
     * Used to enter the state in which a user can add vertices to the canvas
     * by clicking anywhere as many times as they want.
     */
    private void enterAddVerticesState() {
        setSelectedVertices(true);
        state = States.VERTEX_ADDING; //enter the vertex adding state
    }
    
    /**
     * Used to exit the state in which a user can add vertices to the canvas by
     * clicking anywhere. Called when the user enters selection state or add
     * edge state.
     */
    private void exitAddVerticesState() {
        setSelectedVertices(false);
    }
    
    private void enterAddEdgesState() {
        setSelectedEdges(true);

        state = States.EDGE_ADDING; //enter the edge adding state
        //highlight all of the vertexes to provide a visual cue that the user
        //is supposed to click one to add the edge

        //Update vertex selection
        graphSelectionHandeler.getVerticesList().clearSelection(); //clear the visual selection in the JList
        //deselect the vertex
        graphSelectionHandeler.getSelectedVertexIndices().clear();
        graphSelectionHandeler.updateSelectedVertices();

        //Update edge selection
        //if selectedEdgeIndeces is not empty
        if (!graphSelectionHandeler.getSelectedEdgeIndices().isEmpty()) {
            //get the last index of selectedEdgeIndices
            int sEISize = graphSelectionHandeler.getSelectedEdgeIndices().size();
            //find the last selected index
            int lastIndex = graphSelectionHandeler.getSelectedEdgeIndices().get(sEISize - 1);
            //get the size of the selectedEdges list
            int size = graphSelectionHandeler.getSelectedEdges().size();
            //set the editingEdge to the last selected edge
            Edge editingEdge = graphSelectionHandeler.getSelectedEdges().get(size - 1);
            //update canvas's editing edge with this
            canvas.setEditingEdge(editingEdge);
            //set the last index to be the only one selected
            graphSelectionHandeler.getEdgesList().setSelectedIndex(lastIndex);
            //deselect all edges
            graphSelectionHandeler.getSelectedEdgeIndices().clear();
            //add the last index
            graphSelectionHandeler.getSelectedEdgeIndices().add(lastIndex);
            graphSelectionHandeler.updateSelectedEdges();
        }

        //Assign the canAddEdges values of all the vertices and get the number
        //of vertices that can't have edges added to them
        int numberOfFalses = assignCanAddEdges();

        //Highglight appropriate vertices
        graph.highlightAvailableVertices();
    }

    private void exitAddEdgesState() {
        setSelectedEdges(false);
        graph.setFirstSelectedVertex(null); //prepare for the next edge
        //Unhighlight all vertices
        for (Vertex v : graph.getVertices()) {
            graph.unHighlightVertex(v);
        }
        //set the editingEdge to null
        canvas.setEditingEdge(null);
        //in case the user was holding down the mouse when they switched states
        canvas.setMovingControlPoint(false);
    }
    
    private void enterAddPathsState() {
        setSelectedPaths(true);
        state = States.PATH_ADDING;
    }
    
    private void exitAddPathsState() {
        setSelectedPaths(false);
    }
    
    //SUBMARK: Set Selected Mode
    private void setSelectedVertices(boolean selected) {
        addVerticesButton.setSelected(selected);
        addVerticesMenuItem.setSelected(selected);
    }

    private void setSelectedEdges(boolean selected) {
        addEdgesButton.setSelected(selected);
        addEdgesMenuItem.setSelected(selected);
    }

    private void setSelectedSelection(boolean selected) {
        selectionButton.setSelected(selected);
        selectionMenuItem.setSelected(selected);
    }
    
    private void setSelectedPaths(boolean selected) {
        addPathsButton.setSelected(selected);
        addPathsMenuItem.setSelected(selected);
    }
    
    /**
     * Determines whether all vertices are available to add edges to (and
     * assigns their canAddEdges value) when the user enters the addEdgeState.
     * (A vertex is available if its degree is less than (order-1), where order
     * is the number of vertices in the graph.
     *
     * @return The number of vertexes that were assigned a canAddEdges value of
     * false
     */
    private int assignCanAddEdges() {
        int numberOfFalses = 0;
        for (Vertex v : graph.getVertices()) {
            //if this vertex is available to add edges to:
            if (v.getDegree() < graph.getVertices().size() - 1) {
                v.setCanAddEdges(true);
            } else { //if this vertex is completely full
                v.setCanAddEdges(false);
                numberOfFalses++; //increment the number of falses
            }
        }
        return numberOfFalses;
    }

    public States getState() {
        return state;
    }
    
}
