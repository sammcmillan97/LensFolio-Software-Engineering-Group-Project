package nz.ac.canterbury.seng302.portfolio.util;

import nz.ac.canterbury.seng302.portfolio.model.Deadline;
import nz.ac.canterbury.seng302.portfolio.model.Event;
import nz.ac.canterbury.seng302.portfolio.model.Milestone;
import nz.ac.canterbury.seng302.portfolio.model.PlannerDailyEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlannerUtil {
    private static final Logger PORTFOLIO_LOGGER = LoggerFactory.getLogger("com.portfolio");

    private PlannerUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Creates a map of representing the days when events occur and the amount of events occurring on that day.
     * Used in the full calendar
     * Key is a string of the date in the form: "2022-01-01"
     * Value is a plannerDailyEvent representing data needed to be displayed in the planner
     * @param eventList The list of events from any given project
     * @return The map of String(date strings) Value(PlannerDailyEvent)
     */
    public static Map<String, PlannerDailyEvent> getEventsForCalender(List<Event> eventList) {

        HashMap<String, PlannerDailyEvent> eventMap = new HashMap<>();

        for (Event event : eventList) {
            LocalDate eventStart = Instant.ofEpochMilli(event.getEventStartDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate eventFinish = Instant.ofEpochMilli(event.getEventEndDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();

            for (LocalDate date = eventStart; date.isBefore(eventFinish.plusDays(1)); date = date.plusDays(1)) {
                String eventDateString = date.toString();
                if(eventMap.containsKey(eventDateString)) {
                    PlannerDailyEvent tempDailyEvent = eventMap.get(eventDateString);
                    tempDailyEvent.addNumberOfEvents();
                    tempDailyEvent.addDescription(event.getEventName());
                    eventMap.replace(eventDateString, tempDailyEvent);
                } else {
                    PlannerDailyEvent tempDailyEvent = new PlannerDailyEvent("e" + eventDateString, eventDateString,
                            event.getEventName(), 1, "daily-event");
                    eventMap.put(eventDateString, tempDailyEvent);
                }
            }
        }
        return eventMap;
    }

    /**
     * Creates a map representing the days when deadlines occur and the amount of deadlines occurring on that day.
     * Used in the full calendar
     * Key is a string of the date in the form: "2022-01-01"
     * Value is a plannerDailyEvent representing data needed to be displayed in the planner
     * @param deadlineList The list of deadlines from any given project
     * @return The map of String(date strings) Value(PlannerDailyEvent)
     */
    public static Map<String, PlannerDailyEvent> getDeadlinesForCalender(List<Deadline> deadlineList) {

        HashMap<String, PlannerDailyEvent> deadlineMap = new HashMap<>();

        for (Deadline deadline: deadlineList) {
            String deadlineDateString = deadline.getDeadlineDate().toString().substring(0, 10);
            if (deadlineMap.containsKey(deadlineDateString)) {
                PlannerDailyEvent tempDailyEvent = deadlineMap.get(deadlineDateString);
                tempDailyEvent.addNumberOfEvents();
                tempDailyEvent.addDescription(deadline.getDeadlineName());
                deadlineMap.replace(deadlineDateString, tempDailyEvent);
            } else {
                PlannerDailyEvent tempDailyEvent = new PlannerDailyEvent("d" + deadlineDateString, deadlineDateString,
                        deadline.getDeadlineName(),1,  "daily-deadline");
                deadlineMap.put(deadlineDateString, tempDailyEvent);
            }
        }
        return deadlineMap;
    }

    /**
     * Creates a map representing the days when milestones occur and the amount of milestones occurring on that day.
     * Used in the full calendar
     * Key is a string of the date in the form: "2022-01-01"
     * Value is a plannerDailyEvent representing data needed to be displayed in the planner
     * @param mileStoneList The list of milestones from any given project
     * @return The map of String(date strings) Value(PlannerDailyEvent)
     */
    public static Map<String, PlannerDailyEvent> getMilestonesForCalender(List<Milestone> mileStoneList) {

        HashMap<String, PlannerDailyEvent> milestoneMap = new HashMap<>();

        for (Milestone milestone: mileStoneList) {
            String milestoneDateString = milestone.getMilestoneDate().toString().substring(0, 10);
            if (milestoneMap.containsKey(milestoneDateString)) {
                PlannerDailyEvent tempDailyEvent = milestoneMap.get(milestoneDateString);
                tempDailyEvent.addNumberOfEvents();
                tempDailyEvent.addDescription(milestone.getMilestoneName());
                milestoneMap.replace(milestoneDateString, tempDailyEvent);
            } else {
                PlannerDailyEvent tempDailyEvent = new PlannerDailyEvent("m" + milestoneDateString, milestoneDateString,
                        milestone.getMilestoneName(),1,  "daily-milestone");
                milestoneMap.put(milestoneDateString, tempDailyEvent);
            }
        }
        return milestoneMap;
    }
}