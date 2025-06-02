package controller;


import java.io.IOException;

/**
 * Interface for the controller for our Calendar.
 */
public interface IController {

  /**
   * Starts the controller, and starts the application in either headless or interactive mode.
   * @throws IOException if bad input.
   */
  public void go() throws IOException;
}
