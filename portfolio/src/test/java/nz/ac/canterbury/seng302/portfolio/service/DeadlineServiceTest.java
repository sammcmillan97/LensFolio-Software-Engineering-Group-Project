package nz.ac.canterbury.seng302.portfolio.service;


import nz.ac.canterbury.seng302.portfolio.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@AutoConfigureTestDatabase
@SpringBootTest
public class DeadlineServiceTest {
    @Autowired
    DeadlineService deadlineService;

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    DeadlineRepository deadlineRepository;

    static List<Project> projects;

    /**
     * Initialise the database with the projects before each test
     */
    @BeforeEach
    void storeProjects() {
        projectRepository.save(new Project("Project Name", "Test Project", Date.valueOf("2022-05-01"), Date.valueOf("2022-06-30")));
        projectRepository.save(new Project("Project Name", "Test Project", Date.valueOf("2022-05-01"), Date.valueOf("2022-06-30")));
        projects = (List<Project>) projectRepository.findAll();
    }

    /**
     * Refresh the database each test
     */
    @AfterEach
    void cleanDatabase() {
        projectRepository.deleteAll();
        deadlineRepository.deleteAll();
    }

    /**
     * When there are no deadlines in the database, test saving a deadline using deadline service
     */
    @Test
    void whenNoDeadlines_testSaveDeadlineToSameProject() {
        List<Deadline> deadlines = (List<Deadline>) deadlineRepository.findAll();
        assertThat(deadlines.size()).isZero();
        deadlineService.saveDeadline(new Deadline(projects.get(0).getId(), "Test Deadline", Date.valueOf("2022-06-06")));
        deadlines = (List<Deadline>) deadlineRepository.findAll();
        assertThat(deadlines.size()).isEqualTo(1);
    }

    /**
     * When there is a deadline in the database, test saving a deadline using deadline service
     */
    @Test
    void whenOneDeadline_testSaveDeadlineToSameProject() {
        deadlineService.saveDeadline(new Deadline(projects.get(0).getId(), "Test Deadline", Date.valueOf("2022-06-06")));
        List<Deadline> deadlines = (List<Deadline>) deadlineRepository.findAll();
        assertThat(deadlines.size()).isEqualTo(1);
        deadlineService.saveDeadline(new Deadline(projects.get(0).getId(), "Test deadline Duo", Date.valueOf("2022-07-07")));
        deadlines = (List<Deadline>) deadlineRepository.findAll();
        assertThat(deadlines.size()).isEqualTo(2);
    }

    /**
     * When there are many deadlines in the database, test saving a deadline using deadline service
     */
    @Test
    void whenManyDeadlines_testSaveDeadlineToSameProject() {
        deadlineService.saveDeadline(new Deadline(projects.get(0).getId(), "Test Deadline Uno", Date.valueOf("2022-06-06")));
        deadlineService.saveDeadline(new Deadline(projects.get(0).getId(), "Test Deadline Deux", Date.valueOf("2022-06-06")));
        deadlineService.saveDeadline(new Deadline(projects.get(0).getId(), "Test Deadline Tri", Date.valueOf("2022-06-06")));
        deadlineService.saveDeadline(new Deadline(projects.get(0).getId(), "Test Deadline Quad", Date.valueOf("2022-06-06")));
        List<Deadline> deadlines = (List<Deadline>) deadlineRepository.findAll();
        assertThat(deadlines.size()).isEqualTo(4);
        deadlineService.saveDeadline(new Deadline(projects.get(0).getId(), "Test Deadline Pent", Date.valueOf("2022-06-06")));
        deadlines = (List<Deadline>) deadlineRepository.findAll();
        assertThat(deadlines.size()).isEqualTo(5);
    }

