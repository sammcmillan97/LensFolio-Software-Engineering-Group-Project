package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Evidence;
import nz.ac.canterbury.seng302.portfolio.model.User;
import nz.ac.canterbury.seng302.portfolio.service.EvidenceService;
import nz.ac.canterbury.seng302.portfolio.service.PortfolioUserService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The controller for handling backend of the portfolio page
 */
@Controller
public class PortfolioController {

    @Autowired
    private UserAccountClientService userService;

    @Autowired
    private EvidenceService evidenceService;

    @Autowired
    private PortfolioUserService portfolioUserService;



    /**
     * Display the user's portfolio page.
     * @param principal Authentication state of client
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @return The portfolio page.
     */
    @GetMapping("/portfolio")
    public String portfolio(
            @AuthenticationPrincipal AuthState principal,
            Model model
    ) {
        User user = userService.getUserAccountByPrincipal(principal);
        model.addAttribute("user", user);
        model.addAttribute("pageUser", user);
        model.addAttribute("owner", true);

        int userId = user.getId();
        int projectId = portfolioUserService.getUserById(userId).getCurrentProject();
        List<Evidence> evidenceList = evidenceService.getEvidenceForPortfolio(userId, projectId);

        model.addAttribute("evidenceList", evidenceList);
        return "portfolio";
    }

    /**
     * Display a user's portfolio page.
     * If the user does not exist redirects to the requesters profile page.
     * Users who try to view their own page are taken to a page with edit permissions.
     * @param principal Authentication state of client
     * @param userId The ID of the user whose portfolio we are viewing
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @return The portfolio page, or a redirect to profile if the user does not exist.
     */
    @GetMapping("/portfolio-{userId}")
    public String viewPortfolio(
            @AuthenticationPrincipal AuthState principal,
            @PathVariable("userId") int userId,
            Model model
    ) {
        User user = userService.getUserAccountByPrincipal(principal);
        model.addAttribute("user", user);

        User pageUser = userService.getUserAccountById(userId);
        model.addAttribute("pageUser", pageUser);

        int projectId = portfolioUserService.getUserById(userId).getCurrentProject();
        List<Evidence> evidenceList = evidenceService.getEvidenceForPortfolio(userId, projectId);

        model.addAttribute("evidenceList", evidenceList);

        if (Objects.equals(pageUser.getUsername(), "")) {
            return "redirect:/profile";
        } else if (user.getId() == pageUser.getId()) {
            return "redirect:/portfolio"; // Take user to their own portfolio if they try to view it
        } else {
            model.addAttribute("owner", false);
            return "portfolio";
        }
    }
}

