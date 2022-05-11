package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.model.User;
import nz.ac.canterbury.seng302.portfolio.service.SprintService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
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
import java.util.List;
import java.util.Objects;


/**
 * Controller for the edit project details page
 */
@Controller
public class EditProjectController {
    @Autowired
    ProjectService projectService;
    @Autowired
    SprintService sprintService;
    @Autowired
    UserAccountClientService userAccountClientService;

    /* Create default project. TODO: use database to check for this*/
    Project defaultProject = new Project("Project 2022", "", "04/Mar/2022",
                                  "04/Nov/2022");

    /**
     * Method to return a calendar object representing the very beginning of a day
     * @return Calendar object
     */
    private Calendar getCalendarDay() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.AM_PM, 0);
        return cal;
    }

    /**
     * Get mapping to return the edit projects page
     * @param principal Authentication principal storing current user information
     * @param projectId The project ID of the project being displayed
     * @param model ThymeLeaf model
     * @return Edit project page
     */
    @GetMapping("/editProject-{id}")
    public String projectForm(@AuthenticationPrincipal AuthState principal, @PathVariable("id") String projectId, Model model) {
        if (!userAccountClientService.isTeacher(principal)) {
            return "redirect:/projects";
        }

        // Add user details to model
        int userId = Integer.parseInt(principal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("nameid"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("-100"));
        User user = userAccountClientService.getUserAccountById(userId);
        model.addAttribute("user", user);

        int id = Integer.parseInt(projectId);
        Project project;

        // If editing existing project
        if (id != -1) {
            // Try to find existing project
            try {
                project = projectService.getProjectById(id);
            } catch (Exception ignored) {
                // TODO
                project = defaultProject;
            }

        // Otherwise, we are adding new project, so setup default values
        } else {
            project = new Project();
            // Set project name with current year
            Calendar cal = getCalendarDay();
            project.setName("Project " + cal.get(Calendar.YEAR));

            // Set project start date as current date
            project.setStartDate(java.util.Date.from(cal.toInstant()));

            // Set project end date as 8 months after start
            cal.add(Calendar.MONTH, 8);
            project.setEndDate(java.util.Date.from(cal.toInstant()));
        }

        /* Add project details to the model */
        model.addAttribute("projectId", project.getId());
        model.addAttribute("projectName", project.getName());
        model.addAttribute("projectDescription", project.getDescription());
        model.addAttribute("projectStartDateString", Project.dateToString(project.getStartDate(), "yyyy-MM-dd"));
        model.addAttribute("projectEndDateString", Project.dateToString(project.getEndDate(), "yyyy-MM-dd"));

        // A project can only be added up to a year ago
        Calendar cal = getCalendarDay();
        cal.add(Calendar.YEAR, -1);
        java.util.Date minStartDate = java.util.Date.from(cal.toInstant());
        model.addAttribute("minProjectStartDate", Project.dateToString(minStartDate, "yyyy-MM-dd"));

        // Check if the project has any sprints
        List<Sprint> projectSprints = sprintService.getByParentProjectId(project.getId());
        if (! projectSprints.isEmpty()) {
            // Find the first sprint start date and last sprint end date
            java.util.Date firstSprintStartDate = null;
            java.util.Date lastSprintEndDate = null;
            for (Sprint s: projectSprints) {
                // Find the earliest sprint
                if (Objects.equals(s.getNumber(), 1)) {
                    firstSprintStartDate = s.getStartDate();
                }
                // Find the last sprint
                if (Objects.equals(s.getNumber(), projectSprints.size())) {
                    lastSprintEndDate = s.getEndDate();
                }
            }
            // Add the attributes to the model
            model.addAttribute("projectHasSprints", true);
            model.addAttribute("projectFirstSprintStartDate", firstSprintStartDate);
            model.addAttribute("projectLastSprintEndDate", lastSprintEndDate);
        } else {
            model.addAttribute("projectHasSprints", false);
        }

        return "editProject";
    }

    /**
     * Post mapping to edit details of a project on the edit project page
     * @param principal Authentication principal storing current user information
     * @param projectId The project ID to be edited
     * @param projectName The project name to be edited
     * @param projectStartDate The project start date to be edited
     * @param projectEndDate The project end date to be edited
     * @param projectDescription The project description to be edited
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @return Edit project page
     */
    @PostMapping("/editProject-{id}")
    public String projectSave(
            @AuthenticationPrincipal AuthState principal,
            @PathVariable("id") String projectId,
            @RequestParam(value="projectName") String projectName,
            @RequestParam(value="projectStartDate") Date projectStartDate,
            @RequestParam(value="projectEndDate") Date projectEndDate,
            @RequestParam(value="projectDescription") String projectDescription,
            Model model
    ) {
        if (!userAccountClientService.isTeacher(principal)) {
            return "redirect:/projects";
        }

        // Ensure request parameters represent a valid project
        // Check id can be parsed
        int id;
        try {
            id = Integer.parseInt(projectId);
        } catch (NumberFormatException e) {
            //TODO Add logging for error
            return "redirect:/projects";
        }

        // Check required fields are not null
        if (projectName == null || projectEndDate == null || projectStartDate == null) {
            //TODO Add logging for error
            return "redirect:/editProject-" + projectId;
        }

        // Check that projectStartDate does not occur more than a year ago
        Calendar yearAgoCal = getCalendarDay();
        yearAgoCal.add(Calendar.YEAR, -1);

        Calendar projectStartCal = getCalendarDay();
        projectStartCal.setTime(projectStartDate);

        if (projectStartCal.before(yearAgoCal)) {
            // TODO Add logging for error.
            return "redirect:/editProject-" + projectId;
        }

        // Ensure projectEndDate occurs after projectStartDate
        Calendar projectEndCal = getCalendarDay();
        projectEndCal.setTime(projectEndDate);
        if (!projectEndCal.after(projectStartCal)) {
            // TODO Add logging for error.
            return "redirect:/editProject-" + projectId;
        }

        // If editing existing project
        Project savedProject;
        if (id > 0) {
            try {
                Project existingProject = projectService.getProjectById(id);
                existingProject.setName(projectName);
                existingProject.setStartDate(projectStartDate);
                existingProject.setEndDate(projectEndDate);
                existingProject.setDescription(projectDescription);
                savedProject = projectService.saveProject(existingProject);

            } catch(Exception ignored) {
                //TODO Add logging for error.
                return "redirect:/editProject-" + projectId;
            }

        // Otherwise, create a new project with given values
        } else {
            Project newProject = new Project(projectName, projectDescription, projectStartDate, projectEndDate);
            savedProject = projectService.saveProject(newProject);
        }

        return "redirect:/projectDetails-" + savedProject.getId();
    }

    /**
     * Delete endpoint for projects. Takes id parameter from http request and deletes the corresponding project from
     * the database.
     * @param projectId ID of the project to be deleted from the database.
     * @return Redirects back to the GET mapping for /projects.
     */
    @DeleteMapping(value="/editProject-{id}")
    public String deleteProjectById(@AuthenticationPrincipal AuthState principal, @PathVariable("id") String projectId) {
        if (!userAccountClientService.isTeacher(principal)) {
            return "redirect:/projects";
        }

        int id = Integer.parseInt(projectId);
        try {
            projectService.deleteProjectById(id);
        } catch (Exception e) {
            //TODO log error.
        }
        return "redirect:/projects";
    }

}
