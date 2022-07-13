package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.PlannerDailyEvent;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.User;
import nz.ac.canterbury.seng302.portfolio.service.*;
import nz.ac.canterbury.seng302.portfolio.util.PlannerUtil;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    private UserAccountClientService userService;

    private boolean sprintUpdated = false;
    private String sprintDate;

    private String redirectToProjects = "redirect:/projects";

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
        Project project = null;

        try {
            project = projectService.getProjectById(projectId);
        } catch (Exception ignored) {
            return redirectToProjects;
        }

        int userId = Integer.parseInt(principal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("nameid"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("-100"));


        User user = userService.getUserAccountById(userId);

        Map<String, PlannerDailyEvent> eventMap = PlannerUtil.getEventsForCalender(eventService.getByEventParentProjectId(projectId));
        Map<String, PlannerDailyEvent> deadlineMap = PlannerUtil.getDeadlinesForCalender(deadlineService.getByDeadlineParentProjectId(projectId));

        model.addAttribute("deadlines", deadlineMap);
        model.addAttribute("events", eventMap);
        model.addAttribute("user", user);
        model.addAttribute("project", project);
        model.addAttribute("sprints", sprintService.getByParentProjectId(project.getId()));

        if (sprintUpdated) {
            model.addAttribute("recentUpdate", sprintDate);
            sprintUpdated = false;
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
        Project project = null;
        if (!projects.isEmpty()) {
            project = projects.get(0);
        } else {
            Calendar startDate = Calendar.getInstance();
            Calendar endDate = Calendar.getInstance();
            endDate.add(Calendar.MONTH, 8);
            project = new Project("Default Project", "Random Description", startDate.getTime(), endDate.getTime());
        }
        int userId = Integer.parseInt(principal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("nameid"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("-100"));


        User user = userService.getUserAccountById(userId);

        model.addAttribute("user", user);
        model.addAttribute("project", project);
        model.addAttribute("sprints", sprintService.getByParentProjectId(project.getId()));

        if (sprintUpdated) {
            model.addAttribute("recentUpdate", sprintDate);
            sprintUpdated = false;
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
            sprintService.updateStartDate(Integer.parseInt(sprintId), startDate);
            Calendar tempEndDate = Calendar.getInstance();
            tempEndDate.setTime(endDate);
            tempEndDate.add(Calendar.DATE, -1);
            sprintService.updateEndDate(Integer.parseInt(sprintId), tempEndDate.getTime());
            sprintUpdated = true;
            sprintDate = new SimpleDateFormat("yyyy-MM-dd").format(paginationDate);
        } catch ( Exception e ) {
            sprintUpdated = false;
        }
        return "redirect:/planner-" + projectId;
    }

}
