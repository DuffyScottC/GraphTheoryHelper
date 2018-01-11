/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import element.Edge;
import element.Graph;
import element.Vertex;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.DefaultListModel;
import views.AddGraphDialog;
import views.Canvas;
import views.GraphFrame;

/**
 *
 * @author Scott
 */
public class Values {
    //MARK: Edge properties
    public static final float EDGE_STROKE_WIDTH = 2.5f;
    public static final float EDGE_HIGHLIGHT_STROKE_WIDTH = 3.5f;
    public static final Color EDGE_STROKE_COLOR = Color.black;
    public static final Color EDGE_HIGHLIGHT_COLOR = Color.green;
    public static final Color EDGE_PRESSED_COLOR = EDGE_HIGHLIGHT_COLOR.darker();//new Color(0, 200, 0);
    
    //MARK: Vertex properties
    public static final float VERTEX_STROKE_WIDTH = 1.0f;
    public static final float VERTEX_HIGHLIGHT_STROKE_WIDTH = 2.0f;
    public static final Color VERTEX_FILL_COLOR = Color.red;
    public static final Color VERTEX_STROKE_COLOR = Color.black;
    public static final Color VERTEX_PRESSED_COLOR = VERTEX_FILL_COLOR.darker();//new Color(200, 0, 0);
    public static final Color VERTEX_AVAILABLE_STROKE_COLOR = EDGE_HIGHLIGHT_COLOR;
    
    //MARK: Misc properties
    public static final int DIAMETER = 15;
    public static final double FORMAT_RADIUS = 150;
    public static final double LINE_SELECTION_DISTANCE = 4;
    public static final float SELECTION_STROKE = 2.0f;
    private static final int SELECTION_TRANSPARENCY = 80;
    public static final Color SELECTION_STROKE_COLOR = new Color(125, 125, 125, SELECTION_TRANSPARENCY);
    public static final Color SELECTION_FILL_COLOR = new Color(192, 192, 192, SELECTION_TRANSPARENCY);
}
