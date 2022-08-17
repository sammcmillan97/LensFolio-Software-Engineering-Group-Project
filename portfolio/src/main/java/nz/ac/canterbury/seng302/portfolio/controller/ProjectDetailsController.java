package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.project.*;
import nz.ac.canterbury.seng302.portfolio.model.user.User;
import nz.ac.canterbury.seng302.portfolio.service.project.*;
import nz.ac.canterbury.seng302.portfolio.service.user.PortfolioUserService;
import nz.ac.canterbury.seng302.portfolio.service.user.UserAccountClientService;
import nz.ac.canterbury.seng302.portfolio.util.ProjectDetailsUtil;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * The Controller for handling the backend of the project details page
 */
@Controller
public class ProjectDetailsController {

    @Autowired
    private ProjectService projectService;
    @Autowired
    private SprintService sprintService;
    @Autowired
    private EventService eventService;
    @Autowired
    private DeadlineService deadlineService;
    @Autowired
    private MilestoneService milestoneService;
    @Autowired
    private UserAccountClientService userAccountClientService;
    @Autowired
    private PortfolioUserService portfolioUserService;

    /**
     * The Get mapping for displaying the details of a specific project through the project details page. Will display a
     * different page based on if the current user is a teacher or student.
     * @param principal Authentication principal storing current user information
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @param id The ID of the project being displayed
     * @return The project page displaying the selected projects details
     */
    @GetMapping("/projectDetails-{id}")
    public String projectDetails(@AuthenticationPrincipal AuthState principal, Model model, @PathVariable("id") String id) {
        // Add user details to model
        User user = userAccountClientService.getUserAccountByPrincipal(principal);
        model.addAttribute("user", user);

        /* Add project details to the model */
        int projectId = Integer.parseInt(id);
        try {
            Project project = projectService.getProjectById(projectId);
            model.addAttribute("project", project);

            List<Sprint> sprintList = sprintService.getByParentProjectId(projectId);
            ProjectDetailsUtil.colorSprints(sprintList);
            List<Event> eventList = eventService.getByEventParentProjectId(projectId);
            ProjectDetailsUtil.embedEvents(eventList, sprintList);
            List<Deadline> deadlineList = deadlineService.getByDeadlineParentProjectId(projectId);
            ProjectDetailsUtil.embedDeadlines(deadlineList, sprintList);
            List<Milestone> milestoneList = milestoneService.getByMilestoneParentProjectId(projectId);
            ProjectDetailsUtil.embedMilestones(milestoneList, sprintList);
            List<Pair<Integer, String>> importantDates = ProjectDetailsUtil.getOrderedImportantDates(eventList, sprintList, deadlineList, milestoneList);

            model.addAttribute("sprintList", sprintList);
            model.addAttribute("eventList", eventList);
            model.addAttribute("deadlineList", deadlineList);
            model.addAttribute("milestoneList", milestoneList);
            model.addAttribute("importantDates", importantDates);
        } catch (NoSuchElementException e) {
            return "redirect:/projects";
        }

        /* Return the name of the Thymeleaf template
        detects the role of the current user and returns appropriate page */
        if (userAccountClientService.isTeacher(principal)) {
            return "teacherProjectDetails";

        } else {
            return "userProjectDetails";
        }
    }

}
