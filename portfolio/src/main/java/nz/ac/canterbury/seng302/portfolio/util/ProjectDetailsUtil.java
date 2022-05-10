package nz.ac.canterbury.seng302.portfolio.util;

import nz.ac.canterbury.seng302.portfolio.model.Event;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import org.springframework.data.util.Pair;

import java.util.*;

public class ProjectDetailsUtil {

    public static void embedEvents(List<Event> eventList, List<Sprint> sprintList) {
        int completed = 0;
        for (int i = 0; i < eventList.size(); i++) {
            for (Sprint sprint : sprintList) {
                if ((eventList.get(i).getEventStartDate().after(sprint.getStartDate()) || eventList.get(i).getEventStartDate().equals(sprint.getStartDate())) && (eventList.get(i).getEventEndDate().before(sprint.getEndDate()) || eventList.get(i).getEventEndDate().equals(sprint.getEndDate()))) {
                    sprint.addEventsInside(i);
                    eventList.get(i).setColourStart(sprint.getColour());
                    eventList.get(i).setColourEnd(sprint.getColour());
                    completed = completed + 2;
                } else if ((eventList.get(i).getEventStartDate().after(sprint.getStartDate()) || eventList.get(i).getEventStartDate().equals(sprint.getStartDate())) && (eventList.get(i).getEventStartDate().before(sprint.getEndDate()) || eventList.get(i).getEventStartDate().equals(sprint.getEndDate()))) {
                    sprint.addEventsInside(i);
                    eventList.get(i).setColourStart(sprint.getColour());
                    completed++;
                } else if ((eventList.get(i).getEventEndDate().after(sprint.getStartDate()) || eventList.get(i).getEventEndDate().equals(sprint.getStartDate())) && (eventList.get(i).getEventEndDate().before(sprint.getEndDate()) || eventList.get(i).getEventEndDate().equals(sprint.getEndDate()))) {
                    sprint.addEventsInside(i);
                    eventList.get(i).setColourEnd(sprint.getColour());
                    completed++;
                } else if (eventList.get(i).getEventStartDate().before(sprint.getStartDate()) && eventList.get(i).getEventEndDate().after(sprint.getEndDate())) {
                    sprint.addEventsInside(i);
                }
            }
            if (completed >= 1) {
                eventList.get(i).setCompleted(true);
            }
            completed = 0;
        }
    }

    public static List<Pair<Integer, String>> getOrderedImportantDates(List<Event> eventList, List<Sprint> sprintList) {
        List<Pair<Integer, String>> importantDates = new ArrayList<>();

        for (int i = 0; i < eventList.size(); i++) {
            if (!eventList.get(i).isCompleted()) {
                importantDates.add(Pair.of(i, "Event"));
            }
        }
        for (int i = 0; i < sprintList.size(); i++) {
            importantDates.add(Pair.of(i, "Sprint"));
        }
        importantDates.sort(Comparator.comparing((Pair<Integer, String> a) -> (a.getSecond().equals("Sprint") ? sprintList.get(a.getFirst()).getStartDate() : eventList.get(a.getFirst()).getEventStartDate())));
        return importantDates;
    }

    public static void colorSprints(List<Sprint> sprintList) {
        ColourPicker.setColourZero();
        for (Sprint sprint: sprintList) {
            sprint.setColour(ColourPicker.getNextColour());
        }
    }
}
