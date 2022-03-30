package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.ProjectRepository;
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
public class ProjectServiceTest {
    @Autowired
    ProjectService projectService;

    @Autowired
    ProjectRepository projectRepository;

    @BeforeEach
    void cleanDatabase() {
        projectRepository.deleteAll();
    }

    //Test saving a project when no current projects in the database.
    @Test
    void whenNoProjects_testSaveProject() {
        List<Project> projects = (List<Project>) projectRepository.findAll();
        assertThat(projects.size()).isZero();
        projectService.saveProject(new Project("Project Name", "Test Project", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        projects = (List<Project>) projectRepository.findAll();
        assertThat(projects.size()).isEqualTo(1);
    }
    //Test saving a project when one current project in the database.
    @Test
    void whenOneProjectSaved_testSaveProject(){
        projectRepository.save(new Project("Project Name", "Test Project", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        List<Project> projects = (List<Project>) projectRepository.findAll();
        assertThat(projects.size()).isEqualTo(1);
        projectService.saveProject(new Project("Project Name", "Test Project", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        projects = (List<Project>) projectRepository.findAll();
        assertThat(projects.size()).isEqualTo(2);
    }
    //Test saving a project when many current projects in the database.
    @Test
    void whenManyProjectSaved_testSaveProject(){
        projectRepository.save(new Project("Project Name", "Test Project", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        projectRepository.save(new Project("Project Name", "Test Project", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        projectRepository.save(new Project("Project Name", "Test Project", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        projectRepository.save(new Project("Project Name", "Test Project", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        List<Project> projects = (List<Project>) projectRepository.findAll();
        assertThat(projects.size()).isEqualTo(4);
        projectService.saveProject(new Project("Project Name", "Test Project", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        projects = (List<Project>) projectRepository.findAll();
        assertThat(projects.size()).isEqualTo(5);
    }
    //Test getting all projects from the database when no projects are saved.
    @Test
    void whenNoProjectSaved_testGetAllProjects() {
        List<Project> projects = projectService.getAllProjects();
        assertThat(projects.size()).isZero();
    }
    //Test getting all projects from the database when one project are saved.
    @Test
    void whenOneProjectSaved_testGetAllProjects() {
        projectService.saveProject(new Project("Project Name", "Test Project", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        List<Project> projects = projectService.getAllProjects();
        assertThat(projects.size()).isEqualTo(1);
    }
    //Test getting all projects from the database when many projects are saved.
    @Test
    void whenManyProjectSaved_testGetAllProjects() {
        projectRepository.save(new Project("Project Name", "Test Project", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        projectRepository.save(new Project("Project Name", "Test Project", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        projectRepository.save(new Project("Project Name", "Test Project", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        projectRepository.save(new Project("Project Name", "Test Project", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        List<Project> projects = projectService.getAllProjects();
        assertThat(projects.size()).isEqualTo(4);
    }
    //Test getting one project by the projects id when the id does not exist.
    @Test
    void whenProjectIdNotExists_testGetProjectById() throws Exception {
        List<Project> projects = (List<Project>) projectRepository.findAll();
        assertThat(projects.size()).isZero();
        Exception exception = assertThrows(Exception.class, () -> {
            projectService.getProjectById(900000);
        });
        String expectedMessage = "Project not found";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
    //Test getting one project by the projects id when the id does exist.
    @Test
    void whenProjectIdExists_testGetProjectById() throws Exception {
        projectRepository.save(new Project("Project Name", "Test Project", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        List<Project> projects = (List<Project>) projectRepository.findAll();
        int projectId = projects.get(0).getId();
        assertThat(projectId).isNotNull();
        Project project = projectService.getProjectById(projectId);
        assertThat(project.getName()).isEqualTo("Project Name");
        assertThat(project.getDescription()).isEqualTo("Test Project");
        assertThat(project.getStartDate()).isEqualTo(Timestamp.valueOf("2022-04-15 00:00:00"));
        assertThat(project.getEndDate()).isEqualTo(Timestamp.valueOf("2022-05-16 00:00:00"));
    }
    //Test deleting a project from the database when no projects are saved. Should throw and exception.
    @Test
    void whenNoProjectExists_testDeleteProjectById() {
        List<Project> projects = (List<Project>) projectRepository.findAll();
        assertThat(projects.size()).isZero();
        Exception exception = assertThrows(Exception.class, () -> {
            projectService.deleteProjectById(90000);
        });
        String expectedMessage = "No project found to delete";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
    //Test deleting a project from the database when one project is saved.
    @Test
    void whenOneProjectExists_testDeleteProjectById() throws Exception {
        projectRepository.save(new Project("Project Name", "Test Project", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        List<Project> projects = (List<Project>) projectRepository.findAll();
        assertThat(projects.size()).isEqualTo(1);
        int projectId = projects.get(0).getId();
        projectService.deleteProjectById(projectId);
        projects = (List<Project>) projectRepository.findAll();
        assertThat(projects.size()).isZero();
    }
    //Test deleting a project from the database when many projects are saved.
    @Test
    void whenManyProjectsExist_testDeleteProjectById() throws Exception {
        projectRepository.save(new Project("Project Name", "Test Project", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        projectRepository.save(new Project("Project Name", "Test Project", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        projectRepository.save(new Project("Project Name", "Test Project", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        projectRepository.save(new Project("Project Name", "Test Project", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        List<Project> projects = (List<Project>) projectRepository.findAll();
        assertThat(projects.size()).isEqualTo(4);
        int projectId = projects.get(2).getId();
        projectService.deleteProjectById(projectId);
        projects = (List<Project>) projectRepository.findAll();
        assertThat(projects.size()).isEqualTo(3);
    }
    /*Test for delete by 0 issue in case it occurs in future. Checks that the first project added to database
    does not have a project id of 0. See GitLab wiki Delete by 0 issue for more context.
     */
    @Test
    void whenFirstProjectSaved_testNotHasIdZero() throws Exception {
        List<Project> projects = (List<Project>) projectRepository.findAll();
        assertThat(projects.size()).isZero();
        projectRepository.save(new Project("Project Name", "Test Project", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        projects = (List<Project>) projectRepository.findAll();
        assertThat(projects.size()).isEqualTo(1);
        Exception exception = assertThrows(Exception.class, () -> {
            projectService.getProjectById(0);
        });
        String expectedMessage = "Project not found";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
        assertThat(projects.get(0).getId()).isNotEqualTo(0);
    }
}
