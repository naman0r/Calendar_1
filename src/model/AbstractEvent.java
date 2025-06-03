package model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * An abstract base class for any calendar event.  Location and Status are now
 * nullable; no default is filled in if the caller never sets them.
 */
public abstract class AbstractEvent implements IEvent {
  protected final String subject;
  protected final LocalDateTime start;
  protected final LocalDateTime end;
  protected final String description;
  protected final Location location; // may be null
  protected final Status status;     // may be null
  protected final Integer seriesId;  // null if not part of a series

  protected AbstractEvent(
          String subject,
          LocalDateTime start,
          LocalDateTime end,
          String description,
          Location location,
          Status status,
          Integer seriesId
  ) {
    if (subject == null || subject.trim().isEmpty()) {
      throw new IllegalArgumentException("Subject cannot be null or empty");
    }
    if (start == null) {
      throw new IllegalArgumentException("Start time cannot be null");
    }
    this.subject = subject;
    this.start = start;
    this.end = end; // may be null for “all‐day” logic upstream
    this.description = (description == null) ? "" : description;
    this.location = location; // now simply accept whatever caller passed (even null)
    this.status   = status;   // likewise, can remain null
    this.seriesId = seriesId;
  }

  @Override
  public String getSubject() {
    return subject;
  }

  @Override
  public LocalDateTime getStart() {
    return start;
  }

  @Override
  public LocalDateTime getEnd() {
    return end;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public Location getLocation() {
    return location; // may be null
  }

  @Override
  public Status getStatus() {
    return status;   // may be null
  }

  @Override
  public Integer getSeriesId() {
    return seriesId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof IEvent)) return false;
    IEvent other = (IEvent) o;
    return Objects.equals(subject, other.getSubject())
            && Objects.equals(start, other.getStart())
            && Objects.equals(end, other.getEnd());
  }

  @Override
  public int hashCode() {
    return Objects.hash(subject, start, end);
  }

  /**
   * Abstract Builder for any concrete Event subclass.  Location and Status stay nullable.
   */
  public static abstract class Builder<T extends Builder<T>> {
    protected String subject;
    protected LocalDateTime start;
    protected LocalDateTime end;
    protected String description;
    protected Location location; // remain null if caller never sets
    protected Status status;     // remain null if caller never sets
    protected Integer seriesId;

    public T subject(String subject) {
      this.subject = subject;
      return self();
    }

    public T start(LocalDateTime start) {
      this.start = start;
      return self();
    }

    public T end(LocalDateTime end) {
      this.end = end;
      return self();
    }

    public T description(String desc) {
      this.description = desc;
      return self();
    }

    public T location(Location location) {
      this.location = location;
      return self();
    }

    public T status(Status status) {
      this.status = status;
      return self();
    }

    public T seriesId(Integer id) {
      this.seriesId = id;
      return self();
    }

    protected abstract T self();
    public abstract AbstractEvent build();
  }
}
