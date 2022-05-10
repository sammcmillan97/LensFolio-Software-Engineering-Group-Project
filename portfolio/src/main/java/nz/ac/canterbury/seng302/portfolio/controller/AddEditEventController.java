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
import org.springframework.web.bind.annotation.*;

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
        //Check if it is existing or new event
        if (Integer.parseInt(eventId) != -1) {
            event = eventService.getEventById(Integer.parseInt(eventId));
        } else {
            //Create new event
            event = new Event();
            event.setEventName("EventName");
            //Default start and end date is the project start and end date
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

    @PostMapping("/projects/edit/event/{parentProjectId}/{eventId}")
    public String addEditEvent(
            @AuthenticationPrincipal AuthState principle,
            @PathVariable("parentProjectId") String projectIdString,
            @PathVariable("eventId") String eventIdString,
            @RequestParam(value="eventName") String eventName,
            @RequestParam(value="eventStartDate") java.sql.Date eventStartDate,
            @RequestParam(value="eventEndDate") java.sql.Date eventEndDate,
            @RequestParam(value="eventStartTime") java.sql.Time eventStartTime,
            @RequestParam(value="eventEndTime") java.sql.Time eventEndTime,
            Model model) {
        //Check if it is a teacher making the request
        if (!userAccountClientService.isTeacher(principle)) {
            return "redirect:/projects";
        }
        System.out.println(eventEndTime);
        System.out.println(eventStartTime);
        // Ensure request parameters represent a valid sprint.
        // Check ids can be parsed
        int eventId;
        int projectId;
        try {
            // Parse ids  from string
            eventId = Integer.parseInt(eventIdString);
            projectId = Integer.parseInt(projectIdString);
        } catch (NumberFormatException e) {
            return "redirect:/projects";
        }
        //Check if it's an existing event
        if(eventId == -1) {
            Event newEvent  = new Event(projectId, eventName, eventStartDate.getTime() , eventEndDate);
            eventService.saveEvent(newEvent);
        } else {
            //Edit existing event
            try {
                Event existingEvent = eventService.getEventById(eventId);
                existingEvent.setEventName(eventName);
                eventService.updateStartDate(eventId, eventStartDate);
                eventService.updateEndDate(eventId, eventEndDate);
                eventService.saveEvent(existingEvent);
            } catch (Exception e) {
                System.out.println("Failed to update existing event");
            }
        }
        return "redirect:/projects/" + projectIdString;
    }

    /**
     * The method which is used to delete an event from a project by using its id.
     * @param principal is the Authentication principal storing the current user information
     * @param parentProjectId is the id of the parent project of the event being deleted
     * @param eventId is the id of the event being deleted
     * @return the project page of the parent project
     */
    @DeleteMapping(value="/projects/delete/event/{parentProjectId}/{eventId}")
    public String deleteProjectEventById(@AuthenticationPrincipal AuthState principal,
                                         @PathVariable("parentProjectId") String parentProjectId,
                                         @PathVariable("eventId") String eventId) {
        if (!userAccountClientService.isTeacher(principal)) {
            return "redirect:/projects";
        }

        eventService.deleteEventById(Integer.parseInt(eventId));
        return "redirect:/projects/" + parentProjectId;
    }
}
