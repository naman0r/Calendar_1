package model;

import java.time.LocalDateTime;

public final class SingleEvent extends AbstractEvent {

  // Only constructed by the Builder
  private SingleEvent(Builder builder) {
    super(
            builder.subject,
            builder.start,
            builder.end,
            builder.description,
            builder.location,
            builder.status,
            null // SingleEvent: always null seriesId
    );
  }

  public static Builder getBuilder() {
    return new Builder();
  }

  @Override
  public boolean isAllDay() {
    // An event is all-day if it starts at 8am and ends at 5pm on the same day
    return start.getHour() == 8 && start.getMinute() == 0 &&
           end.getHour() == 17 && end.getMinute() == 0 &&
           start.toLocalDate().equals(end.toLocalDate());
  }

  @Override
  public boolean overlapsWith(IEvent other) {
    if (other == null) return false;
    // Two events overlap if one starts before the other ends
    return this.start.isBefore(other.getEnd()) && other.getStart().isBefore(this.end);
  }

  public static class Builder {
    private String subject;
    private LocalDateTime start;
    private LocalDateTime end;
    private String description;
    private Location location;
    private Status status;

    private Builder() {}

    public Builder subject(String subject) {
      this.subject = subject;
      return this;
    }

    public Builder start(LocalDateTime start) {
      this.start = start;
      return this;
    }

    public Builder end(LocalDateTime end) {
      this.end = end;
      return this;
    }

    public Builder description(String description) {
      this.description = description;
      return this;
    }

    public Builder location(Location location) {
      this.location = location;
      return this;
    }

    public Builder status(Status status) {
      this.status = status;
      return this;
    }

    public SingleEvent build() {
      if (subject == null || start == null) {
        throw new IllegalStateException("Subject and start time required");
      }
      // If end is null, set to all-day default (8am-5pm)
      if (end == null) {
        this.start = this.start.toLocalDate().atTime(8, 0);
        this.end = this.start.toLocalDate().atTime(17, 0);
      }
      
      // Set defaults if not provided
      if (location == null) {
        this.location = new Location("");
      }
      if (status == null) {
        this.status = Status.PUBLIC;
      }
      
      return new SingleEvent(this);
    }
  }
}
