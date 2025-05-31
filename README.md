The following sections describe the feature set of the app is expected to offer in this assignment. For this assignment, we will assume that the timezone is EST, and you are required to support, at the minimum, a single calendar.


## Feature Set
### Events
An event is required to have
- a subject,
- start date and time.
- Optionally it may have a longer description,
- end date and time,
- location (physical or online)
- and a status (whether the event is public or private).

**There cannot be two events with the same subject, start date/time and end date/time.**

Create A Single Calendar Event
The application should support the ability to create a single event in a calendar.
- If an event does not have an end date/time then the event is an "All Day Event", which is defined as 8am to 5pm. 
- It is possible for a single event to span several days.

Keep in mind that two events cannot have the same subject, start date/time and end date/time.

Create An Event Series
The application should support the ability to create a series of events (i.e. a recurring event). In addition to the required and optional details mentioned above, events in an event series repeat on specific days of a week (as indicated by the user) for a specific number of occurrences or until a specific end date and time. A single event in a series can only span one day (i.e. it must start and finish on the same day).

All events in an event series are required to have the same start time. Keep in mind that two events in the calendar cannot have the same subject, start date/time and end date/time.

