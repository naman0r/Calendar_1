package model;

import java.time.LocalDateTime;

/**
 * Interface for a calendar event.  Location/status may be null (no default).
 */
public interface IEvent {
  String getSubject();
  LocalDateTime getStart();
  LocalDateTime getEnd();
  String getDescription();
  Location getLocation();  // may return null if not set
  Status getStatus();      // may return null if not set
  Integer getSeriesId();   // null if single‐event
  boolean isAllDay();
  boolean overlapsWith(IEvent other);
  @Override boolean equals(Object other);
  @Override int hashCode();
}
