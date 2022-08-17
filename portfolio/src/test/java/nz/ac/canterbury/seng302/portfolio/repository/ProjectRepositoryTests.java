package nz.ac.canterbury.seng302.portfolio.repository;

import nz.ac.canterbury.seng302.portfolio.model.Project;
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
class ProjectRepositoryTests {

    @Autowired
    private DataSource dataSource;
    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private EntityManager entityManager;
    @Autowired private ProjectRepository projectRepository;

    @BeforeEach
    void cleanDatabase() {
        projectRepository.deleteAll();
    }

    @Test
    void injectedComponentsAreNotNull() {
        assertThat(dataSource).isNotNull();
        assertThat(jdbcTemplate).isNotNull();
        assertThat(entityManager).isNotNull();
        assertThat(projectRepository).isNotNull();
    }

    @Test
    void givenMultipleProjectsExist_findAllProjects() {
        Project project1 = new Project("Project1", "Test Project", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16"));
        Project project2 = new Project("Project1", "Test Project", Date.valueOf("2022-03-22"), Date.valueOf("2022-04-01"));
        List<Project> projects = new ArrayList<>();
        projects.add(project1);
        projects.add(project2);
        projectRepository.saveAll(projects);
        List<Project> projectsFromDatabase = StreamSupport.stream(projectRepository.findAll().spliterator(), false).toList();
        Project retrievedProject1 = projectsFromDatabase.get(0);
        Project retrievedProject2 = projectsFromDatabase.get(1);

        assertEquals(project1, retrievedProject1);
        assertEquals(project2, retrievedProject2);
    }

    @Test
    void givenValidId_findProjectById() {
        Project project1 = new Project("Project1", "Test Project", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16"));
        Project project2 = new Project("Project1", "Test Project", Date.valueOf("2022-03-22"), Date.valueOf("2022-04-01"));
        List<Project> projects = new ArrayList<Project>();
        projects.add(project1);
        projects.add(project2);
        projectRepository.saveAll(projects);
        Project retrievedProject1 = projectRepository.findById(projects.get(0).getId());
        Project retrievedProject2 = projectRepository.findById(projects.get(1).getId());

        assertEquals(project1, retrievedProject1);
        assertEquals(project2, retrievedProject2);

    }

    @Test
    void givenValidDetails_addProjectViaRepository() {
        Project project1 = new Project("Project1", "Test Project", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16"));

        projectRepository.save(project1);

        // Check that the project was inserted correctly
        Project retrievedProject1 = projectRepository.findById(project1.getId());
        assertEquals(project1, retrievedProject1);

    }
}