    /**
     * When there is one deadline in a project in the database, test saving a deadline to a different project.
     */
    @Test
    void whenOneDeadline_testSaveDeadlineToDifferentProject() {
        deadlineService.saveDeadline(new Deadline(projects.get(0).getId(), "Test Deadline", Date.valueOf("2022-06-06")));
        List<Deadline> deadlines = (List<Deadline>) deadlineRepository.findAll();
        assertThat(deadlines.size()).isEqualTo(1);
        assertThat(projects.get(0).getId()).isNotEqualTo(projects.get(1).getId());
        deadlineService.saveDeadline(new Deadline(projects.get(1).getId(), "Test deadline Duo", Date.valueOf("2022-06-06")));
        deadlines = (List<Deadline>) deadlineRepository.findAll();
        assertThat(deadlines.size()).isEqualTo(2);
    }

    /**
     * When there are many deadlines in each project in the database, test saving deadlines to each different project.
     */
    @Test
    void whenManyDeadlines_testSaveDeadlineToDifferentProject() {
        deadlineService.saveDeadline(new Deadline(projects.get(0).getId(), "Test deadline 1", Date.valueOf("2022-06-06")));
        deadlineService.saveDeadline(new Deadline(projects.get(0).getId(), "Test deadline 2", Date.valueOf("2022-06-06")));
        deadlineService.saveDeadline(new Deadline(projects.get(0).getId(), "Test deadline 3", Date.valueOf("2022-06-06")));
        deadlineService.saveDeadline(new Deadline(projects.get(0).getId(), "Test deadline 4", Date.valueOf("2022-06-06")));
        List<Deadline> deadlines = (List<Deadline>) deadlineRepository.findAll();
        assertThat(deadlines.size()).isEqualTo(4);
        assertThat(projects.get(0).getId()).isNotEqualTo(projects.get(1).getId());
        deadlineService.saveDeadline(new Deadline(projects.get(1).getId(), "Test deadline 5", Date.valueOf("2022-06-06")));
        deadlineService.saveDeadline(new Deadline(projects.get(1).getId(), "Test deadline 6", Date.valueOf("2022-06-06")));
        deadlineService.saveDeadline(new Deadline(projects.get(1).getId(), "Test deadline 7", Date.valueOf("2022-06-06")));
        deadlines = (List<Deadline>) deadlineRepository.findAll();
        assertThat(deadlines.size()).isEqualTo(7);
    }

    /**
     * When there are no deadlines in the database, test retrieving all deadlines using deadline service.
     */
    @Test
    void whenNoDeadlinesSaved_testGetAllDeadlines() {
        List<Deadline> deadlines = (List<Deadline>) deadlineRepository.findAll();
        assertThat(deadlines.size()).isZero();
        assertThat(deadlineService.getAllDeadlines().size()).isZero();
    }

    /**
     * When there is one deadline in the database, test retrieving all deadlines using deadline service
     */
    @Test
    void whenOneDeadlineSaved_testGetAllDeadlines() {
        deadlineService.saveDeadline(new Deadline(projects.get(0).getId(), "Test Deadline", Date.valueOf("2022-06-06")));
        List<Deadline> deadlines = (List<Deadline>) deadlineRepository.findAll();
        assertThat(deadlines.size()).isEqualTo(1);
        assertThat(deadlineService.getAllDeadlines().size()).isEqualTo(1);
    }

    /**
     * When there are many deadlines in the database, test retrieving all deadlines using deadline service.
     */
    @Test
    void whenManyDeadlinesSaved_testGetAllDeadlines() {
        deadlineService.saveDeadline(new Deadline(projects.get(0).getId(), "Test deadline Uno", Date.valueOf("2022-06-06")));
        deadlineService.saveDeadline(new Deadline(projects.get(0).getId(), "Test deadline Duo", Date.valueOf("2022-06-06")));
        deadlineService.saveDeadline(new Deadline(projects.get(0).getId(), "Test deadline Tri", Date.valueOf("2022-06-06")));
        deadlineService.saveDeadline(new Deadline(projects.get(0).getId(), "Test deadline Quad", Date.valueOf("2022-06-06")));
        List<Deadline> deadlines = (List<Deadline>) deadlineRepository.findAll();
        assertThat(deadlines.size()).isEqualTo(4);
        assertThat(deadlineService.getAllDeadlines().size()).isEqualTo(4);
    }

