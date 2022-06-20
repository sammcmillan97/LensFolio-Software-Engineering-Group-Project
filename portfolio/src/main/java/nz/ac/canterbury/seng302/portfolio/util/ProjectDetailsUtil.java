package nz.ac.canterbury.seng302.portfolio.util;

import nz.ac.canterbury.seng302.portfolio.model.Event;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import org.springframework.data.util.Pair;

import java.util.*;

public class ProjectDetailsUtil {

    /**
     * Takes a list of Event objects and a list of Sprint objects and determines which Events occur within a Sprint. If
     * and Event occurs within a Sprint the Event's id number is stored within the Sprint.
     * @param eventList List of Event objects to embed in a Sprint
     * @param sprintList List of Sprint objects which will have Events embedded within
     */
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
                    completed++;
                }
            }
            if (completed >= 1) {
                eventList.get(i).setCompleted(true);
            }
            completed = 0;
        }
    }

    /**
     * Takes a list of Event objects and a list of Sprint objects and orders them by Start Date. Returns a list of Pairs
     * which hold the id of the Event or Sprint and a string which describes whether the id is for a Sprint or Event.
     * @param eventList list of Event objects to order
     * @param sprintList list of Sprint objects to order
     * @return list of Pair<Integer, String> objects which hold the id of the object and what type it is
     */
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

    /**
     * Assigns a color to each Sprint object within sprintList
     * @param sprintList list of Sprint objects to assign colours
     */
    public static void colorSprints(List<Sprint> sprintList) {
        ColourPicker.setColourZero();
        for (Sprint sprint: sprintList) {
            sprint.setColour(ColourPicker.getNextColour());
        }
    }
}
