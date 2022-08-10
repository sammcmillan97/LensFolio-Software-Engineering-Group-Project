package nz.ac.canterbury.seng302.portfolio.util;

import nz.ac.canterbury.seng302.portfolio.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@SpringBootTest
class PlannerUtilTest {

    Project project;
    List<Event> eventList;
    List<Deadline> deadlineList;
    List<Milestone> milestoneList;

    /**
     * Initialise the database with projects before each test
     */
    @BeforeEach
    void createProject() {
        this.project = new Project("Project Name", "Test Project", Date.valueOf("2022-01-01"), Date.valueOf("2022-01-10"));
        eventList = new ArrayList<>();
        deadlineList = new ArrayList<>();
        milestoneList = new ArrayList<>();
    }

    @Test
    void whenOneEventCoversThreeDays_testGetEventsForCalender() {
        Event testEvent = new Event(project.getId(), "Test Event", java.sql.Date.valueOf("2022-01-01"), java.sql.Date.valueOf("2022-01-03"));
        eventList.add(testEvent);
        Map<String, PlannerDailyEvent> eventMap = PlannerUtil.getEventsForCalender(eventList);
        Assertions.assertEquals(3, eventMap.size());
        PlannerDailyEvent event = eventMap.get("2022-01-01");
        Assertions.assertEquals("Test Event\n", event.getDescription());
        Assertions.assertEquals("e2022-01-01", event.getId());
        Assertions.assertEquals("daily-event", event.getType());
        Assertions.assertEquals(1, event.getNumberOfEvents());
        Assertions.assertEquals(1, eventMap.get("2022-01-02").getNumberOfEvents());
        Assertions.assertEquals(1, eventMap.get("2022-01-03").getNumberOfEvents());
        Assertions.assertNull(eventMap.get("2021-12-31"));
        Assertions.assertNull(eventMap.get("2022-01-04"));
    }

    @Test
    void whenOneEventCoversOneDay_testGetEventsForCalender() {
        Event testEvent = new Event(project.getId(), "Test Event", java.sql.Date.valueOf("2022-01-01"), java.sql.Date.valueOf("2022-01-01"));
        eventList.add(testEvent);
        Map<String, PlannerDailyEvent> eventMap = PlannerUtil.getEventsForCalender(eventList);
        Assertions.assertEquals(1, eventMap.size());
        Assertions.assertEquals(1, eventMap.get("2022-01-01").getNumberOfEvents());
    }

    @Test
    void whenOneEventCoversWholeProject_testGetEventsForCalendar() {
        Event testEvent = new Event(project.getId(), "Test Event", java.sql.Date.valueOf("2022-01-01"), java.sql.Date.valueOf("2022-01-10"));
        eventList.add(testEvent);
        Map<String, PlannerDailyEvent> eventMap = PlannerUtil.getEventsForCalender(eventList);
        Assertions.assertEquals(10, eventMap.size());
        Assertions.assertEquals(1, eventMap.get("2022-01-01").getNumberOfEvents());
        Assertions.assertEquals(1, eventMap.get("2022-01-10").getNumberOfEvents());
    }

    @Test
    void whenTwoEventsOverlap_testGetEventsForCalendar() {
        Event testEvent1 = new Event(project.getId(), "Test Event 1", java.sql.Date.valueOf("2022-01-01"), java.sql.Date.valueOf("2022-01-2"));
        Event testEvent2 = new Event(project.getId(), "Test Event 2", java.sql.Date.valueOf("2022-01-02"), java.sql.Date.valueOf("2022-01-3"));
        eventList.add(testEvent1);
        eventList.add(testEvent2);
        Map<String, PlannerDailyEvent> eventMap = PlannerUtil.getEventsForCalender(eventList);
        PlannerDailyEvent overlappingEvent = eventMap.get("2022-01-02");
        Assertions.assertEquals(3, eventMap.size());
        Assertions.assertEquals(1, eventMap.get("2022-01-01").getNumberOfEvents());
        Assertions.assertEquals(2, overlappingEvent.getNumberOfEvents());
        Assertions.assertEquals("Test Event 1\nTest Event 2\n", overlappingEvent.getDescription());
        Assertions.assertEquals(1, eventMap.get("2022-01-03").getNumberOfEvents());
    }

