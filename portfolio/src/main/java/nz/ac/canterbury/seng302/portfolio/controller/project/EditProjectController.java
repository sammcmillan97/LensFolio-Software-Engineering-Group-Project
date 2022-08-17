package nz.ac.canterbury.seng302.portfolio.controller.project;

import nz.ac.canterbury.seng302.portfolio.model.project.DateRestrictions;
import nz.ac.canterbury.seng302.portfolio.model.user.User;
import nz.ac.canterbury.seng302.portfolio.service.project.ProjectDateService;
import nz.ac.canterbury.seng302.portfolio.service.user.UserAccountClientService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import nz.ac.canterbury.seng302.portfolio.service.project.ProjectService;
import nz.ac.canterbury.seng302.portfolio.model.project.Project;
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
    ProjectDateService projectDateService;

    @Autowired
    UserAccountClientService userAccountClientService;

    /* Create default project.*/
    Project defaultProject = new Project("Project 2022", "", "04/Mar/2022",
                                  "04/Nov/2022");

    private static final String PROJECT_REDIRECT = "redirect:/projects";
    private static final String EDIT_PROJECT_REDIRECT = "redirect:/editProject-";
    private static final String DATE_FORMAT_STRING = "yyyy-MM-dd";

    private static final String REDIRECT_PROJECT_DETAILS = "redirect:/projectDetails-";

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
            return REDIRECT_PROJECT_DETAILS + projectId;
        }

        // Add user details to model
        User user = userAccountClientService.getUserAccountByPrincipal(principal);
        model.addAttribute("user", user);

        int id = Integer.parseInt(projectId);
        Project project;

        // If editing existing project
        if (id != -1) {
            // Try to find existing project
            try {
                project = projectService.getProjectById(id);
            } catch (Exception ignored) {
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
        model.addAttribute("projectStartDateString", Project.dateToString(project.getStartDate(), DATE_FORMAT_STRING));
        model.addAttribute("projectEndDateString", Project.dateToString(project.getEndDate(), DATE_FORMAT_STRING));

        // A project can only be added up to a year ago
        Calendar cal = getCalendarDay();
        cal.add(Calendar.YEAR, -1);
        java.util.Date minStartDate = java.util.Date.from(cal.toInstant());
        model.addAttribute("minProjectStartDate", Project.dateToString(minStartDate, DATE_FORMAT_STRING));

        // A project must end within 10 years from today
        cal.add(Calendar.YEAR, 11);
        java.util.Date maxEndDate = java.util.Date.from(cal.toInstant());
        model.addAttribute("maxProjectEndDate", Project.dateToString(maxEndDate, DATE_FORMAT_STRING));

        DateRestrictions dateRestrictions = projectDateService.getDateRestrictions(Integer.parseInt(projectId));
        model.addAttribute("dateRestrictions", dateRestrictions);

        return "projectTemplates/editProject";
    }

    /**
     * Post mapping to edit details of a project on the edit project page
     * @param principal Authentication principal storing current user information
     * @param projectId The project ID to be edited
     * @param projectName The project name to be edited
     * @param projectStartDate The project start date to be edited
     * @param projectEndDate The project end date to be edited
     * @param projectDescription The project description to be edited
     * @return Edit project page
     */
    @PostMapping("/editProject-{id}")
    public String projectSave(
            @AuthenticationPrincipal AuthState principal,
            @PathVariable("id") String projectId,
            @RequestParam(value="projectName") String projectName,
            @RequestParam(value="projectStartDate") Date projectStartDate,
            @RequestParam(value="projectEndDate") Date projectEndDate,
            @RequestParam(value="projectDescription") String projectDescription
    ) {
        if (!userAccountClientService.isTeacher(principal)) {
            return REDIRECT_PROJECT_DETAILS + projectId;
        }

        // Ensure request parameters represent a valid project
        // Check id can be parsed
        int id;
        try {
            id = Integer.parseInt(projectId);
        } catch (NumberFormatException e) {
            return PROJECT_REDIRECT;
        }

        // Check the project name isn't null, empty, too long or consists of whitespace
        // Also check that the project description and date fields are valid.
        if (projectName == null || projectName.length() > 255 || projectName.isBlank() ||
                projectDescription.length() > 255 || projectEndDate == null || projectStartDate == null) {
            return EDIT_PROJECT_REDIRECT + projectId;
            // Check project name is
        }

        // Check that projectStartDate does not occur more than a year ago
        Calendar yearAgoCal = getCalendarDay();
        yearAgoCal.add(Calendar.YEAR, -1);

        Calendar projectStartCal = getCalendarDay();
        projectStartCal.setTime(projectStartDate);

        if (projectStartCal.before(yearAgoCal)) {
            return EDIT_PROJECT_REDIRECT + projectId;
        }

        // Ensure projectEndDate occurs after projectStartDate
        Calendar projectEndCal = getCalendarDay();
        projectEndCal.setTime(projectEndDate);
        if (!projectEndCal.after(projectStartCal)) {
            return EDIT_PROJECT_REDIRECT + projectId;
        }

        DateRestrictions dateRestrictions = projectDateService.getDateRestrictions(Integer.parseInt(projectId));
        if (dateRestrictions.hasRestrictions()) {
            if (projectStartCal.before(dateRestrictions.getStartDate())) {
                return EDIT_PROJECT_REDIRECT + projectId;
            }
            if (!projectEndCal.after(dateRestrictions.getEndDate())) {
                return EDIT_PROJECT_REDIRECT + projectId;
            }
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
                return EDIT_PROJECT_REDIRECT + projectId;
            }

        // Otherwise, create a new project with given values
        } else {
            Project newProject = new Project(projectName, projectDescription, projectStartDate, projectEndDate);
            savedProject = projectService.saveProject(newProject);
        }

        return REDIRECT_PROJECT_DETAILS + savedProject.getId();
    }

    /**
     * Delete endpoint for projects. Takes id parameter from http request and deletes the corresponding project from
     * the database.
     * @param projectId ID of the project to be deleted from the database.
     * @return Redirects back to the GET mapping for /projects.
     */
    @DeleteMapping(value="/editProject-{id}")
    public String deleteProjectById(@AuthenticationPrincipal AuthState principal, @PathVariable("id") String projectId) {
        if (userAccountClientService.isTeacher(principal)) {
            int id = Integer.parseInt(projectId);
            try {
                projectService.deleteProjectById(id);
            } catch (Exception ignored) {
                // Don't do anything if delete fails
            }
        } else {
            return REDIRECT_PROJECT_DETAILS + projectId;
        }
        return PROJECT_REDIRECT;
    }

}
