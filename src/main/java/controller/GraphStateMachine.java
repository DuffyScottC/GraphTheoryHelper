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
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JTextField;
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
     * WALK_ADDING - the user is in the walk adding state.
     */
    private States state = States.SELECTION;
    private final JToggleButton addVerticesButton;
    private final JToggleButton addEdgesButton;
    private final JToggleButton selectionButton;
    private final JToggleButton addWalksButton;
    private final JCheckBoxMenuItem addVerticesMenuItem;
    private final JCheckBoxMenuItem addEdgesMenuItem;
    private final JCheckBoxMenuItem selectionMenuItem;
    private final JCheckBoxMenuItem addWalksMenuItem;
    private final GraphSelectionHandler graphSelectionHandler;
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
        addWalksButton = frame.getAddWalksButton();
        addVerticesMenuItem = frame.getAddVerticesMenuItem();
        addEdgesMenuItem = frame.getAddEdgesMenuItem();
        selectionMenuItem = frame.getSelectionMenuItem();
        addWalksMenuItem = frame.getAddWalksMenuItem();
        this.graphSelectionHandler = graphSelectionHandeler;
        this.canvas = canvas;
        this.graph = graph;
        
        //Add vertices
        addVerticesButton.addActionListener((ActionEvent e) -> {
            enterState(States.VERTEX_ADDING);
            canvas.repaint();
        });
        addVerticesMenuItem.addActionListener((ActionEvent e) -> {
            //first make sure we are not in a text box
            Component component = frame.getFocusOwner();
            //if we're not typing in a text box
            if (!(component instanceof JTextField)) {
                enterState(States.VERTEX_ADDING);
                canvas.repaint();
            }
            //otherwise, allow the user to type
        });

        //Selection
        selectionButton.addActionListener((ActionEvent e) -> {
            enterState(States.SELECTION);
            canvas.repaint();
        });
        selectionMenuItem.addActionListener((ActionEvent e) -> {
            //first make sure we are not in a text box
            Component component = frame.getFocusOwner();
            //if we're not typing in a text box
            if (!(component instanceof JTextField)) {
                enterState(States.SELECTION);
                canvas.repaint();
            }
        });

        //Add edges
        addEdgesButton.addActionListener((ActionEvent e) -> {
            addEdgesButtonAndMenuItemCode(frame, vertices);
        });
        addEdgesMenuItem.addActionListener((ActionEvent e) -> {
            //first make sure we are not in a text box
            Component component = frame.getFocusOwner();
            //if we're not typing in a text box
            if (!(component instanceof JTextField)) {
                addEdgesButtonAndMenuItemCode(frame, vertices);
            }
        });
        
        //Add walks
        addWalksButton.addActionListener((ActionEvent e) -> {
            enterState(States.WALK_ADDING);
            canvas.repaint();
        });
        addWalksMenuItem.addActionListener((ActionEvent e) -> {
            //first make sure we are not in a text box
            Component component = frame.getFocusOwner();
            //if we're not typing in a text box
            if (!(component instanceof JTextField)) {
                enterState(States.WALK_ADDING);
                canvas.repaint();
            }
        });
    }
    
    private void addEdgesButtonAndMenuItemCode(GraphFrame frame, List<Vertex> vertices) {
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
    }
    
    int debugCount = 0;
    /**
     * Exit the current state and enter the given state
     * @param newState The state to enter
     */
    public void enterState(States newState) {
        debugCount += 1;
        //Exit the old state
        switch (state) {
            case VERTEX_ADDING:
                exitAddVerticesState();
                break;
            case EDGE_ADDING:
                exitAddEdgesState();
                break;
            case SELECTION:
                exitSelectionState();
                break;
            case WALK_ADDING:
                exitAddWalksState();
                break;
            default:
        }
        //Enter the new state
        switch (newState) {
            case VERTEX_ADDING:
                enterAddVerticesState();
                break;
            case EDGE_ADDING:
                enterAddEdgesState();
                break;
            case SELECTION:
                enterSelectionState();
                break;
            case WALK_ADDING:
                enterAddWalksState();
                break;
            default:
        }
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
        graphSelectionHandler.getVerticesList().clearSelection(); //clear the visual selection in the JList
        //deselect the vertex
        graphSelectionHandler.getSelectedVertexIndices().clear();
        graphSelectionHandler.updateSelectedVertices();

        //Update edge selection
        //if selectedEdgeIndeces is not empty
        if (!graphSelectionHandler.getSelectedEdgeIndices().isEmpty()) {
            //get the last index of selectedEdgeIndices
            int sEISize = graphSelectionHandler.getSelectedEdgeIndices().size();
            //find the last selected index
            int lastIndex = graphSelectionHandler.getSelectedEdgeIndices().get(sEISize - 1);
            //get the size of the selectedEdges list
            int size = graphSelectionHandler.getSelectedEdges().size();
            //set the editingEdge to the last selected edge
            Edge editingEdge = graphSelectionHandler.getSelectedEdges().get(size - 1);
            //update canvas's editing edge with this
            canvas.setEditingEdge(editingEdge);
            //set the last index to be the only one selected
            graphSelectionHandler.getEdgesList().setSelectedIndex(lastIndex);
            //deselect all edges
            graphSelectionHandler.getSelectedEdgeIndices().clear();
            //add the last index
            graphSelectionHandler.getSelectedEdgeIndices().add(lastIndex);
            graphSelectionHandler.updateSelectedEdges();
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
            v.setIsHighlighted(false);
        }
        //set the editingEdge to null
        canvas.setEditingEdge(null);
        //in case the user was holding down the mouse when they switched states
        canvas.setMovingControlPoint(false);
    }
    
    private void enterAddWalksState() {
        //clear the selection
        graphSelectionHandler.clearSelection();
        setSelectedWalks(true);
        state = States.WALK_ADDING;
    }
    
    private void exitAddWalksState() {
        setSelectedWalks(false);
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
    
    private void setSelectedWalks(boolean selected) {
        addWalksButton.setSelected(selected);
        addWalksMenuItem.setSelected(selected);
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
