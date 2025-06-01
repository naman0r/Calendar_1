package model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ICalendar {
  /**
   * Adds an event to the calendar.
   * Returns true if added, false if a duplicate exists.
   */
  boolean addEvent(IEvent event);


  // single event creation.
  boolean makeEvent(String subject, String description, LocalDateTime start, LocalDateTime end,
                      String location, String status);

  //
  boolean makeEvent(String subject, String description, LocalDateTime start, LocalDateTime end,
                    List<DayOfWeek> days, int count);

  boolean makeEvent(String subject, String description, LocalDateTime start, LocalDateTime end,
                    List<DayOfWeek> days, LocalDateTime endDate);

  boolean makeEvent(String subject, LocalDateTime start); // this is for an all day event.


  boolean editEvent();


  // char can either be 'f', which means forward, or 'e' which means entire. this is to edit a series event.
  boolean editSeriesEvent(String property, String subject,  LocalDateTime start, LocalDateTime end,
                          String newValue, char type);

  //

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
