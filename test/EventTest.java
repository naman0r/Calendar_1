import static org.junit.Assert.*;

import model.Event;
import model2.Location;
import model2.Status;
import org.junit.Test;

import java.time.LocalDateTime;

public class EventTest {

  @Test
  public void testCreateEventWithAllFields() {
    LocalDateTime start = LocalDateTime.of(2025, 6, 1, 10, 0);
    LocalDateTime end = LocalDateTime.of(2025, 6, 1, 11, 0);
    Event event = new Event.EventBuilder()
            .subject("Test Event")
            .from(start)
            .to(end)
            .description("Description here")
            .location(Location.PHYSICAL)
            .status(Status.PUBLIC)
            .build();

    assertEquals("Test Event", event.getSubject());
    assertEquals(start, event.getStart());
    assertEquals(end, event.getEnd());
    assertEquals("Description here", event.getDescription());
    assertEquals("physical", event.getLocation());
    assertEquals("public", event.getStatus());
    assertFalse(event.isAllDay());
  }

  @Test
  public void testAllDayEventDefaultsTime() {
    // If allDay is true and no end given, start = 8am, end = 5pm
    LocalDateTime start = LocalDateTime.of(2025, 6, 1, 0, 0); // Only date part matters
    Event event = new Event.EventBuilder()
            .subject("All Day Event")
            .from(start)
            .allDay(true)
            .build();

    assertEquals("All Day Event", event.getSubject());
    assertEquals(LocalDateTime.of(2025, 6, 1, 8, 0), event.getStart());
    assertEquals(LocalDateTime.of(2025, 6, 1, 17, 0), event.getEnd());
    assertTrue(event.isAllDay());
  }

  @Test
  public void testEqualityAndHashCode() {
    LocalDateTime start = LocalDateTime.of(2025, 6, 1, 10, 0);
    LocalDateTime end = LocalDateTime.of(2025, 6, 1, 11, 0);

    Event event1 = new Event.EventBuilder()
            .subject("Event")
            .from(start)
            .to(end)
            .build();

    Event event2 = new Event.EventBuilder()
            .subject("Event")
            .from(start)
            .to(end)
            .build();

    assertEquals(event1, event2);
    assertEquals(event1.hashCode(), event2.hashCode());
  }

  @Test
  public void testOverlap() {
    // 10:00-11:00
    Event event1 = new Event.EventBuilder()
            .subject("A")
            .from(LocalDateTime.of(2025, 6, 1, 10, 0))
            .to(LocalDateTime.of(2025, 6, 1, 11, 0))
            .build();

    // 10:30-11:30 overlaps
    Event event2 = new Event.EventBuilder()
            .subject("B")
            .from(LocalDateTime.of(2025, 6, 1, 10, 30))
            .to(LocalDateTime.of(2025, 6, 1, 11, 30))
            .build();

    // 11:01-12:00 does not overlap
    Event event3 = new Event.EventBuilder()
            .subject("C")
            .from(LocalDateTime.of(2025, 6, 1, 11, 1))
            .to(LocalDateTime.of(2025, 6, 1, 12, 0))
            .build();

    assertTrue(event1.overlapsWith(event2));
    assertFalse(event1.overlapsWith(event3));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testBuilderThrowsIfNoSubject() {
    new Event.EventBuilder()
            .from(LocalDateTime.of(2025, 6, 1, 10, 0))
            .build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testBuilderThrowsIfNoStart() {
    new Event.EventBuilder()
            .subject("Missing start")
            .build();
  }
}
