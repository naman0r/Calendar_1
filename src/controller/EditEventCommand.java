package controller;

import java.io.IOException;
import java.time.LocalDateTime;
import model.ICalendar;
import view.IView;

/**
 * Command to edit events.
 */
public class EditEventCommand implements IControllerCommand {
  private final String property;
  private final String subject;
  private final LocalDateTime start;
  private final LocalDateTime end;
  private final String newValue;
  private final char editType;
  
  public EditEventCommand(String property, String subject, LocalDateTime start, 
                         LocalDateTime end, String newValue, char editType) {
    this.property = property;
    this.subject = subject;
    this.start = start;
    this.end = end;
    this.newValue = newValue;
    this.editType = editType;
  }

  @Override
  public void execute(ICalendar model, IView view) throws IOException {
    boolean success;
    if (editType == 's') {
      // Single event edit - need to find exact match
      if (end == null) {
        view.showError("Single event edit requires both start and end times.");
        return;
      }
      success = model.editSeriesEvent(property, subject, start, end, newValue, 's');
    } else {
      // Series edit (forward or entire)
      success = model.editSeriesEvent(property, subject, start, end, newValue, editType);
    }
    
    if (success) {
      view.showEventEdited();
    } else {
      view.showEventEditingFailed();
    }
  }
} 