package controller;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses text commands into executable command objects using simple string operations.
 */
public class CalendarCommandParser {

  /**
   * Parses a command String and returns the appropriate command object. Returns null if command
   * is malformed or unknown.
   */
  public static IControllerCommand parse(String commandText) {
    if (commandText == null || commandText.trim().isEmpty()) {
      return null;
    }

    String trimmed = commandText.trim();

    // Handle exit command
    if (trimmed.equalsIgnoreCase("exit")) {
      return new ExitCommand();
    }

    // Create event commands
    if (trimmed.startsWith("create event")) {
      return parseCreateEvent(trimmed);
    }

    // Edit event commands
    if (trimmed.startsWith("edit event")) {
      return parseEditEvent(trimmed);
    }

    if (trimmed.startsWith("edit events")) {
      return parseEditEvents(trimmed);
    }

    if (trimmed.startsWith("edit series")) {
      return parseEditSeries(trimmed);
    }

    // Query commands
    if (trimmed.startsWith("print events on")) {
      return parsePrintEventsOnDate(trimmed);
    }

    if (trimmed.startsWith("print events from")) {
      return parsePrintEventsInRange(trimmed);
    }

    if (trimmed.startsWith("show status on")) {
      return parseShowStatus(trimmed);
    }

    return null; // Unknown command
  }

  private static IControllerCommand parseCreateEvent(String command) {
    // Remove "create event " prefix (12 characters)
    String remainder = command.substring(12).trim();

    // Extract subject (quoted or single word)
    String subject = extractSubject(remainder);
    if (subject == null) return null;

    // Get the rest after subject
    String rest = getRemainingAfterSubject(remainder);
    if (rest == null) return null;

    // Check if it's an all-day event: "on YYYY-MM-DD"
    if (rest.startsWith("on ")) {
      return parseAllDayEvent(subject, rest.substring(3));
    }

    // Check if it's a timed event: "from YYYY-MM-DDThh:mm to YYYY-MM-DDThh:mm"
    if (rest.startsWith("from ")) {
      return parseTimedEvent(subject, rest.substring(5));
    }

    return null;
  }

  private static IControllerCommand parseAllDayEvent(String subject, String rest) {
    String[] parts = rest.split(" ");
    if (parts.length < 1) return null;

    LocalDate startDate = parseDate(parts[0]);
    if (startDate == null) return null;

    // Single all-day event: "YYYY-MM-DD"
    if (parts.length == 1) {
      return new CreateEventCommand(subject, null, startDate.atTime(8, 0), null, null, null);
    }

    // Recurring all-day event: "YYYY-MM-DD repeats DAYS for N times" or "until YYYY-MM-DD"
    if (parts.length >= 3 && "repeats".equals(parts[1])) {
      List<DayOfWeek> days = parseDays(parts[2]);
      if (days == null) return null;

      if (parts.length >= 5 && "for".equals(parts[3])) {
        try {
          int count = Integer.parseInt(parts[4]);
          return new CreateSeriesEventCommand(subject, null, startDate.atTime(8, 0), null, days, count, null);
        } catch (NumberFormatException e) {
          return null;
        }
      }

      if (parts.length >= 5 && "until".equals(parts[3])) {
        LocalDate endDate = parseDate(parts[4]);
        if (endDate == null) return null;
        return new CreateSeriesEventCommand(subject, null, startDate.atTime(8, 0), null, days, 0, endDate.atTime(8, 0));
      }
    }

    return null;
  }

  private static IControllerCommand parseTimedEvent(String subject, String rest) {
    // Find "to" keyword to split start and end times
    int toIndex = rest.indexOf(" to ");
    if (toIndex == -1) return null;

    String startStr = rest.substring(0, toIndex);
    String afterTo = rest.substring(toIndex + 4); // +4 for " to "

    // Split end time and possible repeating pattern
    String[] afterToParts = afterTo.split(" ", 2);
    String endStr = afterToParts[0];

    LocalDateTime start = parseDateTime(startStr);
    LocalDateTime end = parseDateTime(endStr);
    if (start == null || end == null) return null;

    // Single timed event if no more parts
    if (afterToParts.length == 1) {
      return new CreateEventCommand(subject, null, start, end, null, null);
    }

    // Check for repeating pattern
    String repeatingPart = afterToParts[1].trim();
    if (repeatingPart.startsWith("repeats ")) {
      return parseRepeatingPattern(subject, start, end, repeatingPart.substring(8));
    }

    return null;
  }

