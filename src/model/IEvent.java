package model;

import java.time.LocalDateTime;

public interface IEvent {


  String getSubject(); // not nullable

  LocalDateTime getStart(); //
  LocalDateTime getEnd(); // nullable for all=day events.

  String getDescription(); // nullable


  String getLocation();  // virtual or physical, Nullable.
  String getStatus(); // public or private;, Nullable

  Integer getSeriesId(); // returns null for SingleEvents.

  boolean isAllDay();

  boolean overlapsWith(IEvent other);

  boolean equals(Object other);

  int hashCode();

}
