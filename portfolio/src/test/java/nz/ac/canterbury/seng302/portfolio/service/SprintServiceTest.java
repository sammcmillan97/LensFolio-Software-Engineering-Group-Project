package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.ProjectRepository;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.model.SprintRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureTestDatabase
@SpringBootTest
class SprintServiceTest {
    @Autowired
    SprintService sprintService;

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    SprintRepository sprintRepository;

    static List<Project> projects;

    //Initialise the database with projects before each test.
    @BeforeEach
    void storeProjects() {
        projectRepository.save(new Project("Project Name", "Test Project", Date.valueOf("2022-04-9"), Date.valueOf("2022-06-16")));
        projectRepository.save(new Project("Project Name", "Test Project", Date.valueOf("2022-04-9"), Date.valueOf("2022-06-16")));
        projects = (List<Project>)projectRepository.findAll();
    }
    //Refresh the database after each test.
    @AfterEach
    void cleanDatabase() {
        projectRepository.deleteAll();
        sprintRepository.deleteAll();
    }
    //When there are no sprints in the database, test saving a sprint using sprint service.
    @Test
    void whenNoSprints_testSaveSprintToSameProject() {
        List<Sprint> sprints = (List<Sprint>) sprintRepository.findAll();
        assertEquals(0, sprints.size());
        sprintService.saveSprint(new Sprint(projects.get(0).getId(), "Test Sprint",1, "Description",
                Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        sprints = (List<Sprint>) sprintRepository.findAll();
        assertEquals(1, sprints.size());
    }
    //When there is one sprint in the database, test saving a sprint using sprint service.
    @Test
    void whenOneSprint_testSaveSprintToSameProject() {
        sprintService.saveSprint(new Sprint(projects.get(0).getId(), "Test Sprint",1, "Description",
                Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        List<Sprint> sprints = (List<Sprint>) sprintRepository.findAll();
        assertEquals(1, sprints.size());
        sprintService.saveSprint(new Sprint(projects.get(0).getId(), "Test Sprint",2, "Description",
                Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        sprints = (List<Sprint>) sprintRepository.findAll();
        assertEquals(2, sprints.size());
    }
    //When there are many sprints in the database, test saving a sprint using sprint service.
    @Test
    void whenManySprints_testSaveSprintToSameProject() {
        sprintService.saveSprint(new Sprint(projects.get(0).getId(), "Test Sprint",1, "Description",
                Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        sprintService.saveSprint(new Sprint(projects.get(0).getId(), "Test Sprint",2, "Description",
                Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        sprintService.saveSprint(new Sprint(projects.get(0).getId(), "Test Sprint",3, "Description",
                Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        sprintService.saveSprint(new Sprint(projects.get(0).getId(), "Test Sprint",4, "Description",
                Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        List<Sprint> sprints = (List<Sprint>) sprintRepository.findAll();
        assertEquals(4, sprints.size());
        sprintService.saveSprint(new Sprint(projects.get(0).getId(), "Test Sprint",5, "Description",
                Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        sprints = (List<Sprint>) sprintRepository.findAll();
        assertEquals(5, sprints.size());
    }
    //When there is one sprint in a project in the database, test saving a sprint to a different project.
    @Test
    void whenOneSprint_testSaveSprintToDifferentProject() {
        sprintService.saveSprint(new Sprint(projects.get(0).getId(), "Test Sprint",1, "Description",
                Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        List<Sprint> sprints = (List<Sprint>) sprintRepository.findAll();
        assertEquals(1, sprints.size());
        assertNotEquals(projects.get(0).getId(), projects.get(1).getId());
        sprintService.saveSprint(new Sprint(projects.get(1).getId(), "Test Sprint",2, "Description",
                Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        sprints = (List<Sprint>) sprintRepository.findAll();
        assertEquals(2, sprints.size());
    }
    //When there is many sprints in each project in the database, test saving sprints to each different project.
    @Test
    void whenManySprints_testSaveSprintToDifferentProject() {
        sprintService.saveSprint(new Sprint(projects.get(0).getId(), "Test Sprint",1, "Description",
                Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        sprintService.saveSprint(new Sprint(projects.get(0).getId(), "Test Sprint",2, "Description",
                Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        sprintService.saveSprint(new Sprint(projects.get(1).getId(), "Test Sprint",3, "Description",
                Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        sprintService.saveSprint(new Sprint(projects.get(1).getId(), "Test Sprint",4, "Description",
                Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        List<Sprint> sprints = (List<Sprint>) sprintRepository.findAll();
        assertEquals(4, sprints.size());
        assertNotEquals(projects.get(0).getId(), projects.get(1).getId());
        sprintService.saveSprint(new Sprint(projects.get(0).getId(), "Test Sprint",5, "Description",
                Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        sprintService.saveSprint(new Sprint(projects.get(1).getId(), "Test Sprint",6, "Description",
                Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        sprints = (List<Sprint>) sprintRepository.findAll();
        assertEquals(6, sprints.size());
    }
    //When there are no sprints in the database, test retrieving all sprints using sprint service.
    @Test
    void whenNoSprintSaved_testGetAllSprints() {
        List<Sprint> sprints = (List<Sprint>) sprintRepository.findAll();
        assertEquals(0, sprints.size());
        assertEquals(0, sprintService.getAllSprints().size());
    }
    //When there is one sprint in the database, test retrieving all sprints using sprint service.
    @Test
    void whenOneSprintSaved_testGetAllSprints() {
        sprintService.saveSprint(new Sprint(projects.get(0).getId(), "Test Sprint",1, "Description",
                Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        List<Sprint> sprints = (List<Sprint>) sprintRepository.findAll();
        assertEquals(1, sprints.size());
        assertEquals(1, sprintService.getAllSprints().size());
    }
    //When there are many sprints in the database, test retrieving all sprints using sprint service.
    @Test
    void whenManySprintsSaved_testGetAllSprints() {
        sprintService.saveSprint(new Sprint(projects.get(0).getId(), "Test Sprint",1, "Description",
                Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        sprintService.saveSprint(new Sprint(projects.get(0).getId(), "Test Sprint",2, "Description",
                Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        sprintService.saveSprint(new Sprint(projects.get(1).getId(), "Test Sprint",3, "Description",
                Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        sprintService.saveSprint(new Sprint(projects.get(1).getId(), "Test Sprint",4, "Description",
                Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        List<Sprint> sprints = (List<Sprint>) sprintRepository.findAll();
        assertEquals(4, sprints.size());
        assertEquals(4, sprintService.getAllSprints().size());
    }
    //When the sprint id does not exist, test retrieving the sprint by its id.
    @Test
    void whenSprintIdNotExists_testGetSprintById() {
        List<Sprint> sprints = (List<Sprint>) sprintRepository.findAll();
        assertEquals(0, sprints.size());
        Exception exception = assertThrows(Exception.class, () -> sprintService.getSprintById(900000));
        String expectedMessage = "Sprint not found";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
    //When the sprint id does exist, test retrieving the sprint by its id.
    @Test
    void whenSprintIdExists_testGetSprintById() {
        sprintService.saveSprint(new Sprint(projects.get(0).getId(), "Test Sprint",1, "Description",
                Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        List<Sprint> sprints = (List<Sprint>) sprintRepository.findAll();
        int sprintId = sprints.get(0).getId();
        Sprint sprint = sprintService.getSprintById(sprintId);
        assertEquals("Test Sprint", sprint.getName());
        assertEquals("Sprint 1", sprint.getLabel());
        assertEquals("Description", sprint.getDescription());
        assertEquals(Timestamp.valueOf("2022-04-15 00:00:00"), sprint.getStartDate());
        assertEquals(Timestamp.valueOf("2022-05-16 00:00:00"), sprint.getEndDate());
    }
    //When no sprints are saved to the database, test retrieving sprints by parent project id.
    @Test
    void whenNoSprintSaved_testGetByParentProjectId() {
        List<Sprint> sprints = (List<Sprint>) sprintRepository.findAll();
        assertEquals(0, sprints.size());
        List<Sprint> sprintList = sprintService.getByParentProjectId(projects.get(0).getId());
        assertEquals(0, sprintList.size());
    }
    //When one sprint is saved to the database, test retrieving sprints by parent project id.
    @Test
    void whenOneSprintSaved_testGetByParentProjectId() {
        sprintService.saveSprint(new Sprint(projects.get(0).getId(), "Test Sprint",1, "Description",
                Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        List<Sprint> sprints = (List<Sprint>) sprintRepository.findAll();
        assertEquals(1, sprints.size());
        List<Sprint> sprintList = sprintService.getByParentProjectId(projects.get(0).getId());
        assertEquals(1, sprintList.size());
    }
    //When many sprints are saved to the database, test retrieving sprints by parent project id.
    @Test
    void whenManySprintsSaved_testGetByParentProjectId() {
        sprintService.saveSprint(new Sprint(projects.get(0).getId(), "Test Sprint",1, "Description",
                Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        sprintService.saveSprint(new Sprint(projects.get(0).getId(), "Test Sprint",2, "Description",
                Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        sprintService.saveSprint(new Sprint(projects.get(0).getId(), "Test Sprint",3, "Description",
                Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        sprintService.saveSprint(new Sprint(projects.get(0).getId(), "Test Sprint",4, "Description",
                Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        List<Sprint> sprints = (List<Sprint>) sprintRepository.findAll();
        assertEquals(4, sprints.size());
        List<Sprint> sprintList = sprintService.getByParentProjectId(projects.get(0).getId());
        assertEquals(4, sprintList.size());
    }
    //When many sprints are saved to many projects in the database, test retrieving sprints by parent project id.
    @Test
    void whenManySprintsSavedToDifferentProjects_testGetByParentProjectId() {
        sprintService.saveSprint(new Sprint(projects.get(0).getId(), "Test Sprint",1, "Description",
                Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        sprintService.saveSprint(new Sprint(projects.get(0).getId(), "Test Sprint",2, "Description",
                Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        sprintService.saveSprint(new Sprint(projects.get(1).getId(), "Test Sprint",3, "Description",
                Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        sprintService.saveSprint(new Sprint(projects.get(1).getId(), "Test Sprint",4, "Description",
                Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        List<Sprint> sprints = (List<Sprint>) sprintRepository.findAll();
        assertEquals(4, sprints.size());
        List<Sprint> sprintList = sprintService.getByParentProjectId(projects.get(0).getId());
        int listSize = sprintList.size();
        assertEquals(2, listSize);
        sprintList = sprintService.getByParentProjectId(projects.get(1).getId());
        int listSize2 = sprintList.size();
        assertEquals(2, listSize2);
        assertEquals(4, (listSize+listSize2));
    }

    //When sprint exists and its start date is changed to before the current date, and the new date is within project
    // boundaries and not within another sprint, test sprint start date is changed.
    @Test
    void whenSprintStartDateChangedToDateBeforeCurrentAndNotInAnotherSprintAndWithinProjectDates_testSprintStartDateChanged() throws Exception {
        sprintService.saveSprint(new Sprint(projects.get(0).getId(), "Test Sprint",1, "Description",
                Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        List<Sprint> sprints = (List<Sprint>) sprintRepository.findAll();
        int sprintId = sprints.get(0).getId();
        sprintService.updateStartDate(sprintId, Date.valueOf("2022-04-10"));
        Sprint sprint = sprintRepository.findById(sprintId);
        assertEquals(sprint.getStartDate(), Timestamp.valueOf("2022-04-10 00:00:00"));
    }

    //When sprint exists and its start date is changed to date before current and new date is day after previous sprint
    //end date, test start date changed.
    @Test
    void whenSprintStartDateChangedToDateBeforeCurrentAndAfterEndDateOfPreviousSprint_testSprintStartDateChanged() throws Exception {
        sprintService.saveSprint(new Sprint(projects.get(0).getId(), "Test Sprint",1, "Description",
                Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        sprintService.saveSprint(new Sprint(projects.get(0).getId(), "Test Sprint",2, "Description",
                Date.valueOf("2022-05-20"), Date.valueOf("2022-07-16")));
        List<Sprint> sprints = (List<Sprint>) sprintRepository.findAll();
        int sprintId = sprints.get(1).getId();
        sprintService.updateStartDate(sprintId, Date.valueOf("2022-05-17"));
        Sprint sprint = sprintRepository.findById(sprintId);
        assertEquals(sprint.getStartDate(), Timestamp.valueOf("2022-05-17 00:00:00"));
    }

    //When sprint exists and its start date is changed to before the current date, and the new date is not within project
    // boundaries and not within another sprint, test exception is thrown.
    @Test
    void whenSprintStartDateChangedToDateBeforeCurrentAndNotInAnotherSprintAndNotWithinProjectDates_testExceptionThrown() {
        sprintService.saveSprint(new Sprint(projects.get(0).getId(), "Test Sprint",1, "Description",
                Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        List<Sprint> sprints = (List<Sprint>) sprintRepository.findAll();
        int sprintId = sprints.get(0).getId();

        Exception exception = assertThrows(Exception.class, () -> {
            sprintService.updateStartDate(sprintId, Date.valueOf("2022-03-10"));
        });
        String expectedMessage = "Sprint start date must be within project dates";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    //When sprint exists and its start date is changed to before the current date, and the new date is within project
    // boundaries and within another sprint, test exception is thrown.
    @Test
    void whenSprintStartDateChangedToDateBeforeCurrentAndInAnotherSprint_testExceptionThrown() {
        sprintService.saveSprint(new Sprint(projects.get(0).getId(), "Test Sprint",1, "Description",
                Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        sprintService.saveSprint(new Sprint(projects.get(0).getId(), "Test Sprint",2, "Description",
                Date.valueOf("2022-05-16"), Date.valueOf("2022-07-16")));
        List<Sprint> sprints = (List<Sprint>) sprintRepository.findAll();
        int sprintId = sprints.get(1).getId();

        Exception exception = assertThrows(Exception.class, () -> {
            sprintService.updateStartDate(sprintId, Date.valueOf("2022-04-20"));
        });
        String expectedMessage = "Sprint must not be within another sprint";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    //When Sprint exists and its start date is changed to before the start date of another sprint, test that an
    //Exception is thrown. Edge case test the day before sprint start date.
    @Test
    void whenSprintStartDateChangedToEarlierDateAndBeforeStartOfAnotherSprint_testExceptionThrown() {
        sprintService.saveSprint(new Sprint(projects.get(0).getId(), "Test Sprint",1, "Description",
                Date.valueOf("2022-04-20"), Date.valueOf("2022-05-16")));
        sprintService.saveSprint(new Sprint(projects.get(0).getId(), "Test Sprint",2, "Description",
                Date.valueOf("2022-05-16"), Date.valueOf("2022-07-16")));
        List<Sprint> sprints = (List<Sprint>) sprintRepository.findAll();
        int sprintId = sprints.get(1).getId();

        Exception exception = assertThrows(Exception.class, () -> {
            sprintService.updateStartDate(sprintId, Date.valueOf("2022-04-19"));
        });
        String expectedMessage = "Sprint must not be within another sprint";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    //When  sprint exists and its start date is changed to the start date of another sprint, test and exception is
    //Thrown. Edge case.
    @Test
    void whenSprintStartDateChangedToEarlierDateAndIsStartDateOfAnotherSprint_testExceptionThrown() {
        sprintService.saveSprint(new Sprint(projects.get(0).getId(), "Test Sprint",1, "Description",
                Date.valueOf("2022-04-20"), Date.valueOf("2022-05-16")));
        sprintService.saveSprint(new Sprint(projects.get(0).getId(), "Test Sprint",2, "Description",
                Date.valueOf("2022-05-16"), Date.valueOf("2022-07-16")));
        List<Sprint> sprints = (List<Sprint>) sprintRepository.findAll();
        int sprintId = sprints.get(1).getId();

        Exception exception = assertThrows(Exception.class, () -> {
            sprintService.updateStartDate(sprintId, Date.valueOf("2022-04-20"));
        });
        String expectedMessage = "Sprint must not be within another sprint";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    //When a sprint exists and its start date is changed to the end date of and earlier sprint, test an exception is
    //Thrown. Edge case
    @Test
    void whenSprintStartDateChangedToEarlierDateAndIsEndDateOfAnotherSprint_testExceptionThrown() {
        sprintService.saveSprint(new Sprint(projects.get(0).getId(), "Test Sprint",1, "Description",
                Date.valueOf("2022-04-20"), Date.valueOf("2022-05-16")));
        sprintService.saveSprint(new Sprint(projects.get(0).getId(), "Test Sprint",2, "Description",
                Date.valueOf("2022-05-16"), Date.valueOf("2022-07-16")));
        List<Sprint> sprints = (List<Sprint>) sprintRepository.findAll();
        int sprintId = sprints.get(1).getId();

        Exception exception = assertThrows(Exception.class, () -> {
            sprintService.updateStartDate(sprintId, Date.valueOf("2022-05-16"));
        });
        String expectedMessage = "Sprint must not be within another sprint";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    //When sprint exists and its start date is changed to after the current date, and the new date is before the sprints
    // end date (so within project boundaries and not within another sprint), test sprint start date is changed.
    @Test
    void whenSprintStartDateChangedToDateAfterCurrentAndNotAfterEndDate_testSprintStartDateChanged() throws Exception {
        sprintService.saveSprint(new Sprint(projects.get(0).getId(), "Test Sprint",1, "Description",
                Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        List<Sprint> sprints = (List<Sprint>) sprintRepository.findAll();
        int sprintId = sprints.get(0).getId();
        sprintService.updateStartDate(sprintId, Date.valueOf("2022-04-20"));
        Sprint sprint = sprintRepository.findById(sprintId);
        assertEquals(sprint.getStartDate(), Timestamp.valueOf("2022-04-20 00:00:00"));
    }

    //When sprint exists and its start date is changed to after the current date, and the new date is after the sprint's
    // end date, exception is thrown.
    @Test
    void whenSprintStartDateChangedToDateAfterCurrentAndAfterEndDate_testExceptionThrown() {
        sprintService.saveSprint(new Sprint(projects.get(0).getId(), "Test Sprint",1, "Description",
                Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        List<Sprint> sprints = (List<Sprint>) sprintRepository.findAll();
        int sprintId = sprints.get(0).getId();

        Exception exception = assertThrows(Exception.class, () -> {
            sprintService.updateStartDate(sprintId, Date.valueOf("2022-06-20"));
        });
        String expectedMessage = "Sprint start date must not be after end date";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    //When sprint exists and its end date is changed to after the current date, and the new date is within project
    // boundaries and not within another sprint, test sprint end date is changed.
    @Test
    void whenSprintEndDateChangedToDateAfterCurrentAndNotInAnotherSprintAndWithinProjectDates_testSprintEndDateChanged() throws Exception {
        sprintService.saveSprint(new Sprint(projects.get(0).getId(), "Test Sprint",1, "Description",
                Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        List<Sprint> sprints = (List<Sprint>) sprintRepository.findAll();
        int sprintId = sprints.get(0).getId();
        sprintService.updateEndDate(sprintId, Date.valueOf("2022-05-20"));
        Sprint sprint = sprintRepository.findById(sprintId);
        assertEquals(sprint.getEndDate(), Timestamp.valueOf("2022-05-20 00:00:00"));
    }

    //When sprint exists and its end date is changed to date after current and new date is day before next sprint
    //start date, test end date changed.
    @Test
    void whenSprintEndDateChangedToDateAfterCurrentAndBeforeStartDateOfNextSprint_testSprintEndDateChanged() throws Exception {
        sprintService.saveSprint(new Sprint(projects.get(0).getId(), "Test Sprint",1, "Description",
                Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        sprintService.saveSprint(new Sprint(projects.get(0).getId(), "Test Sprint",2, "Description",
                Date.valueOf("2022-05-20"), Date.valueOf("2022-07-16")));
        List<Sprint> sprints = (List<Sprint>) sprintRepository.findAll();
        int sprintId = sprints.get(0).getId();
        sprintService.updateEndDate(sprintId, Date.valueOf("2022-05-19"));
        Sprint sprint = sprintRepository.findById(sprintId);
        assertEquals(sprint.getEndDate(), Timestamp.valueOf("2022-05-19 00:00:00"));
    }

    //When sprint exists and its end date is changed to after the current date, and the new date is not within project
    // boundaries and not within another sprint, test exception is thrown.
    @Test
    void whenSprintEndDateChangedToDateAfterCurrentAndNotInAnotherSprintAndNotWithinProjectDates_testExceptionThrown() {
        sprintService.saveSprint(new Sprint(projects.get(0).getId(), "Test Sprint",1, "Description",
                Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        List<Sprint> sprints = (List<Sprint>) sprintRepository.findAll();
        int sprintId = sprints.get(0).getId();

        Exception exception = assertThrows(Exception.class, () -> {
            sprintService.updateEndDate(sprintId, Date.valueOf("2024-03-10"));
        });
        String expectedMessage = "Sprint end date must be within project dates";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    //When sprint exists and its end date is changed to after the current date, and the new date is within project
    // boundaries and within another sprint, test exception is thrown.
    @Test
    void whenSprintEndDateChangedToDateAfterCurrentAndInAnotherSprint_testExceptionThrown() {
        sprintService.saveSprint(new Sprint(projects.get(0).getId(), "Test Sprint",1, "Description",
                Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        sprintService.saveSprint(new Sprint(projects.get(0).getId(), "Test Sprint",2, "Description",
                Date.valueOf("2022-05-16"), Date.valueOf("2022-06-15")));
        List<Sprint> sprints = (List<Sprint>) sprintRepository.findAll();
        int sprintId = sprints.get(0).getId();

        Exception exception = assertThrows(Exception.class, () -> {
            sprintService.updateEndDate(sprintId, Date.valueOf("2022-05-25"));
        });
        String expectedMessage = "Sprint must not be within another sprint";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    //When Sprint exists and its end date is changed to after the end date of another sprint, test that an
    //Exception is thrown. Edge case test the day after sprint end date.
    @Test
    void whenSprintEndDateChangedToLaterDateAndAfterEndOfAnotherSprint_testExceptionThrown() {
        sprintService.saveSprint(new Sprint(projects.get(0).getId(), "Test Sprint",1, "Description",
                Date.valueOf("2022-04-20"), Date.valueOf("2022-05-16")));
        sprintService.saveSprint(new Sprint(projects.get(0).getId(), "Test Sprint",2, "Description",
                Date.valueOf("2022-05-16"), Date.valueOf("2022-07-16")));
        List<Sprint> sprints = (List<Sprint>) sprintRepository.findAll();
        int sprintId = sprints.get(0).getId();

        Exception exception = assertThrows(Exception.class, () -> {
            sprintService.updateEndDate(sprintId, Date.valueOf("2022-07-17"));
        });
        String expectedMessage = "Sprint must not be within another sprint";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    //When sprint exists and its end date is changed to the end date of another sprint, test an exception is
    //Thrown. Edge case.
    @Test
    void whenSprintEndDateChangedToLaterDateAndIsEndDateOfAnotherSprint_testExceptionThrown() {
        sprintService.saveSprint(new Sprint(projects.get(0).getId(), "Test Sprint",1, "Description",
                Date.valueOf("2022-04-20"), Date.valueOf("2022-05-16")));
        sprintService.saveSprint(new Sprint(projects.get(0).getId(), "Test Sprint",2, "Description",
                Date.valueOf("2022-05-17"), Date.valueOf("2022-07-16")));
        List<Sprint> sprints = (List<Sprint>) sprintRepository.findAll();
        int sprintId = sprints.get(0).getId();

        Exception exception = assertThrows(Exception.class, () -> {
            sprintService.updateEndDate(sprintId, Date.valueOf("2022-07-16"));
        });
        String expectedMessage = "Sprint must not be within another sprint";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    //When a sprint exists and its start date is changed to the end date of and earlier sprint, test an exception is
    //Thrown. Edge case
    @Test
    void whenSprintEndDateChangedToLaterDateAndIsStartDateOfAnotherSprint_testExceptionThrown() {
        sprintService.saveSprint(new Sprint(projects.get(0).getId(), "Test Sprint",1, "Description",
                Date.valueOf("2022-04-20"), Date.valueOf("2022-05-16")));
        sprintService.saveSprint(new Sprint(projects.get(0).getId(), "Test Sprint",2, "Description",
                Date.valueOf("2022-05-20"), Date.valueOf("2022-07-16")));
        List<Sprint> sprints = (List<Sprint>) sprintRepository.findAll();
        int sprintId = sprints.get(0).getId();

        Exception exception = assertThrows(Exception.class, () -> {
            sprintService.updateEndDate(sprintId, Date.valueOf("2022-05-20"));
        });
        String expectedMessage = "Sprint must not be within another sprint";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    //---------------------------------------------
    //When sprint exists and its end date is changed to before the current date, and the new date is after the sprints
    // start date (so within project boundaries and not within another sprint), test sprint end date is changed.
    @Test
    void whenSprintEndDateChangedToDateBeforeCurrentAndNotBeforeStartDate_testSprintEndDateChanged() throws Exception {
        sprintService.saveSprint(new Sprint(projects.get(0).getId(), "Test Sprint",1, "Description",
                Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        List<Sprint> sprints = (List<Sprint>) sprintRepository.findAll();
        int sprintId = sprints.get(0).getId();
        sprintService.updateEndDate(sprintId, Date.valueOf("2022-05-05"));
        Sprint sprint = sprintRepository.findById(sprintId);
        assertEquals(sprint.getEndDate(), Timestamp.valueOf("2022-05-05 00:00:00"));
    }

    //When sprint exists and its end date is changed to before the current date, and the new date is before the sprint's
    // start date, test exception is thrown.
    @Test
    void whenSprintEndDateChangedToDateBeforeCurrentAndBeforeStartDate_testExceptionThrown() {
        sprintService.saveSprint(new Sprint(projects.get(0).getId(), "Test Sprint",1, "Description",
                Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        List<Sprint> sprints = (List<Sprint>) sprintRepository.findAll();
        int sprintId = sprints.get(0).getId();

        Exception exception = assertThrows(Exception.class, () -> {
            sprintService.updateEndDate(sprintId, Date.valueOf("2022-03-05"));
        });
        String expectedMessage = "Sprint end date must not be before start date";
        String actualMessage = exception.getMessage();
        System.out.println(actualMessage);
        assertTrue(actualMessage.contains(expectedMessage));
    }
}