package nz.ac.canterbury.seng302.portfolio;

import nz.ac.canterbury.seng302.portfolio.entities.ProjectEntity;
import nz.ac.canterbury.seng302.portfolio.entities.SprintEntity;
import nz.ac.canterbury.seng302.portfolio.repositories.ProjectEntityRepository;
import nz.ac.canterbury.seng302.portfolio.repositories.SprintEntityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.AssertionsForClassTypes.*;

@DataJpaTest
public class SprintEntityRepositoryTests {

    @Autowired
    private DataSource dataSource;
    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private EntityManager entityManager;
    @Autowired private SprintEntityRepository sprintEntityRepository;
    @Autowired private ProjectEntityRepository projectEntityRepository;

    @BeforeEach
    void cleanDatabase() {
        sprintEntityRepository.deleteAll();
    }

    @Test
    void injectedComponentsAreNotNull() {
        assertThat(dataSource).isNotNull();
        assertThat(jdbcTemplate).isNotNull();
        assertThat(entityManager).isNotNull();
        assertThat(sprintEntityRepository).isNotNull();
    }

    @Test
    void findProjects() {
        ProjectEntity project1 = new ProjectEntity("Project1", "Test Project", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16"));
        SprintEntity sprint1 = new SprintEntity(project1, "Sprint 1", "Sprint Name", "This is a sprint", Date.valueOf("2022-04-15"), Date.valueOf("2022-04-29"));
        SprintEntity sprint2 = new SprintEntity(project1, "Sprint 2", "Sprint Name 2", "This is a sprint", Date.valueOf("2022-04-30"), Date.valueOf("2022-05-15"));
        List<SprintEntity> sprints = new ArrayList<>();
        sprints.add(sprint1);
        sprints.add(sprint2);
        projectEntityRepository.save(project1);
        sprintEntityRepository.saveAll(sprints);

        List<SprintEntity> sprintsFromDatabase = StreamSupport.stream(sprintEntityRepository.findAll().spliterator(), false).toList();
        assertThat(sprintsFromDatabase.get(0)).isNotNull();
        assertThat(sprintsFromDatabase.get(0).getSprintId()).isEqualTo(sprints.get(0).getSprintId());
        assertThat(sprintsFromDatabase.get(0).getSprintName()).isEqualTo(sprints.get(0).getSprintName());
        assertThat(sprintsFromDatabase.get(0).getSprintLabel()).isEqualTo(sprints.get(0).getSprintLabel());
        assertThat(sprintsFromDatabase.get(0).getDescription()).isEqualTo(sprints.get(0).getDescription());
        assertThat(sprintsFromDatabase.get(0).getStartDate()).isEqualTo(sprints.get(0).getStartDate());
        assertThat(sprintsFromDatabase.get(0).getEndDate()).isEqualTo(sprints.get(0).getEndDate());

        assertThat(sprintsFromDatabase.get(1)).isNotNull();
        assertThat(sprintsFromDatabase.get(1).getSprintId()).isEqualTo(sprints.get(1).getSprintId());
        assertThat(sprintsFromDatabase.get(1).getSprintName()).isEqualTo(sprints.get(1).getSprintName());
        assertThat(sprintsFromDatabase.get(1).getSprintLabel()).isEqualTo(sprints.get(1).getSprintLabel());
        assertThat(sprintsFromDatabase.get(1).getDescription()).isEqualTo(sprints.get(1).getDescription());
        assertThat(sprintsFromDatabase.get(1).getStartDate()).isEqualTo(sprints.get(1).getStartDate());
        assertThat(sprintsFromDatabase.get(1).getEndDate()).isEqualTo(sprints.get(1).getEndDate());
    }

    @Test
    void findSprintById() {
        ProjectEntity project1 = new ProjectEntity("Project1", "Test Project", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16"));
        SprintEntity sprint1 = new SprintEntity(project1, "Sprint 1", "Sprint Name", "This is a sprint", Date.valueOf("2022-04-15"), Date.valueOf("2022-04-29"));
        SprintEntity sprint2 = new SprintEntity(project1, "Sprint 2", "Sprint Name 2", "This is a sprint", Date.valueOf("2022-04-30"), Date.valueOf("2022-05-15"));
        List<SprintEntity> sprints = new ArrayList<>();
        sprints.add(sprint1);
        sprints.add(sprint2);
        projectEntityRepository.save(project1);
        sprintEntityRepository.saveAll(sprints);

        SprintEntity retrievedSprint1 = sprintEntityRepository.findById(sprints.get(0).getSprintId()).orElse(null);
        SprintEntity retrievedSprint2 = sprintEntityRepository.findById(sprints.get(1).getSprintId()).orElse(null);

        assertThat(retrievedSprint1).isNotNull();
        assertThat(retrievedSprint1.getSprintId()).isEqualTo(sprints.get(0).getSprintId());
        assertThat(retrievedSprint1.getSprintName()).isEqualTo(sprints.get(0).getSprintName());
        assertThat(retrievedSprint1.getSprintLabel()).isEqualTo(sprints.get(0).getSprintLabel());
        assertThat(retrievedSprint1.getDescription()).isEqualTo(sprints.get(0).getDescription());
        assertThat(retrievedSprint1.getStartDate()).isEqualTo(sprints.get(0).getStartDate());
        assertThat(retrievedSprint1.getEndDate()).isEqualTo(sprints.get(0).getEndDate());

        assertThat(retrievedSprint2).isNotNull();
        assertThat(retrievedSprint2.getSprintId()).isEqualTo(sprints.get(1).getSprintId());
        assertThat(retrievedSprint2.getSprintName()).isEqualTo(sprints.get(1).getSprintName());
        assertThat(retrievedSprint2.getSprintLabel()).isEqualTo(sprints.get(1).getSprintLabel());
        assertThat(retrievedSprint2.getDescription()).isEqualTo(sprints.get(1).getDescription());
        assertThat(retrievedSprint2.getStartDate()).isEqualTo(sprints.get(1).getStartDate());
        assertThat(retrievedSprint2.getEndDate()).isEqualTo(sprints.get(1).getEndDate());
    }


}
