package nz.ac.canterbury.seng302.portfolio.util;

import nz.ac.canterbury.seng302.portfolio.model.project.Deadline;
import nz.ac.canterbury.seng302.portfolio.model.project.Event;
import nz.ac.canterbury.seng302.portfolio.model.project.Milestone;
import nz.ac.canterbury.seng302.portfolio.model.project.Sprint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProjectDetailsUtilTest {
    public static List<Event> eventList;
    public static List<Sprint> sprintList;
    public static List<Deadline> deadlineList;
    public static List<Milestone> milestoneList;

    @BeforeEach
    void setupSprintList() {
        sprintList = new ArrayList<>();
        eventList = new ArrayList<>();
        deadlineList = new ArrayList<>();
        milestoneList = new ArrayList<>();
        sprintList.add(new Sprint(1, "Test Sprint",  "Description",
                Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        sprintList.add(new Sprint(1, "Test Sprint",  "Description",
                Date.valueOf("2022-05-17"), Date.valueOf("2022-06-16")));
        sprintList.add(new Sprint(1, "Test Sprint", "Description",
                Date.valueOf("2022-06-17"), Date.valueOf("2022-07-16")));
        sprintList.add(new Sprint(1, "Test Sprint",  "Description",
                Date.valueOf("2022-07-17"), Date.valueOf("2022-08-16")));
    }

    @Test
    void whenAllEventsOutsideSprints_testEmbedEventsDoesNotEmbedAnyEvents() {
        eventList.add(new Event(1, "Test Event Uno",
                Date.valueOf("2022-03-05"), Date.valueOf("2022-04-06")));
        ProjectDetailsUtil.embedEvents(eventList, sprintList);
        for (Sprint sprint : sprintList) {
            assertEquals(0, sprint.getEventsInside().size());
        }
    }

    @Test
    void whenEventStartsOutsideFirstSprint_andEndsInsideFirstSprint_testEmbedEventsDoesEmbedEvent() {
        eventList.add(new Event(1, "Test Event Uno",
                Date.valueOf("2022-03-05"), Date.valueOf("2022-04-16")));
        ProjectDetailsUtil.embedEvents(eventList, sprintList);
        List<Integer> eventIndexes = sprintList.get(0).getEventsInside();
        assertEquals(eventList.get(0), eventList.get(eventIndexes.get(0)));
    }

    @Test
    void whenEventStartsInsideFirstSprint_andEndsInsideSameSprint_testEmbedEventsDoesEmbedEvent() {
        eventList.add(new Event(1, "Test Event Uno",
                Date.valueOf("2022-04-20"), Date.valueOf("2022-05-10")));
        ProjectDetailsUtil.embedEvents(eventList, sprintList);
        List<Integer> eventIndexes = sprintList.get(0).getEventsInside();
        assertEquals(eventList.get(0), eventList.get(eventIndexes.get(0)));
    }

    @Test
    void whenEventStartsOustideFirstSprint_andEndsInsideLastSprint_testEmbedEventsEmbedsEventInEachSprint() {
        eventList.add(new Event(1, "Test Event Uno",
                Date.valueOf("2022-03-05"), Date.valueOf("2022-07-25")));
        ProjectDetailsUtil.embedEvents(eventList, sprintList);
        for (Sprint sprint : sprintList) {
            List<Integer> eventIndexes = sprint.getEventsInside();
            assertEquals(eventList.get(0), eventList.get(eventIndexes.get(0)));
        }
    }

    @Test
    void whenEventStartsInsideFirstSprint_andEndsInsideLastSprint_testEmbedEventsEmbedsEventInEachSprint() {
        eventList.add(new Event(1, "Test Event Uno",
                Date.valueOf("2022-04-20"), Date.valueOf("2022-07-25")));
        ProjectDetailsUtil.embedEvents(eventList, sprintList);
        for (Sprint sprint : sprintList) {
            List<Integer> eventIndexes = sprint.getEventsInside();
            assertEquals(eventList.get(0), eventList.get(eventIndexes.get(0)));
        }
    }

    @Test
    void whenEventStartsInsideLastSprint_andEndsAfterLastSprint_testEmbedEventInLastSprint() {
        eventList.add(new Event(1, "Test Event Uno",
                Date.valueOf("2022-07-20"), Date.valueOf("2022-08-18")));
        ProjectDetailsUtil.embedEvents(eventList, sprintList);
        List<Integer> eventIndexes = sprintList.get(3).getEventsInside();
        assertEquals(eventList.get(0), eventList.get(eventIndexes.get(0)));
    }

    @Test
    void whenEventStartsInsideFirstSprint_andEndsAfterLastSprint_testEmbedEventsInEachSprint() {
        eventList.add(new Event(1, "Test Event Uno",
                Date.valueOf("2022-04-20"), Date.valueOf("2022-08-18")));
        ProjectDetailsUtil.embedEvents(eventList, sprintList);
        for (Sprint sprint : sprintList) {
            List<Integer> eventIndexes = sprint.getEventsInside();
            assertEquals(eventList.get(0), eventList.get(eventIndexes.get(0)));
        }
    }

    @Test
    void whenEventStartsBeforeFirstSprint_andEndsAfterLastSprint_testEmbedEventsInEachSprint() {
        eventList.add(new Event(1, "Test Event Uno",
                Date.valueOf("2022-04-10"), Date.valueOf("2022-08-18")));
        ProjectDetailsUtil.embedEvents(eventList, sprintList);
        for (Sprint sprint : sprintList) {
            List<Integer> eventIndexes = sprint.getEventsInside();
            assertEquals(eventList.get(0), eventList.get(eventIndexes.get(0)));
        }
    }

    @Test
    void whenEventEndsDayBeforeSprint_testEventNotEmbedded() {
        eventList.add(new Event(1, "Test Event Uno",
                Date.valueOf("2022-04-13"), Date.valueOf("2022-04-14")));
        ProjectDetailsUtil.embedEvents(eventList, sprintList);
        assertEquals(0, sprintList.get(0).getEventsInside().size());
    }

    @Test
    void whenEventEndsOnSprintStartDate_testEventEmbedded() {
        eventList.add(new Event(1, "Test Event Uno",
                Date.valueOf("2022-04-13"), Date.valueOf("2022-04-15")));
        ProjectDetailsUtil.embedEvents(eventList, sprintList);
        assertEquals(1, sprintList.get(0).getEventsInside().size());
    }

    @Test
    void whenEventStartsDayAfterSprint_testEventNotEmbedded() {
        eventList.add(new Event(1, "Test Event Uno",
                Date.valueOf("2022-05-17"), Date.valueOf("2022-05-20")));
        ProjectDetailsUtil.embedEvents(eventList, sprintList);
        assertEquals(0, sprintList.get(0).getEventsInside().size());
    }

    @Test
    void whenEventStartsOnSprintEndDate_testEventEmbedded() {
        eventList.add(new Event(1, "Test Event Uno",
                Date.valueOf("2022-05-16"), Date.valueOf("2022-05-18")));
        ProjectDetailsUtil.embedEvents(eventList, sprintList);
        assertEquals(1, sprintList.get(0).getEventsInside().size());
    }

    @Test
    void whenEventStart_andEndDateInSprint_testEmbedEvent_andEventStart_andEndColourIsSameAsSprintColour() {
        ProjectDetailsUtil.colorSprints(sprintList);
        eventList.add(new Event(1, "Test Event Uno",
                Date.valueOf("2022-04-20"), Date.valueOf("2022-04-22")));
        ProjectDetailsUtil.embedEvents(eventList, sprintList);
        assertEquals(sprintList.get(0).getColour(), eventList.get(0).getColourStart());
        assertEquals(sprintList.get(0).getColour(), eventList.get(0).getColourEnd());
    }

    @Test
    void whenEventStart_andEndDateInDifferentSprints_testEmbedEvent_andEventStart_andEndColourIsSameAsSprintColour() {
        ProjectDetailsUtil.colorSprints(sprintList);
        eventList.add(new Event(1, "Test Event Uno",
                Date.valueOf("2022-04-20"), Date.valueOf("2022-05-22")));
        ProjectDetailsUtil.embedEvents(eventList, sprintList);
        assertEquals(sprintList.get(0).getColour(), eventList.get(0).getColourStart());
        assertEquals(sprintList.get(1).getColour(), eventList.get(0).getColourEnd());
    }

    @Test
    void whenEventStart_andEndDateOutsideAllSprints_testEmbedEvent_andEventStart_andEndNotColoured() {
        ProjectDetailsUtil.colorSprints(sprintList);
        eventList.add(new Event(1, "Test Event Uno",
                Date.valueOf("2022-03-20"), Date.valueOf("2022-03-22")));
        ProjectDetailsUtil.embedEvents(eventList, sprintList);
        assertNull(eventList.get(0).getColourStart());
        assertNull(eventList.get(0).getColourEnd());
    }

    @Test
    void whenEventStart_andEndDateInsideSprintsSeperatedBySprintInTheMiddle_testStart_andEndColourNotOfSprintInside() {
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
    void whenDeadlineOccursBeforeAllSprints_testDeadlineNotEmbedded() {
        deadlineList.add(new Deadline(1, "Test Deadline Hand In",
                Date.valueOf("2022-04-13")));
        ProjectDetailsUtil.embedDeadlines(deadlineList, sprintList);
        for (Sprint sprint : sprintList) {
            assertEquals(0, sprint.getDeadlinesInside().size());
        }
    }

    @Test
    void whenDeadlineOccursAfterAllSprints_testDeadlineNotEmbedded() {
        deadlineList.add(new Deadline(1, "Test Deadline Hand In",
                Date.valueOf("2022-08-18")));
        ProjectDetailsUtil.embedDeadlines(deadlineList, sprintList);
        for (Sprint sprint : sprintList) {
            assertEquals(0, sprint.getDeadlinesInside().size());
        }
    }

    @Test
    void whenDeadlineOccursDayBeforeSprintStarts_testDeadlineNotEmbedded() {
        deadlineList.add(new Deadline(1, "Test Deadline Hand In",
                Date.valueOf("2022-04-14")));
        ProjectDetailsUtil.embedDeadlines(deadlineList, sprintList);
        assertEquals(0, sprintList.get(0).getDeadlinesInside().size());
    }

    @Test
    void whenDeadlineOccursDayAfterSprintEnds_testDeadlineNotEmbedded() {
        deadlineList.add(new Deadline(1, "Test Deadline Hand In",
                Date.valueOf("2022-05-17")));
        ProjectDetailsUtil.embedDeadlines(deadlineList, sprintList);
        assertEquals(0, sprintList.get(0).getDeadlinesInside().size());
    }

    @Test
    void whenDeadlineOccursDaySprintStarts_testDeadlineEmbedded() {
        deadlineList.add(new Deadline(1, "Test Deadline Hand In",
                Date.valueOf("2022-04-15")));
        ProjectDetailsUtil.embedDeadlines(deadlineList, sprintList);
        List<Integer> deadlineIndexes = sprintList.get(0).getDeadlinesInside();
        assertEquals(deadlineList.get(0), deadlineList.get(deadlineIndexes.get(0)));
    }

    @Test
    void whenDeadlineOccursDaySprintEnds_testDeadlineEmbedded() {
        deadlineList.add(new Deadline(1, "Test Deadline Hand In",
                Date.valueOf("2022-05-16")));
        ProjectDetailsUtil.embedDeadlines(deadlineList, sprintList);
        List<Integer> eventIndexes = sprintList.get(0).getDeadlinesInside();
        assertEquals(deadlineList.get(0), deadlineList.get(eventIndexes.get(0)));
    }

    @Test
    void whenDeadlineOccursWithinSprint_testEmbedDeadlineAndDeadlineColourSameAsSprint() {
        ProjectDetailsUtil.colorSprints(sprintList);
        deadlineList.add(new Deadline(1, "Test Deadline Hand In",
                Date.valueOf("2022-04-20")));
        ProjectDetailsUtil.embedDeadlines(deadlineList, sprintList);
        assertEquals(sprintList.get(0).getColour(), deadlineList.get(0).getColour());
    }

    @Test
    void whenDeadlineNotOccursWithinSprint_testEmbedDeadlineAndDeadlineColourNotExist() {
        ProjectDetailsUtil.colorSprints(sprintList);
        deadlineList.add(new Deadline(1, "Test Deadline Hand In",
                Date.valueOf("2022-03-20")));
        ProjectDetailsUtil.embedDeadlines(deadlineList, sprintList);
        assertNull(deadlineList.get(0).getColour());
    }

    @Test
    void whenSprintEventDeadlineListsExist_testOrderImportantDates() {
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
        List<Pair<Integer, String>> importantDates = ProjectDetailsUtil.getOrderedImportantDates(eventList, sprintList, deadlineList, milestoneList);
        for (int i = 0; i < importantDates.size(); i++) {
            assertEquals(indices.get(i), importantDates.get(i));
        }
    }

    @Test
    void whenSprintEventDeadlineListsExistAndEventAndDeadlineDatesAreTheSame_testOrderImportantDates() {
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
        List<Pair<Integer, String>> importantDates = ProjectDetailsUtil.getOrderedImportantDates(eventList, sprintList, deadlineList, milestoneList);
        for (int i = 0; i < importantDates.size(); i++) {
            assertEquals(indices.get(i), importantDates.get(i));
        }
    }

    @Test
    void whenSprintEventDeadlineListsExistAndEventAndSprintDatesAreTheSame_testOrderImportantDates() {
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
        List<Pair<Integer, String>> importantDates = ProjectDetailsUtil.getOrderedImportantDates(eventList, sprintList, deadlineList, milestoneList);
        for (int i = 0; i < importantDates.size(); i++) {
            assertEquals(indices.get(i), importantDates.get(i));
        }
    }

    @Test
    void whenSprintEventDeadlineListsExistAndDeadlineAndSprintDatesAreTheSame_testOrderImportantDates() {
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
        List<Pair<Integer, String>> importantDates = ProjectDetailsUtil.getOrderedImportantDates(eventList, sprintList, deadlineList, milestoneList);
        for (int i = 0; i < importantDates.size(); i++) {
            assertEquals(indices.get(i), importantDates.get(i));
        }
    }

    @Test
    void whenSprintEventDeadlineListsExistAndEventDeadlineAndSprintDatesAreTheSame_testOrderImportantDates() {
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
        List<Pair<Integer, String>> importantDates = ProjectDetailsUtil.getOrderedImportantDates(eventList, sprintList, deadlineList, milestoneList);
        for (int i = 0; i < importantDates.size(); i++) {
            assertEquals(indices.get(i), importantDates.get(i));
        }
    }

    @Test
    void whenDeadlinesAndEventsEmbeddedInSprints_testNotIncludedInImportantDates() {
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
        List<Pair<Integer, String>> importantDates = ProjectDetailsUtil.getOrderedImportantDates(eventList, sprintList, deadlineList, milestoneList);
        for (int i = 0; i < importantDates.size(); i++) {
            assertEquals(indices.get(i), importantDates.get(i));
        }
    }

    @Test
    void whenSprintListExists_testColourSprints() {
        for (Sprint sprint : sprintList) {
            assertNull(sprint.getColour());
        }
        ProjectDetailsUtil.colorSprints(sprintList);
        for (Sprint sprint : sprintList) {
            assertNotNull(sprint.getColour());
        }
    }


    @Test
    void whenMilestoneOccursBeforeAllSprints_testMilestoneNotEmbedded() {
        milestoneList.add(new Milestone(1, "Test Milestone Hand In",
                Date.valueOf("2022-04-13")));
        ProjectDetailsUtil.embedMilestones(milestoneList, sprintList);
        for (Sprint sprint : sprintList) {
            assertEquals(0, sprint.getMilestonesInside().size());
        }
    }


    @Test
    void whenMilestoneOccursAfterAllSprints_testMilestoneNotEmbedded() {
        milestoneList.add(new Milestone(1, "Test Milestone Hand In",
                Date.valueOf("2022-08-18")));
        ProjectDetailsUtil.embedMilestones(milestoneList, sprintList);
        for (Sprint sprint : sprintList) {
            assertEquals(0, sprint.getMilestonesInside().size());
        }
    }

    @Test
    void whenMilestoneOccursDayBeforeSprintStarts_testMilestoneNotEmbedded() {
        milestoneList.add(new Milestone(1, "Test Milestone Hand In",
                Date.valueOf("2022-04-14")));
        ProjectDetailsUtil.embedMilestones(milestoneList, sprintList);
        assertEquals(0, sprintList.get(0).getMilestonesInside().size());
    }

    @Test
    void whenMilestoneOccursDayAfterSprintEnds_testMilestoneNotEmbedded() {
        milestoneList.add(new Milestone(1, "Test Milestone Hand In",
                Date.valueOf("2022-05-17")));
        ProjectDetailsUtil.embedMilestones(milestoneList, sprintList);
        assertEquals(0, sprintList.get(0).getMilestonesInside().size());
    }

    @Test
    void whenMilestoneOccursDaySprintStarts_testMilestoneEmbedded() {
        milestoneList.add(new Milestone(1, "Test Milestone Hand In",
                Date.valueOf("2022-04-15")));
        ProjectDetailsUtil.embedMilestones(milestoneList, sprintList);
        List<Integer> milestoneIndexes = sprintList.get(0).getMilestonesInside();
        assertEquals(milestoneList.get(0), milestoneList.get(milestoneIndexes.get(0)));
    }

    @Test
    void whenMilestoneOccursDaySprintEnds_testMilestoneEmbedded() {
        milestoneList.add(new Milestone(1, "Test Milestone Hand In",
                Date.valueOf("2022-05-16")));
        ProjectDetailsUtil.embedMilestones(milestoneList, sprintList);
        List<Integer> eventIndexes = sprintList.get(0).getMilestonesInside();
        assertEquals(milestoneList.get(0), milestoneList.get(eventIndexes.get(0)));
    }


    @Test
    void whenMilestoneOccursWithinSprint_testEmbedMilestoneAndMilestoneColourSameAsSprint() {
        ProjectDetailsUtil.colorSprints(sprintList);
        milestoneList.add(new Milestone(1, "Test Milestone Hand In",
                Date.valueOf("2022-04-20")));
        ProjectDetailsUtil.embedMilestones(milestoneList, sprintList);
        assertEquals(sprintList.get(0).getColour(), milestoneList.get(0).getColour());
    }

    @Test
    void whenMilestoneNotOccursWithinSprint_testEmbedMilestoneAndMilestoneColourNotExist() {
        ProjectDetailsUtil.colorSprints(sprintList);
        milestoneList.add(new Milestone(1, "Test Milestone Hand In",
                Date.valueOf("2022-03-20")));
        ProjectDetailsUtil.embedMilestones(milestoneList, sprintList);
        assertNull(milestoneList.get(0).getColour());
    }
}