    /**
     * When the deadline id does not exist, test retrieving the deadline by its id.
     */
    @Test
    void whenDeadlineIdDoesNotExist_testGetDeadlineById() {
        List<Deadline> deadlines = (List<Deadline>) deadlineRepository.findAll();
        assertThat(deadlines.size()).isZero();
        Exception exception = assertThrows(Exception.class, () -> deadlineService.getDeadlineById(999999));
        String expectedMessage = "Deadline not found";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    /**
     * When the deadline id does exist, test retrieving the deadline by its id.
     */
    @Test
    void whenDeadlineIdExists_testGetDeadlineById() throws Exception {
        deadlineService.saveDeadline(new Deadline(projects.get(0).getId(), "Test Deadline", Date.valueOf("2022-06-06")));
        List<Deadline> deadlines = (List<Deadline>) deadlineRepository.findAll();
        int deadlineId = deadlines.get(0).getDeadlineId();
        assertThat(deadlineId).isNotNull();
        Deadline deadline = deadlineService.getDeadlineById(deadlineId);
        assertThat(deadline.getDeadlineName()).isEqualTo("Test Deadline");
        assertThat(deadline.getDeadlineDate()).isEqualTo(Timestamp.valueOf("2022-06-06 00:00:00"));
    }

    /**
     * When no deadlines are saved to the database, test retrieving deadlines by parent project id.
     */
    @Test
    void whenNoDeadlinesSaved_testGetByParentProjectId() {
        List<Deadline> deadlines = (List<Deadline>) deadlineRepository.findAll();
        assertThat(deadlines.size()).isZero();
        List<Deadline> deadlineList = deadlineService.getByDeadlineParentProjectId(projects.get(0).getId());
        assertThat(deadlineList.size()).isZero();
    }

    /**
     * When one deadline is saved to the database, test retrieving deadlines by parent project id.
     */
    @Test
    void whenOneDeadlineSaved_testGetByParentProjectId() {
        deadlineService.saveDeadline(new Deadline(projects.get(0).getId(), "Test Deadline", Date.valueOf("2022-06-06")));
        List<Deadline> deadlines = (List<Deadline>) deadlineRepository.findAll();
        assertThat(deadlines.size()).isEqualTo(1);
        List<Deadline> deadlineList = deadlineService.getByDeadlineParentProjectId(projects.get(0).getId());
        assertThat(deadlineList.size()).isEqualTo(1);
    }

    /**
     * When many deadlines are saved to the database, test retrieving deadlines by parent project id.
     */
    @Test
    void whenManyDeadlinesSaved_testGetByParentProjectId() {
        deadlineService.saveDeadline(new Deadline(projects.get(0).getId(), "Test deadline 1", Date.valueOf("2022-06-06")));
        deadlineService.saveDeadline(new Deadline(projects.get(0).getId(), "Test deadline 2", Date.valueOf("2022-06-06")));
        deadlineService.saveDeadline(new Deadline(projects.get(0).getId(), "Test deadline 3", Date.valueOf("2022-06-06")));
        deadlineService.saveDeadline(new Deadline(projects.get(0).getId(), "Test deadline 4", Date.valueOf("2022-06-06")));
        List<Deadline> deadlines = (List<Deadline>) deadlineRepository.findAll();
        assertThat(deadlines.size()).isEqualTo(4);
        List<Deadline> deadlineList = deadlineService.getByDeadlineParentProjectId(projects.get(0).getId());
        assertThat(deadlineList.size()).isEqualTo(4);
    }

    /**
     * When many deadlines are saved to many projects in the database, test retrieve deadlines by parent project id.
     */
    @Test
    void whenManyDeadlinesSavedToDifferentProjects_testRetrieveByParentProjectId() {
        deadlineService.saveDeadline(new Deadline(projects.get(0).getId(), "Test deadline Uno", Date.valueOf("2022-06-06")));
        deadlineService.saveDeadline(new Deadline(projects.get(0).getId(), "Test deadline Duo", Date.valueOf("2022-06-06")));
        deadlineService.saveDeadline(new Deadline(projects.get(1).getId(), "Test deadline Tri", Date.valueOf("2022-06-06")));
        deadlineService.saveDeadline(new Deadline(projects.get(1).getId(), "Test deadline Quad", Date.valueOf("2022-06-06")));
        List<Deadline> deadlines = (List<Deadline>) deadlineRepository.findAll();
        assertThat(deadlines.size()).isEqualTo(4);
        List<Deadline> deadlineList = deadlineService.getByDeadlineParentProjectId(projects.get(0).getId());
        int listSize = deadlineList.size();
        assertThat(listSize).isEqualTo(2);
        deadlineList = deadlineService.getByDeadlineParentProjectId(projects.get(1).getId());
        int listSize2 = deadlineList.size();
        assertThat(listSize2).isEqualTo(2);
        assertThat(listSize + listSize2).isEqualTo(4);
    }

    @Test
    void whenDeadlineExists_testDeleteDeadline() {
        Deadline deadline = new Deadline(projects.get(0).getId(), "Test deadline", Date.valueOf("2022-06-06"));
        deadlineService.saveDeadline(deadline);
        List<Deadline> deadlines = (List<Deadline>) deadlineRepository.findAll();
        assertThat(deadlines.size()).isEqualTo(1);
        deadlineService.deleteDeadlineById(deadline.getDeadlineId());
        deadlines = (List<Deadline>) deadlineRepository.findAll();
        assertThat(deadlines.size()).isZero();
    }

    @Test
    void whenDeadlineDoesNotExist_testDeleteDeadlineThrowsException () {
        Deadline deadline = new Deadline(projects.get(0).getId(), "Test deadline", Date.valueOf("2022-06-06"));
        List<Deadline> deadlines = (List<Deadline>) deadlineRepository.findAll();
        assertThat(deadlines.size()).isEqualTo(0);

        Exception exception = assertThrows(Exception.class, () ->
                deadlineService.deleteDeadlineById(deadline.getDeadlineId()));
        String expectedMessage = "Deadline does not exist";
        String actualMessage = exception.getMessage();
        assertThat(expectedMessage).isEqualTo(actualMessage);
    }

    @Test
    void whenDeadlineDateIsChangedToDateWithinProjectDates_testDeadlineDateChanged() throws Exception {
        Deadline deadline = new Deadline(projects.get(0).getId(), "Test deadline", Date.valueOf("2022-06-06"));
        deadlineService.saveDeadline(deadline);
        List<Deadline> deadlineList = (List<Deadline>) deadlineRepository.findAll();
        int deadlineId = deadlineList.get(0).getDeadlineId();
        deadlineService.updateDeadlineDate(deadlineId, Date.valueOf("2022-06-27"));
        Deadline deadline1 = deadlineRepository.findById(deadlineId);
        assertThat(deadline1.getDeadlineDate()).isEqualTo(Timestamp.valueOf("2022-06-27 00:00:00"));
    }

    @Test
    void whenDeadlineDateIsChangedToDateAfterProjectEndDate_testExceptionThrown() {
        deadlineService.saveDeadline(new Deadline(projects.get(0).getId(), "Test Deadline", Date.valueOf("2022-06-15")));
        List<Deadline> deadlineList = (List<Deadline>) deadlineRepository.findAll();
        int deadlineId = deadlineList.get(0).getDeadlineId();

        Exception exception = assertThrows(Exception.class, () ->
                deadlineService.updateDeadlineDate(deadlineId, Date.valueOf("2022-07-02")));
        String expectedMessage = "Deadline date must be within the project dates";
        String actualMessage = exception.getMessage();
        assertThat(expectedMessage).isEqualTo(actualMessage);
    }

}
