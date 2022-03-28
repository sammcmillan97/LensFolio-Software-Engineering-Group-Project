package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import org.springframework.beans.factory.annotation.Autowired;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.sql.Date;
import java.util.Calendar;


/**
 * Controller for the edit project details page
 */
@Controller
public class EditProjectController {
    @Autowired
    ProjectService projectService;
    @Autowired
    UserAccountClientService userAccountClientService;

    /* Create default project. TODO: use database to check for this*/
    Project defaultProject = new Project("Project 2022", "", "04/Mar/2022",
                                  "04/Nov/2022");

    @GetMapping("/projects/edit/{id}")
    public String projectForm(@AuthenticationPrincipal AuthState principal, @PathVariable("id") String projectId, Model model) {
        String role = userAccountClientService.getRole(principal);
        if (!role.contains("teacher")) {
            return "redirect:/projects";
        }

        // Add user details to model
        Integer userId = Integer.valueOf(principal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("nameid"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("-100"));
        UserResponse user = userAccountClientService.getUserAccountById(userId);
        model.addAttribute("user", user);

        int id = Integer.parseInt(projectId);
        Project project = new Project();

        // If editing existing project
        if (id != -1) {
            // Try to find existing project
            try {
                project = projectService.getProjectById(id);
            } catch (Exception ignored) {
                // TODO
            }

        // Otherwise, we are adding new project, so setup default values
        } else {
            project = defaultProject;
            // Set project name with current year
            Calendar cal = Calendar.getInstance();
            project.setName("Project " + cal.get(Calendar.YEAR));

            // Set project start date as current date
            project.setStartDate(Date.from(cal.toInstant()));

            // Set project end date as 8 months after start
            cal.add(Calendar.MONTH, 8);
            project.setEndDate(Date.from(cal.toInstant()));
        }

        /* Add project details to the model */
        model.addAttribute("projectId", project.getId());
        model.addAttribute("projectName", project.getName());
        model.addAttribute("projectDescription", project.getDescription());
        model.addAttribute("projectStartDateString", Project.dateToString(project.getStartDate(), "yyyy-MM-dd"));
        model.addAttribute("projectEndDateString", Project.dateToString(project.getEndDate(), "yyyy-MM-dd"));

        return "editProject";
    }

    @PostMapping("/projects/edit/{id}")
    public String projectSave(
            @AuthenticationPrincipal AuthState principal,
            @PathVariable("id") String projectId,
            @RequestParam(value="projectName") String projectName,
            @RequestParam(value="projectStartDate") Date projectStartDate,
            @RequestParam(value="projectEndDate") Date projectEndDate,
            @RequestParam(value="projectDescription") String projectDescription,
            Model model
    ) {
        String role = userAccountClientService.getRole(principal);
        if (!role.contains("teacher")) {
            return "redirect:/projects";
        }

        int id = Integer.parseInt(projectId);

        Project savedProject;
        //Try to find existing project and update if exists. Catch 'not found' error and save new project.
        try {
            Project existingProject = projectService.getProjectById(Integer.parseInt(projectId));
            existingProject.setName(projectName);
            existingProject.setStartDate(projectStartDate);
            existingProject.setEndDate(projectEndDate);
            existingProject.setDescription(projectDescription);
            savedProject = projectService.saveProject(existingProject);

        } catch(Exception ignored) {
            Project newProject = new Project(projectName, projectDescription, projectStartDate, projectEndDate);
            savedProject = projectService.saveProject(newProject);
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
    public String deleteProjectById(@AuthenticationPrincipal AuthState principal, @PathVariable("id") String projectId) {
        String role = userAccountClientService.getRole(principal);
        if (!role.contains("teacher")) {
            return "redirect:/projects";
        }

        int id = Integer.parseInt(projectId);
        projectService.deleteProjectById(id);
        return "redirect:/projects";
    }

}
