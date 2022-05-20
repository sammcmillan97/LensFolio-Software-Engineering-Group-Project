package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

public class GroupsController {
    @GetMapping("/groups")
    public String groups(@AuthenticationPrincipal AuthState principal, Model model){

    }
}
