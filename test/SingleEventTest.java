import model.SingleEvent;
import model.Status;
import model.Location;
import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.Test;

public class SingleEventTest {

  @Test(expected = IllegalStateException.class)
  public void buildFailsWhenSubjectIsNull() {
    SingleEvent.getBuilder()
            .start(LocalDateTime.of(2025, 6, 4, 9, 0))
            .build();
  }

  @Test(expected = IllegalStateException.class)
  public void buildFailsWhenSubjectIsEmpty() {
    SingleEvent.getBuilder()
            .subject("   ")
            .start(LocalDateTime.of(2025, 6, 4, 9, 0))
            .build();
  }

  @Test(expected = IllegalStateException.class)
  public void buildFailsWhenStartIsNull() {
    SingleEvent.getBuilder()
            .subject("Meeting")
            .build();
  }

  @Test
  public void defaultAllDayTimesWhenEndIsNull() {
    LocalDateTime anyDate = LocalDate.of(2025, 6, 5).atStartOfDay().withHour(10).withMinute(30);
    // The builder only sets start; end should be forced to 08:00→17:00 on same date
    SingleEvent ev = SingleEvent.getBuilder()
            .subject("All Day")
            .start(anyDate)
            .build();

    // Check that start is 08:00 on that date, end is 17:00 same day
    assertEquals(LocalDateTime.of(2025, 6, 5, 8, 0), ev.getStart());
    assertEquals(LocalDateTime.of(2025, 6, 5, 17, 0), ev.getEnd());

    // isAllDay() should be true
    assertTrue(ev.isAllDay());
  }

  @Test
  public void customTimesWhenEndIsProvided() {
    LocalDateTime s = LocalDateTime.of(2025, 6, 6, 9, 15);
    LocalDateTime e = LocalDateTime.of(2025, 6, 6, 10, 45);
    SingleEvent ev = SingleEvent.getBuilder()
            .subject("One-Hour Meeting")
            .start(s)
            .end(e)
            .build();

    assertEquals(s, ev.getStart());
    assertEquals(e, ev.getEnd());
    // Not an all-day event
    assertFalse(ev.isAllDay());
  }

  @Test
  public void locationAndStatusRemainNullWhenNotSet() {
    LocalDateTime s = LocalDateTime.of(2025, 6, 7, 8, 0);
    LocalDateTime e = LocalDateTime.of(2025, 6, 7, 17, 0);
    SingleEvent ev = SingleEvent.getBuilder()
            .subject("No Location/Status")
            .start(s)
            .end(e)
            .build();

    assertNull(ev.getLocation());
    assertNull(ev.getStatus());
  }

  @Test
  public void locationAndStatusSetCorrectlyWhenProvided() {
    LocalDateTime s = LocalDateTime.of(2025, 6, 8, 14, 0);
    LocalDateTime e = LocalDateTime.of(2025, 6, 8, 15, 0);
    SingleEvent ev = SingleEvent.getBuilder()
            .subject("With Loc/Status")
            .start(s)
            .end(e)
            .location(Location.PHYSICAL)
            .status(Status.PRIVATE)
            .build();

    assertEquals(Location.PHYSICAL, ev.getLocation());
    assertEquals(Status.PRIVATE, ev.getStatus());
    assertFalse(ev.isAllDay());
  }

  @Test
  public void overlapsWithAnotherEvent() {
    LocalDateTime s1 = LocalDateTime.of(2025, 6, 9, 10, 0);
    LocalDateTime e1 = LocalDateTime.of(2025, 6, 9, 11, 0);
    SingleEvent one = SingleEvent.getBuilder()
            .subject("Event1")
            .start(s1)
            .end(e1)
            .build();

    LocalDateTime s2 = LocalDateTime.of(2025, 6, 9, 10, 30);
    LocalDateTime e2 = LocalDateTime.of(2025, 6, 9, 11, 30);
    SingleEvent two = SingleEvent.getBuilder()
            .subject("Event2")
            .start(s2)
            .end(e2)
            .build();

    assertTrue(one.overlapsWith(two));
    assertTrue(two.overlapsWith(one));
  }

  @Test
  public void doesNotOverlapIfAdjacentOrDisjoint() {
    LocalDateTime s1 = LocalDateTime.of(2025, 6, 10, 8, 0);
    LocalDateTime e1 = LocalDateTime.of(2025, 6, 10, 9, 0);
    SingleEvent a = SingleEvent.getBuilder()
            .subject("A")
            .start(s1)
            .end(e1)
            .build();

    // Adjacent: one ends exactly when other starts
    LocalDateTime s2 = LocalDateTime.of(2025, 6, 10, 9, 0);
    LocalDateTime e2 = LocalDateTime.of(2025, 6, 10, 10, 0);
    SingleEvent b = SingleEvent.getBuilder()
            .subject("B")
            .start(s2)
            .end(e2)
            .build();

    assertFalse(a.overlapsWith(b));
    assertFalse(b.overlapsWith(a));

    // Disjoint: completely separate day/time
    LocalDateTime s3 = LocalDateTime.of(2025, 6, 11, 8, 0);
    LocalDateTime e3 = LocalDateTime.of(2025, 6, 11, 9, 0);
    SingleEvent c = SingleEvent.getBuilder()
            .subject("C")
            .start(s3)
            .end(e3)
            .build();

    assertFalse(a.overlapsWith(c));
    assertFalse(c.overlapsWith(a));
  }
}
