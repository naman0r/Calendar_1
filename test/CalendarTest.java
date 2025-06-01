import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;

import model.Calendar;
import model.ICalendar;
import model.IEvent;
import model.Location;
import model.SeriesEvent;
import model.SingleEvent;
import model.Status;

public class CalendarTest {
  ICalendar calendar;

  @Before
  public void setUp() {
    calendar = new Calendar();
  }

  @Test
  public void testCalendarInitialization() {


    SingleEvent e1 = SingleEvent.getBuilder()
            .subject("THis is naman's testevent")
            .start(LocalDateTime.of(2025, 6, 1, 10, 0))
            .build();
    assertFalse(calendar.removeEvent(e1));
    assertTrue(calendar.addEvent(e1));
    assertTrue(calendar.removeEvent(e1));
  }


  @Test
  public void testAddSingleEventSuccess() {
    IEvent event = SingleEvent.getBuilder()
            .subject("Meeting")
            .start(LocalDateTime.of(2025, 6, 1, 10, 0))
            .end(LocalDateTime.of(2025, 6, 1, 11, 0))
            .build();
    assertTrue(calendar.addEvent(event));
    List<IEvent> allEvents = ((Calendar)calendar).getAllEvents();
    assertEquals(1, allEvents.size());
    assertEquals("Meeting", allEvents.get(0).getSubject());
  }

  @Test
  public void testAddDuplicateEventFails() {
    IEvent event1 = SingleEvent.getBuilder()
            .subject("Meeting")
            .start(LocalDateTime.of(2025, 6, 1, 10, 0))
            .end(LocalDateTime.of(2025, 6, 1, 11, 0))
            .build();
    IEvent event2 = SingleEvent.getBuilder()
            .subject("Meeting")
            .start(LocalDateTime.of(2025, 6, 1, 10, 0))
            .end(LocalDateTime.of(2025, 6, 1, 11, 0))
            .build();
    assertTrue(calendar.addEvent(event1));
    assertFalse(calendar.addEvent(event2));
  }

  @Test
  public void testAddEventsAreSorted() {
    IEvent event1 = SingleEvent.getBuilder()
            .subject("Later")
            .start(LocalDateTime.of(2025, 6, 1, 13, 0))
            .end(LocalDateTime.of(2025, 6, 1, 14, 0))
            .build();
    IEvent event2 = SingleEvent.getBuilder()
            .subject("Earlier")
            .start(LocalDateTime.of(2025, 6, 1, 9, 0))
            .end(LocalDateTime.of(2025, 6, 1, 10, 0))
            .build();
    assertTrue(calendar.addEvent(event1));
    assertTrue(calendar.addEvent(event2));
    List<IEvent> allEvents = ((Calendar)calendar).getAllEvents();
    assertEquals("Earlier", allEvents.get(0).getSubject());
    assertEquals("Later", allEvents.get(1).getSubject());
  }



  @Test
  public void testGetEventsOnDate() {
    IEvent event1 = SingleEvent.getBuilder()
            .subject("Morning")
            .start(LocalDateTime.of(2025, 6, 2, 8, 0))
            .end(LocalDateTime.of(2025, 6, 2, 9, 0))
            .build();
    IEvent event2 = SingleEvent.getBuilder()
            .subject("Evening")
            .start(LocalDateTime.of(2025, 6, 2, 17, 0))
            .end(LocalDateTime.of(2025, 6, 2, 18, 0))
            .build();
    calendar.addEvent(event1);
    calendar.addEvent(event2);

    List<IEvent> events = calendar.getEventsOnDate(LocalDate.of(2025, 6, 2));
    assertEquals(2, events.size());
  }


  @Test
  public void testGetEventsOnDateNoMatch() {
    IEvent event = SingleEvent.getBuilder()
            .subject("OutOfRange")
            .start(LocalDateTime.of(2025, 6, 1, 9, 0))
            .end(LocalDateTime.of(2025, 6, 1, 10, 0))
            .build();
    calendar.addEvent(event);
    List<IEvent> events = calendar.getEventsOnDate(LocalDate.of(2025, 6, 2));
    assertTrue(events.isEmpty());
  }


