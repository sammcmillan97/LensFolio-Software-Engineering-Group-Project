package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Event;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.service.EventService;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Controller for the edit event page
 */
@Controller
public class AddEditEventController {

    @Autowired
    UserAccountClientService userAccountClientService;
    @Autowired
    ProjectService projectService;
    @Autowired
    EventService eventService;


    /**
     * The get mapping to return the page to add/edit an event
     */
    @GetMapping("/projects/edit/event/{parentProjectId}/{eventId}")
    public String eventForm(@AuthenticationPrincipal AuthState principal,
                            @PathVariable("parentProjectId") String parentProjectId,
                            @PathVariable("eventId") String eventId,
                            Model model) throws Exception {

        //Check User is a teacher otherwise return to project page
        if (!userAccountClientService.isTeacher(principal)) {
            return "redirect:/projects";
        }

        // Add user details to model for displaying in top banner
        int userId = Integer.parseInt(principal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("nameid"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("-100"));
        UserResponse user = userAccountClientService.getUserAccountById(userId);
        model.addAttribute("user", user);

        // Add parent project ID
        int projectId = Integer.parseInt(parentProjectId);
        Project project = projectService.getProjectById(projectId);
        model.addAttribute("projectId", project.getId());

        Event event;
        if (Integer.parseInt(eventId) != -1) {
            event = eventService.getEventById(Integer.parseInt(eventId));
        } else {
            event = new Event();
            event.setEventName("EventName");
            event.setEventStartDate(project.getStartDate());
            event.setEventEndDate(project.getEndDate());
        }

        //Add event details to model
        model.addAttribute("eventName", event.getEventName());
        model.addAttribute("eventStartDate", Project.dateToString(event.getEventStartDate(), "yyyy-MM-dd"));
        model.addAttribute("eventEndDate", Project.dateToString(event.getEventEndDate(), "yyyy-MM-dd"));

        model.addAttribute("minEventStartDate", Project.dateToString(project.getStartDate(), "yyyy-MM-dd"));
        model.addAttribute("maxEventEndDate", Project.dateToString(project.getEndDate(), "yyyy-MM-dd"));


        return "addEditEvent";
    }


}
