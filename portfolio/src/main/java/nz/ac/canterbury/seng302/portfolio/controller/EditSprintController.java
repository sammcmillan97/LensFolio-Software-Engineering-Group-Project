package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
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

    private Sprint defaultSprint = new Sprint(-1, "A Sprint", -1, "Here's a description", new Date(), new Date());

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
            e.printStackTrace();
            return null;
        }

        // Set min start initially to one day after project start date
        Calendar minStartDate = Calendar.getInstance();
        minStartDate.setTime(parentProject.getStartDate());

        // If there are any sprints before the given sprint, set min start to one day after latest end
        List<Sprint> sprints = sprintService.getByParentProjectId(projectId);
        for (Sprint sprint : sprints) {
            // Skip sprints after the given sprint
            if (!(sprint.getNumber() < sprintNumber)) {
                continue;
            }

            Date endDate = sprint.getEndDate();
            // If min start date is on or before the current sprint's end date
            if (!minStartDate.after(endDate)) {
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
            e.printStackTrace();
            return null;
        }

        // Set max end initially to project end date
        Calendar maxEndDate = Calendar.getInstance();
        maxEndDate.setTime(parentProject.getEndDate());

        // If there are any sprints after the given sprint, set max end date to day before next sprint start date
        List<Sprint> sprints = sprintService.getByParentProjectId(projectId);
        for (Sprint sprint : sprints) {
            // Skip sprints before the given sprint
            if (!(sprintNumber < sprint.getNumber())) {
                continue;
            }

            Date startDate = sprint.getStartDate();
            // If max start date is after the current sprint's start date
            if (maxEndDate.after(startDate)) {
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
            if (!(sprintNumber < nextSprintNumber)) {
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

    @GetMapping("/projects/edit/{parentProjectId}/{sprintId}")
    public String sprintForm(@AuthenticationPrincipal AuthState principal,
                             @PathVariable("parentProjectId") String parentProjectId,
                             @PathVariable("sprintId") String sprintId,
                             Model model) throws Exception {
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

            // Set default start and end dates
            Calendar minStartDate = Calendar.getInstance();

            minStartDate.setTime(getMinSprintStartDate(projectId, sprint.getNumber()));
            sprint.setStartDate(minStartDate.getTime());

            minStartDate.add(Calendar.WEEK_OF_YEAR, 3);
            sprint.setEndDate(minStartDate.getTime());
        }

        // Add sprint details to model
        model.addAttribute("sprintName", sprint.getName());
        model.addAttribute("sprintLabel", sprint.getLabel());
        model.addAttribute("sprintDescription", sprint.getDescription());
        model.addAttribute("sprintStartDate", Project.dateToString(sprint.getStartDate(), "yyyy-MM-dd"));
        model.addAttribute("sprintEndDate", Project.dateToString(sprint.getEndDate(), "yyyy-MM-dd"));

        // Add min sprint start and max sprint end dates to model
        model.addAttribute("minSprintStartDate", Project.dateToString(getMinSprintStartDate(projectId, sprint.getNumber()), "yyyy-MM-dd"));
        model.addAttribute("maxSprintEndDate", Project.dateToString(getMaxSprintEndDate(projectId, sprint.getNumber()), "yyyy-MM-dd"));

        /* Return the name of the Thymeleaf template */
        return "editSprint";
    }

    @PostMapping("/projects/edit/{parentProjectId}/{sprintId}")
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
        String role = userAccountClientService.getRole(principal);
        if (!role.contains("teacher")) {
            return "redirect:/projects";
        }

        // Parse ids and dates from string
        int sprintId = Integer.parseInt(sprintIdString);
        int projectId = Integer.parseInt(projectIdString);

        //Date sprintStartDate = Project.stringToDate(sprintStartDateString);
        //Date sprintEndDate = Project.stringToDate(sprintEndDateString);

        Sprint savedSprint;
        //Try to find existing sprint and update if exists. Catch 'not found' error and save new sprint.
        try {
            Sprint existingSprint = sprintService.getSprintById(sprintId);
            existingSprint.setName(sprintName);
            existingSprint.setStartDate(sprintStartDate);
            existingSprint.setEndDate(sprintEndDate);
            existingSprint.setDescription(sprintDescription);
            savedSprint = sprintService.saveSprint(existingSprint);

        } catch(Exception ignored) {
            Sprint newSprint = new Sprint(projectId, sprintName, getNextSprintNumber(projectId), sprintDescription, sprintStartDate, sprintEndDate);
            savedSprint = sprintService.saveSprint(newSprint);
        }

        return "redirect:/projects/" + projectIdString;
    }

    @DeleteMapping(value="/projects/delete/{parentProjectId}/{sprintId}")
    public String deleteProjectById(@AuthenticationPrincipal AuthState principal,
                                    @PathVariable("parentProjectId") String parentProjectId,
                                    @PathVariable("sprintId") String sprintId) throws Exception {
        String role = userAccountClientService.getRole(principal);
        if (!role.contains("teacher")) {
            return "redirect:/projects";
        }

        int sprintNumber = sprintService.getSprintById(Integer.parseInt(sprintId)).getNumber();
        decrementSprintNumbersGreaterThan(Integer.parseInt(parentProjectId), sprintNumber);
        sprintService.deleteById(Integer.parseInt(sprintId));
        return "redirect:/projects/" + parentProjectId;
    }

}
