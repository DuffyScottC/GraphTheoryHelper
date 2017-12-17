/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

/**
 *
 * @author Scott
 */
import views.GraphFrame;

public class Controller {

  private final GraphFrame frame = new GraphFrame();
  
  public Controller() {
    frame.setTitle( getClass().getSimpleName() );
    frame.setLocationRelativeTo(null);
    // you can adjust the size with something like this:
    // frame.setSize(600, 500);
	
    // event handlers
  }

  public static void main(String[] args) {
    Controller app = new Controller();
    app.frame.setVisible(true);
  }
}