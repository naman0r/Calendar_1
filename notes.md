# These are notes on what I have done so far:

```
Calendar_1/
├── src/
│   ├── CalendarApp.java  -> app entry point,
│   ├── model/
│   │   ├── ICalendar.java  -> ICalendar interface, implemented by Calendar.
│   │   ├── Calendar.java
│   │   ├── IEvent.java  -> Event interface, implemented by AbstractEvent
│   │   ├── AbstractEvent.java
│   │   ├── SingleEvent.java  -> extends AbstractEvent
│   │   ├── SeriesEvent.java  -> extends AbstractEvent
│   │   ├── Location.java  -> (physical, virtual) class, should technicaly be an ENUM but ok
│   │   └── Status.java  -> (public, private) class, should be an enum but this works
│   ├── view/
│   │   ├── IView.java  -> Interface for the view, implemented by IView.
│   │   └── View.java  -> implementation that formats and displays all output messages
│   └── controller/
│       ├── IController.java  -> Interface for controllers that will run the application loop.
│       ├── CalendarController.java  -> Main controller that reads user input and coordinates with the model and view.
│       ├── IControllerCommand.java   -> interface foe command objects (command design pattern)
│       ├── CalendarCommandParser.java -> Converts text commands into executable command objects
|       ├── CreateEventCommand.java  -> Handles creating single events
│       ├── CreateSeriesEventCommand.java  -> handles creating recurring events
│       ├── EditEventCommand.java  -> handles editing existing events
│       ├── PrintEventsOnDateCommand.java   -> handles showing events on a specific date
│       ├── PrintEventsInRangeCommand.java  -> handles showing events in a range of dates
│       ├── ShowStatusCommand.java  -> handles checking if the user is busy at a specific time
│       └── ExitCommand.java  -> what is called when user types in 'exit', handles application termination.
├── test_commands.txt  -> idk if this should be in the res file or not????
├── debug_commands.txt  -> idk if this should be in the res file or not????
├── debug2_commands.txt  -> idk if this should be in the res file or not????
|
└── README.md -> notes on application.
```

## application flow:

```
USER INPUT COMMANDS
        |
        v
CalendarController.go()
        |
        v
CalendarCommandParser.parse()
        |
        v
    [COMMAND ROUTING]
        |
        |-- "exit"
        |     --> ExitCommand
        |           --> view.showGoodbye()
        |
        |-- "create event ..."
        |     --> parseCreateEvent()
        |           |
        |           |-- "... on YYYY-MM-DD"
        |           |     --> parseAllDayEvent()
        |           |           |-- Single: CreateEventCommand
        |           |           |     --> model.makeEvent(subject, desc, start, null, location, status)
        |           |           |     --> view.showEventCreated() OR view.showEventCreationFailed()
        |           |           |
        |           |           |-- Recurring: CreateSeriesEventCommand
        |           |                 --> model.makeEvent(subject, desc, start, end, days, count/endDate)
        |           |                 --> view.showEventSeriesCreated() OR view.showEventSeriesCreationFailed()
        |           |
        |           |-- "... from YYYY-MM-DDTHH:mm to YYYY-MM-DDTHH:mm"
        |                 --> parseTimedEvent()
        |                       |-- Single: CreateEventCommand
        |                       |     --> model.makeEvent(subject, desc, start, end, location, status)
        |                       |     --> view.showEventCreated() OR view.showEventCreationFailed()
        |                       |
        |                       |-- Recurring: CreateSeriesEventCommand
        |                             --> model.makeEvent(subject, desc, start, end, days, count/endDate)
        |                             --> view.showEventSeriesCreated() OR view.showEventSeriesCreationFailed()
        |
        |-- "edit event ..."
        |     --> parseEditEvent()
        |           --> EditEventCommand (type='s')
        |                 --> model.editSeriesEvent(property, subject, start, end, newValue, 's')
        |                 --> view.showEventEdited() OR view.showEventEditingFailed()
        |
        |-- "edit events ..."
        |     --> parseEditEvents()
        |           --> EditEventCommand (type='f')
        |                 --> model.editSeriesEvent(property, subject, start, null, newValue, 'f')
        |                 --> view.showEventEdited() OR view.showEventEditingFailed()
        |
        |-- "edit series ..."
        |     --> parseEditSeries()
        |           --> EditEventCommand (type='e')
        |                 --> model.editSeriesEvent(property, subject, start, null, newValue, 'e')
        |                 --> view.showEventEdited() OR view.showEventEditingFailed()
        |
        |-- "print events on ..."
        |     --> parsePrintEventsOnDate()
        |           --> PrintEventsOnDateCommand
        |                 --> model.getEventsOnDate(date)
        |                 --> view.showEventsOnDate(date, events)
        |
        |-- "print events from ..."
        |     --> parsePrintEventsInRange()
        |           --> PrintEventsInRangeCommand
        |                 --> model.getEventsInRange(start, end)
        |                 --> view.showEventsInRange(start, end, events)
        |
        |-- "show status on ..."
        |     --> parseShowStatus()
        |           --> ShowStatusCommand
        |                 --> model.isBusyAt(dateTime)
        |                 --> view.showStatus(dateTime, isBusy)
        |
        |-- [UNKNOWN COMMAND]
              --> return null
                    --> view.showUnknownCommand(command)
```
