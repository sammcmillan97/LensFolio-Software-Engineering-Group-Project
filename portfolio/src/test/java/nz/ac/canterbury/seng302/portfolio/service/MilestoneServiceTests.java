package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Milestone;
import nz.ac.canterbury.seng302.portfolio.model.MilestoneRepository;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.ProjectRepository;
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
        assertThat(milestones.size()).isZero();
        milestoneService.saveMilestone(new Milestone(projects.get(0).getId(), "Test Milestone", Date.valueOf("2022-06-06")));
        milestones = (List<Milestone>) milestoneRepository.findAll();
        assertThat(milestones.size()).isEqualTo(1);
    }

    @Test
    void whenOneMilestone_testSaveMilestoneToSameProject() {
        milestoneService.saveMilestone(new Milestone(projects.get(0).getId(), "Test Milestone", Date.valueOf("2022-06-06")));
        List<Milestone> milestones = (List<Milestone>) milestoneRepository.findAll();
        assertThat(milestones.size()).isEqualTo(1);
        milestoneService.saveMilestone(new Milestone(projects.get(0).getId(), "Test Milestone Duo", Date.valueOf("2022-07-07")));
        milestones = (List<Milestone>) milestoneRepository.findAll();
        assertThat(milestones.size()).isEqualTo(2);
    }

    @Test
    void whenManyMilestones_testSaveMilestoneToSameProject() {
        milestoneService.saveMilestone(new Milestone(projects.get(0).getId(), "Test Milestone Uno", Date.valueOf("2022-06-06")));
        milestoneService.saveMilestone(new Milestone(projects.get(0).getId(), "Test Milestone Deux", Date.valueOf("2022-06-06")));
        milestoneService.saveMilestone(new Milestone(projects.get(0).getId(), "Test Milestone Tri", Date.valueOf("2022-06-06")));
        milestoneService.saveMilestone(new Milestone(projects.get(0).getId(), "Test Milestone Quad", Date.valueOf("2022-06-06")));
        List<Milestone> milestones = (List<Milestone>) milestoneRepository.findAll();
        assertThat(milestones.size()).isEqualTo(4);
        milestoneService.saveMilestone(new Milestone(projects.get(0).getId(), "Test Milestone Pent", Date.valueOf("2022-06-06")));
        milestones = (List<Milestone>) milestoneRepository.findAll();
        assertThat(milestones.size()).isEqualTo(5);
    }

    @Test
    void whenOneMilestone_testSaveMilestoneToDifferentProject() {
        milestoneService.saveMilestone(new Milestone(projects.get(0).getId(), "Test Milestone", Date.valueOf("2022-06-06")));
        List<Milestone> milestones = (List<Milestone>) milestoneRepository.findAll();
        assertThat(milestones.size()).isEqualTo(1);
        assertThat(projects.get(0).getId()).isNotEqualTo(projects.get(1).getId());
        milestoneService.saveMilestone(new Milestone(projects.get(1).getId(), "Test Milestone Duo", Date.valueOf("2022-06-06")));
        milestones = (List<Milestone>) milestoneRepository.findAll();
        assertThat(milestones.size()).isEqualTo(2);
    }

    @Test
    void whenManyMilestones_testSaveMilestoneToDifferentProject() {
        milestoneService.saveMilestone(new Milestone(projects.get(0).getId(), "Test Milestone 1", Date.valueOf("2022-06-06")));
        milestoneService.saveMilestone(new Milestone(projects.get(0).getId(), "Test Milestone 2", Date.valueOf("2022-06-06")));
        milestoneService.saveMilestone(new Milestone(projects.get(0).getId(), "Test Milestone 3", Date.valueOf("2022-06-06")));
        milestoneService.saveMilestone(new Milestone(projects.get(0).getId(), "Test Milestone 4", Date.valueOf("2022-06-06")));
        List<Milestone> milestones = (List<Milestone>) milestoneRepository.findAll();
        assertThat(milestones.size()).isEqualTo(4);
        assertThat(projects.get(0).getId()).isNotEqualTo(projects.get(1).getId());
        milestoneService.saveMilestone(new Milestone(projects.get(1).getId(), "Test Milestone 5", Date.valueOf("2022-06-06")));
        milestoneService.saveMilestone(new Milestone(projects.get(1).getId(), "Test Milestone 6", Date.valueOf("2022-06-06")));
        milestoneService.saveMilestone(new Milestone(projects.get(1).getId(), "Test Milestone 7", Date.valueOf("2022-06-06")));
        milestones = (List<Milestone>) milestoneRepository.findAll();
        assertThat(milestones.size()).isEqualTo(7);
    }

    @Test
    void whenNoMilestonesSaved_testGetAllMilestones() {
        List<Milestone> milestones = (List<Milestone>) milestoneRepository.findAll();
        assertThat(milestones.size()).isZero();
        assertThat(milestoneService.getAllMilestones().size()).isZero();
    }

    @Test
    void whenOneMilestoneSaved_testGetAllMilestones() {
        milestoneService.saveMilestone(new Milestone(projects.get(0).getId(), "Test Milestone", Date.valueOf("2022-06-06")));
        List<Milestone> milestones = (List<Milestone>) milestoneRepository.findAll();
        assertThat(milestones.size()).isEqualTo(1);
        assertThat(milestoneService.getAllMilestones().size()).isEqualTo(1);
    }

    @Test
    void whenManyMilestonesSaved_testGetAllMilestones() {
        milestoneService.saveMilestone(new Milestone(projects.get(0).getId(), "Test Milestone Uno", Date.valueOf("2022-06-06")));
        milestoneService.saveMilestone(new Milestone(projects.get(0).getId(), "Test Milestone Duo", Date.valueOf("2022-06-06")));
        milestoneService.saveMilestone(new Milestone(projects.get(0).getId(), "Test Milestone Tri", Date.valueOf("2022-06-06")));
        milestoneService.saveMilestone(new Milestone(projects.get(0).getId(), "Test Milestone Quad", Date.valueOf("2022-06-06")));
        List<Milestone> milestones = (List<Milestone>) milestoneRepository.findAll();
        assertThat(milestones.size()).isEqualTo(4);
        assertThat(milestoneService.getAllMilestones().size()).isEqualTo(4);
    }

    @Test
    void whenMilestoneIdDoesNotExist_testGetMilestoneById() {
        List<Milestone> milestones = (List<Milestone>) milestoneRepository.findAll();
        assertThat(milestones.size()).isZero();
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
        assertThat(milestoneId).isNotNull();
        Milestone milestone = milestoneService.getMilestoneById(milestoneId);
        assertThat(milestone.getMilestoneName()).isEqualTo("Test Milestone");
        assertThat(milestone.getMilestoneDate()).isEqualTo(Timestamp.valueOf("2022-06-06 00:00:00"));
    }

    @Test
    void whenNoMilestonesSaved_testGetByParentProjectId() {
        List<Milestone> milestones = (List<Milestone>) milestoneRepository.findAll();
        assertThat(milestones.size()).isZero();
        List<Milestone> milestoneList = milestoneService.getByMilestoneParentProjectId(projects.get(0).getId());
        assertThat(milestoneList.size()).isZero();
    }

    @Test
    void whenOneMilestoneSaved_testGetByParentProjectId() {
        milestoneService.saveMilestone(new Milestone(projects.get(0).getId(), "Test Milestone", Date.valueOf("2022-06-06")));
        List<Milestone> milestones = (List<Milestone>) milestoneRepository.findAll();
        assertThat(milestones.size()).isEqualTo(1);
        List<Milestone> milestoneList = milestoneService.getByMilestoneParentProjectId(projects.get(0).getId());
        assertThat(milestoneList.size()).isEqualTo(1);
    }

    @Test
    void whenManyMilestonesSaved_testGetByParentProjectId() {
        milestoneService.saveMilestone(new Milestone(projects.get(0).getId(), "Test Milestone 1", Date.valueOf("2022-06-06")));
        milestoneService.saveMilestone(new Milestone(projects.get(0).getId(), "Test Milestone 2", Date.valueOf("2022-06-06")));
        milestoneService.saveMilestone(new Milestone(projects.get(0).getId(), "Test Milestone 3", Date.valueOf("2022-06-06")));
        milestoneService.saveMilestone(new Milestone(projects.get(0).getId(), "Test Milestone 4", Date.valueOf("2022-06-06")));
        List<Milestone> milestones = (List<Milestone>) milestoneRepository.findAll();
        assertThat(milestones.size()).isEqualTo(4);
        List<Milestone> milestoneList = milestoneService.getByMilestoneParentProjectId(projects.get(0).getId());
        assertThat(milestoneList.size()).isEqualTo(4);
    }

    @Test
    void whenManyMilestonesSavedToDifferentProjects_testRetrieveByParentProjectId() {
        milestoneService.saveMilestone(new Milestone(projects.get(0).getId(), "Test Milestone Uno", Date.valueOf("2022-06-06")));
        milestoneService.saveMilestone(new Milestone(projects.get(0).getId(), "Test Milestone Duo", Date.valueOf("2022-06-06")));
        milestoneService.saveMilestone(new Milestone(projects.get(1).getId(), "Test Milestone Tri", Date.valueOf("2022-06-06")));
        milestoneService.saveMilestone(new Milestone(projects.get(1).getId(), "Test Milestone Quad", Date.valueOf("2022-06-06")));
        List<Milestone> milestones = (List<Milestone>) milestoneRepository.findAll();
        assertThat(milestones.size()).isEqualTo(4);
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
        assertThat(milestones.size()).isEqualTo(1);
        milestoneService.deleteMilestoneById(milestone.getId());
        milestones = (List<Milestone>) milestoneRepository.findAll();
        assertThat(milestones.size()).isZero();
    }

    @Test
    void whenMilestoneDoesNotExist_testDeleteMilestoneThrowsException () {
        Milestone milestone = new Milestone(projects.get(0).getId(), "Test Milestone", Date.valueOf("2022-06-06"));
        List<Milestone> milestones = (List<Milestone>) milestoneRepository.findAll();
        assertThat(milestones.size()).isEqualTo(0);

        Exception exception = assertThrows(Exception.class, () ->
                milestoneService.deleteMilestoneById(milestone.getId()));
        String expectedMessage = "Milestone does not exist";
        String actualMessage = exception.getMessage();
        assertThat(expectedMessage).isEqualTo(actualMessage);
    }

    @Test
    void whenMilestoneDateIsChangedToDateWithinProjectDates_testMilestoneDateChanged() throws Exception {
        Milestone milestone = new Milestone(projects.get(0).getId(), "Test Milestone", Date.valueOf("2022-06-06"));
        milestoneService.saveMilestone(milestone);
        List<Milestone> milestoneList = (List<Milestone>) milestoneRepository.findAll();
        int milestoneId = milestoneList.get(0).getId();
        milestoneService.updateMilestoneDate(milestoneId, Date.valueOf("2022-06-27"));
        Milestone milestone1 = milestoneRepository.findById(milestoneId);
        assertThat(milestone1.getMilestoneDate()).isEqualTo(Timestamp.valueOf("2022-06-27 00:00:00"));
    }

    @Test
    void whenMilestoneDateIsChangedToDateAfterProjectEndDate_testExceptionThrown() {
        milestoneService.saveMilestone(new Milestone(projects.get(0).getId(), "Test Milestone", Date.valueOf("2022-06-15")));
        List<Milestone> milestoneList = (List<Milestone>) milestoneRepository.findAll();
        int milestoneId = milestoneList.get(0).getId();

        Exception exception = assertThrows(Exception.class, () ->
                milestoneService.updateMilestoneDate(milestoneId, Date.valueOf("2022-07-02")));
        String expectedMessage = "Milestone date must be within the project dates";
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
        String expectedMessage = "Milestone date must be within the project dates";
        String actualMessage = exception.getMessage();
        assertThat(expectedMessage).isEqualTo(actualMessage);
    }
}
