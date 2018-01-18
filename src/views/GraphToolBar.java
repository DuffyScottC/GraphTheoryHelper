/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package views;

import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

/**
 *
 * @author Scott
 */
public class GraphToolBar extends JToolBar {
    
    public GraphToolBar() {
        //initialize the toolbar in the vertical state
        super( VERTICAL );
        
        JButton addVerticesTool = new JButton();
        URL addVerticesIconURL = GraphFrame.class.getResource("../images/add-vertices-Icon.png");
        addVerticesTool.setIcon(new ImageIcon(addVerticesIconURL, "Add vertices"));
        this.add(addVerticesTool);
    }
}
