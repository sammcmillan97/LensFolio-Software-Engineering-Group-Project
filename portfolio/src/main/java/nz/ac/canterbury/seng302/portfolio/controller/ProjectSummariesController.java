package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
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
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Controller for the projects page. Has various end points for interacting with the projects stored in the database.
 */
@Controller
public class ProjectSummariesController {

    /**
     * Autowired project service, which handles the project database calls
     */
    @Autowired
    private ProjectService projectService;
    @Autowired
    private SprintService sprintService;
    @Autowired
    private UserAccountClientService userAccountClientService;

    /**
     * GET endpoint for projects. Returns the projects html page to the client with relevant projects data from the
     * database. If no projects exist in the database a default project is created.
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @return The projects html page with relevant projects data.
     */
    @GetMapping("/projects")
    public String projects(@AuthenticationPrincipal AuthState principal, Model model) {
        // Add user details to model
        Integer userId = Integer.valueOf(principal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("nameid"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("-100"));
        UserResponse user = userAccountClientService.getUserAccountById(userId);
        model.addAttribute("user", user);

        List<Project> projects = projectService.getAllProjects();
        Map<Integer, List<Sprint>> sprints = sprintService.getAllByParentProjectId();

        /* Return the name of the Thymeleaf template
        detects the role of the current user and returns appropriate page
        System.out.println(role);*/
        String role = userAccountClientService.getRole(principal);
        if (role.contains("teacher")) {

            // Add default project if none exist
            if (projects.size() < 1) {
                Project defaultProject = new Project();
                projectService.saveProject(defaultProject);
                projects = projectService.getAllProjects();
            }

            model.addAttribute("projects", projects);
            model.addAttribute("sprints", sprints);

            return "teacherProjectSummaries";

        } else {
            model.addAttribute("projects", projects);
            model.addAttribute("sprints", sprints);

            return "userProjectSummaries";
        }
    }
}
