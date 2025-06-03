import model.Calendar;
import model.IEvent;
import model.Location;
import model.Status;
import model.SingleEvent;

import org.junit.Before;
import org.junit.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class CalendarTest {

  private Calendar cal;

  @Before
  public void setUp() {
    cal = new Calendar();
  }

  //––– Single‐Event Creation ––––––––––––––––––––––––––––––––––––––––––––

  @Test
  public void createSingleEventSucceedsWhenNoEndProvided() {
    LocalDateTime dt = LocalDateTime.of(2025, 6, 11, 12, 0);
    // Note: to call the (String, String, LocalDateTime, LocalDateTime, Location, Status) overload,
    // we must cast the last two arguments to (Location) null, (Status) null:
    boolean ok = cal.makeEvent(
            "Lunch",
            "Team lunch",
            dt,
            null,
            (Location) null,
            (Status) null
    );
    assertTrue(ok);

    // Now retrieve it
    List<IEvent> onDate = cal.getEventsOnDate(LocalDate.of(2025, 6, 11));
    assertEquals(1, onDate.size());
    IEvent e = onDate.get(0);
    assertEquals("Lunch", e.getSubject());
    // Because end==null, it should have been forced to 08:00–17:00 on 2025-06-11
    assertEquals(LocalDateTime.of(2025, 6, 11, 8, 0), e.getStart());
    assertEquals(LocalDateTime.of(2025, 6, 11, 17, 0), e.getEnd());
    assertNull(e.getLocation());
    assertNull(e.getStatus());
  }

  @Test
  public void createSingleEventWithEndSucceedsAndNoDuplicates() {
    LocalDateTime s = LocalDateTime.of(2025, 6, 12, 14, 0);
    LocalDateTime eTime = LocalDateTime.of(2025, 6, 12, 15, 0);

    boolean first = cal.makeEvent(
            "Meeting",
            "Project sync",
            s,
            eTime,
            Location.PHYSICAL,
            Status.PUBLIC
    );
    assertTrue(first);

    // A second, identical call must return false (duplicate)
    boolean second = cal.makeEvent(
            "Meeting",
            "Project sync",
            s,
            eTime,
            Location.PHYSICAL,
            Status.PUBLIC
    );
    assertFalse(second);

    List<IEvent> onDate = cal.getEventsOnDate(LocalDate.of(2025, 6, 12));
    assertEquals(1, onDate.size());
    IEvent e = onDate.get(0);
    assertEquals("Meeting", e.getSubject());
    assertEquals(Location.PHYSICAL, e.getLocation());
    assertEquals(Status.PUBLIC, e.getStatus());
  }

  @Test(expected = IllegalArgumentException.class)
  public void createSingleEventThrowsWhenSubjectOrStartNull() {
    // Passing null subject should throw
    cal.makeEvent(
            null,
            "desc",
            LocalDateTime.now(),
            null,
            (Location) null,
            (Status) null
    );
  }

  //––– All‐Day Shortcut ––––––––––––––––––––––––––––––––––––––––––––––––––

  @Test
  public void createAllDayEventWorksViaMakeEventStringDateTime() {
    LocalDateTime dt = LocalDateTime.of(2025, 6, 13, 9, 30);
    // This calls the single‐arg overload: makeEvent(String, LocalDateTime)
    boolean ok = cal.makeEvent("Holiday", dt);
    assertTrue(ok);

    List<IEvent> list = cal.getEventsOnDate(LocalDate.of(2025, 6, 13));
    assertEquals(1, list.size());
    IEvent e = list.get(0);
    assertEquals("Holiday", e.getSubject());
    // Should have been 08:00–17:00
    assertEquals(LocalDateTime.of(2025, 6, 13, 8, 0), e.getStart());
    assertEquals(LocalDateTime.of(2025, 6, 13, 17, 0), e.getEnd());
  }

  @Test(expected = IllegalArgumentException.class)
  public void createAllDayEventFailsWhenSubjectOrStartNull() {
    // Passing null subject to the (String, LocalDateTime) overload should throw
    cal.makeEvent(null, null);
  }

  //––– Recurring Series (Count‐Based) ––––––––––––––––––––––––––––––––––––

  @Test
  public void createSeriesByCountSucceedsForNonOverlappingDays() {
    LocalDateTime start = LocalDateTime.of(2025, 6, 14, 9, 0); // Saturday
    LocalDateTime end   = LocalDateTime.of(2025, 6, 14, 10, 0);
    // Expect recurring on next Monday (6/16) and Wednesday (6/18)
    boolean ok = cal.makeEvent(
            "Daily",
            "Morning standup",
            start,
            end,
            Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY),
            2
    );
    assertTrue(ok);

    List<IEvent> results = cal.getEventsInRange(
            LocalDateTime.of(2025, 6, 14, 0, 0),
            LocalDateTime.of(2025, 6, 19, 0, 0)
    );
    assertEquals(2, results.size());
    assertEquals(LocalDateTime.of(2025, 6, 16, 9, 0), results.get(0).getStart());
    assertEquals(LocalDateTime.of(2025, 6, 18, 9, 0), results.get(1).getStart());
  }

  @Test
  public void createSeriesByCountFailsIfDuplicateExists() {
    LocalDateTime s1 = LocalDateTime.of(2025, 6, 16, 9, 0);
    LocalDateTime e1 = LocalDateTime.of(2025, 6, 16, 10, 0);
    // Use the single‐event overload correctly (Location, Status)
    cal.makeEvent("Clash", "Existing", s1, e1, (Location) null, (Status) null);

    LocalDateTime start = LocalDateTime.of(2025, 6, 14, 9, 0);
    LocalDateTime end   = LocalDateTime.of(2025, 6, 14, 10, 0);
    boolean ok = cal.makeEvent(
            "Clash",
            "Recurring",
            start,
            end,
            Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY),
            2
    );
    assertFalse(ok);

    List<IEvent> all = cal.getEventsInRange(
            LocalDateTime.of(2025, 6, 14, 0, 0),
            LocalDateTime.of(2025, 6, 17, 23, 59)
    );
    assertEquals(1, all.size());
    assertEquals("Clash", all.get(0).getSubject());
    assertEquals(LocalDateTime.of(2025, 6, 16, 9, 0), all.get(0).getStart());
  }

  @Test(expected = IllegalArgumentException.class)
  public void createSeriesByCountThrowsOnInvalidArgs() {
    // days=empty, count=0 → invalid
    LocalDateTime s = LocalDateTime.of(2025, 6, 15, 8, 0);
    cal.makeEvent(
            "BadSeries",
            "desc",
            s,
            s.plusHours(1),
            Collections.emptyList(),
            0
    );
  }

  //––– Recurring Series (Date‐Based) ––––––––––––––––––––––––––––––––––––

  @Test
  public void createSeriesUntilDateSucceeds() {
    LocalDateTime start = LocalDateTime.of(2025, 6, 15, 8, 0);
    LocalDateTime end   = LocalDateTime.of(2025, 6, 15, 9, 0);
    LocalDateTime until = LocalDateTime.of(2025, 6, 22, 0, 0);

    // Here we call the overload: makeEvent(String, String, LocalDateTime, LocalDateTime, List<DayOfWeek>, LocalDateTime)
    boolean ok = cal.makeEvent(
            "Weekly",
            "Team Sync",
            start,
            end,
            Arrays.asList(DayOfWeek.SUNDAY),
            until
    );
    assertTrue(ok);

    // Occurrences on 6/15 and 6/22 (both Sundays)
    List<IEvent> hits15 = cal.getEventsOnDate(LocalDate.of(2025, 6, 15));
    assertEquals(1, hits15.size());

    List<IEvent> hits22 = cal.getEventsOnDate(LocalDate.of(2025, 6, 22));
    assertEquals(1, hits22.size());
  }

  @Test(expected = IllegalArgumentException.class)
  public void createSeriesUntilDateThrowsOnInvalidArgs() {
    LocalDateTime start = LocalDateTime.of(2025, 6, 15, 8, 0);
    LocalDateTime end   = LocalDateTime.of(2025, 6, 15, 9, 0);
    // Passing endDate = null should throw
    cal.makeEvent(
            "BadUntil",
            "desc",
            start,
            end,
            Arrays.asList(DayOfWeek.MONDAY),
            (LocalDateTime) null
    );
  }

  //––– Query and Remove ––––––––––––––––––––––––––––––––––––––––––––––––––

  @Test
  public void getEventsOnDateFiltersCorrectly() {
    // Three single‐day events on different days
    cal.makeEvent("E1", "desc", LocalDateTime.of(2025, 6, 16, 10, 0), null, (Location) null, (Status) null);
    cal.makeEvent("E2", "desc", LocalDateTime.of(2025, 6, 17, 11, 0), null, (Location) null, (Status) null);
    cal.makeEvent("E3", "desc", LocalDateTime.of(2025, 6, 16, 14, 0), null, (Location) null, (Status) null);

    List<IEvent> june16 = cal.getEventsOnDate(LocalDate.of(2025, 6, 16));
    assertEquals(2, june16.size());

    List<IEvent> june17 = cal.getEventsOnDate(LocalDate.of(2025, 6, 17));
    assertEquals(1, june17.size());
  }

  @Test
  public void getEventsInRangeFiltersCorrectly() {
    cal.makeEvent("A", "d", LocalDateTime.of(2025, 6, 18, 9, 0), null, (Location) null, (Status) null);
    cal.makeEvent("B", "d", LocalDateTime.of(2025, 6, 19, 9, 0), null, (Location) null, (Status) null);
    cal.makeEvent("C", "d", LocalDateTime.of(2025, 6, 20, 9, 0), null, (Location) null, (Status) null);

    LocalDateTime from = LocalDateTime.of(2025, 6, 18, 12, 0);
    LocalDateTime to   = LocalDateTime.of(2025, 6, 20, 12, 0);
    List<IEvent> subset = cal.getEventsInRange(from, to);
    assertEquals(3, subset.size());
  }
  @Test
  public void isBusyAtReturnsTrueWhenOverlapExists() {
    LocalDateTime s = LocalDateTime.of(2025, 6, 21, 8, 0);
    cal.makeEvent("Busy", "d", s, null, (Location) null, (Status) null);
    assertTrue(cal.isBusyAt(LocalDateTime.of(2025, 6, 21, 12, 0)));
    assertFalse(cal.isBusyAt(LocalDateTime.of(2025, 6, 21, 7, 59)));
  }

  @Test
  public void findEventReturnsUniqueOrNull() {
    LocalDateTime s = LocalDateTime.of(2025, 6, 22, 14, 0);
    LocalDateTime e = LocalDateTime.of(2025, 6, 22, 15, 0);
    cal.makeEvent("FindMe", "d", s, e, (Location) null, (Status) null);

    IEvent found = cal.findEvent("FindMe", s, e);
    assertNotNull(found);
    assertEquals("FindMe", found.getSubject());

    assertNull(cal.findEvent("NoSuch", s, e));

    SingleEvent dup = SingleEvent.getBuilder()
            .subject("FindMe")
            .start(s)
            .end(e)
            .build();
    cal.addEvent(dup);

    IEvent stillFound = cal.findEvent("FindMe", s, e);
    assertNotNull(stillFound);
    assertEquals("FindMe", stillFound.getSubject());
  }

  @Test
  public void removeEventWorks() {
    LocalDateTime s = LocalDateTime.of(2025, 6, 23, 10, 0);
    LocalDateTime e = LocalDateTime.of(2025, 6, 23, 11, 0);
    cal.makeEvent("X", "d", s, e, (Location) null, (Status) null);

    IEvent toRemove = cal.findEvent("X", s, e);
    assertTrue(cal.removeEvent(toRemove));

    // Now it’s gone
    assertNull(cal.findEvent("X", s, e));
  }

  @Test
  public void removeNonexistentEventReturnsFalse() {
    LocalDateTime s = LocalDateTime.of(2025, 6, 24, 9, 0);
    SingleEvent fake = SingleEvent.getBuilder()
            .subject("Fake")
            .start(s)
            .end(s.plusHours(1))
            .build();
    assertFalse(cal.removeEvent(fake));
  }

  @Test
  public void makeEventUntilDateWithZeroOccurrencesReturnsFalse() {
    LocalDateTime start = LocalDateTime.of(2025, 6, 25, 9, 0); // Wed
    LocalDateTime end   = LocalDateTime.of(2025, 6, 25, 10, 0);
    LocalDateTime until = LocalDateTime.of(2025, 6, 24, 23, 59); // before 6/25

    boolean ok = cal.makeEvent(
            "NoOccur",
            "d",
            start,
            end,
            Arrays.asList(DayOfWeek.WEDNESDAY),
            until
    );
    assertFalse(ok);
  }
}
