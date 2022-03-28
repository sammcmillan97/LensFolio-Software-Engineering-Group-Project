package nz.ac.canterbury.seng302.portfolio;


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
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
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
    void findProjects() {
        Project project1 = new Project("Project1", "Test Project", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16"));
        Sprint sprint1 = new Sprint(project1.getId(), "Sprint Name", 1, "This is a sprint", Date.valueOf("2022-04-15"), Date.valueOf("2022-04-29"));
        Sprint sprint2 = new Sprint(project1.getId(), "Sprint Name 2", 2, "This is a sprint", Date.valueOf("2022-04-30"), Date.valueOf("2022-05-15"));
        List<Sprint> sprints = new ArrayList<>();
        sprints.add(sprint1);
        sprints.add(sprint2);
        projectRepository.save(project1);
        sprintRepository.saveAll(sprints);

        List<Sprint> sprintsFromDatabase = StreamSupport.stream(sprintRepository.findAll().spliterator(), false).toList();
        assertThat(sprintsFromDatabase.get(0)).isNotNull();
        assertThat(sprintsFromDatabase.get(0).getId()).isEqualTo(sprints.get(0).getId());
        assertThat(sprintsFromDatabase.get(0).getName()).isEqualTo(sprints.get(0).getName());
        assertThat(sprintsFromDatabase.get(0).getNumber()).isEqualTo(sprints.get(0).getNumber());
        assertThat(sprintsFromDatabase.get(0).getDescription()).isEqualTo(sprints.get(0).getDescription());
        assertThat(sprintsFromDatabase.get(0).getStartDate()).isEqualTo(sprints.get(0).getStartDate());
        assertThat(sprintsFromDatabase.get(0).getEndDate()).isEqualTo(sprints.get(0).getEndDate());

        assertThat(sprintsFromDatabase.get(1)).isNotNull();
        assertThat(sprintsFromDatabase.get(1).getId()).isEqualTo(sprints.get(1).getId());
        assertThat(sprintsFromDatabase.get(1).getName()).isEqualTo(sprints.get(1).getName());
        assertThat(sprintsFromDatabase.get(1).getNumber()).isEqualTo(sprints.get(1).getNumber());
        assertThat(sprintsFromDatabase.get(1).getDescription()).isEqualTo(sprints.get(1).getDescription());
        assertThat(sprintsFromDatabase.get(1).getStartDate()).isEqualTo(sprints.get(1).getStartDate());
        assertThat(sprintsFromDatabase.get(1).getEndDate()).isEqualTo(sprints.get(1).getEndDate());
    }

    @Test
    void findSprintById() {
        Project project1 = new Project("Project1", "Test Project", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16"));
        Sprint sprint1 = new Sprint(project1.getId(), "Sprint Name", 1, "This is a sprint", Date.valueOf("2022-04-15"), Date.valueOf("2022-04-29"));
        Sprint sprint2 = new Sprint(project1.getId(), "Sprint Name 2", 2, "This is a sprint", Date.valueOf("2022-04-30"), Date.valueOf("2022-05-15"));
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
        assertThat(retrievedSprint1.getNumber()).isEqualTo(sprints.get(0).getNumber());
        assertThat(retrievedSprint1.getDescription()).isEqualTo(sprints.get(0).getDescription());
        assertThat(retrievedSprint1.getStartDate()).isEqualTo(sprints.get(0).getStartDate());
        assertThat(retrievedSprint1.getEndDate()).isEqualTo(sprints.get(0).getEndDate());

        assertThat(retrievedSprint2).isNotNull();
        assertThat(retrievedSprint2.getId()).isEqualTo(sprints.get(1).getId());
        assertThat(retrievedSprint2.getName()).isEqualTo(sprints.get(1).getName());
        assertThat(retrievedSprint2.getNumber()).isEqualTo(sprints.get(1).getNumber());
        assertThat(retrievedSprint2.getDescription()).isEqualTo(sprints.get(1).getDescription());
        assertThat(retrievedSprint2.getStartDate()).isEqualTo(sprints.get(1).getStartDate());
        assertThat(retrievedSprint2.getEndDate()).isEqualTo(sprints.get(1).getEndDate());
    }

    @Test
    void updateSprint() {
        Project project1 = new Project("Project1", "Test Project", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16"));
        Sprint sprint1 = new Sprint(project1.getId(), "Sprint Name", 1, "This is a sprint", Date.valueOf("2022-04-15"), Date.valueOf("2022-04-29"));
        projectRepository.save(project1);
        sprintRepository.save(sprint1);

        // Check that the sprint was inserted correctly
        Sprint retrievedSprint = sprintRepository.findById(sprint1.getId());
        assertThat(retrievedSprint).isNotNull();
        assertThat(retrievedSprint.getId()).isEqualTo(sprint1.getId());
        assertThat(retrievedSprint.getName()).isEqualTo(sprint1.getName());
        assertThat(retrievedSprint.getNumber()).isEqualTo(sprint1.getNumber());
        assertThat(retrievedSprint.getDescription()).isEqualTo(sprint1.getDescription());
        assertThat(retrievedSprint.getStartDate()).isEqualTo(sprint1.getStartDate());
        assertThat(retrievedSprint.getEndDate()).isEqualTo(sprint1.getEndDate());

//        Sprint newSprint = new Sprint( sprint1.getParentProjectId(), sprint1.getLabel(), "Changed Sprint Name", sprint1.getDescription(), Date.valueOf("2022-04-15"), Date.valueOf("2022-04-29"));
//        sprintRepository.save(newSprint);
//
//        // Use original sprint id to fetch updated sprint to confirm it's using the same id
//        // Check that the sprint was updated correctly
//        retrievedSprint = sprintRepository.findById(sprint1.getId());
//        assertThat(retrievedSprint).isNotNull();
//        assertThat(retrievedSprint.getId()).isEqualTo(newSprint.getId());
//        assertThat(retrievedSprint.getName()).isEqualTo(newSprint.getName());
//        assertThat(retrievedSprint.getNumber()).isEqualTo(newSprint.getNumber());
//        assertThat(retrievedSprint.getDescription()).isEqualTo(newSprint.getDescription());
//        assertThat(retrievedSprint.getStartDate()).isEqualTo(newSprint.getStartDate());
//        assertThat(retrievedSprint.getEndDate()).isEqualTo(newSprint.getEndDate());

    }


}
