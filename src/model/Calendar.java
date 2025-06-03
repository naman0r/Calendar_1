package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A single‐calendar implementation.  Location and Status are now passed in
 * as enums (or null). If null, the event simply has no location/status.
 */
public class Calendar implements ICalendar {

  private static int series_num = 1;
  private final ArrayList<IEvent> events;

  public Calendar() {
    this.events = new ArrayList<>();
  }

  @Override
  public boolean addEvent(IEvent event) {
    for (IEvent e : this.events) {
      if (e.equals(event)) {
        return false;
      }
    }
    this.events.add(event);
    return true;
  }

  /**
   * Create a single (possibly multi‐hour) event. If end == null, defaults to 08:00→17:00.
   * Location and Status may be null (no defaults).
   */
  @Override
  public boolean makeEvent(
          String subject,
          String description,
          LocalDateTime start,
          LocalDateTime end,
          Location location,
          Status status
  ) {
    if (subject == null || start == null) {
      throw new IllegalArgumentException("Subject and start must be non‐null");
    }

    // If no end, default to 08:00→17:00 on that start date
    if (end == null) {
      LocalDateTime base = start.toLocalDate().atStartOfDay();
      start = base.withHour(8).withMinute(0);
      end   = base.withHour(17).withMinute(0);
    }

    IEvent candidate = SingleEvent.getBuilder()
            .subject(subject)
            .description(description)
            .start(start)
            .end(end)
            .location(location) // may be null
            .status(status)     // may be null
            .build();

    // Duplicate‐check
    for (IEvent e : this.events) {
      if (e.equals(candidate)) {
        return false;
      }
    }

    this.events.add(candidate);
    this.events.sort((a, b) -> a.getStart().compareTo(b.getStart()));
    return true;
  }

  /**
   * Create a recurring series specified by exact count. Each occurrence is single‐day.
   * If end == null, default each occurrence to 08:00→17:00.  Location & Status stay null.
   */
  @Override
  public boolean makeEvent(
          String subject,
          String description,
          LocalDateTime start,
          LocalDateTime end,
          List<java.time.DayOfWeek> days,
          int count
  ) {
    if (subject == null || start == null || days == null || days.isEmpty() || count <= 0) {
      throw new IllegalArgumentException("Invalid arguments for makeEvent(series by count)");
    }

    // If no end, default each to 08:00→17:00
    if (end == null) {
      LocalDateTime base = start.toLocalDate().atStartOfDay();
      start = base.withHour(8).withMinute(0);
      end   = base.withHour(17).withMinute(0);
    }

    List<IEvent> toAdd = new ArrayList<>();
    int occurrences = 0;
    LocalDate cursor = start.toLocalDate();

    while (occurrences < count) {
      for (java.time.DayOfWeek d : days) {
        if (occurrences >= count) break;
        LocalDate nextDate = getNextOrSame(cursor, d);
        LocalDateTime nextStart = nextDate.atTime(start.toLocalTime());
        LocalDateTime nextEnd   = nextDate.atTime(end.toLocalTime());

        IEvent candidate = SeriesEvent.getBuilder()
                .seriesId(series_num)
                .subject(subject)
                .description(description)
                .start(nextStart)
                .end(nextEnd)
                .location(null) // remain null
                .status(null)   // remain null
                .build();

        // Duplicate check against existing events
        for (IEvent e : this.events) {
          if (e.equals(candidate)) {
            return false; // abort entire series
          }
        }
        toAdd.add(candidate);
        occurrences++;
      }
      cursor = cursor.plusWeeks(1);
    }

    this.events.addAll(toAdd);
    this.events.sort((a, b) -> a.getStart().compareTo(b.getStart()));
    series_num++;
    return true;
  }

  /**
   * Create a recurring series until endDate (date‐bounded). Each occurrence is single‐day.
   * If end == null, default each to 08:00→17:00. Location & Status stay null.
   */
  @Override
  public boolean makeEvent(
          String subject,
          String description,
          LocalDateTime start,
          LocalDateTime end,
          List<java.time.DayOfWeek> days,
          LocalDateTime endDate
  ) {
    if (subject == null || start == null || days == null || days.isEmpty() || endDate == null) {
      throw new IllegalArgumentException("Invalid arguments for makeEvent(series until endDate)");
    }

    // If no end, default each to 08:00→17:00
    if (end == null) {
      LocalDateTime base = start.toLocalDate().atStartOfDay();
      start = base.withHour(8).withMinute(0);
      end   = base.withHour(17).withMinute(0);
    }

    // Count occurrences up to endDate
    int occurrences = 0;
    java.time.LocalDate cursor = start.toLocalDate();
    java.time.LocalDate last    = endDate.toLocalDate();

    while (!cursor.isAfter(last)) {
      for (java.time.DayOfWeek d : days) {
        java.time.LocalDate nextDate = getNextOrSame(cursor, d);
        if (!nextDate.isAfter(last)) occurrences++;
      }
      cursor = cursor.plusWeeks(1);
    }
    if (occurrences == 0) return false;

    return makeEvent(subject, description, start, end, days, occurrences);
  }