  @Test
  public void testGetEventsInRange() {
    IEvent e1 = SingleEvent.getBuilder().subject("A")
            .start(LocalDateTime.of(2025, 6, 3, 10, 0))
            .end(LocalDateTime.of(2025, 6, 3, 12, 0)).build();
    IEvent e2 = SingleEvent.getBuilder().subject("B")
            .start(LocalDateTime.of(2025, 6, 4, 10, 0))
            .end(LocalDateTime.of(2025, 6, 4, 11, 0)).build();
    IEvent e3 = SingleEvent.getBuilder().subject("C")
            .start(LocalDateTime.of(2025, 6, 5, 10, 0))
            .end(LocalDateTime.of(2025, 6, 5, 11, 0)).build();

    calendar.addEvent(e1);
    calendar.addEvent(e2);
    calendar.addEvent(e3);

    List<IEvent> inRange = calendar.getEventsInRange(
            LocalDateTime.of(2025, 6, 4, 0, 0),
            LocalDateTime.of(2025, 6, 5, 0, 0));
    assertEquals(1, inRange.size());
    assertEquals("B", inRange.get(0).getSubject());
  }


  @Test
  public void testIsBusyAtTrueAndFalse() {
    IEvent event = SingleEvent.getBuilder()
            .subject("BusyTime")
            .start(LocalDateTime.of(2025, 6, 10, 15, 0))
            .end(LocalDateTime.of(2025, 6, 10, 16, 0))
            .build();
    calendar.addEvent(event);
    assertTrue(calendar.isBusyAt(LocalDateTime.of(2025, 6, 10, 15, 30)));
    assertFalse(calendar.isBusyAt(LocalDateTime.of(2025, 6, 10, 16, 1)));
  }


  @Test
  public void testFindEventUnique() {
    IEvent event = SingleEvent.getBuilder()
            .subject("Unique")
            .start(LocalDateTime.of(2025, 6, 8, 12, 0))
            .end(LocalDateTime.of(2025, 6, 8, 13, 0))
            .build();
    calendar.addEvent(event);
    IEvent found = calendar.findEvent("Unique",
            LocalDateTime.of(2025, 6, 8, 12, 0),
            LocalDateTime.of(2025, 6, 8, 13, 0));
    assertNotNull(found);
    assertEquals("Unique", found.getSubject());
  }






  @Test
  public void testRemoveEventSuccess() {
    IEvent event = SingleEvent.getBuilder()
            .subject("RemoveMe")
            .start(LocalDateTime.of(2025, 6, 12, 9, 0))
            .end(LocalDateTime.of(2025, 6, 12, 10, 0))
            .build();
    calendar.addEvent(event);
    assertTrue(calendar.removeEvent(event));
    assertTrue(((Calendar)calendar).getAllEvents().isEmpty());
  }





  @Test
  public void testRemoveEventNotFound() {
    IEvent event = SingleEvent.getBuilder()
            .subject("NotPresent")
            .start(LocalDateTime.of(2025, 6, 13, 11, 0))
            .end(LocalDateTime.of(2025, 6, 13, 12, 0))
            .build();
    assertFalse(calendar.removeEvent(event));
  }


  @Test
  public void testAllDayEventDefaults() {
    IEvent event = SingleEvent.getBuilder()
            .subject("AllDay")
            .start(LocalDateTime.of(2025, 6, 15, 0, 0))
            .build();
    assertEquals(8, event.getStart().getHour());
    assertEquals(17, event.getEnd().getHour());
  }


  @Test
  public void testSeriesEventAddition() {
    IEvent sEvent = SeriesEvent.getBuilder()
            .subject("Recurring")
            .start(LocalDateTime.of(2025, 6, 20, 9, 0))
            .end(LocalDateTime.of(2025, 6, 20, 10, 0))
            .build();
    assertTrue(calendar.addEvent(sEvent));
    assertEquals(1, ((Calendar)calendar).getAllEvents().size());
    assertNotNull(sEvent.getSeriesId());

    System.out.println(((Calendar) calendar).getAllEvents());
  }


  @Test
  public void testSeriesEventBuildAndFields() {
    SeriesEvent event = SeriesEvent.getBuilder()
            .subject("Lecture")
            .start(LocalDateTime.of(2025, 6, 3, 14, 0))
            .end(LocalDateTime.of(2025, 6, 3, 15, 0))
            .description("Weekly lecture")
            .location(Location.VIRTUAL)
            .status(Status.PUBLIC)
            .build();

    assertEquals("Lecture", event.getSubject());
    assertEquals(LocalDateTime.of(2025, 6, 3, 14, 0), event.getStart());
    assertEquals(LocalDateTime.of(2025, 6, 3, 15, 0), event.getEnd());
    assertEquals("Weekly lecture", event.getDescription());
    assertEquals("virtual", event.getLocation());
    assertEquals("public", event.getStatus());
    assertNotNull(event.getSeriesId());

    System.out.println(event.getSeriesId());
  }

  public void testCalendarAddSeriesEvent() {

  }






}