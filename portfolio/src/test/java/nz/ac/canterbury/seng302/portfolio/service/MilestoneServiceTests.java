package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.project.Milestone;
import nz.ac.canterbury.seng302.portfolio.repository.MilestoneRepository;
import nz.ac.canterbury.seng302.portfolio.model.project.Project;
import nz.ac.canterbury.seng302.portfolio.repository.ProjectRepository;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@AutoConfigureTestDatabase
@SpringBootTest
class MilestoneServiceTests {
    @Autowired
    MilestoneService milestoneService;

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    MilestoneRepository milestoneRepository;

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
        milestoneRepository.deleteAll();
    }

    @Test
    void whenNoMilestones_testSaveMilestoneToProject() {
        List<Milestone> milestones = (List<Milestone>) milestoneRepository.findAll();
        assertTrue(milestones.isEmpty());
        milestoneService.saveMilestone(new Milestone(projects.get(0).getId(), "Test Milestone", Date.valueOf("2022-06-06")));
        milestones = (List<Milestone>) milestoneRepository.findAll();
        assertEquals(1, milestones.size());
    }

    @Test
    void whenOneMilestone_testSaveMilestoneToSameProject() {
        milestoneService.saveMilestone(new Milestone(projects.get(0).getId(), "Test Milestone", Date.valueOf("2022-06-06")));
        List<Milestone> milestones = (List<Milestone>) milestoneRepository.findAll();
        assertEquals(1, milestones.size());
        milestoneService.saveMilestone(new Milestone(projects.get(0).getId(), "Test Milestone Duo", Date.valueOf("2022-07-07")));
        milestones = (List<Milestone>) milestoneRepository.findAll();
        assertEquals(2, milestones.size());
    }

    @Test
    void whenManyMilestones_testSaveMilestoneToSameProject() {
        milestoneService.saveMilestone(new Milestone(projects.get(0).getId(), "Test Milestone Uno", Date.valueOf("2022-06-06")));
        milestoneService.saveMilestone(new Milestone(projects.get(0).getId(), "Test Milestone Deux", Date.valueOf("2022-06-06")));
        milestoneService.saveMilestone(new Milestone(projects.get(0).getId(), "Test Milestone Tri", Date.valueOf("2022-06-06")));
        milestoneService.saveMilestone(new Milestone(projects.get(0).getId(), "Test Milestone Quad", Date.valueOf("2022-06-06")));
        List<Milestone> milestones = (List<Milestone>) milestoneRepository.findAll();
        assertEquals(4, milestones.size());
        milestoneService.saveMilestone(new Milestone(projects.get(0).getId(), "Test Milestone Pent", Date.valueOf("2022-06-06")));
        milestones = (List<Milestone>) milestoneRepository.findAll();
        assertEquals(5, milestones.size());
    }

    @Test
    void whenOneMilestone_testSaveMilestoneToDifferentProject() {
        milestoneService.saveMilestone(new Milestone(projects.get(0).getId(), "Test Milestone", Date.valueOf("2022-06-06")));
        List<Milestone> milestones = (List<Milestone>) milestoneRepository.findAll();
        assertEquals(1, milestones.size());
        assertThat(projects.get(0).getId()).isNotEqualTo(projects.get(1).getId());
        milestoneService.saveMilestone(new Milestone(projects.get(1).getId(), "Test Milestone Duo", Date.valueOf("2022-06-06")));
        milestones = (List<Milestone>) milestoneRepository.findAll();
        assertEquals(2, milestones.size());
    }

    @Test
    void whenManyMilestones_testSaveMilestoneToDifferentProject() {
        milestoneService.saveMilestone(new Milestone(projects.get(0).getId(), "Test Milestone 1", Date.valueOf("2022-06-06")));
        milestoneService.saveMilestone(new Milestone(projects.get(0).getId(), "Test Milestone 2", Date.valueOf("2022-06-06")));
        milestoneService.saveMilestone(new Milestone(projects.get(0).getId(), "Test Milestone 3", Date.valueOf("2022-06-06")));
        milestoneService.saveMilestone(new Milestone(projects.get(0).getId(), "Test Milestone 4", Date.valueOf("2022-06-06")));
        List<Milestone> milestones = (List<Milestone>) milestoneRepository.findAll();
        assertEquals(4, milestones.size());
        assertThat(projects.get(0).getId()).isNotEqualTo(projects.get(1).getId());
        milestoneService.saveMilestone(new Milestone(projects.get(1).getId(), "Test Milestone 5", Date.valueOf("2022-06-06")));
        milestoneService.saveMilestone(new Milestone(projects.get(1).getId(), "Test Milestone 6", Date.valueOf("2022-06-06")));
        milestoneService.saveMilestone(new Milestone(projects.get(1).getId(), "Test Milestone 7", Date.valueOf("2022-06-06")));
        milestones = (List<Milestone>) milestoneRepository.findAll();
        assertEquals(7, milestones.size());
    }

    @Test
    void whenNoMilestonesSaved_testGetAllMilestones() {
        List<Milestone> milestones = (List<Milestone>) milestoneRepository.findAll();
        assertTrue(milestones.isEmpty());
        assertTrue(milestoneService.getAllMilestones().isEmpty());
    }

    @Test
    void whenOneMilestoneSaved_testGetAllMilestones() {
        milestoneService.saveMilestone(new Milestone(projects.get(0).getId(), "Test Milestone", Date.valueOf("2022-06-06")));
        List<Milestone> milestones = (List<Milestone>) milestoneRepository.findAll();
        assertEquals(1, milestones.size());
        assertEquals(1, milestoneService.getAllMilestones().size());
    }

    @Test
    void whenManyMilestonesSaved_testGetAllMilestones() {
        milestoneService.saveMilestone(new Milestone(projects.get(0).getId(), "Test Milestone Uno", Date.valueOf("2022-06-06")));
        milestoneService.saveMilestone(new Milestone(projects.get(0).getId(), "Test Milestone Duo", Date.valueOf("2022-06-06")));
        milestoneService.saveMilestone(new Milestone(projects.get(0).getId(), "Test Milestone Tri", Date.valueOf("2022-06-06")));
        milestoneService.saveMilestone(new Milestone(projects.get(0).getId(), "Test Milestone Quad", Date.valueOf("2022-06-06")));
        List<Milestone> milestones = (List<Milestone>) milestoneRepository.findAll();
        assertEquals(4, milestones.size());
        assertEquals(4, milestoneService.getAllMilestones().size());
    }

    @Test
    void whenMilestoneIdDoesNotExist_testGetMilestoneById() {
        List<Milestone> milestones = (List<Milestone>) milestoneRepository.findAll();
        assertTrue(milestones.isEmpty());
        Exception exception = assertThrows(Exception.class, () -> milestoneService.getMilestoneById(999999));
        String expectedMessage = "Milestone not found";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void whenMilestoneIdExists_testGetMilestoneById() throws Exception {
        milestoneService.saveMilestone(new Milestone(projects.get(0).getId(), "Test Milestone", Date.valueOf("2022-06-06")));
        List<Milestone> milestones = (List<Milestone>) milestoneRepository.findAll();
        int milestoneId = milestones.get(0).getId();
        assertNotEquals(0, milestoneId);
        Milestone milestone = milestoneService.getMilestoneById(milestoneId);
        assertThat(milestone.getMilestoneName()).isEqualTo("Test Milestone");
        assertThat(milestone.getMilestoneDate()).isEqualTo(Timestamp.valueOf("2022-06-06 00:00:00"));
    }

    @Test
    void whenNoMilestonesSaved_testGetByParentProjectId() {
        List<Milestone> milestones = (List<Milestone>) milestoneRepository.findAll();
        assertTrue(milestones.isEmpty());
        List<Milestone> milestoneList = milestoneService.getByMilestoneParentProjectId(projects.get(0).getId());
        assertTrue(milestoneList.isEmpty());
    }

    @Test
    void whenOneMilestoneSaved_testGetByParentProjectId() {
        milestoneService.saveMilestone(new Milestone(projects.get(0).getId(), "Test Milestone", Date.valueOf("2022-06-06")));
        List<Milestone> milestones = (List<Milestone>) milestoneRepository.findAll();
        assertEquals(1, milestones.size());
        List<Milestone> milestoneList = milestoneService.getByMilestoneParentProjectId(projects.get(0).getId());
        assertEquals(1, milestoneList.size());
    }

    @Test
    void whenManyMilestonesSaved_testGetByParentProjectId() {
        milestoneService.saveMilestone(new Milestone(projects.get(0).getId(), "Test Milestone 1", Date.valueOf("2022-06-06")));
        milestoneService.saveMilestone(new Milestone(projects.get(0).getId(), "Test Milestone 2", Date.valueOf("2022-06-06")));
        milestoneService.saveMilestone(new Milestone(projects.get(0).getId(), "Test Milestone 3", Date.valueOf("2022-06-06")));
        milestoneService.saveMilestone(new Milestone(projects.get(0).getId(), "Test Milestone 4", Date.valueOf("2022-06-06")));
        List<Milestone> milestones = (List<Milestone>) milestoneRepository.findAll();
        assertEquals(4, milestones.size());
        List<Milestone> milestoneList = milestoneService.getByMilestoneParentProjectId(projects.get(0).getId());
        assertEquals(4, milestoneList.size());
    }

    @Test
    void whenManyMilestonesSavedToDifferentProjects_testRetrieveByParentProjectId() {
        milestoneService.saveMilestone(new Milestone(projects.get(0).getId(), "Test Milestone Uno", Date.valueOf("2022-06-06")));
        milestoneService.saveMilestone(new Milestone(projects.get(0).getId(), "Test Milestone Duo", Date.valueOf("2022-06-06")));
        milestoneService.saveMilestone(new Milestone(projects.get(1).getId(), "Test Milestone Tri", Date.valueOf("2022-06-06")));
        milestoneService.saveMilestone(new Milestone(projects.get(1).getId(), "Test Milestone Quad", Date.valueOf("2022-06-06")));
        List<Milestone> milestones = (List<Milestone>) milestoneRepository.findAll();
        assertEquals(4, milestones.size());
        List<Milestone> milestoneList = milestoneService.getByMilestoneParentProjectId(projects.get(0).getId());
        int listSize = milestoneList.size();
        assertThat(listSize).isEqualTo(2);
        milestoneList = milestoneService.getByMilestoneParentProjectId(projects.get(1).getId());
        int listSize2 = milestoneList.size();
        assertThat(listSize2).isEqualTo(2);
        assertThat(listSize + listSize2).isEqualTo(4);
    }

    @Test
    void whenMilestoneExists_testDeleteMilestone() {
        Milestone milestone = new Milestone(projects.get(0).getId(), "Test Milestone", Date.valueOf("2022-06-06"));
        milestoneService.saveMilestone(milestone);
        List<Milestone> milestones = (List<Milestone>) milestoneRepository.findAll();
        assertEquals(1, milestones.size());
        milestoneService.deleteMilestoneById(milestone.getId());
        milestones = (List<Milestone>) milestoneRepository.findAll();
        assertTrue(milestones.isEmpty());
    }

    @Test
    void whenMilestoneDoesNotExist_testDeleteMilestoneThrowsException () {
        Milestone milestone = new Milestone(projects.get(0).getId(), "Test Milestone", Date.valueOf("2022-06-06"));
        List<Milestone> milestones = (List<Milestone>) milestoneRepository.findAll();
        assertTrue(milestones.isEmpty());

        Exception exception = assertThrows(Exception.class, () ->
                milestoneService.deleteMilestoneById(milestone.getId()));
        String expectedMessage = "Milestone does not exist";
        String actualMessage = exception.getMessage();
        assertThat(expectedMessage).isEqualTo(actualMessage);
    }

    @Test
    void whenNoMilestoneExists_testCreateNewMilestone() throws Exception {
        milestoneService.createNewMilestone(projects.get(0).getId(), "Test Milestone", Date.valueOf("2022-06-06"));
        List<Milestone> milestoneList = milestoneService.getByMilestoneParentProjectId(projects.get(0).getId());
        assertEquals("Test Milestone", milestoneList.get(0).getMilestoneName());
    }

    @Test
    void whenMilestoneExists_testEditMilestone() throws Exception {
        Milestone milestone = new Milestone((projects.get(0).getId()), "Unedited Milestone", Date.valueOf("2022-06-06"));
        milestoneRepository.save(milestone);
        milestoneService.updateMilestone(projects.get(0).getId(), milestone.getId(), "Edited Milestone", Date.valueOf("2022-06-05"));
        List<Milestone> milestoneList = milestoneService.getByMilestoneParentProjectId(projects.get(0).getId());
        assertEquals("Edited Milestone", milestoneList.get(0).getMilestoneName());
        assertEquals(Date.valueOf("2022-06-05"), milestoneList.get(0).getMilestoneDate());

    }

    @Test
    void whenCreateNewMilestoneBeforeProjectStarts_testExceptionThrown() {
        Exception exception = assertThrows(Exception.class, () ->
                milestoneService.createNewMilestone(projects.get(0).getId(), "Test Milestone", Date.valueOf("2022-04-06")));
        String expectedMessage = "Milestone date (2022-04-06) must be within the project dates (2022-05-01 00:00:00.0 - 2022-06-30 00:00:00.0)";
        String actualMessage = exception.getMessage();
        assertThat(actualMessage).isEqualTo(expectedMessage);
    }
}
