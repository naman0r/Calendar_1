package controller;

import java.io.IOException;

import model.ICalendar;

public interface IControllerCommand {

    void execute(ICalendar model, Appendable out) throws IOException;

}
