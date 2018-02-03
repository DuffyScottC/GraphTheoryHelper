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
    //MARK: Edge properties
    public static final float EDGE_STROKE_WIDTH = 2.5f;
    public static final float EDGE_HIGHLIGHT_STROKE_WIDTH = 3.5f;
    public static final double EDGE_SELECTION_DISTANCE = 3.5f;
    public static final Color EDGE_STROKE_COLOR = Color.black;
    public static final Color EDGE_HIGHLIGHT_COLOR = Color.green;
    public static final Color EDGE_PRESSED_COLOR = EDGE_HIGHLIGHT_COLOR.darker();//new Color(0, 200, 0);
    public static final Color EDGE_CTRL_POINT_COLOR = Color.blue;
    public static final int EDGE_CTRL_POINT_DIMESION = 7;
    
    //MARK: Vertex properties
    public static final float VERTEX_STROKE_WIDTH = 1.0f;
    public static final float VERTEX_HIGHLIGHT_STROKE_WIDTH = 2.0f;
    public static final Color VERTEX_FILL_COLOR = Color.red;
    public static final Color VERTEX_STROKE_COLOR = Color.black;
    public static final Color VERTEX_PRESSED_COLOR = VERTEX_FILL_COLOR.darker();//new Color(200, 0, 0);
    public static final Color VERTEX_AVAILABLE_STROKE_COLOR = EDGE_HIGHLIGHT_COLOR;
    
    //MARK: Path properties
    public static final float PATH_EDGE_STROKE_WIDTH = 3.0f;
    public static final float PATH_VERTEX_STROKE_WIDTH = 1.5f;
    public static final Color PATH_STROKE_COLOR = Color.orange;
    public static final Color PATH_VERTEX_FILL_COLOR = Color.orange.brighter();
    public static final Color PATH_VERTEX_STROKE_COLOR = Color.orange;
    
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
            case PATH_ADDING:
                break;
            default:
        }
    */
    public static enum States {
        VERTEX_ADDING,
        EDGE_ADDING,
        SELECTION,
        PATH_ADDING
    }
    
}
