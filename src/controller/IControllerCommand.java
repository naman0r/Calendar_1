package controller;

import java.io.IOException;
import model.ICalendar;
import view.IView;

/**
 * Interface for controller commands that coordinate between model and view.
 */
public interface IControllerCommand {


    /**
     * Executes a specific functionality based on the inputs passed in to the constuctor of this
     *          concrete implementation.
     * @param model The Model of the Application (ICalendar, Calendar concrete)
     * @param view the view of the Applicatoin
     * @throws IOException if invalid inputs are passed in.
     */
    void execute(ICalendar model, IView view) throws IOException;

}
