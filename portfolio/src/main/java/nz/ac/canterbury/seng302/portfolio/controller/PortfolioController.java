package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.User;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Objects;

/**
 * The controller for handling backend of the evidence list page
 */
@Controller
public class PortfolioController {

    @Autowired
    private UserAccountClientService userService;

    /**
     * Display the user's portfolio page.
     * @param principal Authentication state of client
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @return The string "evidenceList"
     */
    @GetMapping("/portfolio-{userId}")
    public String profile(
            @AuthenticationPrincipal AuthState principal,
            @PathVariable("userId") int userId,
            Model model
    ) {
        User user = userService.getUserAccountByPrincipal(principal);
        model.addAttribute("user", user);
        User pageUser = userService.getUserAccountById(userId);
        model.addAttribute("pageUser", pageUser);
        if (Objects.equals(pageUser.getUsername(), "")) {
            return "redirect:/profile";
        } else {
            return "portfolio";
        }
    }
}

