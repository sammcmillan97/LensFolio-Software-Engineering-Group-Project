package nz.ac.canterbury.seng302.portfolio.Util;

import nz.ac.canterbury.seng302.portfolio.model.Deadline;
import nz.ac.canterbury.seng302.portfolio.model.Event;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.util.ProjectDetailsUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ProjectDetailsUtilTest {
    public static List<Event> eventList;
    public static List<Sprint> sprintList;
    public static List<Deadline> deadlineList;

    @BeforeEach
    void setupSprintList() {
        sprintList = new ArrayList<>();
        eventList = new ArrayList<>();
        deadlineList = new ArrayList<>();
        sprintList.add(new Sprint(1, "Test Sprint",1, "Description",
                Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        sprintList.add(new Sprint(1, "Test Sprint",2, "Description",
                Date.valueOf("2022-05-17"), Date.valueOf("2022-06-16")));
        sprintList.add(new Sprint(1, "Test Sprint",3, "Description",
                Date.valueOf("2022-06-17"), Date.valueOf("2022-07-16")));
        sprintList.add(new Sprint(1, "Test Sprint",4, "Description",
                Date.valueOf("2022-07-17"), Date.valueOf("2022-08-16")));
    }

    @Test
    void whenAllEventsOutsideSprintsTestEmbedEventsDoesNotEmbedAnyEvents() {
        eventList.add(new Event(1, "Test Event Uno",
                Date.valueOf("2022-03-05"), Date.valueOf("2022-04-06")));
        ProjectDetailsUtil.embedEvents(eventList, sprintList);
        for (Sprint sprint: sprintList) {
            assertEquals(0, sprint.getEventsInside().size());
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

    @Test
    void whenEventStartsInsideLastSprintAndEndsAfterLastSprintTestEmbedEventInLastSprint() {
        eventList.add(new Event(1, "Test Event Uno",
                Date.valueOf("2022-07-20"), Date.valueOf("2022-08-18")));
        ProjectDetailsUtil.embedEvents(eventList, sprintList);
        List<Integer> eventIndexes = sprintList.get(3).getEventsInside();
        assertEquals(eventList.get(0), eventList.get(eventIndexes.get(0)));
    }

    @Test
    void whenEventStartsInsideFirstSprintAndEndsAfterLastSprintTestEmbedEventsInEachSprint() {
        eventList.add(new Event(1, "Test Event Uno",
                Date.valueOf("2022-04-20"), Date.valueOf("2022-08-18")));
        ProjectDetailsUtil.embedEvents(eventList, sprintList);
        for (Sprint sprint: sprintList) {
            List<Integer> eventIndexes = sprint.getEventsInside();
            assertEquals(eventList.get(0), eventList.get(eventIndexes.get(0)));
        }
    }

    @Test
    void whenEventStartsBeforeFirstSprintAndEndsAfterLastSprintTestEmbedEventsInEachSprint() {
        eventList.add(new Event(1, "Test Event Uno",
                Date.valueOf("2022-04-10"), Date.valueOf("2022-08-18")));
        ProjectDetailsUtil.embedEvents(eventList, sprintList);
        for (Sprint sprint: sprintList) {
            List<Integer> eventIndexes = sprint.getEventsInside();
            assertEquals(eventList.get(0), eventList.get(eventIndexes.get(0)));
        }
    }

    @Test
    void whenEventEndsDayBeforeSprintTestEventNotEmbedded() {
        eventList.add(new Event(1, "Test Event Uno",
                Date.valueOf("2022-04-13"), Date.valueOf("2022-04-14")));
        ProjectDetailsUtil.embedEvents(eventList, sprintList);
        assertEquals(0, sprintList.get(0).getEventsInside().size());
    }

    @Test
    void whenEventEndsOnSprintStartDateTestEventEmbedded() {
        eventList.add(new Event(1, "Test Event Uno",
                Date.valueOf("2022-04-13"), Date.valueOf("2022-04-15")));
        ProjectDetailsUtil.embedEvents(eventList, sprintList);
        assertEquals(1, sprintList.get(0).getEventsInside().size());
    }

    @Test
    void whenEventStartsDayAfterSprintTestEventNotEmbedded() {
        eventList.add(new Event(1, "Test Event Uno",
                Date.valueOf("2022-05-17"), Date.valueOf("2022-05-20")));
        ProjectDetailsUtil.embedEvents(eventList, sprintList);
        assertEquals(0, sprintList.get(0).getEventsInside().size());
    }

    @Test
    void whenEventStartsOnSprintEndDateTestEventEmbedded() {
        eventList.add(new Event(1, "Test Event Uno",
                Date.valueOf("2022-05-16"), Date.valueOf("2022-05-18")));
        ProjectDetailsUtil.embedEvents(eventList, sprintList);
        assertEquals(1, sprintList.get(0).getEventsInside().size());
    }

    @Test
    void whenEventStartAndEndDateInSprintTestEmbedEventAndEventStartAndEndColourIsSameAsSprintColour() {
        ProjectDetailsUtil.colorSprints(sprintList);
        eventList.add(new Event(1, "Test Event Uno",
                Date.valueOf("2022-04-20"), Date.valueOf("2022-04-22")));
        ProjectDetailsUtil.embedEvents(eventList, sprintList);
        assertEquals(sprintList.get(0).getColour(), eventList.get(0).getColourStart());
        assertEquals(sprintList.get(0).getColour(), eventList.get(0).getColourEnd());
    }

    @Test
    void whenEventStartAndEndDateInDifferentSprintsTestEmbedEventAndEventStartAndEndColourIsSameAsSprintColour() {
        ProjectDetailsUtil.colorSprints(sprintList);
        eventList.add(new Event(1, "Test Event Uno",
                Date.valueOf("2022-04-20"), Date.valueOf("2022-05-22")));
        ProjectDetailsUtil.embedEvents(eventList, sprintList);
        assertEquals(sprintList.get(0).getColour(), eventList.get(0).getColourStart());
        assertEquals(sprintList.get(1).getColour(), eventList.get(0).getColourEnd());
    }

    @Test
    void whenEventStartAndEndDateOutsideAllSprintsTestEmbedEventAndEventStartAndEndNotColoured() {
        ProjectDetailsUtil.colorSprints(sprintList);
        eventList.add(new Event(1, "Test Event Uno",
                Date.valueOf("2022-03-20"), Date.valueOf("2022-03-22")));
        ProjectDetailsUtil.embedEvents(eventList, sprintList);
        assertNull(eventList.get(0).getColourStart());
        assertNull(eventList.get(0).getColourEnd());
    }

    @Test
    void whenEventStartAndEndDateInsideSprintsSeperatedBySprintInTheMiddleTestStartAndEndColourNotOfSprintInside() {
        ProjectDetailsUtil.colorSprints(sprintList);
        eventList.add(new Event(1, "Test Event Uno",
                Date.valueOf("2022-04-17"), Date.valueOf("2022-06-20")));
        ProjectDetailsUtil.embedEvents(eventList, sprintList);
        assertEquals(sprintList.get(0).getColour(), eventList.get(0).getColourStart());
        assertEquals(sprintList.get(2).getColour(), eventList.get(0).getColourEnd());
        assertNotEquals(sprintList.get(1).getColour(), eventList.get(0).getColourStart());
        assertNotEquals(sprintList.get(1).getColour(), eventList.get(0).getColourEnd());
    }

    @Test
    void whenDeadlineOccursBeforeAllSprintsTestDeadlineNotEmbedded() {
        deadlineList.add(new Deadline(1, "Test Deadline Hand In",
                Date.valueOf("2022-04-13")));
        ProjectDetailsUtil.embedDeadlines(deadlineList, sprintList);
        for (Sprint sprint: sprintList) {
            assertEquals(0, sprint.getDeadlinesInside().size());
        }
    }

    @Test
    void whenDeadlineOccursAfterAllSprintsTestDeadlineNotEmbedded() {
        deadlineList.add(new Deadline(1, "Test Deadline Hand In",
                Date.valueOf("2022-08-18")));
        ProjectDetailsUtil.embedDeadlines(deadlineList, sprintList);
        for (Sprint sprint: sprintList) {
            assertEquals(0, sprint.getDeadlinesInside().size());
        }
    }

    @Test
    void whenDeadlineOccursDayBeforeSprintStartsTestDeadlineNotEmbedded() {
        deadlineList.add(new Deadline(1, "Test Deadline Hand In",
                Date.valueOf("2022-04-14")));
        ProjectDetailsUtil.embedDeadlines(deadlineList, sprintList);
        assertEquals(0, sprintList.get(0).getDeadlinesInside().size());
    }

    @Test
    void whenDeadlineOccursDayAfterSprintEndsTestDeadlineNotEmbedded() {
        deadlineList.add(new Deadline(1, "Test Deadline Hand In",
                Date.valueOf("2022-05-17")));
        ProjectDetailsUtil.embedDeadlines(deadlineList, sprintList);
        assertEquals(0, sprintList.get(0).getDeadlinesInside().size());
    }

    @Test
    void whenDeadlineOccursDaySprintStartsTestDeadlineEmbedded() {
        deadlineList.add(new Deadline(1, "Test Deadline Hand In",
                Date.valueOf("2022-04-15")));
        ProjectDetailsUtil.embedDeadlines(deadlineList, sprintList);
        List<Integer> deadlineIndexes = sprintList.get(0).getDeadlinesInside();
        assertEquals(deadlineList.get(0), deadlineList.get(deadlineIndexes.get(0)));
    }

    @Test
    void whenDeadlineOccursDaySprintEndsTestDeadlineEmbedded() {
        deadlineList.add(new Deadline(1, "Test Deadline Hand In",
                Date.valueOf("2022-05-16")));
        ProjectDetailsUtil.embedDeadlines(deadlineList, sprintList);
        List<Integer> eventIndexes = sprintList.get(0).getDeadlinesInside();
        assertEquals(deadlineList.get(0), deadlineList.get(eventIndexes.get(0)));
    }

    @Test
    void whenDeadlineOccursWithinSprintTestEmbedDeadlineAndDeadlineColourSameAsSprint() {
        ProjectDetailsUtil.colorSprints(sprintList);
        deadlineList.add(new Deadline(1, "Test Deadline Hand In",
                Date.valueOf("2022-04-20")));
        ProjectDetailsUtil.embedDeadlines(deadlineList, sprintList);
        assertEquals(sprintList.get(0).getColour(), deadlineList.get(0).getColour());
    }

    @Test
    void whenDeadlineNotOccursWithinSprintTestEmbedDeadlineAndDeadlineColourNotExist() {
        ProjectDetailsUtil.colorSprints(sprintList);
        deadlineList.add(new Deadline(1, "Test Deadline Hand In",
                Date.valueOf("2022-03-20")));
        ProjectDetailsUtil.embedDeadlines(deadlineList, sprintList);
        assertNull(deadlineList.get(0).getColour());
    }

    @Test
    void whenSprintEventDeadlineListsExistTestOrderImportantDates() {
        eventList.add(new Event(1, "Test Event Uno",
                Date.valueOf("2022-05-16"), Date.valueOf("2022-05-20")));
        deadlineList.add(new Deadline(1, "Test Deadline Hand In",
                Date.valueOf("2022-05-14")));
        List<Pair<Integer, String>> indices = new ArrayList<>();
        indices.add(Pair.of(0, "Sprint"));
        indices.add(Pair.of(0, "Deadline"));
        indices.add(Pair.of(0, "Event"));
        indices.add(Pair.of(1, "Sprint"));
        indices.add(Pair.of(2, "Sprint"));
        indices.add(Pair.of(3, "Sprint"));
        List<Pair<Integer, String>> importantDates = ProjectDetailsUtil.getOrderedImportantDates(eventList, sprintList, deadlineList);
        for (int i = 0; i < importantDates.size(); i++) {
            assertEquals(indices.get(i), importantDates.get(i));
        }
    }

    @Test
    void whenSprintEventDeadlineListsExistAndEventAndDeadlineDatesAreTheSameTestOrderImportantDates() {
        eventList.add(new Event(1, "Test Event Uno",
                Date.valueOf("2022-05-14"), Date.valueOf("2022-05-20")));
        deadlineList.add(new Deadline(1, "Test Deadline Hand In",
                Date.valueOf("2022-05-14")));
        List<Pair<Integer, String>> indices = new ArrayList<>();
        indices.add(Pair.of(0, "Sprint"));
        indices.add(Pair.of(0, "Event"));
        indices.add(Pair.of(0, "Deadline"));
        indices.add(Pair.of(1, "Sprint"));
        indices.add(Pair.of(2, "Sprint"));
        indices.add(Pair.of(3, "Sprint"));
        List<Pair<Integer, String>> importantDates = ProjectDetailsUtil.getOrderedImportantDates(eventList, sprintList, deadlineList);
        for (int i = 0; i < importantDates.size(); i++) {
            assertEquals(indices.get(i), importantDates.get(i));
        }
    }

    @Test
    void whenSprintEventDeadlineListsExistAndEventAndSprintDatesAreTheSameTestOrderImportantDates() {
        eventList.add(new Event(1, "Test Event Uno",
                Date.valueOf("2022-04-15"), Date.valueOf("2022-05-20")));
        deadlineList.add(new Deadline(1, "Test Deadline Hand In",
                Date.valueOf("2022-05-14")));
        List<Pair<Integer, String>> indices = new ArrayList<>();
        indices.add(Pair.of(0, "Event"));
        indices.add(Pair.of(0, "Sprint"));
        indices.add(Pair.of(0, "Deadline"));
        indices.add(Pair.of(1, "Sprint"));
        indices.add(Pair.of(2, "Sprint"));
        indices.add(Pair.of(3, "Sprint"));
        List<Pair<Integer, String>> importantDates = ProjectDetailsUtil.getOrderedImportantDates(eventList, sprintList, deadlineList);
        for (int i = 0; i < importantDates.size(); i++) {
            assertEquals(indices.get(i), importantDates.get(i));
        }
    }

    @Test
    void whenSprintEventDeadlineListsExistAndDeadlineAndSprintDatesAreTheSameTestOrderImportantDates() {
        eventList.add(new Event(1, "Test Event Uno",
                Date.valueOf("2022-05-15"), Date.valueOf("2022-05-20")));
        deadlineList.add(new Deadline(1, "Test Deadline Hand In",
                Date.valueOf("2022-04-15")));
        List<Pair<Integer, String>> indices = new ArrayList<>();
        indices.add(Pair.of(0, "Deadline"));
        indices.add(Pair.of(0, "Sprint"));
        indices.add(Pair.of(0, "Event"));
        indices.add(Pair.of(1, "Sprint"));
        indices.add(Pair.of(2, "Sprint"));
        indices.add(Pair.of(3, "Sprint"));
        List<Pair<Integer, String>> importantDates = ProjectDetailsUtil.getOrderedImportantDates(eventList, sprintList, deadlineList);
        for (int i = 0; i < importantDates.size(); i++) {
            assertEquals(indices.get(i), importantDates.get(i));
        }
    }

    @Test
    void whenSprintEventDeadlineListsExistAndEventDeadlineAndSprintDatesAreTheSameTestOrderImportantDates() {
        eventList.add(new Event(1, "Test Event Uno",
                Date.valueOf("2022-04-15"), Date.valueOf("2022-05-20")));
        deadlineList.add(new Deadline(1, "Test Deadline Hand In",
                Date.valueOf("2022-04-15")));
        List<Pair<Integer, String>> indices = new ArrayList<>();
        indices.add(Pair.of(0, "Event"));
        indices.add(Pair.of(0, "Deadline"));
        indices.add(Pair.of(0, "Sprint"));
        indices.add(Pair.of(1, "Sprint"));
        indices.add(Pair.of(2, "Sprint"));
        indices.add(Pair.of(3, "Sprint"));
        List<Pair<Integer, String>> importantDates = ProjectDetailsUtil.getOrderedImportantDates(eventList, sprintList, deadlineList);
        for (int i = 0; i < importantDates.size(); i++) {
            assertEquals(indices.get(i), importantDates.get(i));
        }
    }

    @Test
    void whenDeadlinesAndEventsEmbeddedInSprintsTestNotIncludedInImportantDates() {
        eventList.add(new Event(1, "Test Event Uno",
                Date.valueOf("2022-05-10"), Date.valueOf("2022-05-20")));
        deadlineList.add(new Deadline(1, "Test Deadline Hand In",
                Date.valueOf("2022-04-20")));
        ProjectDetailsUtil.embedDeadlines(deadlineList, sprintList);
        ProjectDetailsUtil.embedEvents(eventList, sprintList);
        List<Pair<Integer, String>> indices = new ArrayList<>();
        indices.add(Pair.of(0, "Sprint"));
        indices.add(Pair.of(1, "Sprint"));
        indices.add(Pair.of(2, "Sprint"));
        indices.add(Pair.of(3, "Sprint"));
        List<Pair<Integer, String>> importantDates = ProjectDetailsUtil.getOrderedImportantDates(eventList, sprintList, deadlineList);
        for (int i = 0; i < importantDates.size(); i++) {
            assertEquals(indices.get(i), importantDates.get(i));
        }
    }

    @Test
    void whenSprintListExistsTestColourSprints() {
        for(Sprint sprint: sprintList) {
            assertNull(sprint.getColour());
        }
        ProjectDetailsUtil.colorSprints(sprintList);
        for(Sprint sprint: sprintList) {
            assertNotNull(sprint.getColour());
        }
    }
}
