package nz.ac.canterbury.seng302.portfolio.controller;


import nz.ac.canterbury.seng302.portfolio.model.Milestone;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.User;
import nz.ac.canterbury.seng302.portfolio.service.MilestoneService;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Controller for adding/editing deadlines
 */
public class EditMilestoneController {

    @Autowired
    UserAccountClientService userAccountClientService;

    @Autowired
    ProjectService projectService;

    @Autowired
    MilestoneService milestoneService;

    private final String timeFormat = "yyyy-MM-dd";
    private final String redirectToProjects = "redirect:/projects";

    @GetMapping("/editMilestone-{milestoneId}-{parentProjectId}")
    public String milestoneForm(@AuthenticationPrincipal AuthState principal,
                                @PathVariable("parentProjectId") String projectIdString,
                                @PathVariable("milestoneId") String milestoneIdString,
                                Model model) throws Exception {

        if (!userAccountClientService.isTeacher(principal)) {
            return redirectToProjects;
        }

        int userId = Integer.parseInt(principal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("nameid"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("-100"));
        User user = userAccountClientService.getUserAccountById(userId);
        model.addAttribute("user", user);

        int projectId;
        int milestoneId;

        try {
            projectId = Integer.parseInt(projectIdString);
            milestoneId = Integer.parseInt(milestoneIdString);
        } catch (NumberFormatException e) {
            return redirectToProjects;
        }
        Project project = projectService.getProjectById(projectId);
        Milestone milestone;
        if (milestoneId != -1) {
            milestone = milestoneService.getMilestoneById(milestoneId);
        } else {
            milestone = new Milestone(projectId, "Milestone name", project.getEndDate());
        }

        model.addAttribute("milestone", milestone);
        model.addAttribute("milestoneDate", Project.dateToString(milestone.getMilestoneDate(), timeFormat));
        model.addAttribute("minMilestoneDate", Project.dateToString(project.getStartDate(), timeFormat));
        model.addAttribute("maxMilestoneDate", Project.dateToString(project.getEndDate(), timeFormat));
        return "editMilestone";
    }

    @PostMapping("/editMilestone-{milestoneId}-{parentProjectId}")
    public String submitForm(
            @AuthenticationPrincipal AuthState principle,
            @PathVariable("parentProjectId") String projectIdString,
            @PathVariable("milestoneId") String milestoneIdString,
            @RequestParam(value = "milestoneName") String milestoneName,
            @RequestParam(value = "milestoneDate") String milestoneDateString,
            Model model) throws Exception {

        if (!userAccountClientService.isTeacher(principle)) {
            return redirectToProjects;
        }

        Date milestoneDate = new SimpleDateFormat(timeFormat).parse(milestoneDateString);

        int milestoneId;
        int projectId;
        try {
            projectId = Integer.parseInt(projectIdString);
            milestoneId = Integer.parseInt(milestoneIdString);
        } catch (NumberFormatException e) {
            return redirectToProjects;
        }

        //check if creating or editing existing deadline
        if (milestoneId == -1) {
            try {
                milestoneService.createNewMilestone(projectId, milestoneName, milestoneDate);
            } catch (UnsupportedOperationException e) {
                return ("redirect:/editMilestone-" + milestoneIdString + "-" + projectIdString);
            }
        } else {
            try {
                milestoneService.updateMilestone(projectId, milestoneId, milestoneName, milestoneDate);
            } catch (UnsupportedOperationException e) {
                return ("redirect:/editMilestone-" + milestoneIdString + "-" + projectIdString);
            }
        }
        return "redirect:/projectDetails-" + projectIdString;
    }

}