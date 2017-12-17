/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import views.GraphFrame;

/**
 *
 * @author Scott
 */
public class Controller {

  private final GraphFrame frame = new GraphFrame();
  
  public Controller() {
    frame.setTitle( getClass().getSimpleName() );
    frame.setLocationRelativeTo(null);
    
    
  }

  public static void main(String[] args) {
    Controller app = new Controller();
    app.frame.setVisible(true);
  }
}