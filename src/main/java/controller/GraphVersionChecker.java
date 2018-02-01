/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import views.GraphFrame;
import views.NewVersionDialog;

/**
 * Checks to see whether a new update is available for the program on 
 * GitHub.com when the user opens the program.
 * @author Scott
 */
public class GraphVersionChecker {
    
    private NewVersionDialog newVersionDialog;
    
    public GraphVersionChecker(GraphFrame frame) {
        newVersionDialog = new NewVersionDialog(frame, true);

        newVersionDialog.getLinkButton().addActionListener((ActionEvent e) -> {
            try {
                Desktop.getDesktop().browse(
                        new URI("https://github.com/DuffyScottC/GraphTheoryHelper/releases/latest"));
            } catch (IOException ex) {
                System.out.println(ex.toString());
            } catch (URISyntaxException ex) {
                System.out.println(ex.toString());
            }
        });
        
        newVersionDialog.getCloseButton().addActionListener((ActionEvent e) -> {
            newVersionDialog.setVisible(false);
        });
        
        newVersionDialog.getCopyLinkButton().addActionListener((ActionEvent e) -> {
            String myString = "https://github.com/DuffyScottC/GraphTheoryHelper/releases/latest";
            StringSelection stringSelection = new StringSelection(myString);
            Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
            clpbrd.setContents(stringSelection, null);
        });
    }
    
    public void openDialog() {
        newVersionDialog.setLocationRelativeTo(null);
        newVersionDialog.setTitle("New Version Available");
        newVersionDialog.setVisible(true);
    }
    
}
