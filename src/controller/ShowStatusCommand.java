package controller;

import java.io.IOException;
import java.time.LocalDateTime;
import model.ICalendar;
import view.IView;

/**
 * Command to show busy/available status at a specific date and time.
 */
public class ShowStatusCommand implements IControllerCommand {
  private final LocalDateTime dateTime;
  
  public ShowStatusCommand(LocalDateTime dateTime) {
    this.dateTime = dateTime;
  }

  @Override
  public void execute(ICalendar model, IView view) throws IOException {
    boolean isBusy = model.isBusyAt(dateTime);
    view.showStatus(dateTime, isBusy);
  }
} 