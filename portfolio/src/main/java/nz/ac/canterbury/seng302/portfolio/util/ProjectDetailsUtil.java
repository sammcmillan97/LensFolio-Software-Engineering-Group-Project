package nz.ac.canterbury.seng302.portfolio.util;

import nz.ac.canterbury.seng302.portfolio.model.Event;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import org.springframework.data.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class ProjectDetailsUtil {

    public static void embedEvents(List<Event> eventList, List<Sprint> sprintList) {
        int completed = 0;
        for (int i = 0; i < eventList.size(); i++) {
            for (Sprint sprint : sprintList) {
                if ((eventList.get(i).getEventStartDate().after(sprint.getStartDate()) || eventList.get(i).getEventStartDate().equals(sprint.getStartDate())) && (eventList.get(i).getEventEndDate().before(sprint.getEndDate()) || eventList.get(i).getEventEndDate().equals(sprint.getEndDate()))) {
                    sprint.addEventsInside(i, 0);
                    completed = completed + 2;
                } else if ((eventList.get(i).getEventStartDate().after(sprint.getStartDate()) || eventList.get(i).getEventStartDate().equals(sprint.getStartDate())) && (eventList.get(i).getEventStartDate().before(sprint.getEndDate()) || eventList.get(i).getEventStartDate().equals(sprint.getEndDate()))) {
                    sprint.addEventsInside(i, 1);
                    completed++;
                } else if ((eventList.get(i).getEventEndDate().after(sprint.getStartDate()) || eventList.get(i).getEventEndDate().equals(sprint.getStartDate())) && (eventList.get(i).getEventEndDate().before(sprint.getEndDate()) || eventList.get(i).getEventEndDate().equals(sprint.getEndDate()))) {
                    sprint.addEventsInside(i, 2);
                    completed++;
                } else if (eventList.get(i).getEventStartDate().before(sprint.getStartDate()) && eventList.get(i).getEventEndDate().after(sprint.getEndDate())) {
                    sprint.addEventsInside(i, 3);
                }
            }
            if (completed >= 2) {
                eventList.get(i).setCompleted(true);
            }
        }
    }

    public static List<Pair<Integer, String>> getOrderedImportantDates(List<Event> eventList, List<Sprint> sprintList) {
        int eventListSize = ProjectDetailsUtil.eventListSize(eventList);
        List<Pair<Integer, String>> importantDates = new ArrayList<>();
        int sprintCounter = 0;
        int eventCounter = 0;

        while ((sprintCounter < sprintList.size()) && (eventCounter < eventListSize)) {
            if (!eventList.get(eventCounter).isCompleted()) {
                if (sprintList.get(sprintCounter).getStartDate().before(eventList.get(eventCounter).getEventStartDate())) {
                    importantDates.add(Pair.of(sprintCounter, "Sprint"));
                    sprintCounter++;
                } else {
                    Event event = eventList.get(eventCounter);
                    event.setType("Event");
                    importantDates.add(Pair.of(eventCounter, "Event"));
                    eventCounter++;
                }
            }
        }
        if (sprintCounter < sprintList.size()) {
            while (sprintCounter < sprintList.size()) {
                Sprint sprint = sprintList.get(sprintCounter);
                sprint.setType("Sprint");
                importantDates.add(Pair.of(sprintCounter, "Sprint"));
                sprintCounter++;
            }
        } else {
            while (eventCounter < eventList.size()) {
                if (!eventList.get(eventCounter).isCompleted()) {
                    Event event = eventList.get(eventCounter);
                    event.setType("Event");
                    importantDates.add(Pair.of(eventCounter, "Event"));
                    eventCounter++;
                }
            }
        }
        return importantDates;
    }

    private static int eventListSize(List<Event> eventList) {
        int eventListSize = 0;
        for (Event value : eventList) {
            if (!value.isCompleted()) {
                eventListSize++;
            }
        }
        return eventListSize;
    }


}
