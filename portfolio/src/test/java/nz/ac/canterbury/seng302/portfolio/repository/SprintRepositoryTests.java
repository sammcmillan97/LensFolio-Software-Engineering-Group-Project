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

@DataJpaTest
public class SprintRepositoryTests {

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
    void findAllSprints() {
        Project project1 = new Project("Project1", "Test Project", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16"));
        Sprint sprint1 = new Sprint(project1.getId(), "Sprint 1", 1, "This is a sprint", Date.valueOf("2022-04-15"), Date.valueOf("2022-04-29"));
        Sprint sprint2 = new Sprint(project1.getId(), "Sprint 2", 2, "This is a sprint", Date.valueOf("2022-04-30"), Date.valueOf("2022-05-15"));
        List<Sprint> sprints = new ArrayList<>();
        sprints.add(sprint1);
        sprints.add(sprint2);
        projectRepository.save(project1);
        sprintRepository.saveAll(sprints);

        List<Sprint> sprintsFromDatabase = StreamSupport.stream(sprintRepository.findAll().spliterator(), false).toList();
        assertThat(sprintsFromDatabase.get(0)).isNotNull();
        assertThat(sprintsFromDatabase.get(0).getId()).isEqualTo(sprints.get(0).getId());
        assertThat(sprintsFromDatabase.get(0).getName()).isEqualTo(sprints.get(0).getName());
        assertThat(sprintsFromDatabase.get(0).getLabel()).isEqualTo(sprints.get(0).getLabel());
        assertThat(sprintsFromDatabase.get(0).getDescription()).isEqualTo(sprints.get(0).getDescription());
        assertThat(sprintsFromDatabase.get(0).getStartDate()).isEqualTo(sprints.get(0).getStartDate());
        assertThat(sprintsFromDatabase.get(0).getEndDate()).isEqualTo(sprints.get(0).getEndDate());

        assertThat(sprintsFromDatabase.get(1)).isNotNull();
        assertThat(sprintsFromDatabase.get(1).getId()).isEqualTo(sprints.get(1).getId());
        assertThat(sprintsFromDatabase.get(1).getName()).isEqualTo(sprints.get(1).getName());
        assertThat(sprintsFromDatabase.get(1).getLabel()).isEqualTo(sprints.get(1).getLabel());
        assertThat(sprintsFromDatabase.get(1).getDescription()).isEqualTo(sprints.get(1).getDescription());
        assertThat(sprintsFromDatabase.get(1).getStartDate()).isEqualTo(sprints.get(1).getStartDate());
        assertThat(sprintsFromDatabase.get(1).getEndDate()).isEqualTo(sprints.get(1).getEndDate());
    }

    @Test
    void findSprintById() {
        Project project1 = new Project("Project1", "Test Project", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16"));
        Sprint sprint1 = new Sprint(project1.getId(), "Sprint 1", 1, "This is a sprint", Date.valueOf("2022-04-15"), Date.valueOf("2022-04-29"));
        Sprint sprint2 = new Sprint(project1.getId(), "Sprint 2", 2, "This is a sprint", Date.valueOf("2022-04-30"), Date.valueOf("2022-05-15"));
        List<Sprint> sprints = new ArrayList<>();
        sprints.add(sprint1);
        sprints.add(sprint2);
        projectRepository.save(project1);
        sprintRepository.saveAll(sprints);

        Sprint retrievedSprint1 = sprintRepository.findById(sprints.get(0).getId());
        Sprint retrievedSprint2 = sprintRepository.findById(sprints.get(1).getId());

        assertThat(retrievedSprint1).isNotNull();
        assertThat(retrievedSprint1.getId()).isEqualTo(sprints.get(0).getId());
        assertThat(retrievedSprint1.getName()).isEqualTo(sprints.get(0).getName());
        assertThat(retrievedSprint1.getLabel()).isEqualTo(sprints.get(0).getLabel());
        assertThat(retrievedSprint1.getDescription()).isEqualTo(sprints.get(0).getDescription());
        assertThat(retrievedSprint1.getStartDate()).isEqualTo(sprints.get(0).getStartDate());
        assertThat(retrievedSprint1.getEndDate()).isEqualTo(sprints.get(0).getEndDate());

        assertThat(retrievedSprint2).isNotNull();
        assertThat(retrievedSprint2.getId()).isEqualTo(sprints.get(1).getId());
        assertThat(retrievedSprint2.getName()).isEqualTo(sprints.get(1).getName());
        assertThat(retrievedSprint2.getLabel()).isEqualTo(sprints.get(1).getLabel());
        assertThat(retrievedSprint2.getDescription()).isEqualTo(sprints.get(1).getDescription());
        assertThat(retrievedSprint2.getStartDate()).isEqualTo(sprints.get(1).getStartDate());
        assertThat(retrievedSprint2.getEndDate()).isEqualTo(sprints.get(1).getEndDate());
    }

    @Test
    void addSprintViaRepository() {
        Project project1 = new Project("Project1", "Test Project", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16"));
        Sprint sprint1 = new Sprint(project1.getId(), "Sprint 1", 1, "This is a sprint", Date.valueOf("2022-04-15"), Date.valueOf("2022-04-29"));
        projectRepository.save(project1);
        sprintRepository.save(sprint1);

        // Check that the sprint was inserted correctly
        Sprint retrievedSprint = sprintRepository.findById(sprint1.getId());
        assertThat(retrievedSprint).isNotNull();
        assertThat(retrievedSprint.getId()).isEqualTo(sprint1.getId());
        assertThat(retrievedSprint.getName()).isEqualTo(sprint1.getName());
        assertThat(retrievedSprint.getLabel()).isEqualTo(sprint1.getLabel());
        assertThat(retrievedSprint.getDescription()).isEqualTo(sprint1.getDescription());
        assertThat(retrievedSprint.getStartDate()).isEqualTo(sprint1.getStartDate());
        assertThat(retrievedSprint.getEndDate()).isEqualTo(sprint1.getEndDate());

    }


}
