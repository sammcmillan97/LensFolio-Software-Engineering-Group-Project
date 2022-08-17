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

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureTestDatabase
@SpringBootTest
class ProjectServiceTest {
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
        assertEquals(0, projects.size());
        projectService.saveProject(new Project("Project Name", "Test Project", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        projects = (List<Project>) projectRepository.findAll();
        assertEquals(1, projects.size());
    }
    //Test saving a project when one current project in the database.
    @Test
    void whenOneProjectSaved_testSaveProject(){
        projectRepository.save(new Project("Project Name", "Test Project", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        List<Project> projects = (List<Project>) projectRepository.findAll();
        assertEquals(1, projects.size());
        projectService.saveProject(new Project("Project Name", "Test Project", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        projects = (List<Project>) projectRepository.findAll();
        assertEquals(2, projects.size());
    }
    //Test saving a project when many current projects in the database.
    @Test
    void whenManyProjectSaved_testSaveProject(){
        projectRepository.save(new Project("Project Name", "Test Project", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        projectRepository.save(new Project("Project Name", "Test Project", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        projectRepository.save(new Project("Project Name", "Test Project", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        projectRepository.save(new Project("Project Name", "Test Project", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        List<Project> projects = (List<Project>) projectRepository.findAll();
        assertEquals(4, projects.size());
        projectService.saveProject(new Project("Project Name", "Test Project", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        projects = (List<Project>) projectRepository.findAll();
        assertEquals(5, projects.size());
    }
    //Test getting all projects from the database when no projects are saved.
    @Test
    void whenNoProjectSaved_testGetAllProjects() {
        List<Project> projects = projectService.getAllProjects();
        assertEquals(0, projects.size());
    }

    //Test getting all projects from the database when one project are saved.
    @Test
    void whenOneProjectSaved_testGetAllProjects() {
        projectService.saveProject(new Project("Project Name", "Test Project", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        List<Project> projects = projectService.getAllProjects();
        assertEquals(1, projects.size());
    }
    //Test getting all projects from the database when many projects are saved.
    @Test
    void whenManyProjectSaved_testGetAllProjects() {
        projectRepository.save(new Project("Project Name", "Test Project", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        projectRepository.save(new Project("Project Name", "Test Project", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        projectRepository.save(new Project("Project Name", "Test Project", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        projectRepository.save(new Project("Project Name", "Test Project", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        List<Project> projects = projectService.getAllProjects();
        assertEquals(4, projects.size());
    }
    //Test getting one project by the projects id when the id does not exist.
    @Test
    void whenProjectIdNotExists_testGetProjectById() {
        List<Project> projects = (List<Project>) projectRepository.findAll();
        assertEquals(0, projects.size());
        Exception exception = assertThrows(Exception.class, () -> projectService.getProjectById(900000));
        String expectedMessage = "Project not found";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
    //Test getting one project by the projects id when the id does exist.
    @Test
    void whenProjectIdExists_testGetProjectById() {
        projectRepository.save(new Project("Project Name", "Test Project", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        List<Project> projects = (List<Project>) projectRepository.findAll();
        int projectId = projects.get(0).getId();
        Project project = projectService.getProjectById(projectId);
        assertEquals("Project Name", project.getName());
        assertEquals("Test Project", project.getDescription());
        assertEquals(Timestamp.valueOf("2022-04-15 00:00:00"), project.getStartDate());
        assertEquals(Timestamp.valueOf("2022-05-16 00:00:00"), project.getEndDate());
    }
    //Test deleting a project from the database when no projects are saved. Should throw and exception.
    @Test
    void whenNoProjectExists_testDeleteProjectById() {
        List<Project> projects = (List<Project>) projectRepository.findAll();
        assertEquals(0, projects.size());
        Exception exception = assertThrows(Exception.class, () -> projectService.deleteProjectById(90000));
        String expectedMessage = "No project found to delete";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
    //Test deleting a project from the database when one project is saved.
    @Test
    void whenOneProjectExists_testDeleteProjectById() {
        projectRepository.save(new Project("Project Name", "Test Project", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        List<Project> projects = (List<Project>) projectRepository.findAll();
        assertEquals(1, projects.size());
        int projectId = projects.get(0).getId();
        projectService.deleteProjectById(projectId);
        projects = (List<Project>) projectRepository.findAll();
        assertEquals(0, projects.size());
    }
    //Test deleting a project from the database when many projects are saved.
    @Test
    void whenManyProjectsExist_testDeleteProjectById() {
        projectRepository.save(new Project("Project Name", "Test Project", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        projectRepository.save(new Project("Project Name", "Test Project", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        projectRepository.save(new Project("Project Name", "Test Project", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        projectRepository.save(new Project("Project Name", "Test Project", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        List<Project> projects = (List<Project>) projectRepository.findAll();
        assertEquals(4, projects.size());
        int projectId = projects.get(2).getId();
        projectService.deleteProjectById(projectId);
        projects = (List<Project>) projectRepository.findAll();
        assertEquals(3, projects.size());
    }
    /*Test for delete by 0 issue in case it occurs in future. Checks that the first project added to database
    does not have a project id of 0. See GitLab wiki Delete by 0 issue for more context.
     */
    @Test
    void whenFirstProjectSaved_testNotHasIdZero() {
        List<Project> projects = (List<Project>) projectRepository.findAll();
        assertEquals(0, projects.size());
        projectRepository.save(new Project("Project Name", "Test Project", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16")));
        projects = (List<Project>) projectRepository.findAll();
        assertEquals(1, projects.size());
        Exception exception = assertThrows(Exception.class, () -> projectService.getProjectById(0));
        String expectedMessage = "Project not found";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
        assertNotEquals(0, projects.get(0).getId());
    }

    @Test
    void givenValidTitle_testTitleValid(){
        String title = "Normal Title";
        assertTrue(projectService.titleValid(title));
    }

    @Test
    void givenValidAlphaNumericTitle_testTitleValid(){
        String title = "SENG302";
        assertTrue(projectService.titleValid(title));
    }

    @Test
    void givenValidExceptionalTitle_testTitleValid(){
        String title = "Māori, a-zA-Z0123456789àáâäãåąčćęèéêëėįìíîïłńòóôöõøùúûüųūÿýżźñçšžÀÁÂÄÃÅĄĆČĖĘÈÉÊËÌÍÎÏĮŁŃÒÓÔÖÕØÙÚÛÜŲŪŸÝŻŹÑßÇŒÆŠŽð,. '";
        assertTrue(projectService.titleValid(title));
    }

    @Test
    void givenValidDifferingLanguageTitle_testTitleValid(){
        String title = "私のプロジェクト";
        assertTrue(projectService.titleValid(title));
    }

    @Test
    void givenInvalid_testTitleValid(){
        String title = "😎💖❤🎂🎉✔🎁";
        assertFalse(projectService.titleValid(title));
    }



}
