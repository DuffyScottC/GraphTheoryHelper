/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package views;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

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
//        addVerticesButton.setIcon(new javax.swing.ImageIcon(GraphFrame.class.getClassLoader().getResource("add-vertices-icon.png")));
//        addEdgesButton.setIcon(new javax.swing.ImageIcon(GraphFrame.class.getClassLoader().getResource("add-edges-icon.png")));
//        selectionButton.setIcon(new javax.swing.ImageIcon(GraphFrame.class.getClassLoader().getResource("selection-icon.png")));
//        addWalksButton.setIcon(new javax.swing.ImageIcon(GraphFrame.class.getClassLoader().getResource("add-walks-icon.png")));
//        deleteButton.setIcon(new javax.swing.ImageIcon(GraphFrame.class.getClassLoader().getResource("delete-icon.png")));
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

        mainPanel = new javax.swing.JPanel();
        graphOutputPanel = new javax.swing.JPanel();
        graphOutputTextField = new javax.swing.JTextField();
        modifiedTextField = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        canvasTextArea = new Canvas();
        inspectorPanelJPanel = new InspectorPanel();
        edgesLabel = new javax.swing.JLabel();
        verticesLabel = new javax.swing.JLabel();
        propertiesPanel = new javax.swing.JPanel();
        JLabel3 = new javax.swing.JLabel();
        titleTextField = new javax.swing.JTextField();
        walksLabel = new javax.swing.JLabel();
        walksScrollPane = new javax.swing.JScrollPane();
        walksList = new javax.swing.JList<>();
        hiddenCheckBox = new javax.swing.JCheckBox();
        jScrollPane2 = new javax.swing.JScrollPane();
        verticesList = new javax.swing.JList<>();
        jScrollPane3 = new javax.swing.JScrollPane();
        edgesList = new javax.swing.JList<>();
        toolBar = new javax.swing.JToolBar();
        addVerticesButton = new javax.swing.JToggleButton();
        addEdgesButton = new javax.swing.JToggleButton();
        selectionButton = new javax.swing.JToggleButton();
        addWalksButton = new javax.swing.JToggleButton();
        deleteButton = new javax.swing.JButton();
        myMenuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        openMenuItem = new javax.swing.JMenuItem();
        saveMenuItem = new javax.swing.JMenuItem();
        saveAsMenuItem = new javax.swing.JMenuItem();
        newMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        exportMenuItem = new javax.swing.JMenuItem();
        viewMenu = new javax.swing.JMenu();
        showVertexNamesMenuItem = new javax.swing.JCheckBoxMenuItem();
        selectAllVerticesMenuItem = new javax.swing.JMenuItem();
        formatVerticesMenuItem = new javax.swing.JMenuItem();
        toolsMenu = new javax.swing.JMenu();
        addVerticesMenuItem = new javax.swing.JCheckBoxMenuItem();
        addEdgesMenuItem = new javax.swing.JCheckBoxMenuItem();
        selectionMenuItem = new javax.swing.JCheckBoxMenuItem();
        addWalksMenuItem = new javax.swing.JCheckBoxMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        deleteMenuItem = new javax.swing.JMenuItem();
        changeColorsMenuItem = new javax.swing.JMenuItem();
        addGraphMenuItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        checkForUpdatesMenuItem = new javax.swing.JMenuItem();
        tutorialMenuItem = new javax.swing.JMenuItem();
        donateMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        mainPanel.setLayout(new java.awt.GridBagLayout());

        graphOutputPanel.setLayout(new java.awt.GridBagLayout());

        graphOutputTextField.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        graphOutputPanel.add(graphOutputTextField, gridBagConstraints);

        modifiedTextField.setEditable(false);
        modifiedTextField.setColumns(1);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        graphOutputPanel.add(modifiedTextField, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        mainPanel.add(graphOutputPanel, gridBagConstraints);

        canvasTextArea.setEditable(false);
        canvasTextArea.setColumns(20);
        canvasTextArea.setRows(5);
        jScrollPane1.setViewportView(canvasTextArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        mainPanel.add(jScrollPane1, gridBagConstraints);

        inspectorPanelJPanel.setLayout(new java.awt.GridBagLayout());

        edgesLabel.setText("Edges:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        inspectorPanelJPanel.add(edgesLabel, gridBagConstraints);

        verticesLabel.setText("Vertices:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        inspectorPanelJPanel.add(verticesLabel, gridBagConstraints);

        JLabel3.setText("Title:");
        propertiesPanel.add(JLabel3);

        titleTextField.setEditable(false);
        titleTextField.setColumns(5);
        propertiesPanel.add(titleTextField);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        inspectorPanelJPanel.add(propertiesPanel, gridBagConstraints);

        walksLabel.setText("Walks:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        inspectorPanelJPanel.add(walksLabel, gridBagConstraints);

        walksList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        walksList.setMaximumSize(new java.awt.Dimension(138, 85));
        walksList.setMinimumSize(new java.awt.Dimension(138, 85));
        walksList.setPreferredSize(new java.awt.Dimension(138, 85));
        walksList.setSize(new java.awt.Dimension(138, 0));
        walksScrollPane.setViewportView(walksList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        inspectorPanelJPanel.add(walksScrollPane, gridBagConstraints);

        hiddenCheckBox.setText("Hidden");
        hiddenCheckBox.setFocusable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        inspectorPanelJPanel.add(hiddenCheckBox, gridBagConstraints);

        verticesList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(verticesList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        inspectorPanelJPanel.add(jScrollPane2, gridBagConstraints);

        edgesList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane3.setViewportView(edgesList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        inspectorPanelJPanel.add(jScrollPane3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        mainPanel.add(inspectorPanelJPanel, gridBagConstraints);

        getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);

        toolBar.setOrientation(javax.swing.SwingConstants.VERTICAL);
        toolBar.setRollover(true);

        addVerticesButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/add-vertices-icon.png"))); // NOI18N
        addVerticesButton.setToolTipText("Click to add vertices to the canvas (V)");
        addVerticesButton.setFocusable(false);
        addVerticesButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        addVerticesButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(addVerticesButton);

        addEdgesButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/add-edges-icon.png"))); // NOI18N
        addEdgesButton.setToolTipText("Add/bend edges between vertices (E)");
        addEdgesButton.setFocusable(false);
        addEdgesButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        addEdgesButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(addEdgesButton);

        selectionButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/selection-icon.png"))); // NOI18N
        selectionButton.setToolTipText("Select and move vertices and edges (spacebar)");
        selectionButton.setFocusable(false);
        selectionButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        selectionButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(selectionButton);

        addWalksButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/add-walks-icon.png"))); // NOI18N
        addWalksButton.setToolTipText("Add walks between vertices (W)");
        addWalksButton.setFocusable(false);
        addWalksButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        addWalksButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(addWalksButton);

        deleteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/delete-icon.png"))); // NOI18N
        deleteButton.setToolTipText("Delete (backspace)");
        deleteButton.setFocusable(false);
        deleteButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        deleteButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(deleteButton);

        getContentPane().add(toolBar, java.awt.BorderLayout.WEST);

        fileMenu.setText("File");

        openMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        openMenuItem.setText("Open");
        fileMenu.add(openMenuItem);

        saveMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        saveMenuItem.setText("Save");
        fileMenu.add(saveMenuItem);

        saveAsMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        saveAsMenuItem.setText("Save As");
        fileMenu.add(saveAsMenuItem);

        newMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        newMenuItem.setText("New");
        fileMenu.add(newMenuItem);
        fileMenu.add(jSeparator1);

        exportMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        exportMenuItem.setText("Export");
        fileMenu.add(exportMenuItem);

        myMenuBar.add(fileMenu);

        viewMenu.setText("View");

        showVertexNamesMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        showVertexNamesMenuItem.setText("Show Vertex Names");
        viewMenu.add(showVertexNamesMenuItem);

        selectAllVerticesMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_MASK));
        selectAllVerticesMenuItem.setText("Select All Vertices");
        selectAllVerticesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectAllVerticesMenuItemActionPerformed(evt);
            }
        });
        viewMenu.add(selectAllVerticesMenuItem);

        formatVerticesMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        formatVerticesMenuItem.setText("Format Vertices");
        viewMenu.add(formatVerticesMenuItem);

        myMenuBar.add(viewMenu);

        toolsMenu.setText("Tools");

        addVerticesMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, 0));
        addVerticesMenuItem.setText("Add Vertices");
        toolsMenu.add(addVerticesMenuItem);

        addEdgesMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, 0));
        addEdgesMenuItem.setText("Add Edges");
        addEdgesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addEdgesMenuItemActionPerformed(evt);
            }
        });
        toolsMenu.add(addEdgesMenuItem);

        selectionMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_SPACE, 0));
        selectionMenuItem.setText("Selection");
        toolsMenu.add(selectionMenuItem);

        addWalksMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, 0));
        addWalksMenuItem.setText("Add Walks");
        toolsMenu.add(addWalksMenuItem);
        toolsMenu.add(jSeparator2);

        deleteMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_BACK_SPACE, 0));
        deleteMenuItem.setText("Delete");
        toolsMenu.add(deleteMenuItem);

        changeColorsMenuItem.setText("Change Colors");
        toolsMenu.add(changeColorsMenuItem);

        addGraphMenuItem.setText("Add Graph");
        toolsMenu.add(addGraphMenuItem);

        myMenuBar.add(toolsMenu);

        helpMenu.setText("Help");

        checkForUpdatesMenuItem.setText("Check For Updates");
        helpMenu.add(checkForUpdatesMenuItem);

        tutorialMenuItem.setText("Tutorial");
        helpMenu.add(tutorialMenuItem);

        donateMenuItem.setText("Consider Donating!");
        helpMenu.add(donateMenuItem);

        myMenuBar.add(helpMenu);

        setJMenuBar(myMenuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void addEdgesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addEdgesMenuItemActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_addEdgesMenuItemActionPerformed

    private void selectAllVerticesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectAllVerticesMenuItemActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_selectAllVerticesMenuItemActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel JLabel3;
    private javax.swing.JToggleButton addEdgesButton;
    private javax.swing.JCheckBoxMenuItem addEdgesMenuItem;
    private javax.swing.JMenuItem addGraphMenuItem;
    private javax.swing.JToggleButton addVerticesButton;
    private javax.swing.JCheckBoxMenuItem addVerticesMenuItem;
    private javax.swing.JToggleButton addWalksButton;
    private javax.swing.JCheckBoxMenuItem addWalksMenuItem;
    private javax.swing.JTextArea canvasTextArea;
    private javax.swing.JMenuItem changeColorsMenuItem;
    private javax.swing.JMenuItem checkForUpdatesMenuItem;
    private javax.swing.JButton deleteButton;
    private javax.swing.JMenuItem deleteMenuItem;
    private javax.swing.JMenuItem donateMenuItem;
    private javax.swing.JLabel edgesLabel;
    private javax.swing.JList<String> edgesList;
    private javax.swing.JMenuItem exportMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenuItem formatVerticesMenuItem;
    private javax.swing.JPanel graphOutputPanel;
    private javax.swing.JTextField graphOutputTextField;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JCheckBox hiddenCheckBox;
    private javax.swing.JPanel inspectorPanelJPanel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JTextField modifiedTextField;
    private javax.swing.JMenuBar myMenuBar;
    private javax.swing.JMenuItem newMenuItem;
    private javax.swing.JMenuItem openMenuItem;
    private javax.swing.JPanel propertiesPanel;
    private javax.swing.JMenuItem saveAsMenuItem;
    private javax.swing.JMenuItem saveMenuItem;
    private javax.swing.JMenuItem selectAllVerticesMenuItem;
    private javax.swing.JToggleButton selectionButton;
    private javax.swing.JCheckBoxMenuItem selectionMenuItem;
    private javax.swing.JCheckBoxMenuItem showVertexNamesMenuItem;
    private javax.swing.JTextField titleTextField;
    private javax.swing.JToolBar toolBar;
    private javax.swing.JMenu toolsMenu;
    private javax.swing.JMenuItem tutorialMenuItem;
    private javax.swing.JLabel verticesLabel;
    private javax.swing.JList<String> verticesList;
    private javax.swing.JMenu viewMenu;
    private javax.swing.JLabel walksLabel;
    private javax.swing.JList<String> walksList;
    private javax.swing.JScrollPane walksScrollPane;
    // End of variables declaration//GEN-END:variables

    public JToggleButton getAddEdgesButton() {
        return addEdgesButton;
    }

    public JToggleButton getAddVerticesButton() {
        return addVerticesButton;
    }
    
    public JToggleButton getAddWalksButton() {
        return addWalksButton;
    }

    public Canvas getCanvas() {
        return (Canvas) canvasTextArea;
    }

    public JMenuItem getNewMenuItem() {
        return newMenuItem;
    }

    public JMenuItem getOpenMenuItem() {
        return openMenuItem;
    }

    public JToggleButton getSelectionButton() {
        return selectionButton;
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
    
    public JList<String> getEdgesList() {
        return edgesList;
    }

    public JList<String> getVerticesList() {
        return verticesList;
    }
    
    public JList<String> getWalksList() {
        return walksList;
    }
    
    public JMenuItem getShowVertexNamesMenuItem() {
        return showVertexNamesMenuItem;
    }
    
    public JMenu getFileMenu() {
        return fileMenu;
    }
    
    public JMenuBar getMyMenuBar() {
        return myMenuBar;
    }
    
    public JMenuItem getFormatVerticesMenuItem() {
        return formatVerticesMenuItem;
    }
    
    public JTextField getModifiedTextField() {
        return modifiedTextField;
    }
    
    /**
     * Not to be confused with addVertex. This is a menu item
     * that allows a user to add vertices by typing in a list
     * of edges.
     * @return 
     */
    public JMenuItem getAddGraphMenuItem() {
        return addGraphMenuItem;
    }
    
    public JTextField getGraphOutputTextField() {
        return graphOutputTextField;
    }
    
    public JButton getDeleteButton() {
        return deleteButton;
    }
    
    public JCheckBoxMenuItem getAddVerticesMenuItem() {
        return addVerticesMenuItem;
    }
    
    public JCheckBoxMenuItem getAddEdgesMenuItem() {
        return addEdgesMenuItem;
    }
    
    public JCheckBoxMenuItem getSelectionMenuItem() {
        return selectionMenuItem;
    }
    
    public JCheckBoxMenuItem getAddWalksMenuItem() {
        return addWalksMenuItem;
    }
    
    public JMenuItem getDeleteMenuItem() {
        return deleteMenuItem;
    }
    
    public JMenuItem getChangeColorsMenuItem() {
        return changeColorsMenuItem;
    }
    
    public InspectorPanel getInspectorPanel() {
        return (InspectorPanel) inspectorPanelJPanel;
    }
    
    public JMenuItem getSelectAllVerticesMenuItem() {
        return selectAllVerticesMenuItem;
    }
    
    public JPanel getMainPanel() {
        return mainPanel;
    }
    
    public JMenuItem getExportMenuItem() {
        return exportMenuItem;
    }
    
    public JMenuItem getCheckForUpdatesMenuItem() {
        return checkForUpdatesMenuItem;
    }
    
    public JMenuItem getTutorialMenuItem() {
        return tutorialMenuItem;
    }
    
    public JMenuItem getDonateMenuItem() {
        return donateMenuItem;
    }
    
    public JCheckBox getHiddenCheckBox() {
        return hiddenCheckBox;
    }
}
