/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import controller.Values.Format;
import element.Edge;
import element.Graph;
import element.Walk;
import element.Vertex;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JList;
import javax.swing.JTextField;
import views.Canvas;
import views.GraphFrame;

/**
 *
 * @author Scott
 */
public class GraphSelectionHandler {
    
    /**
     * The selected walk, or the active walk, that the user has clicked or
     * is working on. Kept in sync with {@link Canvas.selectedWalk}
     */
    private Walk selectedWalk = null;
    /**
     * the last selected vertex in the vertices JList (Used for things like
     * setting the title text field, updating the title, changing the color,
     * etc.)
     */
    private final List<Vertex> selectedVertices = new ArrayList();
    /**
     * The last selected edge in the edges JList
     */
    private final List<Edge> selectedEdges = new ArrayList();
    /**
     * A list of the selected indices in the vertices JList. null if there are
     * no selected vertices. (Used for things like setting the title text field
     * or updating the title)
     */
    private final List<Integer> selectedVertexIndices = new ArrayList();
    /**
     * The last selected index in the edges JList
     */
    private final List<Integer> selectedEdgeIndices = new ArrayList();
    private final JList verticesList;
    private final JList edgesList;
    private final JList walksList;
    private final JTextField titleTextField;
    private final List<Vertex> vertices;
    private final List<Edge> edges;
    private final Graph graph;
    private final Canvas canvas;
    
    
    public GraphSelectionHandler(GraphFrame frame, Graph graph) {
        //the visual JList that the user sees and interacts with
        verticesList = frame.getVerticesList(); 
        edgesList = frame.getEdgesList();
        walksList = frame.getWalksList();
        titleTextField = frame.getTitleTextField();
        canvas = frame.getCanvas();
        this.graph = graph;
        vertices = graph.getVertices();
        edges = graph.getEdges();
    }
    
    /**
     * Uses selectedIndex (a member variable) to set selectedVertex, highlight
     * selected vertex, un-highlights previously selected vertex set the
     * titleTextField content, (If selectedIndex = -1, then it deselects all).
     * This also repaints the canvas.
     */
    public void updateSelectedVertices() {
        //Visually deselect the old selectedVertices
        if (!selectedVertices.isEmpty()) { //if there were previously selected vertices
            //loop through the old vertices
            for (Vertex selectedVertex : selectedVertices) {
                //unhighlight each one
                selectedVertex.setFormat(Format.NORMAL);
            }
        }

        //Programattically select the new selectedVertices (or deselect all)
        if (selectedVertexIndices.isEmpty()) { //if the user deselected all vertices
            selectedVertices.clear(); //remove all elements from selectedVertices
            titleTextField.setText("");
            titleTextField.setEditable(false);
            verticesList.clearSelection(); //unselect the vertex in the JList
        } else { //if the user selected vertices
            selectedVertices.clear(); //clear the old selected vertices
            //store the new selected vertices:
            for (int i : selectedVertexIndices) { //loop through the selected indices
                Vertex selectedVertex = vertices.get(i); //store this selected vertex
                selectedVertex.setFormat(Format.HIGHLIGHTED);
                selectedVertices.add(selectedVertex); //add the new selection
            }
            if (selectedVertices.size() == 1) { //if exactly one vertex was selected
                //Get the title and put it in the titleTextField
                titleTextField.setText(selectedVertices.get(0).getTitle());
                titleTextField.setEditable(true); //enable editing of the title
            } else { //if not exactly one vertex was selected
                titleTextField.setText(""); //empty the titleTextField
                titleTextField.setEditable(false); //disable editing of titles
            }
        }
    }
    
    public void updateSelectedEdges() {
        //Visually deselect the old selected edge
        if (!selectedEdges.isEmpty()) { //if there were previously selected edges
            //loop through the old edges
            for (Edge selectedEdge : selectedEdges) {
                //unhighlight each one
                selectedEdge.setFormat(Format.NORMAL);
            }
        }

        //Programatically and visually select the new edge (or deselect entirely)
        if (selectedEdgeIndices.isEmpty()) { //if the user deselected all edges
            selectedEdges.clear();
            edgesList.clearSelection(); //unselect the edge in the JList
        } else { //if the user selected edges
            selectedEdges.clear(); //clear the old selected edges
            //store the new selected edges
            for (int i : selectedEdgeIndices) { //loop through the selected indices
                Edge selectedEdge = edges.get(i); //store this selected edge
                selectedEdge.setFormat(Format.HIGHLIGHTED);
                selectedEdges.add(selectedEdge); //add the new selection
            }
        }
    }
    
    public void setSelectedWalk(Walk selectedWalk) {
        //assign the selectedWalk
        this.selectedWalk = selectedWalk;
        //assign canvas's selectedWalk
        canvas.setSelectedWalk(selectedWalk);
        //get the index of the selectedWalk
        int index = graph.getWalks().indexOf(selectedWalk);
        //select the walk in the JList
        walksList.setSelectedIndex(index);
    }

    public List<Edge> getSelectedEdges() {
        return selectedEdges;
    }

    public List<Integer> getSelectedEdgeIndices() {
        return selectedEdgeIndices;
    }

    public JList getVerticesList() {
        return verticesList;
    }

    public JList getEdgesList() {
        return edgesList;
    }

    public List<Integer> getSelectedVertexIndices() {
        return selectedVertexIndices;
    }
    
    public List<Vertex> getSelectedVertices() {
        return selectedVertices;
    }
    
    public Walk getSelectedWalk() {
        return selectedWalk;
    }
}
