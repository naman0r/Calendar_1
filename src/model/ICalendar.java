package model;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * This is the interface for a Calendar, and represents a Calendar.
 */
public interface ICalendar {

  /**
   * Adds a Single Evemt, returns true if added, false if not added.
   * Will return false if the event is duplicated (subject + start + end).
   * @param event the Event being added.
   * @return boolean representing if event was added successfully or not.
   */
  boolean addEvent(IEvent event);


  /**
   * Removes a specific event, returns true if successfully removed.
   * @param event the Event being removed.
   * @return true if event was removed.
   */
  boolean removeEvent(IEvent event);

  /**
   * Finds all events on a specific date.
   * @param date The Date that is being searched for.
   * @return a list of Events on that date.
   */
  List<IEvent> getEventsOn(LocalDate date);

  /**
   * Finds all events in an interval (inclusive interval)
   * @param start start date.
   * @param end end date.
   * @return a list of {@code IEvent}s in the specified interval,
   */
  List<IEvent> getEventsBetween(LocalDateTime start, LocalDateTime end);


  /**
   * Find an event by Subject, Start datetime and end datetime.
   * @param subject the subject of event.
   * @param start start time of the event.
   * @param end end time of the event.
   * @return return ONE event if found.
   */
  IEvent findEvent(String subject, LocalDateTime start, LocalDateTime end);


  /**
   * Returns true if the user is busy at this date/time.
   * @param dateTime the date/time being searched for,
   * @return boolean, true if busy, false if free.
   */
  boolean isBusy(LocalDateTime dateTime);


  /**
   * Edits an event, returns true if successful. 
   * @param event old event
   * @param property old property
   * @param newValue new object,
   * @return true if successful.
   */
  boolean editEvent(IEvent event, String property, Object newValue);


  /**
   * Gets all the events in a calendar.
   * @return list of the events in the calendar.
   */
  List<IEvent> getAllEvents();
}


