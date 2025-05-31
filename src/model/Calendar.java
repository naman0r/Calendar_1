package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Calendar implements ICalendar {

  private final List<IEvent> events;


  public Calendar(){
    this.events = new ArrayList<>();
  }


  @Override
  public boolean addEvent(IEvent event) {
    for (IEvent e: events) {
      if (e.equals(event)) {
        return false;
      }
    }

    events.add(event);
    return true;
  }

  @Override
  public boolean removeEvent(IEvent event) {
    return  events.remove(event);
  }

  @Override
  public List<IEvent> getEventsOn(LocalDate date) {
    List<IEvent> result = new ArrayList<>();
    for (IEvent e: events) {
      if (e.getStart().toLocalDate().equals(date)) {
        result.add(e);
      }
    }

    return result;
  }

  @Override
  public List<IEvent> getEventsBetween(LocalDateTime start, LocalDateTime end) {
    List<IEvent> result = new ArrayList<>();
    for (IEvent e : events) {
      // Event's interval overlaps with [start, end]
      LocalDateTime eStart = e.getStart();
      LocalDateTime eEnd = e.getEnd();
      if (eStart.isBefore(end.plusSeconds(1)) && (eEnd != null && eEnd.isAfter(start.minusSeconds(1)))) {
        result.add(e);
      }
    }
    return result;
  }



  @Override
  public IEvent findEvent(String subject, LocalDateTime start, LocalDateTime end) {
    for (IEvent e : events) {
      if (Objects.equals(e.getSubject(), subject)
              && e.getStart().equals(start)
              && (e.getEnd() == null ? end == null : e.getEnd().equals(end))) {
        return e;
      }
    }
    return null;
  }

  @Override
  public boolean isBusy(LocalDateTime dateTime) {
    for (IEvent e : events) {
      LocalDateTime eStart = e.getStart();
      LocalDateTime eEnd = e.getEnd();
      if (!dateTime.isBefore(eStart) && (eEnd == null || !dateTime.isAfter(eEnd))) {
        return true;
      }
    }
    return false;
  }



  @Override
  public boolean editEvent(IEvent event, String property, Object newValue) {
    // Remove the event and add a new one with updated field
    int idx = events.indexOf(event);
    if (idx == -1) return false; // Not found

    IEvent oldEvent = events.get(idx);
    Event.EventBuilder builder = new Event.EventBuilder()
            .subject(oldEvent.getSubject())
            .from(oldEvent.getStart())
            .to(oldEvent.getEnd())
            .description(oldEvent.getDescription())
            .allDay(oldEvent.isAllDay());

    // This assumes location/status fields are enums in Event
    if (oldEvent.getLocation() != null) {
      try {
        builder.location(Location.valueOf(oldEvent.getLocation().toUpperCase()));
      } catch (Exception ignored) {}
    }
    if (oldEvent.getStatus() != null) {
      try {
        builder.status(Status.valueOf(oldEvent.getStatus().toUpperCase()));
      } catch (Exception ignored) {}
    }

    // Update specified property
    switch (property.toLowerCase()) {
      case "subject":
        builder.subject((String) newValue);
        break;
      case "start":
        builder.from((LocalDateTime) newValue);
        break;
      case "end":
        builder.to((LocalDateTime) newValue);
        break;
      case "description":
        builder.description((String) newValue);
        break;
      case "location":
        builder.location((Location) newValue);
        break;
      case "status":
        builder.status((Status) newValue);
        break;
      default:
        return false;
    }

    IEvent newEvent = builder.build();

    // Enforce uniqueness
    for (IEvent e : events) {
      if (e.equals(newEvent) && !e.equals(oldEvent)) {
        return false; // would create duplicate
      }
    }

    events.remove(idx);
    events.add(newEvent);
    return true;
  }

  @Override
  public List<IEvent> getAllEvents() {
    return Collections.unmodifiableList(events);
  }
}
