import controller.CalendarController;
import controller.IController;
import model.Calendar;
import model.ICalendar;
import view.IView;
import view.View;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;


/**
 * This is a class which is used to bootstrap our whole Calendar app. Creates the model,
 * Creates the view, and then starts the controller.
 */
public class CalendarApp {

  /**
   * Entry point of our application.
   * @param args parameters that the main method uses to run the app.
   */
  public static void main(String[] args) {
    System.out.println("CalendarApp");
    if (args.length < 2) {
      System.err.println("Usage: java CalendarApp --mode <interactive|headless> [filename]");
      System.err.println("  For interactive mode: java CalendarApp --mode interactive");
      System.err.println("  For headless mode: java CalendarApp --mode headless commands.txt");
      System.exit(1);
    }

    if (!args[0].equalsIgnoreCase("--mode")) {
      System.err.println("Error: First argument must be '--mode'");
      System.exit(1);
    }

    String mode = args[1].toLowerCase();
    
    // Create the model
    ICalendar model = new Calendar();
    
    try {
      switch (mode) {
        case "interactive":
          runInteractiveMode(model);
          break;
        case "headless":
          if (args.length < 3) {
            System.err.println("Error: Headless mode requires a filename");
            System.exit(1);
          }
          runHeadlessMode(model, args[2]);
          break;
        default:
          System.err.println("Error: Mode must be 'interactive' or 'headless'");
          System.exit(1);
      }
    } catch (IOException e) {
      System.err.println("Error: " + e.getMessage());
      System.exit(1);
    }
  }
  
  private static void runInteractiveMode(ICalendar model) throws IOException {
    IView view = new View(System.out);
    view.showWelcome();
    view.showPrompt();
    
    IController controller = new CalendarController(model, new InputStreamReader(System.in), view);
    controller.go();
  }
  
  private static void runHeadlessMode(ICalendar model, String filename) throws IOException {
    try (FileReader fileReader = new FileReader(filename)) {
      IView view = new View(System.out);
      IController controller = new CalendarController(model, fileReader, view);
      controller.go();
    } catch (IOException e) {
      System.err.println("Error reading file '" + filename + "': " + e.getMessage());
      throw e;
    }
  }
}
