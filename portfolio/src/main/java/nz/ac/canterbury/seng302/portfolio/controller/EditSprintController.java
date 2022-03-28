package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import nz.ac.canterbury.seng302.portfolio.service.SprintService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.Date;


/**
 * Controller for the edit sprint details page
 */
@Controller
public class EditSprintController {

    @Autowired
    UserAccountClientService userAccountClientService;
    @Autowired
    ProjectService projectService;
    @Autowired
    SprintService sprintService;

    private Sprint defaultSprint = new Sprint(-1, "A Sprint", "Sprint #", "Here's a description", new Date(), new Date());

    @GetMapping("/projects/edit/{parentProjectId}/{sprintId}")
    public String sprintForm(@AuthenticationPrincipal AuthState principal,
                             @PathVariable("parentProjectId") String parentProjectId,
                             @PathVariable("sprintId") String sprintId,
                             Model model) throws Exception {
        String role = userAccountClientService.getRole(principal);
        if (!role.contains("teacher")) {
            return "redirect:/projects";
        }

        // Add user details to model
        Integer userId = Integer.valueOf(principal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("nameid"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("-100"));
        UserResponse user = userAccountClientService.getUserAccountById(userId);
        model.addAttribute("user", user);

        // Add project/sprint details to model
        Project project = projectService.getProjectById(Integer.parseInt(parentProjectId));
        model.addAttribute("project", project);

        if (Integer.parseInt(sprintId) != -1) {
            Sprint sprint = sprintService.getSprintById(Integer.parseInt(sprintId));
            model.addAttribute("sprint", sprint);
        } else {
            model.addAttribute("sprint", defaultSprint);
        }

        /* Return the name of the Thymeleaf template */
        return "editSprint";
    }

    @PostMapping("/projects/edit/{parentProjectId}/{sprintId}")
    public String sprintSave(
            @AuthenticationPrincipal AuthState principal,
            @RequestParam(value="sprintName") String sprintName,
            @RequestParam(value="sprintStartDate") String sprintStartDate,
            @RequestParam(value="sprintEndDate") String sprintEndDate,
            @RequestParam(value="sprintDescription") String sprintDescription,
            Model model
    ) {
        return "redirect:/edit-sprint";
    }

    @DeleteMapping(value="/projects/delete/{parentProjectId}/{sprintId}")
    public String deleteProjectById(@AuthenticationPrincipal AuthState principal,
                                    @PathVariable("parentProjectId") String parentProjectId,
                                    @PathVariable("sprintId") String sprintId) {
        String role = userAccountClientService.getRole(principal);
        if (!role.contains("teacher")) {
            return "redirect:/projects";
        }

        sprintService.deleteById(Integer.parseInt(sprintId));
        return "redirect:/projects";
    }

}
