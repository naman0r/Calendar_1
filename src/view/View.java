package view;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import model.*;

/**
 * Implementation of the view for the Calendar application.
 * Handles all presentation logic and output formatting.
 */
public class View implements IView {
  private final Appendable out;
  
  public View(Appendable out) {
    this.out = out;
  }
  
  @Override
  public void showEventCreated() throws IOException {
    out.append("Event created successfully.\n");
  }
  
  @Override
  public void showEventSeriesCreated() throws IOException {
    out.append("Event series created successfully.\n");
  }
  
  @Override
  public void showEventEdited() throws IOException {
    out.append("Event(s) edited successfully.\n");
  }
  
  @Override
  public void showEventCreationFailed() throws IOException {
    out.append("Failed to create event. Event already exists.\n");
  }
  
  @Override
  public void showEventSeriesCreationFailed() throws IOException {
    out.append("Failed to create event series. One or more events already exist.\n");
  }
  
  @Override
  public void showEventEditingFailed() throws IOException {
    out.append("Failed to edit event(s). Event not found or editing would create duplicate.\n");
  }
  
  @Override
  public void showEventsOnDate(LocalDate date, List<IEvent> events) throws IOException {
    if (events.isEmpty()) {
      out.append("No events found on ").append(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).append(".\n");
    } else {
      out.append("Events on ").append(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).append(":\n");
      for (IEvent event : events) {
        formatEvent(event);
      }
    }
  }
  
  @Override
  public void showEventsInRange(LocalDateTime start, LocalDateTime end, List<IEvent> events) throws IOException {
    if (events.isEmpty()) {
      out.append("No events found in the specified range.\n");
    } else {
      out.append("Events from ").append(start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")))
         .append(" to ").append(end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"))).append(":\n");
      
      for (IEvent event : events) {
        formatEventWithFullDateTime(event);
      }
    }
  }
  
  @Override
  public void showStatus(LocalDateTime dateTime, boolean isBusy) throws IOException {
    String status = isBusy ? "busy" : "available";
    out.append("Status on ").append(dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")))
       .append(": ").append(status).append("\n");
  }
  
  @Override
  public void showGoodbye() throws IOException {
    out.append("Goodbye!\n");
  }
  
  @Override
  public void showUnknownCommand(String command) throws IOException {
    out.append("Unknown or malformed command: ").append(command).append("\n");
  }
  
  @Override
  public void showError(String message) throws IOException {
    out.append("Error: ").append(message != null ? message : "Unknown error").append("\n");
  }
  
  @Override
  public void showWelcome() throws IOException {
    out.append("Welcome to Calendar App! Type 'exit' to quit.\n");
  }
  
  @Override
  public void showPrompt() throws IOException {
    out.append("> ");
  }
  
  /**
   * Format an event for display in date-specific lists (shows only time).
   */
  private void formatEvent(IEvent event) throws IOException {
    out.append("• ").append(event.getSubject());
    out.append(" (").append(event.getStart().format(DateTimeFormatter.ofPattern("HH:mm")));
    out.append(" - ").append(event.getEnd().format(DateTimeFormatter.ofPattern("HH:mm"))).append(")");
    
    if (event.getLocation() != null && !event.getLocation().trim().isEmpty()) {
      out.append(" at ").append(event.getLocation());
    }
    out.append("\n");
  }
  
  /**
   * Format an event for display in range lists (shows full date and time).
   */
  private void formatEventWithFullDateTime(IEvent event) throws IOException {
    out.append("• ").append(event.getSubject());
    out.append(" (").append(event.getStart().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")));
    out.append(" - ").append(event.getEnd().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"))).append(")");
    
    if (event.getLocation() != null && !event.getLocation().trim().isEmpty()) {
      out.append(" at ").append(event.getLocation());
    }
    out.append("\n");
  }
}
