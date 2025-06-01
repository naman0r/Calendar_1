package model;

import java.time.LocalDateTime;
import java.util.Objects;

abstract class AbstractEvent implements IEvent {
  protected final String subject;
  protected final LocalDateTime start;
  protected final LocalDateTime end;
  protected final String description;
  protected final Location location;
  protected final Status status;
  protected final Integer seriesId;

  protected AbstractEvent(
          String subject,
          LocalDateTime start,
          LocalDateTime end,
          String description,
          Location location,
          Status status,
          Integer seriesId) {
    this.subject = subject;
    this.start = start;
    this.end = end;
    this.description = description;
    this.location = location;
    this.status = status;
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
  public String getLocation() {
    return location.toString().toLowerCase();
  }


  @Override
  public String getStatus() {
    return status.toString().toLowerCase();
  }


  @Override
  public Integer getSeriesId() {
    return seriesId;
  }


  // Standard equals/hashCode, only on unique fields for calendar (subject, start, end)
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof IEvent)) return false;
    IEvent other = (IEvent) o;
    return Objects.equals(subject, other.getSubject()) &&
            Objects.equals(start, other.getStart()) &&
            Objects.equals(end, other.getEnd());
  }

  @Override
  public int hashCode() {
    return Objects.hash(subject, start, end);
  }


  // Abstract builder (see below)
  public static abstract class Builder<T extends Builder<T>> {
    private LocalDateTime start;
    private LocalDateTime end;
    private String description;
    private Location location;
    private Status status;
    Integer seriesId;

    public T subject(String subject) {
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
