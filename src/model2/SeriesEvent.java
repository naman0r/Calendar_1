package model2;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

public final class SeriesEvent extends AbstractEvent {
  private static final AtomicInteger NEXT_SERIES_ID = new AtomicInteger(1);

  // Only constructed by the Builder
  private SeriesEvent(Builder builder) {
    super(
            builder.subject,
            builder.start,
            builder.end,
            builder.description,
            builder.location,
            builder.status,
            builder.seriesId
    );
  }

  public static Builder getBuilder() {
    return new Builder();
  }

  @Override
  public boolean isAllDay() {
    return false;
  }

  @Override
  public boolean overlapsWith(IEvent other) {
    return false;
  }

  public static class Builder {
    private String subject;
    private LocalDateTime start;
    private LocalDateTime end;
    private String description;
    private Location location;
    private Status status;
    private Integer seriesId;

    private Builder() {
      this.seriesId = NEXT_SERIES_ID.getAndIncrement();
    }

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

    public SeriesEvent build() {
      if (subject == null || start == null || end == null) {
        throw new IllegalStateException("Subject, start, and end required");
      }
      if (!start.toLocalDate().equals(end.toLocalDate())) {
        throw new IllegalArgumentException("SeriesEvent must start and end on the same day");
      }
      return new SeriesEvent(this);
    }
  }
}
