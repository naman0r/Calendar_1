package controller;

import java.io.IOException;
import java.time.LocalDateTime;
import model.ICalendar;
import view.IView;

/**
 * Command to create a single event.
 */
public class CreateEventCommand implements IControllerCommand {
  private final String subject;
  private final String description;
  private final LocalDateTime start;
  private final LocalDateTime end;
  private final String location;
  private final String status;
  
  public CreateEventCommand(String subject, String description, LocalDateTime start, 
                           LocalDateTime end, String location, String status) {
    this.subject = subject;
    this.description = description;
    this.start = start;
    this.end = end;
    this.location = location;
    this.status = status;
  }

  @Override
  public void execute(ICalendar model, IView view) throws IOException {
    boolean success = model.makeEvent(subject, description, start, end, location, status);
    if (success) {
      view.showEventCreated();
    } else {
      view.showEventCreationFailed();
    }
  }
}
