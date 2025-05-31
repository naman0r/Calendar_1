import static org.junit.Assert.*;
import model.Calendar;
import model.Event;
import model.ICalendar;
import model.IEvent;
import model.Location;
import model.Status;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class CalendarTest {

  private ICalendar calendar;

  @Before
  public void setUp() {
    calendar = new Calendar();
  }

  @Test
  public void testAddEvent() {
    IEvent event = new Event.EventBuilder()
            .subject("Meeting")
            .from(LocalDateTime.of(2025, 6, 1, 10, 0))
            .to(LocalDateTime.of(2025, 6, 1, 11, 0))
            .description("Team sync")
            .location(Location.VIRTUAL)
            .status(Status.PUBLIC)
            .build();

    assertTrue(calendar.addEvent(event));
    assertFalse(calendar.addEvent(event)); // Duplicate not allowed
  }

  @Test
  public void testRemoveEvent() {
    IEvent event = new Event.EventBuilder()
            .subject("Delete Me")
            .from(LocalDateTime.of(2025, 6, 2, 12, 0))
            .to(LocalDateTime.of(2025, 6, 2, 13, 0))
            .build();

    assertTrue(calendar.addEvent(event));
    assertTrue(calendar.removeEvent(event));
    assertFalse(calendar.removeEvent(event)); // Already removed
  }

  @Test
  public void testGetEventsOn() {
    IEvent event1 = new Event.EventBuilder()
            .subject("Morning")
            .from(LocalDateTime.of(2025, 6, 3, 9, 0))
            .to(LocalDateTime.of(2025, 6, 3, 10, 0))
            .build();

    IEvent event2 = new Event.EventBuilder()
            .subject("Afternoon")
            .from(LocalDateTime.of(2025, 6, 3, 15, 0))
            .to(LocalDateTime.of(2025, 6, 3, 16, 0))
            .build();

    calendar.addEvent(event1);
    calendar.addEvent(event2);

    List<IEvent> result = calendar.getEventsOn(LocalDate.of(2025, 6, 3));
    assertEquals(2, result.size());
    assertTrue(result.contains(event1));
    assertTrue(result.contains(event2));

    assertTrue(calendar.getEventsOn(LocalDate.of(2025, 6, 4)).isEmpty());
  }

  @Test
  public void testGetEventsBetween() {
    IEvent event1 = new Event.EventBuilder()
            .subject("Window 1")
            .from(LocalDateTime.of(2025, 6, 4, 8, 0))
            .to(LocalDateTime.of(2025, 6, 4, 9, 0))
            .build();

    IEvent event2 = new Event.EventBuilder()
            .subject("Window 2")
            .from(LocalDateTime.of(2025, 6, 4, 10, 0))
            .to(LocalDateTime.of(2025, 6, 4, 12, 0))
            .build();

    IEvent event3 = new Event.EventBuilder()
            .subject("Window 3")
            .from(LocalDateTime.of(2025, 6, 5, 10, 0))
            .to(LocalDateTime.of(2025, 6, 5, 12, 0))
            .build();

    calendar.addEvent(event1);
    calendar.addEvent(event2);
    calendar.addEvent(event3);

    List<IEvent> result = calendar.getEventsBetween(
            LocalDateTime.of(2025, 6, 4, 7, 0),
            LocalDateTime.of(2025, 6, 4, 11, 0));

    assertEquals(2, result.size());
    assertTrue(result.contains(event1));
    assertTrue(result.contains(event2));
    assertFalse(result.contains(event3));
  }

  @Test
  public void testFindEvent() {
    LocalDateTime start = LocalDateTime.of(2025, 6, 6, 14, 0);
    LocalDateTime end = LocalDateTime.of(2025, 6, 6, 15, 0);

    IEvent event = new Event.EventBuilder()
            .subject("Find Me")
            .from(start)
            .to(end)
            .build();

    calendar.addEvent(event);

    IEvent found = calendar.findEvent("Find Me", start, end);
    assertNotNull(found);
    assertEquals(event, found);

    assertNull(calendar.findEvent("Not Found", start, end));
  }

  @Test
  public void testIsBusy() {
    IEvent event = new Event.EventBuilder()
            .subject("BusyTime")
            .from(LocalDateTime.of(2025, 6, 7, 10, 0))
            .to(LocalDateTime.of(2025, 6, 7, 11, 0))
            .build();

    calendar.addEvent(event);

    assertTrue(calendar.isBusy(LocalDateTime.of(2025, 6, 7, 10, 30)));
    assertFalse(calendar.isBusy(LocalDateTime.of(2025, 6, 7, 9, 0)));
    assertFalse(calendar.isBusy(LocalDateTime.of(2025, 6, 7, 11, 1)));
  }

  @Test
  public void testEditEvent() {
    IEvent event = new Event.EventBuilder()
            .subject("EditMe")
            .from(LocalDateTime.of(2025, 6, 8, 8, 0))
            .to(LocalDateTime.of(2025, 6, 8, 9, 0))
            .description("Original")
            .status(Status.PRIVATE)
            .build();

    calendar.addEvent(event);

    // Edit the description
    boolean edited = calendar.editEvent(event, "description", "Updated Description");
    assertTrue(edited);

    IEvent updated = calendar.findEvent("EditMe", LocalDateTime.of(2025, 6, 8, 8, 0), LocalDateTime.of(2025, 6, 8, 9, 0));
    assertNotNull(updated);
    assertEquals("Updated Description", updated.getDescription());
  }

  @Test
  public void testNoDuplicateAfterEdit() {
    IEvent event1 = new Event.EventBuilder()
            .subject("Dup")
            .from(LocalDateTime.of(2025, 6, 9, 10, 0))
            .to(LocalDateTime.of(2025, 6, 9, 11, 0))
            .build();

    IEvent event2 = new Event.EventBuilder()
            .subject("Dup2")
            .from(LocalDateTime.of(2025, 6, 9, 12, 0))
            .to(LocalDateTime.of(2025, 6, 9, 13, 0))
            .build();

    calendar.addEvent(event1);
    calendar.addEvent(event2);

    // Attempt to edit event2 so it would be a duplicate of event1
    boolean edited = calendar.editEvent(event2, "subject", "Dup");
    assertFalse(edited); // Not allowed
  }
}
