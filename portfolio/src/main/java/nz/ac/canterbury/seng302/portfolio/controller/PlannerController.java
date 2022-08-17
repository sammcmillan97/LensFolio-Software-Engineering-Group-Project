package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.PlannerDailyEvent;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.model.User;
import nz.ac.canterbury.seng302.portfolio.service.*;
import nz.ac.canterbury.seng302.portfolio.util.PlannerUtil;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Controller for the project planner page
 */
@Controller
public class PlannerController {
    /**
     * Autowired sprint and project services, which handle the database calls
     */
    @Autowired
    private ProjectService projectService;
    @Autowired
    private SprintService sprintService;
    @Autowired
    private EventService eventService;

    @Autowired
    private DeadlineService deadlineService;

    @Autowired
    private MilestoneService milestoneService;

    @Autowired
    private UserAccountClientService userService;

    private boolean plannerUpdated = false;
    private String plannerDate;

    private static final String PROJECTS_REDIRECT = "redirect:/projects";
    private static final String PLANNER_REDIRECT = "redirect:/planner-";

    /**
     * GET endpoint for planner page. Returns the planner html page to the client with relevant project and sprint data
     * from the database
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @return The planner html page with relevant project and sprint data.
     */
    @GetMapping("/planner-{id}")
    public String planner(@AuthenticationPrincipal AuthState principal,
                          @PathVariable("id") String id,
                          Model model) {

        int projectId = Integer.parseInt(id);
        Project project;

        try {
            project = projectService.getProjectById(projectId);
        } catch (Exception ignored) {
            return PROJECTS_REDIRECT;
        }

        int userId = Integer.parseInt(principal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("nameid"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("-100"));


        User user = userService.getUserAccountById(userId);

        Map<String, PlannerDailyEvent> eventMap = PlannerUtil.getEventsForCalender(eventService.getByEventParentProjectId(projectId));
        Map<String, PlannerDailyEvent> deadlineMap = PlannerUtil.getDeadlinesForCalender(deadlineService.getByDeadlineParentProjectId(projectId));
        Map<String, PlannerDailyEvent> milestoneMap = PlannerUtil.getMilestonesForCalender(milestoneService.getByMilestoneParentProjectId(projectId));

        model.addAttribute("milestones", milestoneMap);
        model.addAttribute("deadlines", deadlineMap);
        model.addAttribute("events", eventMap);
        model.addAttribute("user", user);
        model.addAttribute("project", project);
        model.addAttribute("sprints", sprintService.getByParentProjectId(project.getId()));

        if (plannerUpdated) {
            model.addAttribute("recentUpdate", plannerDate);
            plannerUpdated = false;
        }

        return "planner";
    }

    /**
     * The default get mapping for displaying the planner page. Will display the first project on the list of projects,
     * @param principal Authentication principal storing current user information
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @return The planner page
     */
    @GetMapping("/planner")
    public String planner(@AuthenticationPrincipal AuthState principal,
                          Model model) {

        List<Project> projects = projectService.getAllProjects();
        Project project;
        if (!projects.isEmpty()) {
            project = projects.get(0);
        } else {
            Calendar startDate = Calendar.getInstance();
            Calendar endDate = Calendar.getInstance();
            endDate.add(Calendar.MONTH, 8);
            project = new Project("Default Project", "Random Description", startDate.getTime(), endDate.getTime());
        }

        User user = userService.getUserAccountByPrincipal(principal);

        model.addAttribute("user", user);
        model.addAttribute("project", project);
        model.addAttribute("sprints", sprintService.getByParentProjectId(project.getId()));

        if (plannerUpdated) {
            model.addAttribute("recentUpdate", plannerDate);
            plannerUpdated = false;
        }

        return "planner";
    }

    @PostMapping("/editPlanner-{sprintId}-{projectId}")
    public String planner(@AuthenticationPrincipal AuthState principal,
                          Model model,
                          @PathVariable String projectId,
                          @PathVariable String sprintId,
                          @RequestParam Date startDate,
                          @RequestParam Date endDate,
                          @RequestParam Date paginationDate) {
        try {
            if (!userService.isTeacher(principal)) {
                return PLANNER_REDIRECT + projectId;
            }
            sprintService.updateStartDate(Integer.parseInt(sprintId), startDate);
            Calendar tempEndDate = Calendar.getInstance();
            tempEndDate.setTime(endDate);
            tempEndDate.add(Calendar.DATE, -1);
            sprintService.updateEndDate(Integer.parseInt(sprintId), tempEndDate.getTime());
            plannerUpdated = true;
            plannerDate = new SimpleDateFormat("yyyy-MM-dd").format(paginationDate);
        } catch ( Exception e ) {
            plannerUpdated = false;
        }
        return PLANNER_REDIRECT + projectId;
    }

    /**
     *Updates the planner after another user has added/edited an event/milestone/deadline
     * @param paginationDate The current date that the user is on in the planner
     * @return The planner page
     */
    @PostMapping("/reload-planner-{projectId}")
    public String reloadPlanner(@AuthenticationPrincipal AuthState principal,
                          Model model,
                          @PathVariable String projectId,
                          @RequestParam Date paginationDate) {
        plannerDate = new SimpleDateFormat("yyyy-MM-dd").format(paginationDate);
        plannerUpdated = true;
        return PLANNER_REDIRECT + projectId;
    }

    /**
     * Used by the planner page for live updating
     * @param projectId the project id of the current project
     * @return a list of all sprints in the project
     */
    @GetMapping("/getSprintsList")
    public @ResponseBody List<Sprint> getSprintsList(@RequestParam int projectId) {
        return sprintService.getByParentProjectId(projectId);
    }

    /**
     * Fetches a Map of all dates an event occurs on mapped to their names and number of events on that day
     * Used by the planner page for live updating
     * @param projectId the project id of the current project
     * @return a map of events
     */
    @GetMapping("/getEventsList")
    public @ResponseBody Map<String, PlannerDailyEvent> getEventsList(@RequestParam int projectId) {
        return PlannerUtil.getEventsForCalender(eventService.getByEventParentProjectId(projectId));
    }

    /**
     * Fetches a Map of all dates a deadline occurs on mapped to their names and number of deadlines on that day
     * Used by the planner page for live updating
     * @param projectId the project id of the current project
     * @return a map of deadlines
     */
    @GetMapping("/getDeadlinesList")
    public @ResponseBody Map<String, PlannerDailyEvent> getDeadlinesList(@RequestParam int projectId) {
        return PlannerUtil.getDeadlinesForCalender(deadlineService.getByDeadlineParentProjectId(projectId));
    }

    /**
     * Fetches a Map of all dates a milestone occurs on mapped to their names and number of milestones on that day
     * Used by the planner page for live updating
     * @param projectId the project id of the current project
     * @return a map of milestones
     */
    @GetMapping("/getMilestonesList")
    public @ResponseBody Map<String, PlannerDailyEvent> getMilestonesList(@RequestParam int projectId) {
        return PlannerUtil.getMilestonesForCalender(milestoneService.getByMilestoneParentProjectId(projectId));
    }
}
