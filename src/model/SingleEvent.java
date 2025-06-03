package model;

import java.time.LocalDateTime;

/**
 * A one‐day (or multi‐hour) event.  If end == null, builder will default to 08:00→17:00.
 * Location and Status remain null unless explicitly set by the caller.
 */
public final class SingleEvent extends AbstractEvent {

  private SingleEvent(Builder builder) {
    super(
            builder.subject,
            builder.start,
            builder.end,
            builder.description,
            builder.location,
            builder.status,
            null // single‐event has no seriesId
    );
  }

  public static Builder getBuilder() {
    return new Builder();
  }

  @Override
  public boolean isAllDay() {
    if (start == null || end == null) return false;
    return (start.getHour() == 8 && start.getMinute() == 0)
            && (end.getHour()   == 17 && end.getMinute()   == 0)
            && start.toLocalDate().equals(end.toLocalDate());
  }

  @Override
  public boolean overlapsWith(IEvent other) {
    if (other == null) return false;
    if (other.getEnd() == null) return false;
    return this.start.isBefore(other.getEnd()) && other.getStart().isBefore(this.end);
  }

  public static class Builder extends AbstractEvent.Builder<Builder> {
    @Override
    protected Builder self() {
      return this;
    }

    @Override
    public SingleEvent build() {
      if (subject == null || subject.trim().isEmpty()) {
        throw new IllegalStateException("Subject is required for SingleEvent");
      }
      if (start == null) {
        throw new IllegalStateException("Start date/time is required for SingleEvent");
      }
      // If caller did not set an end time, treat as all‐day (08:00→17:00).
      if (end == null) {
        LocalDateTime base = start.toLocalDate().atStartOfDay();
        this.start = base.withHour(8).withMinute(0);
        this.end   = base.withHour(17).withMinute(0);
      }
      // Note: location and status remain exactly as caller provided (possibly null).
      return new SingleEvent(this);
    }
  }
}
