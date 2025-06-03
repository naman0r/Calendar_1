import model.SeriesEvent;
import model.Status;
import model.Location;
import static org.junit.Assert.*;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Arrays;

import org.junit.Test;

public class SeriesEventTest {

  @Test(expected = IllegalStateException.class)
  public void buildFailsWhenSubjectMissing() {
    SeriesEvent.getBuilder()
            .start(LocalDateTime.of(2025, 6, 4, 10, 0))
            .end(LocalDateTime.of(2025, 6, 4, 11, 0))
            .seriesId(1)
            .build();
  }

  @Test(expected = IllegalStateException.class)
  public void buildFailsWhenStartMissing() {
    SeriesEvent.getBuilder()
            .subject("Weekly")
            .end(LocalDateTime.of(2025, 6, 4, 11, 0))
            .seriesId(1)
            .build();
  }

  @Test(expected = IllegalStateException.class)
  public void buildFailsWhenEndMissing() {
    SeriesEvent.getBuilder()
            .subject("Weekly")
            .start(LocalDateTime.of(2025, 6, 4, 10, 0))
            .seriesId(1)
            .build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void buildFailsWhenDatesAreOnDifferentDay() {
    SeriesEvent.getBuilder()
            .subject("Bad")
            .start(LocalDateTime.of(2025, 6, 4, 10, 0))
            .end(LocalDateTime.of(2025, 6, 5, 11, 0)) // different date
            .seriesId(1)
            .build();
  }

  @Test(expected = IllegalStateException.class)
  public void buildFailsWhenSeriesIdMissing() {
    SeriesEvent.getBuilder()
            .subject("NoSeriesId")
            .start(LocalDateTime.of(2025, 6, 6, 9, 0))
            .end(LocalDateTime.of(2025, 6, 6, 10, 0))
            .build();
  }

  @Test
  public void locationAndStatusRemainNullWhenNotSet() {
    SeriesEvent ev = SeriesEvent.getBuilder()
            .subject("NullLocStatus")
            .start(LocalDateTime.of(2025, 6, 7, 14, 0))
            .end(LocalDateTime.of(2025, 6, 7, 15, 0))
            .seriesId(42)
            .build();

    assertNull(ev.getLocation());
    assertNull(ev.getStatus());
  }

  @Test
  public void locationAndStatusCanBeSet() {
    SeriesEvent ev = SeriesEvent.getBuilder()
            .subject("WithLocStatus")
            .start(LocalDateTime.of(2025, 6, 8, 16, 0))
            .end(LocalDateTime.of(2025, 6, 8, 17, 0))
            .location(Location.PHYSICAL)
            .status(Status.PRIVATE)
            .seriesId(99)
            .build();

    assertEquals(Location.PHYSICAL, ev.getLocation());
    assertEquals(Status.PRIVATE, ev.getStatus());
    assertFalse(ev.isAllDay());
  }

  @Test
  public void isAllDayDetectsCorrectly() {
    SeriesEvent fullDay = SeriesEvent.getBuilder()
            .subject("AllDaySeries")
            .start(LocalDateTime.of(2025, 6, 9, 8, 0))
            .end(LocalDateTime.of(2025, 6, 9, 17, 0))
            .seriesId(7)
            .build();
    assertTrue(fullDay.isAllDay());

    SeriesEvent notAllDay = SeriesEvent.getBuilder()
            .subject("NotAllDay")
            .start(LocalDateTime.of(2025, 6, 9, 9, 0))
            .end(LocalDateTime.of(2025, 6, 9, 10, 0))
            .seriesId(7)
            .build();
    assertFalse(notAllDay.isAllDay());
  }

  @Test
  public void overlapsWithSameLogicAsSingleEvent() {
    SeriesEvent first = SeriesEvent.getBuilder()
            .subject("Overlap1")
            .start(LocalDateTime.of(2025, 6, 10, 10, 0))
            .end(LocalDateTime.of(2025, 6, 10, 11, 0))
            .seriesId(5)
            .build();

    SeriesEvent second = SeriesEvent.getBuilder()
            .subject("Overlap2")
            .start(LocalDateTime.of(2025, 6, 10, 10, 30))
            .end(LocalDateTime.of(2025, 6, 10, 11, 30))
            .seriesId(5)
            .build();

    assertTrue(first.overlapsWith(second));
    assertTrue(second.overlapsWith(first));

    // Disjoint/adjacent
    SeriesEvent third = SeriesEvent.getBuilder()
            .subject("Disjoint")
            .start(LocalDateTime.of(2025, 6, 10, 11, 0))
            .end(LocalDateTime.of(2025, 6, 10, 12, 0))
            .seriesId(5)
            .build();
    assertFalse(first.overlapsWith(third));
    assertFalse(third.overlapsWith(first));
  }
}
