package controller;

import java.io.IOException;
import model.ICalendar;
import view.IView;

/**
 * Command to exit the application.
 */
public class ExitCommand implements IControllerCommand {
  
  @Override
  public void execute(ICalendar model, IView view) throws IOException {
    view.showGoodbye();
  }
} 