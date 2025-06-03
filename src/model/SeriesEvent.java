package model;

import java.time.LocalDateTime;

/**
 * A single occurrence in a recurring series. Must start/end on the same calendar day.
 * Location and Status remain null unless explicitly set by the caller.
 */
public final class SeriesEvent extends AbstractEvent {

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
    public SeriesEvent build() {
      if (subject == null || subject.trim().isEmpty()) {
        throw new IllegalStateException("Subject is required for SeriesEvent");
      }
      if (start == null || end == null) {
        throw new IllegalStateException("Both start and end are required for SeriesEvent");
      }
      if (!start.toLocalDate().equals(end.toLocalDate())) {
        throw new IllegalArgumentException("SeriesEvent must start and end on the same date");
      }
      if (seriesId == null) {
        throw new IllegalStateException("seriesId must be provided by Calendar");
      }
      // Location and Status are left exactly as caller provided (possibly null).
      return new SeriesEvent(this);
    }
  }
}
