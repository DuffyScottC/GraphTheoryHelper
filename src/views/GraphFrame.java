/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package views;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author Scott
 */
public class GraphFrame extends javax.swing.JFrame {
    
    /**
     * Creates new form GraphFrame
     */
    public GraphFrame() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        canvasScrollPane = new javax.swing.JScrollPane();
        canvas = new views.Canvas();
        inspectorPanel = new javax.swing.JPanel();
        propertiesPanel = new javax.swing.JPanel();
        JLabel3 = new javax.swing.JLabel();
        titleTextField = new javax.swing.JTextField();
        verticesLabel = new javax.swing.JLabel();
        verticesScrollPane = new javax.swing.JScrollPane();
        verticesList = new javax.swing.JList<>();
        verticesButtonPanel = new javax.swing.JPanel();
        addVertexButton = new javax.swing.JButton();
        removeVertexButton = new javax.swing.JButton();
        edgesButtonPanel = new javax.swing.JPanel();
        addEdgeButton = new javax.swing.JButton();
        removeEdgeButton = new javax.swing.JButton();
        edgesLabel = new javax.swing.JLabel();
        edgesScrollPane = new javax.swing.JScrollPane();
        edgesList = new javax.swing.JList<>();
        statusTextField = new javax.swing.JTextField();
        myMenuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        openMenuItem = new javax.swing.JMenuItem();
        saveMenuItem = new javax.swing.JMenuItem();
        saveAsMenuItem = new javax.swing.JMenuItem();
        newMenuItem = new javax.swing.JMenuItem();
        loadSamplesMenuItem = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        rotate90MenuItem = new javax.swing.JMenuItem();
        viewMenu = new javax.swing.JMenu();
        showVertexNamesMenuItem = new javax.swing.JCheckBoxMenuItem();
        formatVertexMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        canvas.setEditable(false);
        canvas.setColumns(20);
        canvas.setRows(5);
        canvasScrollPane.setViewportView(canvas);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        getContentPane().add(canvasScrollPane, gridBagConstraints);

        inspectorPanel.setLayout(new java.awt.GridBagLayout());

        JLabel3.setText("Title:");
        propertiesPanel.add(JLabel3);

        titleTextField.setEditable(false);
        titleTextField.setColumns(5);
        propertiesPanel.add(titleTextField);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        inspectorPanel.add(propertiesPanel, gridBagConstraints);

