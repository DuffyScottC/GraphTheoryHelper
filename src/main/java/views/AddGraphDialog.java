/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package views;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 *
 * @author Scott
 */
public class AddGraphDialog extends javax.swing.JDialog {

    /**
     * Creates new form AddGraphDialog
     */
    public AddGraphDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
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

    jLabel1 = new javax.swing.JLabel();
    graphTextField = new javax.swing.JTextField();
    errorLabel = new javax.swing.JLabel();
    addButton = new javax.swing.JButton();
    cancelButton = new javax.swing.JButton();

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

    jLabel1.setText("Type in a list of edges such as {{A,B},{B,C},{C,D}}:");

    graphTextField.setText("{}");
    graphTextField.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        graphTextFieldActionPerformed(evt);
      }
    });

    errorLabel.setText(" ");

    addButton.setText("Add");

    cancelButton.setText("Cancel");

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(graphTextField)
          .addGroup(layout.createSequentialGroup()
            .addComponent(jLabel1)
            .addGap(0, 146, Short.MAX_VALUE))
          .addComponent(errorLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
            .addGap(0, 0, Short.MAX_VALUE)
            .addComponent(cancelButton)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(addButton)))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jLabel1)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(graphTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(errorLabel)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(addButton)
          .addComponent(cancelButton))
        .addContainerGap(21, Short.MAX_VALUE))
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

    private void graphTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_graphTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_graphTextFieldActionPerformed

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton addButton;
  private javax.swing.JButton cancelButton;
  private javax.swing.JLabel errorLabel;
  private javax.swing.JTextField graphTextField;
  private javax.swing.JLabel jLabel1;
  // End of variables declaration//GEN-END:variables

    /**
     * Sets the focus to the textField and moves cursor to between the "{}".
     * Called in constructor. 
     */
    public void setFocusToTextField() {
        graphTextField.requestFocus();
        graphTextField.setSelectionStart(1);
        graphTextField.setSelectionEnd(graphTextField.getText().length() - 1);
    }
    
    public JButton getAddButton() {
        return addButton;
    }
    
    public JButton getCancelButton() {
        return cancelButton;
    }
    
    public JTextField getGraphTextField() {
        return graphTextField;
    }
    
    public JLabel getErrorLabel() {
        return errorLabel;
    }
}
