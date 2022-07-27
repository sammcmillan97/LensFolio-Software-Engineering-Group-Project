package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Event;
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

        System.out.println(projectIdString);
        System.out.println(sprintIdString);
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

        System.out.println("reached");

        boolean validation = false;
        if (!sprintService.checkSprintStartDate(sprintId, projectId, sprintStartDate)) {
            model.addAttribute("startDateError", "Sprint start date can't be inside another sprint");
            validation = true;
        }
        if(!sprintService.checkSprintEndDate(sprintId, projectId, sprintStartDate)) {
            model.addAttribute("endDateError", "Sprint start date can't be inside another sprint");
            validation = true;
        }
        if(!sprintService.checkSprintDates(sprintId, projectId, sprintStartDate, sprintEndDate)) {
            model.addAttribute("dateError", "Sprints can't ");
            validation = true;
        }
        if (validation) {
            // Add sprint details to model
            model.addAttribute("sprintName", sprintName);
            model.addAttribute("sprintLabel", "Sprint " + sprintService.getNextSprintNumber(projectId));
            model.addAttribute("sprintDescription", sprintDescription);
            model.addAttribute("sprintStartDate", sprintStartDateString);
            model.addAttribute("sprintEndDate", sprintEndDateString);

            // Add date boundaries for sprint to model
            model.addAttribute("minSprintStartDate", Project.dateToString(project.getStartDate(), TIME_FORMAT));
            model.addAttribute("maxSprintEndDate", Project.dateToString(project.getEndDate(), TIME_FORMAT));
            return "editSprint";
        }

//        if (sprintId != -1) {
//            try {
//                sprint = sprintService.getSprintById(sprintId);
//            } catch (NoSuchElementException e) {
//                return PROJECTS_REDIRECT;
//            }
//            sprintService.editSprint(projectId, sprintId, sprintName, sprintDescription, sprintStartDate, sprintEndDate);
//        } else {
//            sprintService.createNewSprint(projectId, sprintId, sprintName, sprintDescription, sprintStartDate, sprintEndDate);
//        }
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