        verticesLabel.setText("Vertices:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        inspectorPanel.add(verticesLabel, gridBagConstraints);

        verticesList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        verticesScrollPane.setViewportView(verticesList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        inspectorPanel.add(verticesScrollPane, gridBagConstraints);

        verticesButtonPanel.setLayout(new javax.swing.BoxLayout(verticesButtonPanel, javax.swing.BoxLayout.LINE_AXIS));

        addVertexButton.setText("+");
        verticesButtonPanel.add(addVertexButton);

        removeVertexButton.setText("-");
        verticesButtonPanel.add(removeVertexButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        inspectorPanel.add(verticesButtonPanel, gridBagConstraints);

        edgesButtonPanel.setLayout(new javax.swing.BoxLayout(edgesButtonPanel, javax.swing.BoxLayout.LINE_AXIS));

        addEdgeButton.setText("+");
        edgesButtonPanel.add(addEdgeButton);

        removeEdgeButton.setText("-");
        edgesButtonPanel.add(removeEdgeButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        inspectorPanel.add(edgesButtonPanel, gridBagConstraints);

        edgesLabel.setText("Edges:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        inspectorPanel.add(edgesLabel, gridBagConstraints);

        edgesList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        edgesScrollPane.setViewportView(edgesList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        inspectorPanel.add(edgesScrollPane, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        getContentPane().add(inspectorPanel, gridBagConstraints);

        statusTextField.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        getContentPane().add(statusTextField, gridBagConstraints);

        fileMenu.setText("File");

        openMenuItem.setText("Open");
        fileMenu.add(openMenuItem);

        saveMenuItem.setText("Save");
        saveMenuItem.setEnabled(false);
        fileMenu.add(saveMenuItem);

        saveAsMenuItem.setText("Save As");
        fileMenu.add(saveAsMenuItem);

        newMenuItem.setText("New");
        fileMenu.add(newMenuItem);

        loadSamplesMenuItem.setText("Load Samples");
        fileMenu.add(loadSamplesMenuItem);

        myMenuBar.add(fileMenu);

        editMenu.setText("Edit");

        rotate90MenuItem.setText("Rotate 90º");
        rotate90MenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rotate90MenuItemActionPerformed(evt);
            }
        });
        editMenu.add(rotate90MenuItem);

        myMenuBar.add(editMenu);

        viewMenu.setText("View");

        showVertexNamesMenuItem.setText("Show Vertex Names");
        viewMenu.add(showVertexNamesMenuItem);

        formatVertexMenuItem.setText("Format Vertex");
        viewMenu.add(formatVertexMenuItem);

        myMenuBar.add(viewMenu);

        setJMenuBar(myMenuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void rotate90MenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rotate90MenuItemActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rotate90MenuItemActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel JLabel3;
    private javax.swing.JButton addEdgeButton;
    private javax.swing.JButton addVertexButton;
    private views.Canvas canvas;
    private javax.swing.JScrollPane canvasScrollPane;
    private javax.swing.JPanel edgesButtonPanel;
    private javax.swing.JLabel edgesLabel;
    private javax.swing.JList<String> edgesList;
    private javax.swing.JScrollPane edgesScrollPane;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenuItem formatVertexMenuItem;
    private javax.swing.JPanel inspectorPanel;
    private javax.swing.JMenuItem loadSamplesMenuItem;
    private javax.swing.JMenuBar myMenuBar;
    private javax.swing.JMenuItem newMenuItem;
    private javax.swing.JMenuItem openMenuItem;
    private javax.swing.JPanel propertiesPanel;
    private javax.swing.JButton removeEdgeButton;
    private javax.swing.JButton removeVertexButton;
    private javax.swing.JMenuItem rotate90MenuItem;
    private javax.swing.JMenuItem saveAsMenuItem;
    private javax.swing.JMenuItem saveMenuItem;
    private javax.swing.JCheckBoxMenuItem showVertexNamesMenuItem;
    private javax.swing.JTextField statusTextField;
    private javax.swing.JTextField titleTextField;
    private javax.swing.JPanel verticesButtonPanel;
    private javax.swing.JLabel verticesLabel;
    private javax.swing.JList<String> verticesList;
    private javax.swing.JScrollPane verticesScrollPane;
    private javax.swing.JMenu viewMenu;
    // End of variables declaration//GEN-END:variables

    public JButton getAddEdgeButton() {
        return addEdgeButton;
    }

    public JButton getAddVertexButton() {
        return addVertexButton;
    }

    public Canvas getCanvas() {
        //This already is a canvas object. Not certain why I should cast it like my
        //prof did in his example, so I'm not going to.
        //return (Canvas) canvas;
        return canvas;
    }

    public JList<String> getEdgesList() {
        return edgesList;
    }

    public JMenuItem getNewMenuItem() {
        return newMenuItem;
    }

    public JMenuItem getOpenMenuItem() {
        return openMenuItem;
    }

    public JButton getRemoveEdgeButton() {
        return removeEdgeButton;
    }

    public JButton getRemoveVertexButton() {
        return removeVertexButton;
    }
    
    public JMenuItem getRotate90MenuItem() {
        return rotate90MenuItem;
    }

    public JMenuItem getSaveAsMenuItem() {
        return saveAsMenuItem;
    }

    public JMenuItem getSaveMenuItem() {
        return saveMenuItem;
    }

    public JTextField getTitleTextField() {
        return titleTextField;
    }

    public JList<String> getVerticesList() {
        return verticesList;
    }
    
    public JMenuItem getLoadSamplesMenuItem() {
        return loadSamplesMenuItem;
    }
    
    public JPanel getInspectorPanel() {
        return inspectorPanel;
    }
    
    public JMenuItem getShowVertexNamesMenuItem() {
        return showVertexNamesMenuItem;
    }
    
    public JTextField getStatusTextField() {
        return statusTextField;
    }
    
    public JMenu getFileMenu() {
        return fileMenu;
    }
    
    public JMenuBar getMyMenuBar() {
        return myMenuBar;
    }
}
