package controller;

import java.io.IOException;
import java.util.Scanner;

import model.ICalendar;
import view.IView;

public class CalendarController implements IController{
  private final ICalendar model;
  private final Readable in;
  private final IView view;


  /**
   * Public constructor for our controller, which starts the application.
   * @param model the Model (ICalendar, Calendar concrete class).
   * @param in the Input (System.in, StringReader, etc.).
   * @param view the Output ( IView, view concrete implementation).
   */
  public CalendarController(ICalendar model, Readable in, IView view) {
    this.model = model;
    this.in = in;
    this.view = view;
  }


  /**
   * Starts the controller, main logic of the application controller and keeps it going.
   * @throws IOException if an invalid input is received.
   */
  public void go() throws IOException {
    Scanner scanner = new Scanner(this.in);
    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      try {
        IControllerCommand cmd = CalendarCommandParser.parse(line);
        if (cmd == null) {
          view.showUnknownCommand(line);
          continue;
        }
        cmd.execute(this.model, this.view);
        if (line.equalsIgnoreCase("exit")) {
          break;
        }

      }
      catch (Exception e) {
        view.showError(e.getMessage() != null ? e.getMessage() : "Unknown error");
        e.printStackTrace(); // Print stack trace to stderr for debugging

      }
    }


  }
}
