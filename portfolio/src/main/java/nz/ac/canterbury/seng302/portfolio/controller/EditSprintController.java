package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.model.User;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import nz.ac.canterbury.seng302.portfolio.service.SprintService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Controller for the edit sprint details page
 */
@Controller
public class EditSprintController {

    @Autowired
    UserAccountClientService userAccountClientService;
    @Autowired
    ProjectService projectService;
    @Autowired
    SprintService sprintService;

    private static final String PROJECTS_REDIRECT = "redirect:/projects";
    private static final String TIME_FORMAT = "yyyy-MM-dd";


    /**
     * The get mapping to return the page to edit a sprint of a certain Project ID
     * @param principal Authentication principal storing current user information
     * @param projectIdString The Project ID of parent project of the sprint being displayed
     * @param sprintIdString The Sprint ID of the sprint being displayed
     * @param model ThymeLeaf model
     * @return The edit sprint page
     */
    @GetMapping("/editSprint-{sprintId}-{parentProjectId}")
    public String sprintForm(@AuthenticationPrincipal AuthState principal,
                             @PathVariable("parentProjectId") String projectIdString,
                             @PathVariable("sprintId") String sprintIdString,
                             Model model) {
        if (!userAccountClientService.isTeacher(principal)) {
            return PROJECTS_REDIRECT;
        }
        User user = userAccountClientService.getUserAccountByPrincipal(principal);
        model.addAttribute("user", user);

        int projectId;
        int sprintId;
        Sprint sprint;
        Project project;

        try {
            projectId = Integer.parseInt(projectIdString);
            sprintId = Integer.parseInt(sprintIdString);
        } catch (NumberFormatException e) {
            return PROJECTS_REDIRECT;
        }

        try {
            project = projectService.getProjectById(projectId);
        } catch (NoSuchElementException e) {
            return PROJECTS_REDIRECT;
        }
        model.addAttribute("projectId", projectId);

        //editing existing sprint
        if (sprintId != -1) {
            try {
                sprint = sprintService.getSprintById(sprintId);
            } catch (NoSuchElementException e) {
                return PROJECTS_REDIRECT;
            }
        //new sprint
        } else {
            sprint = sprintService.createDefaultSprint(projectId);
        }
        // Add sprint details to model
        model.addAttribute("sprintName", sprint.getName());
        model.addAttribute("sprintLabel", sprint.getLabel());
        model.addAttribute("sprintDescription", sprint.getDescription());
        model.addAttribute("sprintStartDate", Project.dateToString(sprint.getStartDate(), TIME_FORMAT));
        model.addAttribute("sprintEndDate", Project.dateToString(sprint.getEndDate(), TIME_FORMAT));

        // Add date boundaries for sprint to model
        model.addAttribute("minSprintStartDate", Project.dateToString(project.getStartDate(), TIME_FORMAT));
        model.addAttribute("maxSprintEndDate", Project.dateToString(project.getEndDate(), TIME_FORMAT));
        return "editSprint";
    }

    /**
     * Post request handler for adding/editing sprints
     * @param principal principal Authentication state of client
     * @param projectIdString The ID string of the parent project of the sprint
     * @param sprintIdString The ID string of the sprint being edited/created
     * @param sprintName The new sprint name
     * @param sprintStartDateString The new sprint start date
     * @param sprintEndDateString The new sprint end date
     * @param sprintDescription The new sprint description
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @return Project details page or sprint from depending on if validation passes.
     */
    @PostMapping("/editSprint-{sprintId}-{parentProjectId}")
    public String sprintSave(
            @AuthenticationPrincipal AuthState principal,
            @PathVariable("parentProjectId") String projectIdString,
            @PathVariable("sprintId") String sprintIdString,
            @RequestParam(value="sprintName") String sprintName,
            @RequestParam(value="sprintStartDate") String sprintStartDateString,
            @RequestParam(value="sprintEndDate") String sprintEndDateString,
            @RequestParam(value="sprintDescription") String sprintDescription,
            Model model
    ) throws ParseException {
        if (!userAccountClientService.isTeacher(principal)) {
            return PROJECTS_REDIRECT;
        }
        User user = userAccountClientService.getUserAccountByPrincipal(principal);
        model.addAttribute("user", user);

        int projectId;
        int sprintId;
        Sprint sprint;
        Project project;

        Date sprintStartDate = new SimpleDateFormat(TIME_FORMAT).parse(sprintStartDateString);
        Date sprintEndDate = new SimpleDateFormat(TIME_FORMAT).parse(sprintEndDateString);
        try {
            projectId = Integer.parseInt(projectIdString);
            sprintId = Integer.parseInt(sprintIdString);
        } catch (NumberFormatException e) {
            return PROJECTS_REDIRECT ;
        }
        try {
            project = projectService.getProjectById(projectId);
        } catch (NoSuchElementException e) {
            return PROJECTS_REDIRECT;
        }
        model.addAttribute("projectId", projectId);

        //Date validation
        boolean validation = false;
        String startDateError = (sprintService.checkSprintStartDate(sprintId, projectId, sprintStartDate));
        String endDateError = (sprintService.checkSprintEndDate(sprintId, projectId, sprintEndDate));
        String datesError = (sprintService.checkSprintDates(sprintId, projectId, sprintStartDate, sprintEndDate));
        if (!Objects.equals(startDateError, "")) {
            model.addAttribute("startDateError", startDateError);
            validation = true;
        }
        if (!Objects.equals(endDateError, "")) {
            model.addAttribute("endDateError", endDateError);
            validation = true;
        }
        if (!Objects.equals(datesError, "")) {
            model.addAttribute("dateError", datesError);
            validation = true;
        }

        if (validation) {
            // Add sprint details to model
            model.addAttribute("sprintName", sprintName);
            model.addAttribute("sprintDescription", sprintDescription);
            model.addAttribute("sprintStartDate", sprintStartDateString);
            model.addAttribute("sprintEndDate", sprintEndDateString);

            // Add date boundaries for sprint to model
            model.addAttribute("minSprintStartDate", Project.dateToString(project.getStartDate(), TIME_FORMAT));
            model.addAttribute("maxSprintEndDate", Project.dateToString(project.getEndDate(), TIME_FORMAT));
            return "editSprint";
        }

        if (sprintId != -1) {
            try {
                sprintService.getSprintById(sprintId);
            } catch (NoSuchElementException e) {
                return PROJECTS_REDIRECT;
            }
            sprintService.editSprint(projectId, sprintId, sprintName, sprintDescription, sprintStartDate, sprintEndDate);
        } else {
            sprintService.createNewSprint(projectId, sprintName, sprintDescription, sprintStartDate, sprintEndDate);
        }
        return "redirect:/projectDetails-" + projectIdString;
    }


    /**
     * The delete mapping for deleting sprints from a project
     * @param principal Authentication principal storing current user information
     * @param parentProjectId The parent project ID of the sprint being deleted
     * @param sprintId The sprint ID of the sprint being delted
     * @return The projects page
     */
    @DeleteMapping(value="/editSprint-{sprintId}-{parentProjectId}")
    public String deleteSprint(@AuthenticationPrincipal AuthState principal,
                                    @PathVariable("parentProjectId") String parentProjectId,
                                    @PathVariable("sprintId") String sprintId) {
        if (!userAccountClientService.isTeacher(principal)) {
            return PROJECTS_REDIRECT;
        }

        sprintService.deleteById(Integer.parseInt(sprintId));
        return PROJECTS_REDIRECT + parentProjectId;
    }
}
