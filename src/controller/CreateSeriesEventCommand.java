package controller;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;
import model.ICalendar;
import view.IView;

/**
 * Command to create a series of recurring events.
 */
public class CreateSeriesEventCommand implements IControllerCommand {
  private final String subject;
  private final String description;
  private final LocalDateTime start;
  private final LocalDateTime end;
  private final List<DayOfWeek> days;
  private final int count;
  private final LocalDateTime endDate;
  
  public CreateSeriesEventCommand(String subject, String description, LocalDateTime start, 
                                 LocalDateTime end, List<DayOfWeek> days, int count, LocalDateTime endDate) {
    this.subject = subject;
    this.description = description;
    this.start = start;
    this.end = end;
    this.days = days;
    this.count = count;
    this.endDate = endDate;
  }

  @Override
  public void execute(ICalendar model, IView view) throws IOException {
    boolean success;
    if (endDate != null) {
      // Create series until end date
      success = model.makeEvent(subject, description, start, end, days, endDate);
    } else {
      // Create series for specific count
      success = model.makeEvent(subject, description, start, end, days, count);
    }
    
    if (success) {
      view.showEventSeriesCreated();
    } else {
      view.showEventSeriesCreationFailed();
    }
  }
} 