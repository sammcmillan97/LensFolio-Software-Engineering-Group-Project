package nz.ac.canterbury.seng302.portfolio.util;
import nz.ac.canterbury.seng302.portfolio.model.Event;
import nz.ac.canterbury.seng302.portfolio.model.Project;
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

    /**
     * Initialise the database with projects before each test
     */
    @BeforeEach
    void createProject() {
        this.project = new Project("Project Name", "Test Project", Date.valueOf("2022-01-01"), Date.valueOf("2022-01-10"));
        eventList = new ArrayList<>();
    }

    @Test
    void whenOneEventCoversThreeDays_testGetEventsForCalender() {
        Event testEvent = new Event(project.getId(), "Test Event", java.sql.Date.valueOf("2022-01-01"), java.sql.Date.valueOf("2022-01-03"));
        eventList.add(testEvent);
        Map<String, Integer> eventMap = PlannerUtil.getEventsForCalender(eventList, project);
        Assertions.assertEquals(3, eventMap.size());
        Assertions.assertEquals(1, eventMap.get("2022-01-01"));
        Assertions.assertEquals(1, eventMap.get("2022-01-02"));
        Assertions.assertEquals(1, eventMap.get("2022-01-03"));
        Assertions.assertNull(eventMap.get("2021-12-31"));
        Assertions.assertNull(eventMap.get("2022-01-04"));
    }

    @Test
    void whenOneEventCoversOneDay_testGetEventsForCalender() {
        Event testEvent = new Event(project.getId(), "Test Event", java.sql.Date.valueOf("2022-01-01"), java.sql.Date.valueOf("2022-01-01"));
        eventList.add(testEvent);
        Map<String, Integer> eventMap = PlannerUtil.getEventsForCalender(eventList, project);
        Assertions.assertEquals(1, eventMap.size());
        Assertions.assertEquals(1, eventMap.get("2022-01-01"));
    }

    @Test
    void whenOneEventCoversWholeProject_testGetEventsForCalendar() {
        Event testEvent = new Event(project.getId(), "Test Event", java.sql.Date.valueOf("2022-01-01"), java.sql.Date.valueOf("2022-01-10"));
        eventList.add(testEvent);
        Map<String, Integer> eventMap = PlannerUtil.getEventsForCalender(eventList, project);
        Assertions.assertEquals(10, eventMap.size());
        Assertions.assertEquals(1, eventMap.get("2022-01-01"));
        Assertions.assertEquals(1, eventMap.get("2022-01-10"));
    }

    @Test
    void whenTwoEventsOverlap_testGetEventsForCalendar() {
        Event testEvent1 = new Event(project.getId(), "Test Event 1", java.sql.Date.valueOf("2022-01-01"), java.sql.Date.valueOf("2022-01-2"));
        Event testEvent2 = new Event(project.getId(), "Test Event 2", java.sql.Date.valueOf("2022-01-02"), java.sql.Date.valueOf("2022-01-3"));
        eventList.add(testEvent1);
        eventList.add(testEvent2);
        Map<String, Integer> eventMap = PlannerUtil.getEventsForCalender(eventList, project);
        Assertions.assertEquals(3, eventMap.size());
        Assertions.assertEquals(1, eventMap.get("2022-01-01"));
        Assertions.assertEquals(2, eventMap.get("2022-01-02"));
        Assertions.assertEquals(1, eventMap.get("2022-01-03"));
    }

    @Test
    void whenEventIsNotWithinProjectDates_testGetEventsForCalendar() {
        Event testEvent = new Event(project.getId(), "Test Event", java.sql.Date.valueOf("2022-01-11"), java.sql.Date.valueOf("2022-01-12"));
        eventList.add(testEvent);
        Map<String, Integer> eventMap = PlannerUtil.getEventsForCalender(eventList, project);
        Assertions.assertEquals(0, eventMap.size());
    }
}
