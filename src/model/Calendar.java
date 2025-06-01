package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Calendar implements ICalendar {

  private final List<IEvent> events;

  public Calendar() {
    this.events = new ArrayList<>();
  }

  /**
   * Adds an event to the calendar, keeping the list sorted by start datetime.
   * Returns false if a duplicate event (by subject, start, end) exists.
   */
  @Override
  public boolean addEvent(IEvent event) {
    for (IEvent e : events) {
      if (e.getSubject().equals(event.getSubject()) &&
              e.getStart().equals(event.getStart()) &&
              e.getEnd().equals(event.getEnd())) {
        return false; // Duplicate
      }
    }
    // Insert in sorted order by start time
    int idx = Collections.binarySearch(
            events,
            event,
            (a, b) -> a.getStart().compareTo(b.getStart())
    );
    // binarySearch returns negative insertion point - 1 if not found
    if (idx < 0) {
      idx = -(idx + 1);
    }
    events.add(idx, event);
    return true;
  }

  @Override
  public List<IEvent> getEventsOnDate(LocalDate date) {
    List<IEvent> result = new ArrayList<>();
    for (IEvent e : events) {
      if (!e.getStart().toLocalDate().isAfter(date) &&
              !e.getEnd().toLocalDate().isBefore(date)) {
        result.add(e);
      }
    }
    return result;
  }

  @Override
  public List<IEvent> getEventsInRange(LocalDateTime start, LocalDateTime end) {
    List<IEvent> result = new ArrayList<>();
    for (IEvent e : events) {
      if (!e.getEnd().isBefore(start) && !e.getStart().isAfter(end)) {
        result.add(e);
      }
    }
    return result;
  }

  @Override
  public boolean isBusyAt(LocalDateTime dateTime) {
    for (IEvent e : events) {
      if (!dateTime.isBefore(e.getStart()) && !dateTime.isAfter(e.getEnd())) {
        return true;
      }
    }
    return false;
  }

  @Override
  public IEvent findEvent(String subject, LocalDateTime start, LocalDateTime end) {
    IEvent found = null;
    for (IEvent e : events) {
      if (e.getSubject().equals(subject) &&
              e.getStart().equals(start) &&
              e.getEnd().equals(end)) {
        if (found != null) return null; // Not unique
        found = e;
      }
    }
    return found;
  }

  @Override
  public boolean removeEvent(IEvent event) {
    Iterator<IEvent> it = events.iterator();
    while (it.hasNext()) {
      IEvent e = it.next();
      if (e.getSubject().equals(event.getSubject()) &&
              e.getStart().equals(event.getStart()) &&
              e.getEnd().equals(event.getEnd())) {
        it.remove();
        return true;
      }
    }
    return false;
  }

  // For testing/inspection purposes
  public List<IEvent> getAllEvents() {
    return Collections.unmodifiableList(events);
  }
}
