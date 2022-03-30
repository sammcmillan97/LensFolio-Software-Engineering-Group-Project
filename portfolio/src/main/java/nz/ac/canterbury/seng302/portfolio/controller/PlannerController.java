package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import nz.ac.canterbury.seng302.portfolio.service.SprintService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Calendar;
import java.util.List;

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
    private UserAccountClientService userService;

    /**
     * GET endpoint for planner page. Returns the planner html page to the client with relevant project and sprint data
     * from the database
     * @param model Allows addition of objects to the planner html page.
     * @return The planner html page with relevant project and sprint data.
     */
    @GetMapping("/planner/{id}")
    public String planner(@AuthenticationPrincipal AuthState principal,
                          @PathVariable("id") String id,
                          Model model) {

        int projectId = Integer.parseInt(id);
        Project project = null;

        try {
            project = projectService.getProjectById(projectId);
        } catch (Exception ignored) {

        }


        int userId = Integer.parseInt(principal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("nameid"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("-100"));


        UserResponse user = userService.getUserAccountById(userId);

        model.addAttribute("user", user);
        model.addAttribute("project", project);
        model.addAttribute("sprints", sprintService.getByParentProjectId(project.getId()));


        return "planner";
    }

    /**
     * The default get mapping for displaying the planner page. Will display the first project on the list of projects,
     * @param principal
     * @param model
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


        UserResponse user = userService.getUserAccountById(userId);

        model.addAttribute("user", user);
        model.addAttribute("project", project);
        model.addAttribute("sprints", sprintService.getByParentProjectId(project.getId()));
        return "planner";
    }



}
