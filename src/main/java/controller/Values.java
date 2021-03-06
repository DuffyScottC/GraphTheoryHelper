/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.awt.Color;

/**
 *
 * @author Scott
 */
public class Values {
    //MARK: Choosable Graph Colors
    private static final Color purple = new Color(153, 0, 255);
    /**
     * Holds the colors that the user can select to format the elements in the
     * graph. It is initialized in 
     * {@link GraphController.setUpGraphColorChooserDialog} and used to
     * interpret user choices in the ComboBoxes into colors when the 
     * graphColorChooserDialog's OK button is pressed.
     */
    public static final Color[] choosableColors = { //11 colors
        Color.cyan, //0
        Color.blue, //1
        Color.blue.darker(), //2
        purple.darker(), //3
        purple, //4
        purple.brighter(), //5
        Color.pink, //6
        Color.red, //7
        Color.red.darker(), //8
        Color.black, //9
        Color.gray //10
    };
    
    //MARK: Edge properties
    public static final float EDGE_STROKE_WIDTH = 2.5f;
    public static final float EDGE_HIGHLIGHT_STROKE_WIDTH = 3.5f;
    public static final double EDGE_SELECTION_DISTANCE = 4f;
    public static final int EDGE_STROKE_COLOR_INDEX = 9;
    public static final Color EDGE_STROKE_COLOR = Color.black;
    public static final Color EDGE_HIGHLIGHT_COLOR = Color.green;
    public static final Color EDGE_PRESSED_COLOR = EDGE_HIGHLIGHT_COLOR.darker();//new Color(0, 200, 0);
    public static final Color EDGE_CTRL_POINT_COLOR = Color.blue;
    public static final int EDGE_CTRL_POINT_DIMESION = 7;
    
    //MARK: Vertex properties
    public static final float VERTEX_STROKE_WIDTH = 1.0f;
    public static final float VERTEX_HIGHLIGHT_STROKE_WIDTH = 2.0f;
    public static final int VERTEX_FILL_COLOR_INDEX = 7;
    public static final int VERTEX_STROKE_COLOR_INDEX = 9;
    public static final Color VERTEX_FILL_COLOR = Color.red;
    public static final Color VERTEX_STROKE_COLOR = Color.black;
    public static final Color VERTEX_PRESSED_COLOR = VERTEX_FILL_COLOR.darker();//new Color(200, 0, 0);
    public static final Color VERTEX_AVAILABLE_STROKE_COLOR = EDGE_HIGHLIGHT_COLOR;
    
    //MARK: Walk properties
    public static final float WALK_EDGE_STROKE_WIDTH = 3.0f;
    public static final float WALK_VERTEX_STROKE_WIDTH = 1.5f;
    public static final Color WALK_EDGE_STROKE_COLOR = Color.orange;
    public static final Color WALK_VERTEX_FILL_COLOR = Color.orange.brighter();
    public static final Color WALK_VERTEX_STROKE_COLOR = Color.orange;
    public static final Color WALK_VERTEX_PRESSED_COLOR = WALK_VERTEX_FILL_COLOR.darker();
    
    //MARK: User preference keys
    /**
     * The last file location opened when the user saved or loaded a file 
     * in the chooser dialog.
     */
    public static final String LAST_FILE_PATH = "GTH Last File Path";
    /**
     * Whether the user wants the canvas to show or hide vertex names.
     */
    public static final String SHOW_VERTEX_NAMES = "GTH Show Vertex Names";
    
    //MARK: Misc properties
    public static final int DIAMETER = 16;
    public static final double FORMAT_RADIUS = 150;
    public static final float SELECTION_STROKE = 2.0f;
    private static final int SELECTION_TRANSPARENCY = 80;
    public static final Color SELECTION_STROKE_COLOR = new Color(125, 125, 125, SELECTION_TRANSPARENCY);
    public static final Color SELECTION_FILL_COLOR = new Color(192, 192, 192, SELECTION_TRANSPARENCY);
    
    /*
        switch (state) {
            case VERTEX_ADDING:
                break;
            case EDGE_ADDING:
                break;
            case SELECTION:
                break;
            case WALK_ADDING:
                break;
            default:
        }
    */
    /**
     * The main states the entire program can be in: Vertex adding, edge adding,
     * selection, and walk adding.
     */
    public static enum States {
        VERTEX_ADDING,
        EDGE_ADDING,
        SELECTION,
        WALK_ADDING
    }
}
