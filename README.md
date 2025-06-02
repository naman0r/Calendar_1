# Calendar Application

A command-line calendar application that supports creating, editing, and querying calendar events.
The application follows MVC architecture and SOLID principles.

**Designed and implemented by Naman Rusia and Madeline Peskens**

## Features

### Event Management

- **Single Events**: Create events with subject, start/end times, description, location, and status
- **All-Day Events**: Events that span from 8:00 AM to 5:00 PM
- **Recurring Events**: Create series of events that repeat on specific days of the week
- **Event Editing**: Modify event properties with support for single events and event series
- **Event Queries**: Find events by date, date range, or check availability

### Supported Commands

#### Creating Events

**Single Event with Time:**

```
create event <eventSubject> from <dateStringTtimeString> to <dateStringTtimeString>
```

Example: `create event "Team Meeting" from 2024-01-15T10:00 to 2024-01-15T11:00`

**All-Day Event:**

```
create event <eventSubject> on <dateString>
```

Example: `create event Lunch on 2024-01-15`

**Recurring Events (Count-based):**

```
create event <eventSubject> from <dateStringTtimeString> to <dateStringTtimeString> repeats <weekdays> for <N> times
```

Example: `create event "Daily Standup" from 2024-01-15T09:00 to 2024-01-15T09:30 repeats MWF for 5 times`

**Recurring Events (Date-based):**

```
create event <eventSubject> from <dateStringTtimeString> to <dateStringTtimeString> repeats <weekdays> until <dateString>
```

Example: `create event "Weekly Review" from 2024-01-15T16:00 to 2024-01-15T17:00 repeats F until 2024-03-15`

**Recurring All-Day Events:**

```
create event <eventSubject> on <dateString> repeats <weekdays> for <N> times
create event <eventSubject> on <dateString> repeats <weekdays> until <dateString>
```

#### Editing Events

**Edit Single Event:**

```
edit event <property> <eventSubject> from <dateStringTtimeString> to <dateStringTtimeString> with <NewPropertyValue>
```

Example: `edit event subject "Team Meeting" from 2024-01-15T10:00 to 2024-01-15T11:00 with "Project Review"`

**Edit Event Series (Forward):**

```
edit events <property> <eventSubject> from <dateStringTtimeString> with <NewPropertyValue>
```

Example: `edit events subject "Daily Standup" from 2024-01-17T09:00 with "Morning Sync"`

**Edit Entire Series:**

```
edit series <property> <eventSubject> from <dateStringTtimeString> with <NewPropertyValue>
```

Example: `edit series location "Daily Standup" from 2024-01-15T09:00 with "Conference Room A"`

**Editable Properties:** for clarfication

- `subject`: Event title
- `start`: Start date and time (format: YYYY-MM-DDTHH:mm)
- `end`: End date and time (format: YYYY-MM-DDTHH:mm)
- `description`: Event description
- `location`: Event location
- `status`: Event status (public/private)

#### Querying Events

**Events on Specific Date:**

```
print events on <dateString>
```

Example: `print events on 2024-01-15`

**Events in Date Range:**

```
print events from <dateStringTtimeString> to <dateStringTtimeString>
```

Example: `print events from 2024-01-15T09:00 to 2024-01-15T17:00`

**Check Availability:**

```
show status on <dateStringTtimeString>
```

Example: `show status on 2024-01-15T10:30`

#### Other Commands

**Exit Application:**

```
exit
```

### Date and Time Formats

- **Date String**: `YYYY-MM-DD` (e.g., `2024-01-15`)
- **Time String**: `HH:mm` (e.g., `10:00`)
- **DateTime String**: `YYYY-MM-DDTHH:mm` (e.g., `2024-01-15T10:00`)

### Weekday Codes

- `M`: Monday
- `T`: Tuesday
- `W`: Wednesday
- `R`: Thursday
- `F`: Friday
- `S`: Saturday
- `U`: Sunday

Example: `MWF` means Monday, Wednesday, Friday

### Subject Names

- Single word subjects can be used without quotes: `Meeting`
- Multi-word subjects must be enclosed in double quotes: `"Team Meeting"`

## Running the Application

### Interactive Mode

```bash
java CalendarApp --mode interactive
```

In interactive mode, you can type commands one by one and see immediate results. Type `exit` to quit.

### Headless Mode

```bash
java CalendarApp --mode headless <filename>
```

In headless mode, the application reads commands from a text file and executes them sequentially. The file must end with an `exit` command.

Example:

```bash
java CalendarApp --mode headless commands.txt
```

## Architecture

