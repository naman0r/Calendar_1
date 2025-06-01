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

  


}