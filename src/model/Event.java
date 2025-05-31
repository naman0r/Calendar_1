package model;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Immutable Event class representing a calendar event.
 */
public class Event implements IEvent {
  private final String subject;
  private final LocalDateTime start;
  private final LocalDateTime end;
  private final String description;
  private final Location location;
  private final Status status;
  private final boolean allDay;

  private Event(EventBuilder builder) {
    this.subject = builder.subject;
    this.start = builder.start;
    this.end = (builder.end != null) ? builder.end : getDefaultEnd(builder.start);
    this.description = builder.description;
    this.location = builder.location;
    this.status = builder.status;
    this.allDay = builder.allDay;
  }

  // Used if end is null and isAllDay
  private LocalDateTime getDefaultEnd(LocalDateTime start) {
    // All day: 8am to 5pm, same day
    return start.toLocalDate().atTime(LocalTime.of(17, 0));
  }

  @Override
  public String getSubject() {
    return this.subject;
  }

  @Override
  public LocalDateTime getStart() {
    return this.start;
  }

  @Override
  public LocalDateTime getEnd() {
    return this.end;
  }

  @Override
  public String getDescription() {
    return this.description;
  }

  @Override
  public String getLocation() {
    if (this.location == null) {
      return null;
    }
    return this.location.toString().toLowerCase();
  }

  @Override
  public String getStatus() {
    if (this.status == null) {
      return null;
    }

    return this.status.toString().toLowerCase();
  }

  @Override
  public boolean isAllDay() { return allDay; }

  @Override
  public boolean overlapsWith(IEvent other) {
    if (other == null) return false;
    LocalDateTime thisStart = this.getStart();
    LocalDateTime thisEnd = this.getEnd();
    LocalDateTime otherStart = other.getStart();
    LocalDateTime otherEnd = other.getEnd();

    // Defensive for nulls (shouldn't happen for non-all-day)
    if (thisStart == null || thisEnd == null || otherStart == null || otherEnd == null) return false;

    // Overlaps if intervals intersect
    return !thisEnd.isBefore(otherStart) && !thisStart.isAfter(otherEnd);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof IEvent)) return false;
    IEvent event = (IEvent) o;
    // As per spec: subject + start + end uniquely identify
    return Objects.equals(subject, event.getSubject())
            && Objects.equals(start, event.getStart())
            && Objects.equals(end, event.getEnd());
  }

  @Override
  public int hashCode() {
    return Objects.hash(subject, start, end);
  }

  // Builder pattern for flexibility/extensibility
  public static class EventBuilder {
    private String subject;
    private LocalDateTime start;
    private LocalDateTime end;
    private String description;
    private Location location;
    private Status status;
    private boolean allDay = false;

    public EventBuilder subject(String subject) {
      this.subject = subject;
      return this;
    }

    public EventBuilder from(LocalDateTime start) {
      this.start = start;
      return this;
    }

    public EventBuilder to(LocalDateTime end) {
      this.end = end;
      return this;
    }

    public EventBuilder description(String description) {
      this.description = description;
      return this;
    }

    public EventBuilder location(Location location) {
      this.location = location;
      return this;
    }

    public EventBuilder status(Status status) {
      this.status = status;
      return this;
    }

    public EventBuilder allDay(boolean allDay) {
      this.allDay = allDay;
      return this;
    }

    public Event build() {
      // Subject and start are always required
      if (subject == null || start == null) {
        throw new IllegalArgumentException("Subject and start time cannot be null");
      }
      // If allDay is true and end is null, default to 8am-5pm
      if (allDay && end == null) {
        start = start.toLocalDate().atTime(LocalTime.of(8, 0));
        end = start.toLocalDate().atTime(LocalTime.of(17, 0));
      }
      return new Event(this);
    }
  }
}