The application follows the Model-View-Controller (MVC) pattern with proper separation of concerns:

### Model (`src/model/`)

**Handles business logic and data management:**

- **`ICalendar`**: Interface defining calendar operations
- **`Calendar`**: Main calendar implementation with event management logic
- **`IEvent`**: Interface for calendar events
- **`SingleEvent`**: Implementation for single events
- **`SeriesEvent`**: Implementation for recurring events
- **`AbstractEvent`**: Base class for events with common functionality
- **`Location`**: Represents event locations
- **`Status`**: Represents event status (public/private)

### View (`src/view/`)

**Handles presentation logic and user interface:**

- **`IView`**: Interface defining all presentation methods
- **`View`**: Implementation that formats and displays all output
  - Formats event listings
  - Displays status messages
  - Handles error messages
  - Manages user prompts and welcome messages

### Controller (`src/controller/`)

**Coordinates between Model and View:**

- **`IController`**: Interface for controllers
- **`CalendarController`**: Main controller handling user input and coordinating between model and view
- **`CalendarCommandParser`**: Parses text commands into command objects
- **`IControllerCommand`**: Interface for command objects that coordinate model operations with view display
- **Command Classes**: Individual command implementations that delegate business logic to model and presentation to view
  - `CreateEventCommand`
  - `CreateSeriesEventCommand`
  - `EditEventCommand`
  - `PrintEventsOnDateCommand`
  - `PrintEventsInRangeCommand`
  - `ShowStatusCommand`
  - `ExitCommand`

### Main Application

- **`CalendarApp`**: Entry point that creates model, view, and controller instances and handles command-line arguments

### MVC Separation of Concerns

1. **Model**: Contains all business rules, event management, validation, and data operations
2. **View**: Handles all formatting, display logic, and user interface concerns
3. **Controller**: Acts as a coordinator, parsing user input and orchestrating interactions between model and view

This clean separation ensures:

- **Testability**: Each layer can be tested independently
- **Maintainability**: Changes to presentation don't affect business logic
- **Extensibility**: New views (e.g., GUI) can be added without changing model or controller logic
- **Single Responsibility**: Each class has one clear purpose

## Design Principles

The application adheres to SOLID principles:

1. **Single Responsibility**: Each class has a single, well-defined purpose
2. **Open/Closed**: Easy to extend with new commands or event types
3. **Liskov Substitution**: Event implementations are interchangeable
4. **Interface Segregation**: Focused interfaces for different concerns
5. **Dependency Inversion**: Depends on abstractions, not concrete implementations

## Example Usage

### Sample Commands File (`test_commands.txt`)

```
create event "Team Meeting" from 2024-01-15T10:00 to 2024-01-15T11:00
create event Lunch on 2024-01-15
create event "Daily Standup" from 2024-01-15T09:00 to 2024-01-15T09:30 repeats MWF for 5 times
print events on 2024-01-15
print events on 2024-01-17
show status on 2024-01-15T10:30
show status on 2024-01-15T14:00
edit event subject "Team Meeting" from 2024-01-15T10:00 to 2024-01-15T11:00 with "Project Review"
print events on 2024-01-15
exit
```

### Running the Example

```bash
java CalendarApp --mode headless test_commands.txt
```

### Expected Output

```
Event created successfully.
Event created successfully.
Event series created successfully.
Events on 2024-01-15:
• Lunch (08:00 - 17:00)
• Daily Standup (09:00 - 09:30)
• Team Meeting (10:00 - 11:00)
Events on 2024-01-17:
• Daily Standup (09:00 - 09:30)
Status on 2024-01-15T10:30: busy
Status on 2024-01-15T14:00: busy
Event(s) edited successfully.
Events on 2024-01-15:
• Lunch (08:00 - 17:00)
• Daily Standup (09:00 - 09:30)
• Project Review (10:00 - 11:00)
Goodbye!
```

## Error Handling

The application provides clear error messages for:

- Invalid command syntax
- Duplicate events
- Missing required parameters
- Invalid date/time formats
- File not found (headless mode)
- Events not found during editing

## Limitations

- Single calendar support only
- Timezone assumed to be EST
- Events in a series must start and end on the same day
- No persistence between application runs

## Ok i dont know how to run this without doing this,

``

`cd src && javac *.java */*.java && java CalendarApp --mode headless ../test_commands.txt`

`cd src && javac *.java */*.java && java CalendarApp --mode interactive`

cleanup:

find /Users/namanrusia/Developer/Calendar_1 -name "\*.class" -delete

`find . -name "*.class" -delete`
