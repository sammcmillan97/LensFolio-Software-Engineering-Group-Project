package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.service.AuthenticateClientService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
public class ProjectEditsController {

    @Autowired
    AuthenticateClientService authenticateClientService;

    @Autowired
    UserAccountClientService userAccountClientService;

    private long editedAtTime;
    private String editedProject;

    @GetMapping("/projects/editStatus")
    public String projectEditing(@AuthenticationPrincipal AuthState principal,
                                 @RequestParam String id) {
        boolean isAuthenticated = authenticateClientService.checkAuthState().getIsAuthenticated();
        int userId = Integer.parseInt(principal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("nameid"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("-100"));
        if (isAuthenticated && userId != -100) {
            // Proper edit detection here
            if (System.currentTimeMillis() - editedAtTime < 5000 && Objects.equals(id, editedProject)) {
                return "1";
            } else {
                return "0";
            }
        } else {
            return "0";
        }
    }

    @PostMapping("/projects/editing")
    public void isEditingProject(@AuthenticationPrincipal AuthState principal,
                                 @RequestParam String id) {
        boolean isTeacher = userAccountClientService.isTeacher(principal) && authenticateClientService.checkAuthState().getIsAuthenticated();
        int userId = Integer.parseInt(principal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("nameid"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("-100"));
        if (isTeacher && userId != -100) {
            editedAtTime = System.currentTimeMillis();
            editedProject = id;
        }
    }

}
