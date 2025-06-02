package controller;

import java.io.IOException;
import java.util.Scanner;

import model.ICalendar;

public class CalendarController implements IController{
  private final ICalendar model;
  private final Readable in;
  private final Appendable out;

  public CalendarController(ICalendar model, Readable in, Appendable out) {
    this.model = model;
    this.in = in;
    this.out = out;
  }


  /**
   *
   * @throws IOException if an invalid input is received.
   */
  public void go() throws IOException {
    Scanner scanner = new Scanner(this.in);
    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      try {
        IControllerCommand cmd = CalendarCommandParser.parse(line);
        if (cmd == null) {
          out.append("Unknown or malformed command: ").append(line).append("\n");
          continue;
        }
        cmd.execute(this.model, this.out);
        if (line.equalsIgnoreCase("exit")) {
          break;
        }

      }
      catch (Exception e) {
        out.append("Error" + e.getMessage() + "\n");

      }
    }


  }
}
