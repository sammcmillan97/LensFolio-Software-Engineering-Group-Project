package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.*;
import nz.ac.canterbury.seng302.portfolio.service.EventService;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import nz.ac.canterbury.seng302.portfolio.service.SprintService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.portfolio.util.ProjectDetailsUtil;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

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
    private EventRepository eventRepository;
    @Autowired
    private UserAccountClientService userAccountClientService;

    /**
     * The Get mapping for displaying the details of a specific project through the project details page. Will display a
     * different page based on if the current user is a teacher or student.
     * @param principal Authentication principal storing current user information
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @param id The ID of the project being displayed
     * @return The project page displaying the selected projects details
     */
    @GetMapping("/projects/{id}")
    public String projectDetails(@AuthenticationPrincipal AuthState principal, Model model, @PathVariable("id") String id) {
        // Add user details to model
        int userId = Integer.parseInt(principal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("nameid"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("-100"));
        UserResponse user = userAccountClientService.getUserAccountById(userId);
        model.addAttribute("user", user);

        /* Add project details to the model */
        int projectId = Integer.parseInt(id);
        Project project = projectService.getProjectById(projectId);
        model.addAttribute("project", project);

        eventRepository.deleteAll();
        eventService.saveEvent(new Event(Integer.parseInt(id, 10), "Christmas", 1, Date.valueOf("2022-05-1"), Date.valueOf("2022-05-4")));
        eventService.saveEvent(new Event(Integer.parseInt(id, 10), "xmas", 2, Date.valueOf("2022-06-01"), Date.valueOf("2022-06-05")));
        eventService.saveEvent(new Event(Integer.parseInt(id, 10), "Christ", 3, Date.valueOf("2022-05-09"), Date.valueOf("2022-06-25")));

        List<Sprint> sprintList = sprintService.getByParentProjectId(projectId);
        List<Event> eventList = eventService.getByEventParentProjectId(projectId);
        ProjectDetailsUtil.embedEvents(eventList, sprintList);
        List<Pair<Integer, String>> importantDates = ProjectDetailsUtil.getOrderedImportantDates(eventList, sprintList);

        model.addAttribute("sprintList", sprintList);
        model.addAttribute("eventList", eventList);
        model.addAttribute("importantDates", importantDates);
        model.addAttribute("eventColor", "#ff0000");
        /* Return the name of the Thymeleaf template
        detects the role of the current user and returns appropriate page */
        if (userAccountClientService.isTeacher(principal)) {
            return "teacherProjectDetails";

        } else {
            return "userProjectDetails";
        }
    }

}
