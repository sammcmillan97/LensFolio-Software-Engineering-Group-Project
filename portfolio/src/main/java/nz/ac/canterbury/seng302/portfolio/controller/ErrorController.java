package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorController {

    @Autowired
    UserAccountClientService userAccountClientService;

    @GetMapping("/errors")
    public String forbidden(@AuthenticationPrincipal AuthState principal) {
        if (!userAccountClientService.isLoggedIn(principal)) {
            return "redirect:/login";
        } else {
            return "redirect:/projects";
        }
    }
}