  private static IControllerCommand parseRepeatingPattern(String subject, LocalDateTime start, LocalDateTime end, String pattern) {
    String[] parts = pattern.split(" ");
    if (parts.length < 1) return null;

    List<DayOfWeek> days = parseDays(parts[0]);
    if (days == null) return null;

    if (parts.length >= 3 && "for".equals(parts[1])) {
      try {
        int count = Integer.parseInt(parts[2]);
        return new CreateSeriesEventCommand(subject, null, start, end, days, count, null);
      } catch (NumberFormatException e) {
        return null;
      }
    }

    if (parts.length >= 3 && "until".equals(parts[1])) {
      LocalDate endDate = parseDate(parts[2]);
      if (endDate == null) return null;
      return new CreateSeriesEventCommand(subject, null, start, end, days, 0, endDate.atTime(end.toLocalTime()));
    }

    return null;
  }

  private static IControllerCommand parseEditEvent(String command) {
    // edit event <property> <subject> from <start> to <end> with <value>
    String[] parts = command.split(" ");
    if (parts.length < 8) return null; // Need at least: edit event property subject from start to end with

    String property = parts[2];
    
    // Find "from" keyword to locate subject end
    int fromIndex = findKeywordIndex(parts, "from");
    if (fromIndex == -1 || fromIndex <= 3) return null;

    // Extract subject (parts 3 to fromIndex-1)
    String subject = joinParts(parts, 3, fromIndex - 1);
    subject = cleanQuotes(subject);

    // Find "to" and "with" keywords
    int toIndex = findKeywordIndex(parts, "to");
    int withIndex = findKeywordIndex(parts, "with");
    if (toIndex == -1 || withIndex == -1 || toIndex <= fromIndex || withIndex <= toIndex) return null;

    String startStr = parts[fromIndex + 1];
    String endStr = parts[toIndex + 1];
    String newValue = joinParts(parts, withIndex + 1, parts.length - 1);

    LocalDateTime start = parseDateTime(startStr);
    LocalDateTime end = parseDateTime(endStr);
    if (start == null || end == null) return null;

    return new EditEventCommand(property, subject, start, end, newValue, 's');
  }

  private static IControllerCommand parseEditEvents(String command) {
    // edit events <property> <subject> from <start> with <value>
    String[] parts = command.split(" ");
    if (parts.length < 7) return null;

    String property = parts[2];
    
    int fromIndex = findKeywordIndex(parts, "from");
    if (fromIndex == -1 || fromIndex <= 3) return null;

    String subject = joinParts(parts, 3, fromIndex - 1);
    subject = cleanQuotes(subject);

    int withIndex = findKeywordIndex(parts, "with");
    if (withIndex == -1 || withIndex <= fromIndex) return null;

    String startStr = parts[fromIndex + 1];
    String newValue = joinParts(parts, withIndex + 1, parts.length - 1);

    LocalDateTime start = parseDateTime(startStr);
    if (start == null) return null;

    return new EditEventCommand(property, subject, start, null, newValue, 'f');
  }

  private static IControllerCommand parseEditSeries(String command) {
    // edit series <property> <subject> from <start> with <value>
    String[] parts = command.split(" ");
    if (parts.length < 7) return null;

    String property = parts[2];
    
    int fromIndex = findKeywordIndex(parts, "from");
    if (fromIndex == -1 || fromIndex <= 3) return null;

    String subject = joinParts(parts, 3, fromIndex - 1);
    subject = cleanQuotes(subject);

    int withIndex = findKeywordIndex(parts, "with");
    if (withIndex == -1 || withIndex <= fromIndex) return null;

    String startStr = parts[fromIndex + 1];
    String newValue = joinParts(parts, withIndex + 1, parts.length - 1);

    LocalDateTime start = parseDateTime(startStr);
    if (start == null) return null;

    return new EditEventCommand(property, subject, start, null, newValue, 'e');
  }

