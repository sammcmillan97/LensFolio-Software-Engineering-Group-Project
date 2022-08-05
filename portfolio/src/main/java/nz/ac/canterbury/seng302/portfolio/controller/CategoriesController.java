package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Categories;
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

import java.util.List;
import java.util.Objects;

@Controller
public class CategoriesController {

    @Autowired
    private UserAccountClientService userService;

    @Autowired
    private EvidenceService evidenceService;

    @Autowired
    private PortfolioUserService portfolioUserService;

    private static final String PORTFOLIO_REDIRECT = "redirect:/portfolio";

    /**
     * Get request for getting all evidence pieces for the current user with the defined category
     */
    @GetMapping("/portfolio-categories-{category}")
    public String getEvidenceWithCategory(
            @AuthenticationPrincipal AuthState principal,
           @PathVariable("category") String category,
            Model model) {

        User user = userService.getUserAccountByPrincipal(principal);
        model.addAttribute("user", user);
        model.addAttribute("pageUser", user);
        model.addAttribute("owner", true);
        int userId = user.getId();
        int projectId = portfolioUserService.getUserById(userId).getCurrentProject();

        Categories categorySelection;
        if (Objects.equals(category, "quantitative")) {
            categorySelection = Categories.QUANTITATIVE;
        } else if (Objects.equals(category, "qualitative")) {
            categorySelection = Categories.QUALITATIVE;
        } else  if (Objects.equals(category, "service")) {
            categorySelection = Categories.SERVICE;
        } else {
            return PORTFOLIO_REDIRECT;
        }

        List<Evidence> evidenceList = evidenceService.getEvidenceByCategoryForPortfolio(userId, projectId, categorySelection);
        model.addAttribute("evidenceList", evidenceList);
        return "portfolio";
    }

    /**
     * Get request for getting all evidence pieces for a selected user with the defined category
     */
    @GetMapping("/portfolio-categories-{userId}-{category}")
    public String getEvidenceWithCategorySelectedUser(
            @AuthenticationPrincipal AuthState principal,
            @PathVariable("userId") int userId,
           @PathVariable("category") String category,
            Model model
    ) {
        User user = userService.getUserAccountByPrincipal(principal);
        model.addAttribute("user", user);
        User pageUser = userService.getUserAccountById(userId);
        model.addAttribute("pageUser", pageUser);

        int projectId = portfolioUserService.getUserById(userId).getCurrentProject();

        Categories categorySelection;
        if (Objects.equals(category, "quantitative")) {
            categorySelection = Categories.QUANTITATIVE;
        } else if (Objects.equals(category, "qualitative")) {
            categorySelection = Categories.QUALITATIVE;
        } else  if (Objects.equals(category, "service")) {
            categorySelection = Categories.SERVICE;
        } else {
            return PORTFOLIO_REDIRECT;
        }

        List<Evidence> evidenceList = evidenceService.getEvidenceByCategoryForPortfolio(userId, projectId, categorySelection);
        model.addAttribute("evidenceList", evidenceList);

        if (Objects.equals(pageUser.getUsername(), "")) {
            return "redirect:/profile";
        } else if (user.getId() == pageUser.getId()) {
            return PORTFOLIO_REDIRECT; // Take user to their own portfolio if they try to view it
        } else {
            model.addAttribute("owner", false);
            return "portfolio";
        }
    }

}
