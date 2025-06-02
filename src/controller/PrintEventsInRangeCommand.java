package controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import model.ICalendar;
import model.IEvent;
import view.IView;

/**
 * Command to print all events in a specific date/time range.
 */
public class PrintEventsInRangeCommand implements IControllerCommand {
  private final LocalDateTime start;
  private final LocalDateTime end;
  
  public PrintEventsInRangeCommand(LocalDateTime start, LocalDateTime end) {
    this.start = start;
    this.end = end;
  }

  @Override
  public void execute(ICalendar model, IView view) throws IOException {
    List<IEvent> events = model.getEventsInRange(start, end);
    view.showEventsInRange(start, end, events);
  }
} 