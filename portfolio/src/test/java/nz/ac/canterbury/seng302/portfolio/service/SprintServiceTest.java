package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.ProjectRepository;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.model.SprintRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
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
        projectRepository.save(new Project("Project Name", "Test Project", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        projectRepository.save(new Project("Project Name", "Test Project", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
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
        assertThat(sprints.size()).isZero();
        sprintService.saveSprint(new Sprint(projects.get(0).getId(), "Test Sprint",1, "Description",
                Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        sprints = (List<Sprint>) sprintRepository.findAll();
        assertThat(sprints.size()).isEqualTo(1);
    }
    //When there is one sprint in the database, test saving a sprint using sprint service.
    @Test
    void whenOneSprint_testSaveSprintToSameProject() {
        sprintService.saveSprint(new Sprint(projects.get(0).getId(), "Test Sprint",1, "Description",
                Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        List<Sprint> sprints = (List<Sprint>) sprintRepository.findAll();
        assertThat(sprints.size()).isEqualTo(1);
        sprintService.saveSprint(new Sprint(projects.get(0).getId(), "Test Sprint",2, "Description",
                Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        sprints = (List<Sprint>) sprintRepository.findAll();
        assertThat(sprints.size()).isEqualTo(2);
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
        assertThat(sprints.size()).isEqualTo(4);
        sprintService.saveSprint(new Sprint(projects.get(0).getId(), "Test Sprint",5, "Description",
                Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        sprints = (List<Sprint>) sprintRepository.findAll();
        assertThat(sprints.size()).isEqualTo(5);
    }
    //When there is one sprint in a project in the database, test saving a sprint to a different project.
    @Test
    void whenOneSprint_testSaveSprintToDifferentProject() {
        sprintService.saveSprint(new Sprint(projects.get(0).getId(), "Test Sprint",1, "Description",
                Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        List<Sprint> sprints = (List<Sprint>) sprintRepository.findAll();
        assertThat(sprints.size()).isEqualTo(1);
        assertThat(projects.get(0).getId()).isNotEqualTo(projects.get(1).getId());
        sprintService.saveSprint(new Sprint(projects.get(1).getId(), "Test Sprint",2, "Description",
                Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        sprints = (List<Sprint>) sprintRepository.findAll();
        assertThat(sprints.size()).isEqualTo(2);
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
        assertThat(sprints.size()).isEqualTo(4);
        assertThat(projects.get(0).getId()).isNotEqualTo(projects.get(1).getId());
        sprintService.saveSprint(new Sprint(projects.get(0).getId(), "Test Sprint",5, "Description",
                Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        sprintService.saveSprint(new Sprint(projects.get(1).getId(), "Test Sprint",6, "Description",
                Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        sprints = (List<Sprint>) sprintRepository.findAll();
        assertThat(sprints.size()).isEqualTo(6);
    }
    //When there are no sprints in the database, test retrieving all sprints using sprint service.
    @Test
    void whenNoSprintSaved_testGetAllSprints() {
        List<Sprint> sprints = (List<Sprint>) sprintRepository.findAll();
        assertThat(sprints.size()).isZero();
        assertThat(sprintService.getAllSprints().size()).isZero();
    }
    //When there is one sprint in the database, test retrieving all sprints using sprint service.
    @Test
    void whenOneSprintSaved_testGetAllSprints() {
        sprintService.saveSprint(new Sprint(projects.get(0).getId(), "Test Sprint",1, "Description",
                Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        List<Sprint> sprints = (List<Sprint>) sprintRepository.findAll();
        assertThat(sprints.size()).isEqualTo(1);
        assertThat(sprintService.getAllSprints().size()).isEqualTo(1);
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
        assertThat(sprints.size()).isEqualTo(4);
        assertThat(sprintService.getAllSprints().size()).isEqualTo(4);
    }
    //When the sprint id does not exist, test retrieving the sprint by its id.
    @Test
    void whenSprintIdNotExists_testGetSprintById() throws Exception {
        List<Sprint> sprints = (List<Sprint>) sprintRepository.findAll();
        assertThat(sprints.size()).isZero();
        Exception exception = assertThrows(Exception.class, () -> {
            sprintService.getSprintById(900000);
        });
        String expectedMessage = "Sprint not found";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
    //When the sprint id does exist, test retrieving the sprint by its id.
    @Test
    void whenSprintIdExists_testGetSprintById() throws Exception {
        sprintService.saveSprint(new Sprint(projects.get(0).getId(), "Test Sprint",1, "Description",
                Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        List<Sprint> sprints = (List<Sprint>) sprintRepository.findAll();
        int sprintId = sprints.get(0).getId();
        Sprint sprint = sprintService.getSprintById(sprintId);
        assertThat(sprint.getName()).isEqualTo("Test Sprint");
        assertThat(sprint.getLabel()).isEqualTo("Sprint 1");
        assertThat(sprint.getDescription()).isEqualTo("Description");
        assertThat(sprint.getStartDate()).isEqualTo(Timestamp.valueOf("2022-04-15 00:00:00"));
        assertThat(sprint.getEndDate()).isEqualTo(Timestamp.valueOf("2022-05-16 00:00:00"));
    }
    //When no sprints are saved to the database, test retrieving sprints by parent project id.
    @Test
    void whenNoSprintSaved_testGetByParentProjectId() {
        List<Sprint> sprints = (List<Sprint>) sprintRepository.findAll();
        assertThat(sprints.size()).isZero();
        List<Sprint> sprintList = sprintService.getByParentProjectId(projects.get(0).getId());
        assertThat(sprintList.size()).isZero();
    }
    //When one sprint is saved to the database, test retrieving sprints by parent project id.
    @Test
    void whenOneSprintSaved_testGetByParentProjectId() {
        sprintService.saveSprint(new Sprint(projects.get(0).getId(), "Test Sprint",1, "Description",
                Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        List<Sprint> sprints = (List<Sprint>) sprintRepository.findAll();
        assertThat(sprints.size()).isEqualTo(1);
        List<Sprint> sprintList = sprintService.getByParentProjectId(projects.get(0).getId());
        assertThat(sprintList.size()).isEqualTo(1);
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
        assertThat(sprints.size()).isEqualTo(4);
        List<Sprint> sprintList = sprintService.getByParentProjectId(projects.get(0).getId());
        assertThat(sprintList.size()).isEqualTo(4);
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
        assertThat(sprints.size()).isEqualTo(4);
        List<Sprint> sprintList = sprintService.getByParentProjectId(projects.get(0).getId());
        int listSize = sprintList.size();
        assertThat(listSize).isEqualTo(2);
        sprintList = sprintService.getByParentProjectId(projects.get(1).getId());
        int listSize2 = sprintList.size();
        assertThat(listSize2).isEqualTo(2);
        assertThat((listSize+listSize2)).isEqualTo(4);
    }
}
