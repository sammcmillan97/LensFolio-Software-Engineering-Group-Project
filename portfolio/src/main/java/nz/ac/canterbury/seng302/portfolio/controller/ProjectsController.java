package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.model.ProjectRepository;
import nz.ac.canterbury.seng302.portfolio.model.SprintRepository;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
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
public class ProjectsController {

    /**
     * Autowired project service, which handles the project database calls
     */
    @Autowired
    private ProjectService projectService;
    @Autowired
    private UserAccountClientService userAccountClientService;

    /**
     * GET endpoint for projects. Returns the projects html page to the client with relevant projects data from the
     * database. If no projects exist in the database a default project is created.
     * @param model Allows addition of objects to the projects html page.
     * @return The projects html page with relevant projects data.
     */
    @GetMapping("/projects")
    public String projects(@AuthenticationPrincipal AuthState principal, Model model) {
        List<Project> projects = projectService.getAllProjects();

        if (projects.size() < 1) {
            Project defaultProject = new Project();
            projectService.saveProject(defaultProject);
            projects = projectService.getAllProjects();
        }

        model.addAttribute("projects", projects);
        String role = userAccountClientService.getRole(principal);

        //Detects role of user and returns appropriate page
        if (role.equals("teacher")) {
            return "projects";
        } else {
            return "userProjects";
        }
    }

    /**
     * Delete endpoint for projects. Takes id parameter from http request and deletes the corresponding project from
     * the database.
     * @param id ID of the project to be deleted from the database.
     * @return Redirects back to the GET mapping for /projects.
     */
    @DeleteMapping(value="/projects")
    public String deleteProjectById(@AuthenticationPrincipal AuthState principal, @RequestParam(name="id") int id) throws Exception {
        String role = userAccountClientService.getRole(principal);
        if (role.equals("teacher")) {
            projectService.deleteProjectById(id);
        }
        return "redirect:/projects";
    }

    @PostMapping(value="/projects")
    public String editProjectById(@AuthenticationPrincipal AuthState principal,
                                  @RequestParam(name = "projectId", defaultValue = "-1") int projectId,
                                  @RequestParam(name = "projectName") String projectName,
                                  @RequestParam(name = "projectDescription") String projectDescription,
                                  @RequestParam(name = "projectStartDate") Date projectStartDate,
                                  @RequestParam(name = "projectEndDate") Date projectEndDate,
                                  Model model) {
        String role = userAccountClientService.getRole(principal);
        if (role.equals("teacher")) {
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

                }
            }
        }

        return "redirect:/projects";
    }
}
