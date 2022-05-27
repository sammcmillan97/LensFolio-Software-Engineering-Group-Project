package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.model.User;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import nz.ac.canterbury.seng302.portfolio.service.SprintService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;


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
     * Gets the soonest available date occurring after all of a project's sprints that occur before a given sprint
     * @param projectId The parent project for which to find the next available date
     * @param sprintNumber The position of the sprint in question in the order of a project's sprints
     * @return The soonest available date occurring after all the project's sprints occurring before the given sprint
     */
    private Date getMinSprintStartDate(int projectId, int sprintNumber) {
        // Try to find project matching id
        Project parentProject;
        try {
            parentProject = projectService.getProjectById(projectId);
        } catch (Exception e) {
            return null;
        }

        // Set min start initially to one day after project start date
        Calendar minStartDate = getCalendarDay();
        minStartDate.setTime(parentProject.getStartDate());

        // If there are any sprints before the given sprint, set min start to one day after latest end
        List<Sprint> sprints = sprintService.getByParentProjectId(projectId);
        for (Sprint sprint : sprints) {
            // Skip sprints after the given sprint
            if (sprint.getNumber() >= sprintNumber) {
                continue;
            }

            Date endDate = sprint.getEndDate();
            Calendar calDate = getCalendarDay();
            calDate.setTime(endDate);
            // If min start date is on or before the current sprint's end date
            if (!minStartDate.after(calDate)) {
                minStartDate.setTime(endDate);
                minStartDate.add(Calendar.DATE, 1);
            }
        }

        return minStartDate.getTime();
    }


    /**
     * Gets the latest available date occurring before any following sprints begin, or the project ends.
     * @param projectId The parent project for which to find the latest available date
     * @param sprintNumber The position of the sprint in question in the order of a project's sprints
     * @return The latest available date occurring before any following sprints begin, or the project ends
     */
    private Date getMaxSprintEndDate(int projectId, int sprintNumber) {
        // Try to find project matching id
        Project parentProject;
        try {
            parentProject = projectService.getProjectById(projectId);
        } catch (Exception e) {
            return null;
        }

        // Set max end initially to project end date
        Calendar maxEndDate = getCalendarDay();
        maxEndDate.setTime(parentProject.getEndDate());

        // If there are any sprints after the given sprint, set max end date to day before next sprint start date
        List<Sprint> sprints = sprintService.getByParentProjectId(projectId);
        for (Sprint sprint : sprints) {
            // Skip sprints before the given sprint
            if (sprintNumber >= sprint.getNumber()) {
                continue;
            }

            Date startDate = sprint.getStartDate();
            Calendar startCal = getCalendarDay();
            startCal.setTime(startDate);
            // If max end date is after the current sprint's start date
            if (maxEndDate.after(startCal)) {
                maxEndDate.setTime(startDate);
                maxEndDate.add(Calendar.DATE, -1);
            }
        }

        return maxEndDate.getTime();
    }

    /**
     * Gets the next sprint number for a given project
     * @param projectId The parent project for which to find the next sprint number
     * @return The sprint number of the project's next sprint
     */
    private int getNextSprintNumber(int projectId) {
        // Number of first sprint is 1
        int nextSprintNumber = 1;

        // If there are any sprints with sprint number equal or greater to current sprint number
        // set nextSprintNumber one greater
        List<Sprint> sprints = sprintService.getByParentProjectId(projectId);
        for (Sprint sprint : sprints) {
            int sprintNumber = sprint.getNumber();
            if (sprintNumber >= nextSprintNumber) {
                nextSprintNumber = sprintNumber + 1;
            }
        }

        return nextSprintNumber;
    }

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
     * @param parentProjectId The Project ID of parent project of the sprint being displayed
     * @param sprintId The Sprint ID of the sprint being displayed
     * @param model ThymeLeaf model
     * @return The edit sprint page
     */
    @GetMapping("/editSprint-{sprintId}-{parentProjectId}")
    public String sprintForm(@AuthenticationPrincipal AuthState principal,
                             @PathVariable("parentProjectId") String parentProjectId,
                             @PathVariable("sprintId") String sprintId,
                             Model model) throws Exception {
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

        // Add project id to model
        int projectId = Integer.parseInt(parentProjectId);
        Project project = projectService.getProjectById(projectId);
        model.addAttribute("projectId", project.getId());

        // Get Sprint details
        // If editing existing sprint
        Sprint sprint;
        if (Integer.parseInt(sprintId) != -1) {
            sprint = sprintService.getSprintById(Integer.parseInt(sprintId));

        // Otherwise, we are adding a new sprint
        // So populate with default sprint values
        } else {
            sprint = new Sprint();
            sprint.setNumber(getNextSprintNumber(projectId));
            sprint.setName(sprint.getLabel());

            // Get date boundaries for new sprint
            Calendar minStartDate = getCalendarDay();
            Calendar maxEndDate = getCalendarDay();
            minStartDate.setTime(Objects.requireNonNull(getMinSprintStartDate(projectId, sprint.getNumber())));
            maxEndDate.setTime(Objects.requireNonNull(getMaxSprintEndDate(projectId, sprint.getNumber())));

            // Default start date is first available date
            sprint.setStartDate(minStartDate.getTime());

            // Default end date is 3 weeks after start, or the project's end date
            minStartDate.add(Calendar.WEEK_OF_YEAR, 3);
            if (maxEndDate.before(minStartDate)) {
                sprint.setEndDate(maxEndDate.getTime());
            } else {
                sprint.setEndDate(minStartDate.getTime());
            }
        }

        // Add sprint details to model
        model.addAttribute("sprintName", sprint.getName());
        model.addAttribute("sprintLabel", sprint.getLabel());
        model.addAttribute("sprintDescription", sprint.getDescription());
        model.addAttribute("sprintStartDate", Project.dateToString(sprint.getStartDate(), "yyyy-MM-dd"));
        model.addAttribute("sprintEndDate", Project.dateToString(sprint.getEndDate(), "yyyy-MM-dd"));

        // Add date boundaries for sprint to model
        model.addAttribute("minSprintStartDate", Project.dateToString(getMinSprintStartDate(projectId, sprint.getNumber()), "yyyy-MM-dd"));
        model.addAttribute("maxSprintEndDate", Project.dateToString(getMaxSprintEndDate(projectId, sprint.getNumber()), "yyyy-MM-dd"));

        /* Return the name of the Thymeleaf template */
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
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
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
            return "redirect:/projects";
        }

        // Ensure request parameters represent a valid sprint.
        // Check ids can be parsed
        int sprintId;
        int projectId;
        try {
            // Parse ids and dates from string
            sprintId = Integer.parseInt(sprintIdString);
            projectId = Integer.parseInt(projectIdString);
        } catch (NumberFormatException e) {
            return "redirect:/projects";
        }

        // Get sprint number
        int sprintNumber;
        try {
            // If editing existing sprint
            if (sprintId > 0) {
                Sprint sprint = sprintService.getSprintById(sprintId);
                sprintNumber = sprint.getNumber();

            // Otherwise, creating sprint so get next sprint number
            } else {
                sprintNumber = getNextSprintNumber(projectId);
            }
        } catch (Exception e) {
            return "redirect:/projects/edit/" + projectId + "/" + sprintId;
        }

        // Ensure required fields are not null
        if (sprintName == null || sprintStartDate == null || sprintEndDate == null) {
            return "redirect:/projects/edit/" + projectId + "/" + sprintId;
        }

        // Ensure sprint dates are within bounds
        Calendar sprintStartCal = getCalendarDay();
        sprintStartCal.setTime(sprintStartDate);
        Calendar sprintEndCal = getCalendarDay();
        sprintEndCal.setTime(sprintEndDate);

        // Check sprint starts after project start and all previous sprints
        Calendar minSprintStart = getCalendarDay();
        minSprintStart.setTime(Objects.requireNonNull(getMinSprintStartDate(projectId, sprintNumber)));
        if (sprintStartCal.before(minSprintStart)) {
            return "redirect:/projects/edit/" + projectId + "/" + sprintId;
        }

        // Check sprint ends before project end and all following sprints
        Calendar maxSprintEnd = getCalendarDay();
        maxSprintEnd.setTime(Objects.requireNonNull(getMaxSprintEndDate(projectId, sprintNumber)));
        if (sprintEndCal.after(maxSprintEnd)) {
            return "redirect:/projects/edit/" + projectId + "/" + sprintId;
        }

        // Ensure sprintEndDate occurs after sprintStartDate
        if (!sprintEndCal.after(sprintStartCal)) {
            return "redirect:/projects/edit/" + projectId + "/" + sprintId;
        }

        //Try to find existing sprint and update if exists. Catch 'not found' error and save new sprint.
        try {
            Sprint existingSprint = sprintService.getSprintById(sprintId);
            existingSprint.setName(sprintName);
            existingSprint.setStartDate(sprintStartDate);
            existingSprint.setEndDate(sprintEndDate);
            existingSprint.setDescription(sprintDescription);
            sprintService.saveSprint(existingSprint);

        } catch(Exception ignored) {
            Sprint newSprint = new Sprint(projectId, sprintName, getNextSprintNumber(projectId), sprintDescription, sprintStartDate, sprintEndDate);
            sprintService.saveSprint(newSprint);
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
    public String deleteProjectById(@AuthenticationPrincipal AuthState principal,
                                    @PathVariable("parentProjectId") String parentProjectId,
                                    @PathVariable("sprintId") String sprintId) throws Exception {
        if (!userAccountClientService.isTeacher(principal)) {
            return "redirect:/projects";
        }

        int sprintNumber = sprintService.getSprintById(Integer.parseInt(sprintId)).getNumber();
        decrementSprintNumbersGreaterThan(Integer.parseInt(parentProjectId), sprintNumber);
        sprintService.deleteById(Integer.parseInt(sprintId));
        return "redirect:/projectDetails-" + parentProjectId;
    }

}
