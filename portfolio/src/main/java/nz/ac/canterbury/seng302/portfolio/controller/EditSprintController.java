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
     * Decrements the sprint number of every one of a project's sprints with a sprint number
     * greater than a given sprint number.
     *
     * This is a helper function for deleting sprints.
     * @param parentProjectId The project whose sprints to decrement sprint numbers
     * @param minimumSprintNumber Any of the project's sprints with number greater than minimumSprintNumber will be decremented
     */
    private void decrementSprintNumbersGreaterThan(int parentProjectId, int minimumSprintNumber) {
        List<Sprint> sprints = sprintService.getByParentProjectId(parentProjectId);
        for (Sprint sprint : sprints) {
            int sprintNumber = sprint.getNumber();
            if (minimumSprintNumber < sprintNumber) {
                sprint.setNumber(sprintNumber - 1);
            }
        }
    }

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
     * The post mapping to edit a sprint ID
     * @param principal Authentication principal storing current user information
     * @param projectIdString The parent project ID of the sprint that is being edited
     * @param sprintIdString The ID of the sprint that is being edited
     * @param sprintName The name of the sprint being edited
     * @param sprintStartDate The start date of the sprint being edited
     * @param sprintEndDate The end date of the sprint being edited
     * @param sprintDescription The description of the sprint being edited
     * @return The edit sprints page
     */
    @PostMapping("/editSprint-{sprintId}-{parentProjectId}")
    public String sprintSave(
            @AuthenticationPrincipal AuthState principal,
            @PathVariable("parentProjectId") String projectIdString,
            @PathVariable("sprintId") String sprintIdString,
            @RequestParam(value="sprintName") String sprintName,
            @RequestParam(value="sprintStartDate") java.sql.Date sprintStartDate,
            @RequestParam(value="sprintEndDate") java.sql.Date sprintEndDate,
            @RequestParam(value="sprintDescription") String sprintDescription,
            Model model
    ) {
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

        if( sprintId != -1) {
            try {
                sprint = sprintService.getSprintById(sprintId);
            } catch (NoSuchElementException e) {
                return PROJECTS_REDIRECT;
            }
            sprintService.editSprint(projectId, sprintId, sprintName, sprintDescription, sprintStartDate, sprintEndDate);
        } else {
            sprintService.createNewSprint(projectId, sprintId, sprintName, sprintDescription, sprintStartDate, sprintEndDate);
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

        int sprintNumber = sprintService.getSprintById(Integer.parseInt(sprintId)).getNumber();
        decrementSprintNumbersGreaterThan(Integer.parseInt(parentProjectId), sprintNumber);
        sprintService.deleteById(Integer.parseInt(sprintId));
        return PROJECTS_REDIRECT + parentProjectId;
    }
}
