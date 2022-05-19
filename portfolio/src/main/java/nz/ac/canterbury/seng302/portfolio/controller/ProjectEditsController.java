package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.ProjectEdits;
import nz.ac.canterbury.seng302.portfolio.service.AuthenticateClientService;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
public class ProjectEditsController {

    @Autowired
    AuthenticateClientService authenticateClientService;

    @Autowired
    UserAccountClientService userAccountClientService;

    @Autowired
    ProjectService projectService;

    private final ProjectEdits projectEdits = new ProjectEdits();

    @GetMapping("projects-editStatus")
    public String projectEditing(@AuthenticationPrincipal AuthState principal,
                                 @RequestParam String id) {
        boolean isAuthenticated = authenticateClientService.checkAuthState().getIsAuthenticated();
        int userId = Integer.parseInt(principal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("nameid"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("-100"));

        int projectId;
        try {
            projectId = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            return ""; // If project id is not an integer, the request was invalid, and we just return the empty string
        }

        if (isAuthenticated && userId != -100) {
            return projectEdits.getEdits(projectId, userId);
        } else {
            return ""; //Return empty string as user is not authenticated
        }
    }

    @PostMapping("/projects-editing")
    public void isEditingProject(@AuthenticationPrincipal AuthState principal,
                                 @RequestParam String id) {
        boolean isTeacher = userAccountClientService.isTeacher(principal) && authenticateClientService.checkAuthState().getIsAuthenticated();
        int userId = Integer.parseInt(principal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("nameid"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("-100"));

        int projectId;
        Project project;
        try {
            projectId = Integer.parseInt(id);
            project = projectService.getProjectById(projectId);
        } catch (NumberFormatException | NoSuchElementException e) {
            return;
            // If project id is not an integer or does not correspond to a project, the request was invalid so we return
        }
        if (isTeacher && userId != -100) {
            String editString = userAccountClientService.getUserAccountById(userId).getFirstName() +
                    " is editing " + project.getName();
            projectEdits.newEdit(projectId, userId, editString);
        }
    }

}
