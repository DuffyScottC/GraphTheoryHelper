/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package views;

import java.awt.Dimension;
import javax.swing.JPanel;

/**
 *
 * @author Scott
 */
public class InspectorPanel extends JPanel {

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(150, 300);
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

}
