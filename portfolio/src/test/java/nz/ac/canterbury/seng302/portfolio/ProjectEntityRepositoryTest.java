package nz.ac.canterbury.seng302.portfolio;

import nz.ac.canterbury.seng302.portfolio.entities.ProjectEntity;
import nz.ac.canterbury.seng302.portfolio.repositories.ProjectEntityRepository;
import org.junit.jupiter.api.AfterEach;
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
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.AssertionsForClassTypes.*;

@DataJpaTest
public class ProjectEntityRepositoryTest {

    @Autowired
    private DataSource dataSource;
    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private EntityManager entityManager;
    @Autowired private ProjectEntityRepository projectEntityRepository;

    @BeforeEach
    void cleanDatabase() {
        projectEntityRepository.deleteAll();
    }

    @Test
    void injectedComponentsAreNotNull() {
        assertThat(dataSource).isNotNull();
        assertThat(jdbcTemplate).isNotNull();
        assertThat(entityManager).isNotNull();
        assertThat(projectEntityRepository).isNotNull();
    }

    @Test
    void findUsers() {
        ProjectEntity project1 = new ProjectEntity("Project1", "Test Project", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16"));
        ProjectEntity project2 = new ProjectEntity("Project1", "Test Project", Date.valueOf("2022-03-22"), Date.valueOf("2022-04-01"));
        List<ProjectEntity> projects = new ArrayList<>();
        projects.add(project1);
        projects.add(project2);
        projectEntityRepository.saveAll(projects);
        List<ProjectEntity> projectsFromDatabase = StreamSupport.stream(projectEntityRepository.findAll().spliterator(), false).toList();
        assertThat(projectsFromDatabase.get(0).getProject_id()).isEqualTo(projects.get(0).getProject_id());
        assertThat(projectsFromDatabase.get(0).getProject_name()).isEqualTo(projects.get(0).getProject_name());
        assertThat(projectsFromDatabase.get(0).getDescription()).isEqualTo(projects.get(0).getDescription());
        assertThat(projectsFromDatabase.get(0).getStart_date()).isEqualTo(projects.get(0).getStart_date());
        assertThat(projectsFromDatabase.get(0).getEnd_date()).isEqualTo(projects.get(0).getEnd_date());
        assertThat(projectsFromDatabase.get(1).getProject_id()).isEqualTo(projects.get(1).getProject_id());
        assertThat(projectsFromDatabase.get(1).getProject_name()).isEqualTo(projects.get(1).getProject_name());
        assertThat(projectsFromDatabase.get(1).getDescription()).isEqualTo(projects.get(1).getDescription());
        assertThat(projectsFromDatabase.get(1).getStart_date()).isEqualTo(projects.get(1).getStart_date());
        assertThat(projectsFromDatabase.get(1).getEnd_date()).isEqualTo(projects.get(1).getEnd_date());
    }

    @Test
    void findUserById() {
        ProjectEntity project1 = new ProjectEntity("Project1", "Test Project", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16"));
        ProjectEntity project2 = new ProjectEntity("Project1", "Test Project", Date.valueOf("2022-03-22"), Date.valueOf("2022-04-01"));
        List<ProjectEntity> projects = new ArrayList<ProjectEntity>();
        projects.add(project1);
        projects.add(project2);
        projectEntityRepository.saveAll(projects);
        ProjectEntity project = projectEntityRepository.findById(project2.getProject_id()).orElse(null);
        assertThat(project).isNotNull();
        assertThat(project.getProject_id()).isEqualTo(projects.get(1).getProject_id());

    }
}
