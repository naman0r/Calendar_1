package model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class Calendar implements ICalendar {

  private static int series_num = 1;
  public final ArrayList<IEvent> events;

  public Calendar() {
    this.events = new ArrayList<>();
  }

  public boolean addEvent(IEvent event) {

    for (IEvent e : this.events) {
      if (e.equals(event)) {
        return false;
      }
    }

    this.events.add(event);
    return true;
  }


  public boolean makeEvent(String subject, String description, LocalDateTime start, LocalDateTime end,
                           String location, String status) {

    if (end == null) {
      // 8 am to 5 pm all day event.
      end = start.withHour(17).withMinute(0).withSecond(0);
      start = start.withHour(8).withMinute(0).withSecond(0);
    }

    IEvent eventToBeAdded = SingleEvent.getBuilder().subject(subject)
            .description(description).start(start).end(end)
            .location(location != null ? new Location(location) : new Location(""))
            .status(status != null ? new Status(status) : Status.PUBLIC).build();

    for (IEvent e : this.events) {
      if (e.equals(eventToBeAdded)) {
        return false;
      }
    }

    // need to rreplace this with insertion sort insertion.
    events.add(eventToBeAdded);
    events.sort((a, b) -> a.getStart().compareTo(b.getStart()));
    return true;
  }

  // creates recurring events on Start - end on 'Days' days count times.
  @Override
  public boolean makeEvent(String subject, String description, LocalDateTime start, LocalDateTime end,
                           List<DayOfWeek> days, int count) {


    // subject needs to exist, and start needs to exist, and days needs to exist,
    // and count should be possitive non-zero.
    if (subject == null || start == null ||
            days == null || days.isEmpty() || count <= 0) {
      throw new IllegalArgumentException("Invalid Arguments to make a series event" +
              "that occurs a certain number of times on some days");

    }

    if (end == null) {
      // making it an 8-5 event if start or end is null.
      end = start.withHour(17).withMinute(0).withSecond(0);
      start = start.withHour(8).withMinute(0).withSecond(0);
    }

    ArrayList<IEvent> eventsToBeAdded = new ArrayList<IEvent>();
    int occurances = 0;
    LocalDate currentDate = start.toLocalDate();

    while (occurances < count) {
      for (DayOfWeek d : days) {
        if (occurances >= count) break;
        
        // find the next date with this DayOfWeek, starting from currentDate
        LocalDate nextDate = getNextOrSame(currentDate, d);
        LocalDateTime nextStart = nextDate.atTime(start.toLocalTime());
        LocalDateTime nextEnd = nextDate.atTime(end.toLocalTime());

        IEvent proposed = SeriesEvent.getBuilder()
                .seriesId(series_num)
                .subject(subject)
                .description(description)
                .start(nextStart)
                .end(nextEnd)
                .location(new Location(""))
                .status(Status.PUBLIC)
                .build();

        for (IEvent e : this.events) {
          if (e.equals(proposed)) {
            // duplicate check, fail all....
            return false;

          }
        }

        eventsToBeAdded.add(proposed);
        occurances++;
      }
      // Move to next week after processing all days
      currentDate = currentDate.plusWeeks(1);
    }

    // if it has not failed yet, means that no duplicates, events can be added.
    // add the events...
    this.events.addAll(eventsToBeAdded);
    this.events.sort((a, b) -> a.getStart().compareTo(b.getStart()));
    series_num++; // increment the series_num of the CALENDAR class,
    // not an object of the class, so this means we should not use the this keyword.
    return true;

  }

  public boolean makeEvent(
          String subject,
          String description,
          LocalDateTime start,
          LocalDateTime end,
          List<DayOfWeek> days,
          LocalDateTime endDate) {
    // just figure out the number of occurances, and then call the method above (to
    // create a series event with an explicit number of count days

    // Input checks
    if (subject == null || start == null || days == null || days.isEmpty() || endDate == null) {
      throw new IllegalArgumentException("Invalid arguments to make a series event until end date");
    }

    // All-day event logic if end is null
    if (end == null) {
      end = start.withHour(17).withMinute(0).withSecond(0);
      start = start.withHour(8).withMinute(0).withSecond(0);
    }

    // Find the number of occurrences
    int occurrences = 0;
    LocalDate currentDate = start.toLocalDate();
    LocalDate lastDate = endDate.toLocalDate();
    
    while (!currentDate.isAfter(lastDate)) {
      for (DayOfWeek d : days) {
        LocalDate nextDate = getNextOrSame(currentDate, d);
        // Only count events within the allowed date range
        if (!nextDate.isAfter(lastDate)) {
          occurrences++;
        }
      }
      currentDate = currentDate.plusWeeks(1);
    }

    if (occurrences == 0) return false; // No events in range

    // Use the existing method
    return makeEvent(subject, description, start, end, days, occurrences);

  }


  // duplicate code, remove from interface and remove this method (or make
  // end date == null case call this method instead)
  public boolean makeEvent(String subject, LocalDateTime start) {
    if (subject == null || start == null) {
      throw new IllegalArgumentException("Invalid arguments to make a" +
              "Single event until start date");
    }

    IEvent toBeAdded = SingleEvent.getBuilder().subject(subject)
            .start(start.withHour(8).withMinute(0).withSecond(0))
            .end(start.withHour(17).withMinute(0).withSecond(0))
            .build();

    for (IEvent e : this.events) {
      if (e.equals(toBeAdded)) {
        return false;
      }

    }
    this.events.add(toBeAdded);
    this.events.sort((a, b) -> a.getStart().compareTo(b.getStart()));
    return true;
  }


  @Override
  public boolean editEvent() {
    // This method signature is incomplete - not implementing as interface needs improvement
    return false;
  }

  @Override
  public boolean editSeriesEvent(String property, String subject, LocalDateTime start, LocalDateTime end, String newValue, char type) {
    // Find the event to edit
    IEvent targetEvent = findEvent(subject, start, end);
    if (targetEvent == null) {
      return false; // Event not found
    }
    
    if (targetEvent.getSeriesId() == null) {
      // Single event - just edit this one
      return editSingleEvent(targetEvent, property, newValue);
    } else {
      // Series event - handle based on type
      return editEventSeries(targetEvent, property, newValue, type);
    }
  }
  
  private boolean editSingleEvent(IEvent event, String property, String newValue) {
    // Remove old event
    this.events.remove(event);
    
    // Create new event with updated property
    IEvent updatedEvent = createUpdatedEvent(event, property, newValue);
    if (updatedEvent == null) {
      // Re-add original event if update failed
      this.events.add(event);
      return false;
    }
    
    // Check for duplicates
    for (IEvent e : this.events) {
      if (e.equals(updatedEvent)) {
        // Re-add original event and fail
        this.events.add(event);
        return false;
      }
    }
    
    this.events.add(updatedEvent);
    this.events.sort((a, b) -> a.getStart().compareTo(b.getStart()));
    return true;
  }
  
  private boolean editEventSeries(IEvent targetEvent, String property, String newValue, char type) {
    Integer seriesId = targetEvent.getSeriesId();
    List<IEvent> seriesEvents = this.events.stream()
        .filter(e -> seriesId.equals(e.getSeriesId()))
        .collect(Collectors.toList());
    
    List<IEvent> eventsToEdit = new ArrayList<>();
    
    switch (type) {
      case 'f': // Forward - this event and all after it
        eventsToEdit = seriesEvents.stream()
            .filter(e -> !e.getStart().isBefore(targetEvent.getStart()))
            .collect(Collectors.toList());
        break;
      case 'e': // Entire series
        eventsToEdit = seriesEvents;
        break;
      default:
        return false; // Invalid type
    }
    
    // Remove old events
    this.events.removeAll(eventsToEdit);
    
    // Create updated events
    List<IEvent> updatedEvents = new ArrayList<>();
    for (IEvent event : eventsToEdit) {
      IEvent updated = createUpdatedEvent(event, property, newValue);
      if (updated == null) {
        // Restore original events and fail
        this.events.addAll(eventsToEdit);
        return false;
      }
      updatedEvents.add(updated);
    }
    
    // Check for duplicates
    for (IEvent updated : updatedEvents) {
      for (IEvent existing : this.events) {
        if (existing.equals(updated)) {
          // Restore original events and fail
          this.events.addAll(eventsToEdit);
          return false;
        }
      }
    }
    
    // If start time is being changed, these events may no longer be a series
    if ("start".equals(property)) {
      // Assign new series IDs if needed
      int newSeriesId = series_num++;
      List<IEvent> newSeriesEvents = new ArrayList<>();
      for (IEvent event : updatedEvents) {
        newSeriesEvents.add(createEventWithNewSeriesId(event, newSeriesId));
      }
      updatedEvents = newSeriesEvents;
    }
    
    this.events.addAll(updatedEvents);
    this.events.sort((a, b) -> a.getStart().compareTo(b.getStart()));
    return true;
  }
  
  private IEvent createUpdatedEvent(IEvent original, String property, String newValue) {
    try {
      switch (property.toLowerCase()) {
        case "subject":
          return createEventWithProperty(original, "subject", newValue);
        case "start":
          LocalDateTime newStart = LocalDateTime.parse(newValue);
          return createEventWithProperty(original, "start", newStart);
        case "end":
          LocalDateTime newEnd = LocalDateTime.parse(newValue);
          return createEventWithProperty(original, "end", newEnd);
        case "description":
          return createEventWithProperty(original, "description", newValue);
        case "location":
          return createEventWithProperty(original, "location", new Location(newValue));
        case "status":
          return createEventWithProperty(original, "status", new Status(newValue));
        default:
          return null;
      }
    } catch (Exception e) {
      return null;
    }
  }
  
  private IEvent createEventWithProperty(IEvent original, String property, Object value) {
    if (original.getSeriesId() != null) {
      SeriesEvent.Builder builder = SeriesEvent.getBuilder()
          .subject(original.getSubject())
          .start(original.getStart())
          .end(original.getEnd())
          .description(original.getDescription())
          .location(new Location(original.getLocation()))
          .status(new Status(original.getStatus()))
          .seriesId(original.getSeriesId());
      
      switch (property) {
        case "subject": builder.subject((String) value); break;
        case "start": builder.start((LocalDateTime) value); break;
        case "end": builder.end((LocalDateTime) value); break;
        case "description": builder.description((String) value); break;
        case "location": builder.location((Location) value); break;
        case "status": builder.status((Status) value); break;
      }
      return builder.build();
    } else {
      SingleEvent.Builder builder = SingleEvent.getBuilder()
          .subject(original.getSubject())
          .start(original.getStart())
          .end(original.getEnd())
          .description(original.getDescription())
          .location(new Location(original.getLocation()))
          .status(new Status(original.getStatus()));
      
      switch (property) {
        case "subject": builder.subject((String) value); break;
        case "start": builder.start((LocalDateTime) value); break;
        case "end": builder.end((LocalDateTime) value); break;
        case "description": builder.description((String) value); break;
        case "location": builder.location((Location) value); break;
        case "status": builder.status((Status) value); break;
      }
      return builder.build();
    }
  }
  
  private IEvent createEventWithNewSeriesId(IEvent original, int newSeriesId) {
    return SeriesEvent.getBuilder()
        .subject(original.getSubject())
        .start(original.getStart())
        .end(original.getEnd())
        .description(original.getDescription())
        .location(new Location(original.getLocation()))
        .status(new Status(original.getStatus()))
        .seriesId(newSeriesId)
        .build();
  }

  @Override
  public List<IEvent> getEventsOnDate(LocalDate date) {
    return this.events.stream()
        .filter(event -> {
          LocalDate eventStart = event.getStart().toLocalDate();
          LocalDate eventEnd = event.getEnd().toLocalDate();
          return !date.isBefore(eventStart) && !date.isAfter(eventEnd);
        })
        .collect(Collectors.toList());
  }

  @Override
  public List<IEvent> getEventsInRange(LocalDateTime start, LocalDateTime end) {
    return this.events.stream()
        .filter(event -> {
          // Event overlaps with range if:
          // event start is before range end AND event end is after range start
          return event.getStart().isBefore(end) && event.getEnd().isAfter(start);
        })
        .collect(Collectors.toList());
  }

  @Override
  public boolean isBusyAt(LocalDateTime dateTime) {
    return this.events.stream()
        .anyMatch(event -> 
            !dateTime.isBefore(event.getStart()) && dateTime.isBefore(event.getEnd()));
  }

  @Override
  public IEvent findEvent(String subject, LocalDateTime start, LocalDateTime end) {
    List<IEvent> matches = this.events.stream()
        .filter(event -> 
            event.getSubject().equals(subject) && 
            event.getStart().equals(start) && 
            event.getEnd().equals(end))
        .collect(Collectors.toList());
    
    // Return null if not found or not unique
    return matches.size() == 1 ? matches.get(0) : null;
  }

  @Override
  public boolean removeEvent(IEvent event) {
    return this.events.remove(event);
  }


  private static LocalDate getNextOrSame(LocalDate from, DayOfWeek desired) {
    int daysDiff = (desired.getValue() - from.getDayOfWeek().getValue() + 7) % 7;
    return from.plusDays(daysDiff);
  }
}
