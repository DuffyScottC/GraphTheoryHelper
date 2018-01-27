/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.views2;

import java.awt.Color;
import javax.swing.JButton;
import javax.swing.JTextField;

/**
 *
 * @author Scott
 */
public class GraphColorChooserDialog extends javax.swing.JDialog {
    
    private Color vertexFillColor;
    private Color vertexStrokeColor;
    private Color edgeStrokeColor;
    //Create a new SampleCanvas object
    private views.SampleCanvas sampleCanvas;
    
    /**
     * Creates new form GraphColorChooserDialog
     */
    public GraphColorChooserDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        //add the sampleCanvas object to the jScrollPane
        sampleCanvas = new views.SampleCanvas();
        sampleCanvas.setColumns(20);
        sampleCanvas.setRows(5);
        sampleCanvasScrollPane.setViewportView(sampleCanvas);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        vertexFillColorTextField = new javax.swing.JTextField();
        vertexStrokeColorTextField = new javax.swing.JTextField();
        edgeStrokeColorTextField = new javax.swing.JTextField();
        vertexFillColorChooseButton = new javax.swing.JButton();
        vertexStrokeColorChooseButton = new javax.swing.JButton();
        edgeStrokeColorChooseButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        sampleCanvasScrollPane = new javax.swing.JScrollPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setText("Vertex Stroke Color:");

        jLabel2.setText("Vertex Fill Color:");

        jLabel3.setText("Edge Stroke Color:");

        vertexFillColorTextField.setColumns(6);

        vertexStrokeColorTextField.setColumns(6);

        edgeStrokeColorTextField.setColumns(6);

        vertexFillColorChooseButton.setText("Choose");

        vertexStrokeColorChooseButton.setText("Choose");

        edgeStrokeColorChooseButton.setText("Choose");

        okButton.setText("OK");

        cancelButton.setText("Cancel");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(cancelButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(okButton))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(vertexFillColorTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(vertexFillColorChooseButton))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(vertexStrokeColorTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(vertexStrokeColorChooseButton))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(edgeStrokeColorTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(edgeStrokeColorChooseButton)))
                        .addGap(0, 114, Short.MAX_VALUE))
                    .addComponent(sampleCanvasScrollPane, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(vertexFillColorTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vertexFillColorChooseButton))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(vertexStrokeColorTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vertexStrokeColorChooseButton))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(edgeStrokeColorTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(edgeStrokeColorChooseButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sampleCanvasScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(okButton)
                    .addComponent(cancelButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GraphColorChooserDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GraphColorChooserDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GraphColorChooserDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GraphColorChooserDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                GraphColorChooserDialog dialog = new GraphColorChooserDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton edgeStrokeColorChooseButton;
    private javax.swing.JTextField edgeStrokeColorTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JButton okButton;
    private javax.swing.JScrollPane sampleCanvasScrollPane;
    private javax.swing.JButton vertexFillColorChooseButton;
    private javax.swing.JTextField vertexFillColorTextField;
    private javax.swing.JButton vertexStrokeColorChooseButton;
    private javax.swing.JTextField vertexStrokeColorTextField;
    // End of variables declaration//GEN-END:variables

    public JButton getCancelButton() {
        return cancelButton;
    }

    public JButton getEdgeStrokeColorChooseButton() {
        return edgeStrokeColorChooseButton;
    }

    public JTextField getEdgeStrokeColorTextField() {
        return edgeStrokeColorTextField;
    }

    public JButton getOKButton() {
        return okButton;
    }

    public SampleCanvas getSampleCanvas() {
        return sampleCanvas;
    }

    public JButton getVertexFillColorChooseButton() {
        return vertexFillColorChooseButton;
    }

    public JTextField getVertexFillColorTextField() {
        return vertexFillColorTextField;
    }

    public JButton getVertexStrokeColorChooseButton() {
        return vertexStrokeColorChooseButton;
    }

    public JTextField getVertexStrokeColorTextField() {
        return vertexStrokeColorTextField;
    }
    
    public Color getVertexFillColor() {
        return vertexFillColor;
    }

    public Color getVertexStrokeColor() {
        return vertexStrokeColor;
    }
    
    public Color getEdgeStrokeColor() {
        return edgeStrokeColor;
    }
    
    /**
     * Sets the dialog's vertexFillColor, the sampleCanvas's vertexFillColor,
     * and the text field background
     * @param vertexFillColor 
     */
    public void setVertexFillColor(Color vertexFillColor) {
        this.vertexFillColor = vertexFillColor;
        sampleCanvas.setVertexFillColor(vertexFillColor);
        vertexFillColorTextField.setBackground(vertexFillColor);
    }

    /**
     * Sets the dialog's vertexStrokeColor, the sampleCanvas's vertexStrokeColor,
     * and the text field background
     * @param vertexStrokeColor 
     */
    public void setVertexStrokeColor(Color vertexStrokeColor) {
        this.vertexStrokeColor = vertexStrokeColor;
        sampleCanvas.setVertexStrokeColor(vertexStrokeColor);
        vertexStrokeColorTextField.setBackground(vertexStrokeColor);
    }
    
    /**
     * Sets the dialog's edgeStrokeColor, the sampleCanvas's edgeStrokeColor,
     * and the text field background
     * @param edgeStrokeColor 
     */
    public void setEdgeStrokeColor(Color edgeStrokeColor) {
        this.edgeStrokeColor = edgeStrokeColor;
        sampleCanvas.setEdgeStrokeColor(edgeStrokeColor);
        edgeStrokeColorTextField.setBackground(edgeStrokeColor);
    }
}
