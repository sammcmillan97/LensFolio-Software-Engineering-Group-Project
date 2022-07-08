package nz.ac.canterbury.seng302.portfolio.util;

import nz.ac.canterbury.seng302.portfolio.model.Event;
import nz.ac.canterbury.seng302.portfolio.model.Project;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlannerUtil {

    /**
     * Creates a map of representing the days when events occur and the amount of events occurring on that day.
     * Used in the full calendar
     * Key is a string of the date in the form: "2022-01-01"
     * Value is a int representing how many events occur on that day
     * @param eventList The list of events from any given project
     * @param project The parent project
     * @return The map of key (day strings) value (amount of events)
     */
    public static Map<String, Integer> getEventsForCalender(List<Event> eventList, Project project) {

        LocalDate startDate = Instant.ofEpochMilli(project.getStartDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endDate = Instant.ofEpochMilli(project.getEndDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();

        HashMap<String, Integer> eventMap = new HashMap<>();

        for (Event event : eventList) {
            LocalDate eventStart = Instant.ofEpochMilli(event.getEventStartDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDate().minusDays(1);
            LocalDate eventFinish = Instant.ofEpochMilli(event.getEventEndDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDate().plusDays(1);

            for (LocalDate date = startDate; date.isBefore(endDate.plusDays(1)); date = date.plusDays(1)) {

                if (date.isAfter(eventStart) && date.isBefore(eventFinish)) {
                    eventMap.merge(date.toString(), 1, Integer::sum);
                }
                if (date.isAfter(eventFinish)) {
                    break;
                }
            }
        }
        return eventMap;
    }

}