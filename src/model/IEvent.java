package model;


import java.time.LocalDateTime;

/**
 * This is the interface for an Event.
 */
public interface IEvent {

  /*
   * For each event, we need a way to tell if:
   *  - the event is an all day event (absence of an end time), defined as 8 am to 5 pm.
   *  - Keep in mind that two events cannot have the same subject,
   *        start date/time and end date/time.
   *  -
   *  -
   *  -
   *  -
   */

  String getSubject();
  LocalDateTime getStart();
  LocalDateTime getEnd();  // May be null if all-day
  String getDescription();
  String getLocation();
  String getStatus(); // "public" or "private"
  boolean isAllDay();
  boolean overlapsWith(IEvent other);
  boolean equals(Object other);
  int hashCode();
}
