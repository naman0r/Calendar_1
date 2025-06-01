import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

import model.Calendar;
import model.SingleEvent;

public class CalendarTest2 {

  @Test
  public void testCalendarInitialization() {
    Calendar calendar = new Calendar();

    SingleEvent e1 = SingleEvent.getBuilder()
            .subject("THis is naman's testevent")
            .start(LocalDateTime.of(2025, 6, 1, 10, 0))
            .build();

    assertFalse(calendar.removeEvent(e1));

    assertTrue(calendar.addEvent(e1));

    assertTrue(calendar.removeEvent(e1));

  }

}