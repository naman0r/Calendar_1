package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Interface for a single calendar.  Location and Status parameters remain nullable.
 */
public interface ICalendar {
  boolean addEvent(IEvent event);

  boolean makeEvent(
          String subject,
          String description,
          LocalDateTime start,
          LocalDateTime end,
          Location location,
          Status status
  );

  boolean makeEvent(
          String subject,
          String description,
          LocalDateTime start,
          LocalDateTime end,
          List<java.time.DayOfWeek> days,
          int count
  );

  boolean makeEvent(
          String subject,
          String description,
          LocalDateTime start,
          LocalDateTime end,
          List<java.time.DayOfWeek> days,
          LocalDateTime endDate
  );

  boolean makeEvent(String subject, LocalDateTime start);

  boolean editEvent();  // not used directly
  boolean editSeriesEvent(
          String property,
          String subject,
          LocalDateTime start,
          LocalDateTime end,
          String newValue,
          char type
  );

  List<IEvent> getEventsOnDate(LocalDate date);
  List<IEvent> getEventsInRange(LocalDateTime start, LocalDateTime end);
  boolean isBusyAt(LocalDateTime dateTime);
  IEvent findEvent(String subject, LocalDateTime start, LocalDateTime end);
  boolean removeEvent(IEvent event);
}
