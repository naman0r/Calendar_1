package view;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import model.IEvent;

/**
 * Interface for the view for our Calendar.
 * Handles all presentation logic and formatting.
 */
public interface IView {
  
  /**
   * Display a success message for event creation.
   */
  void showEventCreated() throws IOException;
  
  /**
   * Display a success message for event series creation.
   */
  void showEventSeriesCreated() throws IOException;
  
  /**
   * Display a success message for event editing.
   */
  void showEventEdited() throws IOException;
  
  /**
   * Display an error message for failed event creation (duplicate exists).
   */
  void showEventCreationFailed() throws IOException;
  
  /**
   * Display an error message for failed series creation (one or more duplicates).
   */
  void showEventSeriesCreationFailed() throws IOException;
  
  /**
   * Display an error message for failed event editing.
   */
  void showEventEditingFailed() throws IOException;
  
  /**
   * Display events for a specific date.
   */
  void showEventsOnDate(LocalDate date, List<IEvent> events) throws IOException;
  
  /**
   * Display events in a date range.
   */
  void showEventsInRange(LocalDateTime start, LocalDateTime end, List<IEvent> events) throws IOException;
  
  /**
   * Display busy/available status for a specific date and time.
   */
  void showStatus(LocalDateTime dateTime, boolean isBusy) throws IOException;
  
  /**
   * Display a goodbye message when exiting.
   */
  void showGoodbye() throws IOException;
  
  /**
   * Display an error message for unknown or malformed commands.
   */
  void showUnknownCommand(String command) throws IOException;
  
  /**
   * Display a general error message.
   */
  void showError(String message) throws IOException;
  
  /**
   * Display a welcome message for interactive mode.
   */
  void showWelcome() throws IOException;
  
  /**
   * Display a prompt for interactive mode.
   */
  void showPrompt() throws IOException;
}
