/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import element.Vertex;
import javax.swing.JOptionPane;
import controller.Values.States;

/**
 *
 * @author Scott
 */
public class GraphStateMachine {
    
    States state = States.SELECTION;
    
    public GraphStateMachine() {
        
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
     * Used to enter the state in which a user can add vertices to the canvas by
     * clicking anywhere as many times as they want.
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
        //highlight all of the vertexes to provide a visual cue that the user is supposed
        //to click one to add the edge

        //Update vertex selection
        verticesList.clearSelection(); //clear the visual selection in the JList
        //deselect the vertex
        selectedVertexIndices.clear();
        updateSelectedVertices();

        //Update edge selection
        //if selectedEdgeIndeces is not empty
        if (!selectedEdgeIndices.isEmpty()) {
            //find the last selected index
            int lastIndex = selectedEdgeIndices.get(selectedEdgeIndices.size() - 1);
            //set the editingEdge to the last selected edge
            editingEdge = selectedEdges.get(selectedEdges.size() - 1);
            canvas.setEditingEdge(editingEdge);
            //set the last index to be the only one selected
            edgesList.setSelectedIndex(lastIndex);
            //deselect all edges
            selectedEdgeIndices.clear();
            //add the last index
            selectedEdgeIndices.add(lastIndex);
            updateSelectedEdges();
        }

        //Assign the canAddEdges values of all the vertices and get the number of vertices
        //that can't have edges added to them
        int numberOfFalses = assignCanAddEdges();

        //Highglight appropriate vertices
        highlightAvailableVertices();
    }

    private void exitAddEdgesState() {
        setSelectedEdges(false);
        firstSelectedVertex = null; //prepare for the next edge
        canvas.setFirstSelectedVertex(null);
        //Unhighlight all vertices
        for (Vertex v : vertices) {
            unHighlightVertex(v);
        }
        //set the editingEdge to null
        editingEdge = null;
        canvas.setEditingEdge(null);
        //in case the user was holding down the mouse when they switched states
        movingControlPoint = false;
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
    
    //SUBMARK: State ActionListeners
    /**
     * The code that runs in both the selectionButton and the 
     * selectionMenuItem
     */
    private void selection() {
        enterState(States.SELECTION);
        canvas.repaint();
    }

    /**
     * The code that runs in both the addPathsButton and the
     * addPathsMenuItem
     */
    private void addPaths() {
        enterState(States.PATH_ADDING);
    }

    /**
     * The code that runs in both the addVerticesButton and the
     * addVerticesMenuItem
     */
    private void addVertices() {
        enterState(States.VERTEX_ADDING);
        canvas.repaint();
    }
    
    /**
     * The code that runs in both the addEdgesButton and the 
     * addEdgesMenuItem
     */
    private void addEdges() {
        if (vertices == null) {
            JOptionPane.showMessageDialog(frame, "Need at least two vertices to add an edge.");
            setSelectedEdges(false);
            isCommandPressed = false; //unpress command
            return;
        }
        if (vertices.isEmpty() || vertices.size() == 1) {
            JOptionPane.showMessageDialog(frame, "Need at least two vertices to add an edge.");
            setSelectedEdges(false);
            isCommandPressed = false; //unpress command
            return;
        }
        enterState(States.EDGE_ADDING);
        canvas.repaint();
    }
    
}
