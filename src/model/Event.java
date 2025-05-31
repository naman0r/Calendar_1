package model;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;

public class Event implements IEvent {
  private String subject;
  private LocalDateTime start;

  private LocalDateTime end;
  private String description;
  private String location; //could be an enum
  private String status; //could make an enum

  private Event() {
    
  }

  @Override
  public String getSubject() {
    return this.subject;
  }

  @Override
  public LocalDateTime getStart() {
    return this.start;
  }

  @Override
  public LocalDateTime getEnd() {
    return this.end;
  }

  @Override
  public String getDescription() {
    return this.description;
  }

  @Override
  public String getLocation() {
    return this.location;
  }

  @Override
  public String getStatus() {
    return this.status;
  }

  @Override
  public boolean isAllDay() {
    return (this.getEnd() == null);
  }

  @Override
  public boolean overlapsWith(IEvent other) {
    return false;
  }

  public static EventBuilder getBuilder() {
    return new EventBuilder();
  }

  public static class EventBuilder {
    protected LocalDateTime start;
    protected String subject;

    HashMap<String, Object> map = new HashMap<>();

    private EventBuilder() {
      this.start = null;
      this.subject = null;
      map.put("end", (LocalDateTime) null);
      map.put("description", (String) null);
      map.put("location", (String) null);
      map.put("status", (String) null);
    }

    private EventBuilder returnBuilder() {
      return this;
    }

    private EventBuilder createEvent(String subject) {
      this.subject = subject;
      return returnBuilder();
    }

    private EventBuilder from(LocalDateTime start) {
      this.start = start;
      return returnBuilder();
    }

    private EventBuilder to(LocalDateTime end) {
      this.map.put("end", (LocalDateTime) end);
      return returnBuilder();
    }

    public Event build() {
      if (this.start == null || this.subject == null) {
        throw new IllegalArgumentException("Start and subject cannot be null");
      }
      Event e = new Event();
      e.end = (LocalDateTime) this.map.get("end");
      e.description = (String) this.map.get("description");
      e.location = (String) this.map.get("location");
      e.status = (String) this.map.get("status");
      return e;
    }
  }

}
