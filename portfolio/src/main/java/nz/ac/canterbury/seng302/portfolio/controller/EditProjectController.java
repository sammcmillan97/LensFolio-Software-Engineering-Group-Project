package nz.ac.canterbury.seng302.portfolio.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import org.springframework.beans.factory.annotation.Autowired;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.security.core.annotation.AuthenticationPrincipal;


/**
 * Controller for the edit project details page
 */
@Controller
public class EditProjectController {
    @Autowired
    ProjectService projectService;

    /* Create default project. TODO: use database to check for this*/
    Project defaultProject = new Project("Project 2022", "", "04/Mar/2022",
                                  "04/Nov/2022");

    @GetMapping("/projects/edit/{id}")
    public String projectForm(@PathVariable("id") String projectId, Model model) {
        int id = Integer.parseInt(projectId);

        Project project;
        try {
            project = projectService.getProjectById(id);
        } catch (Exception ignored) {
            // TODO
            project = defaultProject;
        }

        /* Add project to the model */
        model.addAttribute("project", project);

        return "editProject";
    }

    @PostMapping("/projects/edit/{id}")
    public String projectSave(
            @AuthenticationPrincipal AuthState principal,
            @PathVariable("id") String projectId,
            @RequestParam(value="projectName") String projectName,
            @RequestParam(value="projectStartDate") String projectStartDate,
            @RequestParam(value="projectEndDate") String projectEndDate,
            @RequestParam(value="projectDescription") String projectDescription,
            Model model
    ) {
        int id = Integer.parseInt(projectId);

        Project savedProject;
        // An id of -1 signals to create a new project
        if (id == -1) {
            Project newProject = new Project(projectName, projectDescription, projectStartDate, projectEndDate);
            savedProject = projectService.saveProject(newProject);

        // Otherwise edit the existing project matching id
        } else {
            try {
                Project existingProject = projectService.getProjectById(Integer.parseInt(projectId));
                existingProject.setName(projectName);
                existingProject.setStartDateString(projectStartDate);
                existingProject.setEndDateString(projectEndDate);
                existingProject.setDescription(projectDescription);
                savedProject = projectService.saveProject(existingProject);

            } catch(Exception ignored) {
                // TODO
                return "redirect:/projects";
            }
        }

        return "redirect:/projects/" + savedProject.getId();
    }

    /**
     * Delete endpoint for projects. Takes id parameter from http request and deletes the corresponding project from
     * the database.
     * @param projectId ID of the project to be deleted from the database.
     * @return Redirects back to the GET mapping for /projects.
     */
    @DeleteMapping(value="/projects/delete/{id}")
    public String deleteProjectById(@PathVariable("id") String projectId) {
        int id = Integer.parseInt(projectId);
        projectService.deleteProjectById(id);
        return "redirect:/projects";
    }

}
