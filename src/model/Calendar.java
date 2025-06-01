package model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

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

    IEvent eventToBeAdded = SeriesEvent.getBuilder().subject(subject)
            .description(description).start(start).end(end).location(Location.valueOf(location))
            .status(Status.valueOf(status)).build();

    for (IEvent e : this.events) {
      if (e.equals(eventToBeAdded)) {
        return false;
      }
    }

    // need to rreplace this with insertion sort insertion.
    events.add(eventToBeAdded);
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
    LocalDate startDate = start.toLocalDate(); // getting the start date.

    while (occurances < count) {
      for (DayOfWeek d : days) {
        // find the next date with this DayOfWeek, starting from startDate

        LocalDate nextDate = getNextOrSame(startDate, d);
        LocalDateTime nextStart = nextDate.atTime(start.toLocalTime());
        LocalDateTime nextEnd = nextDate.atTime(end.toLocalTime());

        IEvent proposed = SeriesEvent.getBuilder()
                .seriesId(series_num)
                .subject(subject)
                .description(description)
                .start(nextStart)
                .end(nextEnd)
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





  // STUBS FOR UNIMPLEMENTED METHODS!!!!!
  @Override
  public boolean editEvent() {
    return false;
  }

  @Override
  public boolean editSeriesEvent(String property, String subject, LocalDateTime start, LocalDateTime end, String newValue, char type) {
    return false;
  }

  @Override
  public List<IEvent> getEventsOnDate(LocalDate date) {
    return List.of();
  }

  @Override
  public List<IEvent> getEventsInRange(LocalDateTime start, LocalDateTime end) {
    return List.of();
  }

  @Override
  public boolean isBusyAt(LocalDateTime dateTime) {
    return false;
  }

  @Override
  public IEvent findEvent(String subject, LocalDateTime start, LocalDateTime end) {
    return null;
  }

  @Override
  public boolean removeEvent(IEvent event) {
    return false;
  }


  private static LocalDate getNextOrSame(LocalDate from, DayOfWeek desired) {
    int daysDiff = (desired.getValue() - from.getDayOfWeek().getValue() + 7) % 7;

    return from.plusDays(daysDiff);
  }




}
