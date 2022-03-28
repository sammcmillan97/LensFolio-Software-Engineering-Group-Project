package nz.ac.canterbury.seng302.portfolio;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.ProjectRepository;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.model.SprintRepository;
import nz.ac.canterbury.seng302.portfolio.service.SprintService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Date;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class SprintsIntegrationTests {
    @Autowired
    private SprintRepository sprintRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private SprintService sprintService;

    private Project testProject1;
    private Project testProject2;
    private Sprint testSprint1;
    private Sprint testSprint2;
    private Sprint testSprint3;


    @BeforeEach
    void setupDataset() {
        projectRepository.deleteAll();
        sprintRepository.deleteAll();

        // Test Project 1
        testProject1 = new Project("Project1", "Test Project 1", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16"));
        testProject1 = projectRepository.save(testProject1);

        testSprint1 = new Sprint(testProject1.getId(), "Test Sprint 1", 1, "Test sprint 1", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16"));
        testSprint2 = new Sprint(testProject1.getId(), "Test Sprint 1", 2, "Test sprint 1", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16"));
        testSprint1 = sprintRepository.save(testSprint1);
        testSprint2 = sprintRepository.save(testSprint2);

        // Test Project 2
        testProject2 = new Project("Project2", "Test Project 2", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16"));
        testProject2 = projectRepository.save(testProject2);

        testSprint3 = new Sprint(testProject2.getId(), "Test Sprint 1", 1, "Test sprint 1", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16"));
        testSprint3 = sprintRepository.save(testSprint3);
    }

    @Test
    void whenNoSprints_testAddSprint() {

    }

    @Test
    void whenExistingSprints_testAddSprint() {

    }

    @Test
    void whenParentProjectIdInvalid_testAddSprint() {

    }

    @Test
    void whenNoSprints_testEditSprint() {

    }

    @Test
    void whenExistingSprints_testEditSprint() {

    }

    @Test
    void whenParentProjectIdInvalid_testEditSprint() {

    }

}