  private static IControllerCommand parsePrintEventsOnDate(String command) {
    // print events on YYYY-MM-DD
    String dateStr = command.substring(16).trim();
    LocalDate date = parseDate(dateStr);
    if (date == null) return null;

    return new PrintEventsOnDateCommand(date);
  }

  private static IControllerCommand parsePrintEventsInRange(String command) {
    // print events from YYYY-MM-DDThh:mm to YYYY-MM-DDThh:mm
    String remainder = command.substring(18).trim(); // Remove "print events from "
    
    int toIndex = remainder.indexOf(" to ");
    if (toIndex == -1) return null;

    String startStr = remainder.substring(0, toIndex);
    String endStr = remainder.substring(toIndex + 4);

    LocalDateTime start = parseDateTime(startStr);
    LocalDateTime end = parseDateTime(endStr);
    if (start == null || end == null) return null;

    return new PrintEventsInRangeCommand(start, end);
  }

  private static IControllerCommand parseShowStatus(String command) {
    // show status on YYYY-MM-DDThh:mm
    String dateTimeStr = command.substring(14).trim();
    LocalDateTime dateTime = parseDateTime(dateTimeStr);
    if (dateTime == null) return null;

    return new ShowStatusCommand(dateTime);
  }

  // Helper Methods
  
  private static String extractSubject(String remainder) {
    if (remainder.startsWith("\"")) {
      int endQuote = remainder.indexOf("\"", 1);
      if (endQuote == -1) return null;
      return remainder.substring(1, endQuote);
    } else {
      int spaceIndex = remainder.indexOf(" ");
      if (spaceIndex == -1) return null;
      return remainder.substring(0, spaceIndex);
    }
  }

  private static String getRemainingAfterSubject(String remainder) {
    if (remainder.startsWith("\"")) {
      int endQuote = remainder.indexOf("\"", 1);
      if (endQuote == -1) return null;
      return remainder.substring(endQuote + 1).trim();
    } else {
      int spaceIndex = remainder.indexOf(" ");
      if (spaceIndex == -1) return null;
      return remainder.substring(spaceIndex + 1).trim();
    }
  }

  private static int findKeywordIndex(String[] parts, String keyword) {
    for (int i = 0; i < parts.length; i++) {
      if (keyword.equals(parts[i])) {
        return i;
      }
    }
    return -1;
  }

  private static String joinParts(String[] parts, int start, int end) {
    StringBuilder sb = new StringBuilder();
    for (int i = start; i <= end && i < parts.length; i++) {
      if (i > start) sb.append(" ");
      sb.append(parts[i]);
    }
    return sb.toString();
  }

  private static String cleanQuotes(String subject) {
    if (subject.startsWith("\"") && subject.endsWith("\"") && subject.length() > 1) {
      return subject.substring(1, subject.length() - 1);
    }
    return subject;
  }

  private static LocalDate parseDate(String dateStr) {
    try {
      return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    } catch (Exception e) {
      return null;
    }
  }

  private static LocalDateTime parseDateTime(String dateTimeStr) {
    try {
      return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
    } catch (Exception e) {
      return null;
    }
  }

  private static List<DayOfWeek> parseDays(String daysStr) {
    List<DayOfWeek> days = new ArrayList<>();
    for (char c : daysStr.toCharArray()) {
      switch (c) {
        case 'M': days.add(DayOfWeek.MONDAY); break;
        case 'T': days.add(DayOfWeek.TUESDAY); break;
        case 'W': days.add(DayOfWeek.WEDNESDAY); break;
        case 'R': days.add(DayOfWeek.THURSDAY); break;
        case 'F': days.add(DayOfWeek.FRIDAY); break;
        case 'S': days.add(DayOfWeek.SATURDAY); break;
        case 'U': days.add(DayOfWeek.SUNDAY); break;
        default: return null;
      }
    }
    return days.isEmpty() ? null : days;
  }
} 