  /**
   * Create an all‐day event on a single date (08:00→17:00). Location & Status stay null.
   */
  @Override
  public boolean makeEvent(String subject, LocalDateTime start) {
    if (subject == null || start == null) {
      throw new IllegalArgumentException("Invalid arguments for all‐day makeEvent");
    }
    LocalDateTime base = start.toLocalDate().atStartOfDay();
    IEvent candidate = SingleEvent.getBuilder()
            .subject(subject)
            .start(base.withHour(8).withMinute(0))
            .end(base.withHour(17).withMinute(0))
            .location(null) // stay null
            .status(null)   // stay null
            .build();

    for (IEvent e : this.events) {
      if (e.equals(candidate)) {
        return false;
      }
    }
    this.events.add(candidate);
    this.events.sort((a, b) -> a.getStart().compareTo(b.getStart()));
    return true;
  }

  @Override
  public boolean editEvent() {
    // Not used directly; see editSeriesEvent(...) for actual editing logic.
    return false;
  }

  @Override
  public boolean editSeriesEvent(
          String property,
          String subject,
          LocalDateTime start,
          LocalDateTime end,
          String newValue,
          char type
  ) {
    IEvent target = findEvent(subject, start, end);
    if (target == null) {
      return false; // not found or not unique
    }

    if (target.getSeriesId() == null) {
      return editSingleEvent(target, property, newValue);
    } else {
      return editEventSeries(target, property, newValue, type);
    }
  }

  private boolean editSingleEvent(IEvent original, String property, String newValue) {
    this.events.remove(original);
    IEvent updated = createUpdatedEvent(original, property, newValue);
    if (updated == null) {
      this.events.add(original);
      return false;
    }
    for (IEvent e : this.events) {
      if (e.equals(updated)) {
        this.events.add(original);
        return false;
      }
    }
    this.events.add(updated);
    this.events.sort((a, b) -> a.getStart().compareTo(b.getStart()));
    return true;
  }

  @SuppressWarnings("unchecked")
  private boolean editEventSeries(
          IEvent target,
          String property,
          String newValue,
          char type
  ) {
    Integer sid = target.getSeriesId();
    List<IEvent> seriesEvents = this.events.stream()
            .filter(e -> sid.equals(e.getSeriesId()))
            .collect(Collectors.toList());

    List<IEvent> toEdit = new ArrayList<>();
    switch (type) {
      case 'f':
        toEdit = seriesEvents.stream()
                .filter(e -> !e.getStart().isBefore(target.getStart()))
                .collect(Collectors.toList());
        break;
      case 'e':
        toEdit = new ArrayList<>(seriesEvents);
        break;
      default:
        return false;
    }

    this.events.removeAll(toEdit);

    List<IEvent> updatedList = new ArrayList<>();
    for (IEvent evt : toEdit) {
      IEvent updated = createUpdatedEvent(evt, property, newValue);
      if (updated == null) {
        this.events.addAll(toEdit);
        return false;
      }
      updatedList.add(updated);
    }
    for (IEvent upd : updatedList) {
      for (IEvent ex : this.events) {
        if (ex.equals(upd)) {
          this.events.addAll(toEdit);
          return false;
        }
      }
    }

    if ("start".equalsIgnoreCase(property)) {
      int newSid = series_num++;
      List<IEvent> reassign = new ArrayList<>();
      for (IEvent upd : updatedList) {
        reassign.add(SeriesEvent.getBuilder()
                .subject(upd.getSubject())
                .start(upd.getStart())
                .end(upd.getEnd())
                .description(upd.getDescription())
                .location(upd.getLocation()) // may be null
                .status(upd.getStatus())     // may be null
                .seriesId(newSid)
                .build());
      }
      updatedList = reassign;
    }

    this.events.addAll(updatedList);
    this.events.sort((a, b) -> a.getStart().compareTo(b.getStart()));
    return true;
  }

