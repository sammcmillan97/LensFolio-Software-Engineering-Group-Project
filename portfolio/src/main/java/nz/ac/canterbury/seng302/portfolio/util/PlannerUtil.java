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


    public static void getEventsForCalender(List<Event> eventList, Project project) {

        LocalDate startDate = Instant.ofEpochMilli(project.getStartDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endDate = Instant.ofEpochMilli(project.getEndDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();

        HashMap<String, Integer> eventMap = new HashMap<>();

        for(Event event: eventList) {
            LocalDate eventStart = Instant.ofEpochMilli(event.getEventStartDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDate().minusDays(1);
            LocalDate eventFinish = Instant.ofEpochMilli(event.getEventEndDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDate().plusDays(1);

            for(LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {

                System.out.println("Current Date is: " + date);
                System.out.println("Event Start Date is: " + eventStart);
                System.out.println("Event end date is: " + eventFinish);

                if(date.isAfter(eventStart) && date.isBefore(eventFinish)) {
                    eventMap.merge(date.toString(), 1, Integer::sum);
                }
                if(date.isAfter(eventFinish)) {
                    break;
                }
            }
        }
        for (Map.Entry<String, Integer> entry : eventMap.entrySet()) {
            System.out.println(entry.getKey());
            System.out.println(entry.getValue());
        }
        System.out.println(project.getStartDate());
    }
}
