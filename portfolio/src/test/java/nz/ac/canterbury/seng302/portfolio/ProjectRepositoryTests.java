package nz.ac.canterbury.seng302.portfolio;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.ProjectRepository;
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
public class ProjectRepositoryTests {

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
    void findProjects() {
        Project project1 = new Project("Project1", "Test Project", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16"));
        Project project2 = new Project("Project1", "Test Project", Date.valueOf("2022-03-22"), Date.valueOf("2022-04-01"));
        List<Project> projects = new ArrayList<>();
        projects.add(project1);
        projects.add(project2);
        projectRepository.saveAll(projects);
        List<Project> projectsFromDatabase = StreamSupport.stream(projectRepository.findAll().spliterator(), false).toList();
        assertThat(projectsFromDatabase.get(0).getId()).isEqualTo(projects.get(0).getId());
        assertThat(projectsFromDatabase.get(0).getName()).isEqualTo(projects.get(0).getName());
        assertThat(projectsFromDatabase.get(0).getDescription()).isEqualTo(projects.get(0).getDescription());
        assertThat(projectsFromDatabase.get(0).getStartDateString()).isEqualTo(projects.get(0).getStartDateString());
        assertThat(projectsFromDatabase.get(0).getEndDateString()).isEqualTo(projects.get(0).getEndDateString());
        assertThat(projectsFromDatabase.get(1).getId()).isEqualTo(projects.get(1).getId());
        assertThat(projectsFromDatabase.get(1).getName()).isEqualTo(projects.get(1).getName());
        assertThat(projectsFromDatabase.get(1).getDescription()).isEqualTo(projects.get(1).getDescription());
        assertThat(projectsFromDatabase.get(1).getStartDateString()).isEqualTo(projects.get(1).getStartDateString());
        assertThat(projectsFromDatabase.get(1).getEndDateString()).isEqualTo(projects.get(1).getEndDateString());
    }

    @Test
    void findProjectById() {
        Project project1 = new Project("Project1", "Test Project", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16"));
        Project project2 = new Project("Project1", "Test Project", Date.valueOf("2022-03-22"), Date.valueOf("2022-04-01"));
        List<Project> projects = new ArrayList<Project>();
        projects.add(project1);
        projects.add(project2);
        projectRepository.saveAll(projects);
        Project retrievedProject1 = projectRepository.findById(projects.get(0).getId());
        Project retrievedProject2 = projectRepository.findById(projects.get(1).getId());

        assertThat(retrievedProject1).isNotNull();
        assertThat(retrievedProject1.getId()).isEqualTo(projects.get(0).getId());
        assertThat(retrievedProject1.getName()).isEqualTo(projects.get(0).getName());
        assertThat(retrievedProject1.getDescription()).isEqualTo(projects.get(0).getDescription());
        assertThat(retrievedProject1.getStartDateString()).isEqualTo(projects.get(0).getStartDateString());
        assertThat(retrievedProject1.getEndDateString()).isEqualTo(projects.get(0).getEndDateString());

        assertThat(retrievedProject2).isNotNull();
        assertThat(retrievedProject2.getId()).isEqualTo(projects.get(1).getId());
        assertThat(retrievedProject2.getName()).isEqualTo(projects.get(1).getName());
        assertThat(retrievedProject2.getDescription()).isEqualTo(projects.get(1).getDescription());
        assertThat(retrievedProject2.getStartDateString()).isEqualTo(projects.get(1).getStartDateString());
        assertThat(retrievedProject2.getEndDateString()).isEqualTo(projects.get(1).getEndDateString());

    }

    @Test
    void updateSprint() {
        Project project1 = new Project("Project1", "Test Project", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16"));

        projectRepository.save(project1);

        // Check that the project was inserted correctly
        Project retrievedProject = projectRepository.findById(project1.getId());
        assertThat(retrievedProject).isNotNull();
        assertThat(retrievedProject.getId()).isEqualTo(project1.getId());
        assertThat(retrievedProject.getName()).isEqualTo(project1.getName());
        assertThat(retrievedProject.getDescription()).isEqualTo(project1.getDescription());
        assertThat(retrievedProject.getStartDateString()).isEqualTo(project1.getStartDateString());
        assertThat(retrievedProject.getEndDateString()).isEqualTo(project1.getEndDateString());

//        Project newProject = new Project(project1.getId(), "Changed Project Name", project1.getDescription(), Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16"));
//        projectRepository.save(newProject);

        // Use original project id to fetch updated sprint to confirm it's using the same id
        // Check that the project was updated correctly
//        retrievedProject = projectRepository.findById(project1.getId());
//        assertThat(retrievedProject).isNotNull();
//        assertThat(retrievedProject.getId()).isEqualTo(newProject.getId());
//        assertThat(retrievedProject.getName()).isEqualTo(newProject.getName());
//        assertThat(retrievedProject.getDescription()).isEqualTo(newProject.getDescription());
//        assertThat(retrievedProject.getStartDateString()).isEqualTo(newProject.getStartDateString());
//        assertThat(retrievedProject.getEndDateString()).isEqualTo(newProject.getEndDateString());

    }
}
