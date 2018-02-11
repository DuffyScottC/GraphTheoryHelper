/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;

/**
 *
 * @author Scott
 */
public class ColorChooserComboBox extends JComboBox {
    
    public ColorChooserComboBox() {
        super();
        int width = 100;
        int height = 10;
        BufferedImage bufferedImage = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = bufferedImage.createGraphics();
        g2.setColor(Color.red);
        g2.fillRect(0, 0, width, height);
        
        Object[] items = {
            new ImageIcon(bufferedImage)
        };
        setModel(new DefaultComboBoxModel(items));
    }
    
}
