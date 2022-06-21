package nz.ac.canterbury.seng302.portfolio.Util;

import nz.ac.canterbury.seng302.portfolio.model.Event;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.util.ProjectDetailsUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ProjectDetailsUtilTest {
    public List<Event> eventList;
    public static List<Sprint> sprintList;

    @BeforeAll
    static void setupSprintList() {
        sprintList = new ArrayList<>();
        sprintList.add(new Sprint(1, "Test Sprint",1, "Description",
                Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        sprintList.add(new Sprint(1, "Test Sprint",2, "Description",
                Date.valueOf("2022-05-17"), Date.valueOf("2022-06-16")));
        sprintList.add(new Sprint(1, "Test Sprint",3, "Description",
                Date.valueOf("2022-06-17"), Date.valueOf("2022-07-16")));
        sprintList.add(new Sprint(1, "Test Sprint",4, "Description",
                Date.valueOf("2022-07-17"), Date.valueOf("2022-08-16")));
    }

    @BeforeEach
    void clearEventList() {
        eventList = new ArrayList<>();
    }

    @Test
    void whenAllEventsOutsideSprintsTestEmbedEventsDoesNotEmbedAnyEvents() {
        eventList.add(new Event(1, "Test Event Uno",
                Date.valueOf("2022-03-05"), Date.valueOf("2022-04-06")));
        ProjectDetailsUtil.embedEvents(eventList, sprintList);
        for (Sprint sprint: sprintList) {
            assertEquals(sprint.getEventsInside().size(), 0);
        }
    }

    @Test
    void whenEventStartsOutsideFirstSprintAndEndsInsideFirstSprintTestEmbedEventsDoesEmbedEvent() {
        eventList.add(new Event(1, "Test Event Uno",
                Date.valueOf("2022-03-05"), Date.valueOf("2022-04-16")));
        ProjectDetailsUtil.embedEvents(eventList, sprintList);
        List<Integer> eventIndexes = sprintList.get(0).getEventsInside();
        assertEquals(eventList.get(0), eventList.get(eventIndexes.get(0)));
    }

    @Test
    void whenEventStartsInsideFirstSprintAndEndsInsideSameSprintTestEmbedEventsDoesEmbedEvent() {
        eventList.add(new Event(1, "Test Event Uno",
                Date.valueOf("2022-04-20"), Date.valueOf("2022-05-10")));
        ProjectDetailsUtil.embedEvents(eventList, sprintList);
        List<Integer> eventIndexes = sprintList.get(0).getEventsInside();
        assertEquals(eventList.get(0), eventList.get(eventIndexes.get(0)));
    }

    @Test
    void whenEventStartsOustideFirstSprintAndEndsInsideLastSprintTestEmbedEventsEmbedsEventInEachSprint() {
        eventList.add(new Event(1, "Test Event Uno",
                Date.valueOf("2022-03-05"), Date.valueOf("2022-07-25")));
        ProjectDetailsUtil.embedEvents(eventList, sprintList);
        for (Sprint sprint: sprintList) {
            List<Integer> eventIndexes = sprint.getEventsInside();
            assertEquals(eventList.get(0), eventList.get(eventIndexes.get(0)));
        }
    }

    @Test
    void whenEventStartsInsideFirstSprintAndEndsInsideLastSprintTestEmbedEventsEmbedsEventInEachSprint() {
        eventList.add(new Event(1, "Test Event Uno",
                Date.valueOf("2022-04-20"), Date.valueOf("2022-07-25")));
        ProjectDetailsUtil.embedEvents(eventList, sprintList);
        for (Sprint sprint: sprintList) {
            List<Integer> eventIndexes = sprint.getEventsInside();
            assertEquals(eventList.get(0), eventList.get(eventIndexes.get(0)));
        }
    }
}
