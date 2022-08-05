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
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class CategoriesController {

    @Autowired
    private UserAccountClientService userService;

    @Autowired
    private EvidenceService evidenceService;

    @Autowired
    private PortfolioUserService portfolioUserService;


    /**
     * Get request for getting all evidence pieces for the current user with the defined category
     */
    @GetMapping("/portfolio/category")
    public String getEvidenceWithCategory(
            @AuthenticationPrincipal AuthState principal,
//            @RequestParam("category") String category,
            Model model) {

        User user = userService.getUserAccountByPrincipal(principal);
        model.addAttribute("user", user);
        model.addAttribute("pageUser", user);
        model.addAttribute("owner", true);
        String category = "service";
        int userId = user.getId();
        int projectId = portfolioUserService.getUserById(userId).getCurrentProject();
        List<Evidence> evidenceList = evidenceService.getEvidenceByCategoryForPortfolio(userId, projectId, category);

        model.addAttribute("evidenceList", evidenceList);
        return "portfolio";

    }

}
