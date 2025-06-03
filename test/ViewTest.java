import view.View;
import model.Status;
import model.Location;

import static org.junit.Assert.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import model.IEvent;
import org.junit.Before;
import org.junit.Test;

public class ViewTest {

  private StringBuilder out;
  private View view;

  @Before
  public void setUp() {
    out = new StringBuilder();
    view = new View(out);
  }

  // Helper fake event for formatting tests
  private static class FakeEvent implements IEvent {
    private final String subject;
    private final LocalDateTime start;
    private final LocalDateTime end;
    private final Location location;

    FakeEvent(String subject, LocalDateTime start, LocalDateTime end, Location location) {
      this.subject = subject;
      this.start = start;
      this.end = end;
      this.location = location;
    }

    @Override
    public String getSubject() {
      return subject;
    }

    @Override
    public LocalDateTime getStart() {
      return start;
    }

    @Override
    public LocalDateTime getEnd() {
      return end;
    }

    @Override
    public String getDescription() {
      return null;
    }

    @Override
    public Location getLocation() {
      return location;
    }

    @Override
    public model.Status getStatus() {
      return null;
    }

    @Override
    public Integer getSeriesId() {
      return null;
    }

    @Override
    public boolean isAllDay() {
      return false;
    }

    @Override
    public boolean overlapsWith(IEvent other) {
      return false;
    }

    @Override
    public boolean equals(Object other) {
      return false;
    }

    @Override
    public int hashCode() {
      return 0;
    }
  }

  @Test
  public void testShowEventCreated() throws IOException {
    view.showEventCreated();
    assertEquals("Event created successfully.\n", out.toString());
  }

  @Test
  public void testShowEventSeriesCreated() throws IOException {
    view.showEventSeriesCreated();
    assertEquals("Event series created successfully.\n", out.toString());
  }

  @Test
  public void testShowEventEdited() throws IOException {
    view.showEventEdited();
    assertEquals("Event(s) edited successfully.\n", out.toString());
  }

  @Test
  public void testShowEventCreationFailed() throws IOException {
    view.showEventCreationFailed();
    assertEquals("Failed to create event. Event already exists.\n", out.toString());
  }

  @Test
  public void testShowEventSeriesCreationFailed() throws IOException {
    view.showEventSeriesCreationFailed();
    assertEquals("Failed to create event series. One or more events already exist.\n", out.toString());
  }

  @Test
  public void testShowEventEditingFailed() throws IOException {
    view.showEventEditingFailed();
    assertEquals("Failed to edit event(s). Event not found or editing would create duplicate.\n", out.toString());
  }

  @Test
  public void testShowEventsOnDateEmpty() throws IOException {
    LocalDate date = LocalDate.of(2025, 6, 1);
    view.showEventsOnDate(date, Collections.emptyList());
    assertEquals("No events found on 2025-06-01.\n", out.toString());
  }

  @Test
  public void testShowEventsOnDateNonEmpty() throws IOException {
    LocalDate date = LocalDate.of(2025, 6, 2);
    FakeEvent e1 = new FakeEvent(
            "Meeting",
            LocalDateTime.of(2025, 6, 2, 9, 0),
            LocalDateTime.of(2025, 6, 2, 10, 0),
            null
    );
    FakeEvent e2 = new FakeEvent(
            "Lunch",
            LocalDateTime.of(2025, 6, 2, 12, 0),
            LocalDateTime.of(2025, 6, 2, 13, 0),
            Location.PHYSICAL
    );
    List<IEvent> events = List.of(e1, e2);

    view.showEventsOnDate(date, events);

    String expected =
            "Events on 2025-06-02:\n" +
                    "• Meeting (09:00 - 10:00)\n" +
                    "• Lunch (12:00 - 13:00) at physical\n";
    assertEquals(expected, out.toString());
  }

  @Test
  public void testShowEventsInRangeEmpty() throws IOException {
    LocalDateTime start = LocalDateTime.of(2025, 6, 3, 0, 0);
    LocalDateTime end = LocalDateTime.of(2025, 6, 4, 0, 0);
    view.showEventsInRange(start, end, Collections.emptyList());
    assertEquals("No events found in the specified range.\n", out.toString());
  }

  @Test
  public void testShowEventsInRangeNonEmpty() throws IOException {
    LocalDateTime start = LocalDateTime.of(2025, 6, 5, 8, 0);
    LocalDateTime end = LocalDateTime.of(2025, 6, 5, 18, 0);
    FakeEvent e = new FakeEvent(
            "Workshop",
            LocalDateTime.of(2025, 6, 5, 10, 0),
            LocalDateTime.of(2025, 6, 5, 12, 0),
            Location.VIRTUAL
    );
    List<IEvent> events = List.of(e);

    view.showEventsInRange(start, end, events);

    String expected =
            "Events from 2025-06-05T08:00 to 2025-06-05T18:00:\n" +
                    "• Workshop (2025-06-05T10:00 - 2025-06-05T12:00) at virtual\n";
    assertEquals(expected, out.toString());
  }

  @Test
  public void testShowStatusBusyAndAvailable() throws IOException {
    LocalDateTime dt = LocalDateTime.of(2025, 6, 6, 14, 30);
    view.showStatus(dt, true);
    view.showStatus(dt, false);

    String expected =
            "Status on 2025-06-06T14:30: busy\n" +
                    "Status on 2025-06-06T14:30: available\n";
    assertEquals(expected, out.toString());
  }

  @Test
  public void testShowGoodbye() throws IOException {
    view.showGoodbye();
    assertEquals("Goodbye!\n", out.toString());
  }

  @Test
  public void testShowUnknownCommand() throws IOException {
    view.showUnknownCommand("foobar");
    assertEquals("Unknown or malformed command: foobar\n", out.toString());
  }

  @Test
  public void testShowErrorNullMessage() throws IOException {
    view.showError(null);
    assertEquals("Error: Unknown error\n", out.toString());
  }

  @Test
  public void testShowErrorCustomMessage() throws IOException {
    view.showError("Something went wrong");
    assertEquals("Error: Something went wrong\n", out.toString());
  }

  @Test
  public void testShowWelcomeAndPrompt() throws IOException {
    view.showWelcome();
    view.showPrompt();
    assertEquals("Welcome to Calendar App! Type 'exit' to quit.\n> ", out.toString());
  }
}