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
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Objects;

/**
 * The controller for handling backend of the skills page
 */
@Controller
public class SkillsController {

    @Autowired
    private UserAccountClientService userService;
    @Autowired
    EvidenceService evidenceService;
    @Autowired
    private PortfolioUserService portfolioUserService;

    /**
     * Finds the logged in user's id and then loads the evidence page using the more generic endpoint
     * @param principal Authentication state of client
     * @param skill The skill to search for
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @return A page with all of the user's evidence with the given skill
     */
    @GetMapping("/portfolio-skill")
    public String getEvidenceWithSkill(
                                        @AuthenticationPrincipal AuthState principal,
                                        @RequestParam("skill") String skill,
                                        Model model) {
        User user = userService.getUserAccountByPrincipal(principal);
        model.addAttribute("user", user);
        model.addAttribute("pageUser", user);
        model.addAttribute("owner", true);

        int userId = user.getId();

        int projectId = portfolioUserService.getUserById(userId).getCurrentProject();

        List<Evidence> evidenceWithSkillList = getEvidenceBySkill(skill, userId, projectId);

        model.addAttribute("evidenceList", evidenceWithSkillList);

        // Add all of the skills that the user has to the page
        List<Evidence> allUsersEvidenceList = evidenceService.getEvidenceForPortfolio(userId, projectId);
        model.addAttribute("skillsList", evidenceService.getSkillsFromEvidence(allUsersEvidenceList));

        String skillName = skill.replace("_", " ");
        model.addAttribute("skillName", skillName);

        return "skills";
    }

    /**
     * Fetches all evidence with the given skill and user and return a page with it in.
     * @param principal Authentication state of client
     * @param userId The id of the user whose evidence needs to be searched
     * @param skill The skill to search for
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @return A page with all of the user's evidence with the given skill
     */
    @GetMapping("/portfolio-{userId}-skill")
    public String getOtherUsersEvidenceWithSkill(
            @AuthenticationPrincipal AuthState principal,
            @PathVariable("userId") int userId,
            @RequestParam("skill") String skill,
            Model model) {

        User user = userService.getUserAccountByPrincipal(principal);
        model.addAttribute("user", user);
        User pageUser = userService.getUserAccountById(userId);

        int projectId = portfolioUserService.getUserById(userId).getCurrentProject();

        List<Evidence> evidenceWithSkillList = getEvidenceBySkill(skill, userId, projectId);

        // Add all of the skills that the user has to the page
        List<Evidence> allUsersEvidenceList = evidenceService.getEvidenceForPortfolio(userId, projectId);
        model.addAttribute("skillsList", evidenceService.getSkillsFromEvidence(allUsersEvidenceList));

        String skillName = skill.replace("_", " ");
        model.addAttribute("skillName", skillName);
        model.addAttribute("user", user);
        model.addAttribute("pageUser", pageUser);
        model.addAttribute("evidenceList", evidenceWithSkillList);

        if (Objects.equals(pageUser.getUsername(), "")) {
            return "redirect:/profile";
        } else if (user.getId() == pageUser.getId()) {
            return "redirect:/portfolio-skill?skill=" + skill; // Take user to their own skills if they try to view it
        } else {
            model.addAttribute("owner", false);
            return "skills";
        }
    }

    public List<Evidence> getEvidenceBySkill(String skill, int userId, int projectId){
        if (skill.length()==0){
            return evidenceService.retrieveEvidenceWithNoSkill(projectId);
        } else {
           return evidenceService.retrieveEvidenceBySkillAndUser(skill, userId, projectId);
        }
    }
}
