package controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import model.ICalendar;
import model.IEvent;
import view.IView;

/**
 * Command to print all events on a specific date.
 */
public class PrintEventsOnDateCommand implements IControllerCommand {
  private final LocalDate date;
  
  public PrintEventsOnDateCommand(LocalDate date) {
    this.date = date;
  }

  @Override
  public void execute(ICalendar model, IView view) throws IOException {
    List<IEvent> events = model.getEventsOnDate(date);
    view.showEventsOnDate(date, events);
  }
} 