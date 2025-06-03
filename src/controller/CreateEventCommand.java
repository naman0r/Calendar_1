package controller;

import java.io.IOException;
import java.time.LocalDateTime;

import model.ICalendar;
import model.Location;
import model.Status;
import view.IView;

/**
 * Command to create a single event.
 * Now that Location and Status are enums, we parse the raw strings here.
 */
public class CreateEventCommand implements IControllerCommand {
  private final String subject;
  private final String description;
  private final LocalDateTime start;
  private final LocalDateTime end;
  private final String location; // raw string from parser
  private final String status;   // raw string from parser

  public CreateEventCommand(String subject,
                            String description,
                            LocalDateTime start,
                            LocalDateTime end,
                            String location,
                            String status) {
    this.subject = subject;
    this.description = description;
    this.start = start;
    this.end = end;
    this.location = location;
    this.status = status;
  }

  @Override
  public void execute(ICalendar model, IView view) throws IOException {
    // Convert raw strings into enums (null or empty → leave as null)
    Location locEnum = null;
    if (this.location != null && !this.location.trim().isEmpty()) {
      locEnum = Location.valueOfStrict(this.location.trim());
    }

    Status statEnum = null;
    if (this.status != null && !this.status.trim().isEmpty()) {
      statEnum = Status.valueOfStrict(this.status.trim());
    }

    // Call the updated makeEvent signature:
    boolean success = model.makeEvent(
            subject,
            description,
            start,
            end,
            locEnum,
            statEnum
    );

    if (success) {
      view.showEventCreated();
    } else {
      view.showEventCreationFailed();
    }
  }
}
