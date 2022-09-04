package nz.ac.canterbury.seng302.portfolio.controller.evidence;

import nz.ac.canterbury.seng302.portfolio.model.evidence.Categories;
import nz.ac.canterbury.seng302.portfolio.model.evidence.Evidence;
import nz.ac.canterbury.seng302.portfolio.model.user.User;
import nz.ac.canterbury.seng302.portfolio.service.evidence.EvidenceService;
import nz.ac.canterbury.seng302.portfolio.service.user.PortfolioUserService;
import nz.ac.canterbury.seng302.portfolio.service.user.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

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
    private static final int MAX_WEBLINKS_PER_EVIDENCE = 5;

    /**
     * Get request for getting all evidence pieces for the current user with the defined category
     * @param principal Authentication state of client
     * @param category The selected category that each evidence will contain
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @return The evidence page with selected category
     */
    @GetMapping("/portfolio-categories")
    public String getEvidenceWithCategory(
            @AuthenticationPrincipal AuthState principal,
            @RequestParam("category") String category,
            Model model) {

        User user = userService.getUserAccountByPrincipal(principal);
        model.addAttribute("user", user);
        model.addAttribute("pageUser", user);
        model.addAttribute("owner", true);
        int userId = user.getId();
        int projectId = portfolioUserService.getUserById(userId).getCurrentProject();

        Categories categorySelection;
        List<Evidence> evidenceList;

        if (Objects.equals(category, "Quantitative")) {
            categorySelection = Categories.QUANTITATIVE;
            evidenceList = evidenceService.getEvidenceByCategoryForPortfolio(userId, projectId, categorySelection);
        } else if (Objects.equals(category, "Qualitative")) {
            categorySelection = Categories.QUALITATIVE;
            evidenceList = evidenceService.getEvidenceByCategoryForPortfolio(userId, projectId, categorySelection);
        } else  if (Objects.equals(category, "Service")) {
            categorySelection = Categories.SERVICE;
            evidenceList = evidenceService.getEvidenceByCategoryForPortfolio(userId, projectId, categorySelection);
        } else if (Objects.equals(category, "")) {
            evidenceList = evidenceService.retrieveEvidenceWithNoCategory(userId, projectId);
        } else {
            return PORTFOLIO_REDIRECT;
        }

        // Add all of the skills that the user has to the page
        List<Evidence> allUsersEvidenceList = evidenceService.getEvidenceForPortfolio(userId, projectId);
        model.addAttribute("maxWeblinks", MAX_WEBLINKS_PER_EVIDENCE);
        model.addAttribute("skillsList", evidenceService.getSkillsFromEvidence(allUsersEvidenceList));
        model.addAttribute("categoryName", category);
        model.addAttribute("evidenceList", evidenceList);
        return "templatesEvidence/categories";
    }

    /**
     * Get request for getting all evidence pieces for a selected user with the defined category
     * @param principal Authentication state of client
     * @param category The selected category that each evidence will contain
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @return The evidence page of the selected user with the selected category
     */
    @GetMapping("/portfolio-{userId}-categories")
    public String getEvidenceWithCategorySelectedUser(
            @AuthenticationPrincipal AuthState principal,
            @PathVariable("userId") int userId,
            @RequestParam("category") String category,
            Model model
    ) {
        User user = userService.getUserAccountByPrincipal(principal);
        model.addAttribute("user", user);
        User pageUser = userService.getUserAccountById(userId);
        model.addAttribute("pageUser", pageUser);

        int projectId = portfolioUserService.getUserById(userId).getCurrentProject();

        Categories categorySelection;
        List<Evidence> evidenceList;

        if (Objects.equals(category, "Quantitative")) {
            categorySelection = Categories.QUANTITATIVE;
            evidenceList = evidenceService.getEvidenceByCategoryForPortfolio(userId, projectId, categorySelection);
        } else if (Objects.equals(category, "Qualitative")) {
            categorySelection = Categories.QUALITATIVE;
            evidenceList = evidenceService.getEvidenceByCategoryForPortfolio(userId, projectId, categorySelection);
        } else  if (Objects.equals(category, "Service")) {
            categorySelection = Categories.SERVICE;
            evidenceList = evidenceService.getEvidenceByCategoryForPortfolio(userId, projectId, categorySelection);
        } else if (Objects.equals(category, "")) {
            evidenceList = evidenceService.retrieveEvidenceWithNoCategory(userId, projectId);
        } else {
            return PORTFOLIO_REDIRECT;
        }
        // Add all of the skills that the user has to the page
        List<Evidence> allUsersEvidenceList = evidenceService.getEvidenceForPortfolio(userId, projectId);
        model.addAttribute("skillsList", evidenceService.getSkillsFromEvidence(allUsersEvidenceList));

        model.addAttribute("evidenceList", evidenceList);
        model.addAttribute("categoryName", category);
        if (Objects.equals(pageUser.getUsername(), "")) {
            return "redirect:/profile";
        } else if (user.getId() == pageUser.getId()) {
            return PORTFOLIO_REDIRECT + "-categories?category=" + category; // Take user to their own portfolio if they try to view it
        } else {
            model.addAttribute("owner", false);
            return "templatesEvidence/categories";
        }
    }
}