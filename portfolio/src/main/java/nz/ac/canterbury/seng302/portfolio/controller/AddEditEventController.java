package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.*;
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

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

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
    @GetMapping("/editEvent-{eventId}-{parentProjectId}")
    public String eventForm(@AuthenticationPrincipal AuthState principal,
                            @PathVariable("parentProjectId") String parentProjectId,
                            @PathVariable("eventId") String eventId,
                            Model model) throws Exception {

        //Check User is a teacher otherwise return to project page
        if (!userAccountClientService.isTeacher(principal)) {
            return "redirect:/projects";
        }

        // Add user details to model for displaying in top banner
        int userId = userAccountClientService.getUserId(principal);
        User user = userAccountClientService.getUserAccountById(userId);
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
        model.addAttribute("eventStartDate", Project.dateToString(event.getEventStartDate(), "yyyy-MM-dd'T'HH:mm"));
        model.addAttribute("eventEndDate", Project.dateToString(event.getEventEndDate(), "yyyy-MM-dd'T'HH:mm"));

        // Add event date boundaries for event to the model
        model.addAttribute("minEventStartDate", Project.dateToString(project.getStartDate(), "yyyy-MM-dd'T'HH:mm"));
        model.addAttribute("maxEventEndDate", Project.dateToString(project.getEndDate(), "yyyy-MM-dd'T'HH:mm"));

        return "addEditEvent";
    }

    @PostMapping("/editEvent-{eventId}-{parentProjectId}")
    public String addEditEvent(
            @AuthenticationPrincipal AuthState principle,
            @PathVariable("parentProjectId") String projectIdString,
            @PathVariable("eventId") String eventIdString,
            @RequestParam(value="eventName") String eventName,
            @RequestParam(value="eventStartDate") String eventStart,
            @RequestParam(value="eventEndDate") String eventEnd,
            Model model) throws ParseException {
        //Check if it is a teacher making the request
        if (!userAccountClientService.isTeacher(principle)) {
            return "redirect:/projects";
        }
        // Ensure request parameters represent a valid sprint.
        // Check ids can be parsed
        int eventId;
        int projectId;

        // Convert String values of start and end date-time to timestamp
        Timestamp startDate = Timestamp.valueOf(eventStart.replace("T", " ") + ":00");
        Timestamp endDate = Timestamp.valueOf(eventEnd.replace("T", " ") + ":00");

        // Convert Timestamp values of start and end date-time to Date
        Date eventStartDate = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a").parse(Event.dateToString(startDate));
        Date eventEndDate = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a").parse(Event.dateToString(endDate));

        try {
            // Parse ids  from string
            eventId = Integer.parseInt(eventIdString);
            projectId = Integer.parseInt(projectIdString);
        } catch (NumberFormatException e) {
            return "redirect:/projects";
        }
        //Check if it's an existing event
        if(eventId == -1) {
            Event newEvent  = new Event(projectId, eventName, eventStartDate , eventEndDate);
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
        return "redirect:/projectDetails-" + projectIdString;
    }

    /**
     * The method which is used to delete an event from a project by using its id.
     * @param principal is the Authentication principal storing the current user information
     * @param parentProjectId is the id of the parent project of the event being deleted
     * @param eventId is the id of the event being deleted
     * @return the project page of the parent project
     */
    @DeleteMapping(value="/editEvent-{eventId}-{parentProjectId}")
    public String deleteProjectEventById(@AuthenticationPrincipal AuthState principal,
                                         @PathVariable("parentProjectId") String parentProjectId,
                                         @PathVariable("eventId") String eventId) {
        if (!userAccountClientService.isTeacher(principal)) {
            return "redirect:/projects";
        }

        eventService.deleteEventById(Integer.parseInt(eventId));
        return "redirect:/projectDetails-" + parentProjectId;
    }
}
