package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Date;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureTestDatabase
@SpringBootTest
class ProjectDateServiceTests {

    @Autowired
    ProjectDateService projectDateService;

    @Autowired
    private SprintService sprintService;

    @Autowired
    private EventService eventService;

    @Autowired
    private MilestoneService milestoneService;

    @Autowired
    private DeadlineService deadlineService;

    //Refresh the database after each test.
    @AfterEach
    void cleanDatabase() {
        for (Sprint sprint : sprintService.getAllSprints()) {
            sprintService.deleteById(sprint.getId());
        }
        for (Event event : eventService.getAllEvents()) {
            eventService.deleteEventById(event.getEventId());
        }
        for (Deadline deadline : deadlineService.getAllDeadlines()) {
            deadlineService.deleteDeadlineById(deadline.getDeadlineId());
        }
        for (Milestone milestone : milestoneService.getAllMilestones()) {
            milestoneService.deleteMilestoneById(milestone.getId());
        }
    }

    //Test that when no restrictions are present,
    @Test
    void whenNoItemsInProject_testRestrictionsEmpty() {
        DateRestrictions restrictions = projectDateService.getDateRestrictions(1);
        assertFalse(restrictions.hasRestrictions());
    }

    //Test that when no restrictions are present,
    @Test
    void whenOneSprintInProject_testRestrictionsExist() {
        sprintService.saveSprint(new Sprint(1, "Test Sprint", "Test Sprint Description", Date.valueOf("2022-04-9"), Date.valueOf("2022-06-16")));
        DateRestrictions restrictions = projectDateService.getDateRestrictions(1);
        assertTrue(restrictions.hasRestrictions());
        assertEquals(Date.valueOf("2022-04-9"), restrictions.getStartDate());
        assertEquals(Date.valueOf("2022-06-16"), restrictions.getEndDate());
        assertEquals("a sprint", restrictions.getStartDateText());
        assertEquals("a sprint", restrictions.getEndDateText());
    }

    //Test that when no restrictions are present,
    @Test
    void whenManyItemsInProject_testRestrictionsExist() {
        sprintService.saveSprint(new Sprint(1, "Test Sprint", "Test Sprint Description", Date.valueOf("2022-04-9"), Date.valueOf("2022-06-16")));
        eventService.saveEvent(new Event(1, "Test Event", Date.valueOf("2022-04-8"), Date.valueOf("2022-06-15")));
        deadlineService.saveDeadline(new Deadline(1, "Test Milestone", Date.valueOf("2022-04-7")));
        milestoneService.saveMilestone(new Milestone(1, "Test Deadline", Date.valueOf("2022-06-17")));

        DateRestrictions restrictions = projectDateService.getDateRestrictions(1);
        assertTrue(restrictions.hasRestrictions());
        assertEquals(Date.valueOf("2022-04-7"), restrictions.getStartDate());
        assertEquals(Date.valueOf("2022-06-17"), restrictions.getEndDate());
        assertEquals("a deadline", restrictions.getStartDateText());
        assertEquals("a milestone", restrictions.getEndDateText());
    }

}
