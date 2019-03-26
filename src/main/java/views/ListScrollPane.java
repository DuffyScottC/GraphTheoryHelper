/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package views;

import java.awt.Dimension;
import javax.swing.JScrollPane;

/**
 * Used to ensure that the list has the correct preferred size.
 * 
 * @author Scott
 */
public class ListScrollPane extends JScrollPane {
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(147, 140);
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }
}