    @Test
    void whenOneDeadlineExists_testGetDeadlinesForCalender() {
        Deadline testDeadline = new Deadline(project.getId(), "Test Deadline", java.sql.Date.valueOf("2022-01-01"));
        deadlineList.add(testDeadline);
        Map<String, PlannerDailyEvent> deadlineMap = PlannerUtil.getDeadlinesForCalender(deadlineList);
        PlannerDailyEvent deadline = deadlineMap.get("2022-01-01");
        Assertions.assertEquals(1, deadlineMap.size());
        Assertions.assertEquals("d2022-01-01", deadline.getId());
        Assertions.assertEquals("2022-01-01", deadline.getDate());
        Assertions.assertEquals("Test Deadline\n", deadline.getDescription());
        Assertions.assertEquals(1, deadline.getNumberOfEvents());
        Assertions.assertEquals("daily-deadline", deadline.getType());
    }

    @Test
    void whenTwoDeadlinesOccurOnSameDay_testGetDeadlinesForCalender() {
        Deadline testDeadline1 = new Deadline(project.getId(), "Test Deadline1", java.sql.Date.valueOf("2022-01-01"));
        Deadline testDeadline2 = new Deadline(project.getId(), "Test Deadline2", java.sql.Date.valueOf("2022-01-01"));
        deadlineList.add(testDeadline1);
        deadlineList.add(testDeadline2);
        Map<String, PlannerDailyEvent> deadlineMap = PlannerUtil.getDeadlinesForCalender(deadlineList);
        PlannerDailyEvent deadline = deadlineMap.get("2022-01-01");
        Assertions.assertEquals(1, deadlineMap.size());
        Assertions.assertEquals("Test Deadline1\nTest Deadline2\n", deadline.getDescription());
        Assertions.assertEquals(2, deadline.getNumberOfEvents());
        Assertions.assertEquals("daily-deadline", deadline.getType());
    }

    @Test
    void whenOneMilestoneExists_testGetMilestoneForCalender() {
        Milestone testMilestone = new Milestone(project.getId(), "Test Milestone", java.sql.Date.valueOf("2022-01-01"));
        milestoneList.add(testMilestone);
        Map<String, PlannerDailyEvent> milestoneMap = PlannerUtil.getMilestonesForCalender(milestoneList);
        PlannerDailyEvent milestone = milestoneMap.get("2022-01-01");
        Assertions.assertEquals(1, milestoneMap.size());
        Assertions.assertEquals("m2022-01-01", milestone.getId());
        Assertions.assertEquals("2022-01-01", milestone.getDate());
        Assertions.assertEquals("Test Milestone\n", milestone.getDescription());
        Assertions.assertEquals(1, milestone.getNumberOfEvents());
        Assertions.assertEquals("daily-milestone", milestone.getType());
    }

    @Test
    void whenTwoMilestonesOccurOnSameDay_testGetMilestonesForCalender() {
        Milestone testMilestone1 = new Milestone(project.getId(), "Test Milestone1", java.sql.Date.valueOf("2022-01-01"));
        Milestone testMilestone2 = new Milestone(project.getId(), "Test Milestone2", java.sql.Date.valueOf("2022-01-01"));
        milestoneList.add(testMilestone1);
        milestoneList.add(testMilestone2);
        Map<String, PlannerDailyEvent> milestoneMap = PlannerUtil.getMilestonesForCalender(milestoneList);
        PlannerDailyEvent milestone = milestoneMap.get("2022-01-01");
        Assertions.assertEquals(1, milestoneMap.size());
        Assertions.assertEquals("Test Milestone1\nTest Milestone2\n", milestone.getDescription());
        Assertions.assertEquals(2, milestone.getNumberOfEvents());
        Assertions.assertEquals("daily-milestone", milestone.getType());
    }


}
