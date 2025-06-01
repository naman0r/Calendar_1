package model2;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ICalendar {
  /**
   * Adds an event to the calendar.
   * Returns true if added, false if a duplicate exists.
   */
  boolean addEvent(IEvent event);

  /**
   * Returns all events on the given date.
   */
  List<IEvent> getEventsOnDate(LocalDate date);

  /**
   * Returns all events in the interval [start, end].
   */
  List<IEvent> getEventsInRange(LocalDateTime start, LocalDateTime end);

  /**
   * Checks if there is any event at the given date and time.
   */
  boolean isBusyAt(LocalDateTime dateTime);

  /**
   * Finds a unique event by subject and start/end time (for editing).
   * Returns null if not found or if not unique.
   */
  IEvent findEvent(String subject, LocalDateTime start, LocalDateTime end);

  /**
   * Remove an event from the calendar.
   */
  boolean removeEvent(IEvent event);

  //  can add more methods as required for editing series, etc.
}
