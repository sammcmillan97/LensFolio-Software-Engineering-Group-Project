package nz.ac.canterbury.seng302.portfolio.repository;


import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.ProjectRepository;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.model.SprintRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class SprintRepositoryTests {

    @Autowired
    private DataSource dataSource;
    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private EntityManager entityManager;
    @Autowired private SprintRepository sprintRepository;
    @Autowired private ProjectRepository projectRepository;

    @BeforeEach
    void cleanDatabase() {
        sprintRepository.deleteAll();
    }

    @Test
    void injectedComponentsAreNotNull() {
        assertThat(dataSource).isNotNull();
        assertThat(jdbcTemplate).isNotNull();
        assertThat(entityManager).isNotNull();
        assertThat(sprintRepository).isNotNull();
    }

    @Test
    void givenMulipleSprintsExist_findAllSprints() {
        Project project1 = new Project("Project1", "Test Project", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16"));
        Sprint sprint1 = new Sprint(project1.getId(), "Sprint 1", "This is a sprint", Date.valueOf("2022-04-15"), Date.valueOf("2022-04-29"));
        Sprint sprint2 = new Sprint(project1.getId(), "Sprint 2", "This is a sprint", Date.valueOf("2022-04-30"), Date.valueOf("2022-05-15"));
        List<Sprint> sprints = new ArrayList<>();
        sprints.add(sprint1);
        sprints.add(sprint2);
        projectRepository.save(project1);
        sprintRepository.saveAll(sprints);

        List<Sprint> sprintsFromDatabase = StreamSupport.stream(sprintRepository.findAll().spliterator(), false).toList();

        Sprint retrievedSprint1 = sprintsFromDatabase.get(0);
        Sprint retrievedSprint2 = sprintsFromDatabase.get(1);

        assertEquals(sprint1, retrievedSprint1);
        assertEquals(sprint2, retrievedSprint2);
    }

    @Test
    void givenValidId_findSprintById() {
        Project project1 = new Project("Project1", "Test Project", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16"));
        Sprint sprint1 = new Sprint(project1.getId(), "Sprint 1", "This is a sprint", Date.valueOf("2022-04-15"), Date.valueOf("2022-04-29"));
        Sprint sprint2 = new Sprint(project1.getId(), "Sprint 2", "This is a sprint", Date.valueOf("2022-04-30"), Date.valueOf("2022-05-15"));
        List<Sprint> sprints = new ArrayList<>();
        sprints.add(sprint1);
        sprints.add(sprint2);
        projectRepository.save(project1);
        sprintRepository.saveAll(sprints);

        Sprint retrievedSprint1 = sprintRepository.findById(sprints.get(0).getId());
        Sprint retrievedSprint2 = sprintRepository.findById(sprints.get(1).getId());

        assertEquals(sprint1, retrievedSprint1);
        assertEquals(sprint2, retrievedSprint2);
    }

    @Test
    void givenValidDetails_addSprintViaRepository() {
        Project project1 = new Project("Project1", "Test Project", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16"));
        Sprint sprint1 = new Sprint(project1.getId(), "Sprint 1", "This is a sprint", Date.valueOf("2022-04-15"), Date.valueOf("2022-04-29"));
        projectRepository.save(project1);
        sprintRepository.save(sprint1);

        // Check that the sprint was inserted correctly
        Sprint retrievedSprint1 = sprintRepository.findById(sprint1.getId());
        assertEquals(sprint1, retrievedSprint1);

    }


}
