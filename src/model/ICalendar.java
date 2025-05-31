package model;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * This is the interface for a Calendar, and represents a Calendar.
 */
public interface ICalendar {

  boolean addEvent(IEvent event);
  boolean removeEvent(IEvent event);
  List<IEvent> getEventsOn(LocalDate date);
  List<IEvent> getEventsBetween(LocalDateTime start, LocalDateTime end);
  IEvent findEvent(String subject, LocalDateTime start, LocalDateTime end);
  boolean isBusy(LocalDateTime dateTime);
  boolean editEvent(IEvent event, String property, Object newValue);

}


