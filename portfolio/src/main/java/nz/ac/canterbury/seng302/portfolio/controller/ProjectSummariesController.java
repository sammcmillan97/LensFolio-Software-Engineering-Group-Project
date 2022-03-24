package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.model.ProjectRepository;
import nz.ac.canterbury.seng302.portfolio.model.SprintRepository;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import nz.ac.canterbury.seng302.portfolio.service.SprintService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import java.sql.Date;

/**
 * Controller for the projects page. Has various end points for interacting with the projects stored in the database.
 */
@Controller
public class ProjectSummariesController {

    /**
     * Repository which allows the controller to interact with the database.
     */
    @Autowired
    private ProjectService projectService;
    @Autowired
    private SprintService sprintService;

    /**
     * GET endpoint for projects. Returns the projects html page to the client with relevant projects data from the
     * database. If no projects exist in the database a default project is created.
     * @param model Allows addition of objects to the projects html page.
     * @return The projects html page with relevant projects data.
     */
    @GetMapping("/projects")
    public String projects(Model model) {
        List<Project> projects = projectService.getAllProjects();
        Map<Integer, List<Sprint>> sprints = sprintService.getAllByParentProjectId();

        if (projects.size() < 1) {
            Project defaultProject = new Project();
            projectService.saveProject(defaultProject);
            projects = projectService.getAllProjects();
        }

        model.addAttribute("projects", projects);
        model.addAttribute("sprints", sprints);

        return "projectSummaries";
    }

    /**
     * Delete endpoint for projects. Takes id parameter from http request and deletes the corresponding project from
     * the database.
     * @param id ID of the project to be deleted from the database.
     * @return Redirects back to the GET mapping for /projects.
     */
    @DeleteMapping(value="/projects")
    public String deleteProjectById(@RequestParam(name="id") int id) throws Exception {
        projectService.deleteProjectById(id);
        return "redirect:/projects";
    }

    @PostMapping(value="/projects")
    public String editProjectById(@RequestParam(name = "projectId", defaultValue = "-1") int projectId,
                                  @RequestParam(name = "projectName") String projectName,
                                  @RequestParam(name = "projectDescription") String projectDescription,
                                  @RequestParam(name = "projectStartDate") Date projectStartDate,
                                  @RequestParam(name = "projectEndDate") Date projectEndDate,
                                  Model model) {
        if (projectId == -1) {
            Project newProject = new Project(projectName, projectDescription, projectStartDate, projectEndDate);
            projectService.saveProject(newProject);
        } else {
            try {
                Project existingProject = projectService.getProjectById(projectId);
                existingProject.setName(projectName);
                existingProject.setStartDate(projectStartDate);
                existingProject.setEndDate(projectEndDate);
                existingProject.setDescription(projectDescription);
                projectService.saveProject(existingProject);
            } catch(Exception ignored) {
                // TODO
            }
        }

        return "redirect:/projects";
    }
}