  /**
   * Rebuild a SingleEvent or SeriesEvent but with one field changed.
   * Returns null on failure (invalid property or parse failure, etc.).
   */
  @SuppressWarnings("unchecked")
  private IEvent createUpdatedEvent(IEvent original, String property, String newValue) {
    try {
      switch (property.toLowerCase()) {
        case "subject":
          return recreateWithProperty(original, "subject", (Object) newValue);
        case "start":
          LocalDateTime ns = LocalDateTime.parse(newValue);
          return recreateWithProperty(original, "start", ns);
        case "end":
          LocalDateTime ne = LocalDateTime.parse(newValue);
          return recreateWithProperty(original, "end", ne);
        case "description":
          return recreateWithProperty(original, "description", (Object) newValue);
        case "location":
          // Parse into enum; if newValue is empty or null, location becomes null
          Location loc = (newValue == null || newValue.trim().isEmpty())
                  ? null
                  : Location.valueOfStrict(newValue);
          return recreateWithProperty(original, "location", loc);
        case "status":
          Status st = (newValue == null || newValue.trim().isEmpty())
                  ? null
                  : Status.valueOfStrict(newValue);
          return recreateWithProperty(original, "status", st);
        default:
          return null;
      }
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * Helper to rebuild either a SingleEvent or SeriesEvent from 'original', changing one property.
   */
  private IEvent recreateWithProperty(IEvent original, String property, Object value) {
    if (original.getSeriesId() != null) {
      SeriesEvent.Builder b = SeriesEvent.getBuilder()
              .subject(original.getSubject())
              .start(original.getStart())
              .end(original.getEnd())
              .description(original.getDescription())
              .location(original.getLocation())
              .status(original.getStatus())
              .seriesId(original.getSeriesId());

      switch (property) {
        case "subject":     b.subject((String) value); break;
        case "start":       b.start((LocalDateTime) value); break;
        case "end":         b.end((LocalDateTime) value); break;
        case "description": b.description((String) value); break;
        case "location":    b.location((Location) value); break;
        case "status":      b.status((Status) value); break;
      }
      return b.build();
    } else {
      SingleEvent.Builder b = SingleEvent.getBuilder()
              .subject(original.getSubject())
              .start(original.getStart())
              .end(original.getEnd())
              .description(original.getDescription())
              .location(original.getLocation())
              .status(original.getStatus());

      switch (property) {
        case "subject":     b.subject((String) value); break;
        case "start":       b.start((LocalDateTime) value); break;
        case "end":         b.end((LocalDateTime) value); break;
        case "description": b.description((String) value); break;
        case "location":    b.location((Location) value); break;
        case "status":      b.status((Status) value); break;
      }
      return b.build();
    }
  }

  @Override
  public List<IEvent> getEventsOnDate(LocalDate date) {
    return this.events.stream()
            .filter(e -> {
              LocalDate s = e.getStart().toLocalDate();
              LocalDate en = e.getEnd().toLocalDate();
              return !date.isBefore(s) && !date.isAfter(en);
            })
            .collect(Collectors.toList());
  }

  @Override
  public List<IEvent> getEventsInRange(LocalDateTime start, LocalDateTime end) {
    return this.events.stream()
            .filter(e -> e.getStart().isBefore(end) && e.getEnd().isAfter(start))
            .collect(Collectors.toList());
  }

  @Override
  public boolean isBusyAt(LocalDateTime dateTime) {
    return this.events.stream()
            .anyMatch(e -> !dateTime.isBefore(e.getStart()) && dateTime.isBefore(e.getEnd()));
  }

  @Override
  public IEvent findEvent(String subject, LocalDateTime start, LocalDateTime end) {
    List<IEvent> found = this.events.stream()
            .filter(e ->
                    e.getSubject().equals(subject) &&
                            e.getStart().equals(start) &&
                            e.getEnd().equals(end)
            )
            .collect(Collectors.toList());
    return (found.size() == 1) ? found.get(0) : null;
  }

  @Override
  public boolean removeEvent(IEvent event) {
    return this.events.remove(event);
  }

  private static java.time.LocalDate getNextOrSame(
          java.time.LocalDate from,
          java.time.DayOfWeek desired
  ) {
    int diff = (desired.getValue() - from.getDayOfWeek().getValue() + 7) % 7;
    return from.plusDays(diff);
  }
}
