package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.*;
import nz.ac.canterbury.seng302.portfolio.service.EventService;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import nz.ac.canterbury.seng302.portfolio.service.SprintService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
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

        List<Sprint> sprintList = sprintService.getByParentProjectId(projectId);
        model.addAttribute("sprintList", sprintList);

        eventRepository.deleteAll();
        eventService.saveEvent(new Event(Integer.parseInt(id, 10), "Christmas", 1, Date.valueOf("2022-05-1"), Date.valueOf("2022-05-4")));
        eventService.saveEvent(new Event(Integer.parseInt(id, 10), "xmas", 2, Date.valueOf("2022-06-01"), Date.valueOf("2022-06-05")));
        eventService.saveEvent(new Event(Integer.parseInt(id, 10), "Christ", 3, Date.valueOf("2022-05-09"), Date.valueOf("2022-06-25")));

        List<Event> eventList = eventService.getByEventParentProjectId(projectId);
        List<Pair<Integer, String>> importantDates = new ArrayList<>();

        List<Event> removeList = new ArrayList<>();

        int completed = 0;
        for (int i = 0; i < eventList.size(); i++) {
            for (int w = 0; w < sprintList.size(); w++) {
                if ((eventList.get(i).getEventStartDate().after(sprintList.get(w).getStartDate()) || eventList.get(i).getEventStartDate().equals(sprintList.get(w).getStartDate())) && (eventList.get(i).getEventEndDate().before(sprintList.get(w).getEndDate()) || eventList.get(i).getEventEndDate().equals(sprintList.get(w).getEndDate()))){
                    sprintList.get(w).addEventsInside(i, 0);
                    completed = completed + 2;
                } else if ((eventList.get(i).getEventStartDate().after(sprintList.get(w).getStartDate()) || eventList.get(i).getEventStartDate().equals(sprintList.get(w).getStartDate())) && (eventList.get(i).getEventStartDate().before(sprintList.get(w).getEndDate()) || eventList.get(i).getEventStartDate().equals(sprintList.get(w).getEndDate()))) {
                    sprintList.get(w).addEventsInside(i, 1);
                    completed++;
                } else if ((eventList.get(i).getEventEndDate().after(sprintList.get(w).getStartDate()) || eventList.get(i).getEventEndDate().equals(sprintList.get(w).getStartDate())) && (eventList.get(i).getEventEndDate().before(sprintList.get(w).getEndDate()) || eventList.get(i).getEventEndDate().equals(sprintList.get(w).getEndDate()))) {
                    sprintList.get(w).addEventsInside(i, 2);
                    completed++;
                } else if (eventList.get(i).getEventStartDate().before(sprintList.get(w).getStartDate()) && eventList.get(i).getEventEndDate().after(sprintList.get(w).getEndDate())) {
                    sprintList.get(w).addEventsInside(i, 3);
                }
            }
            if (completed >= 2) {
                eventList.get(i).setCompleted(true);
            }
        }


        int eventListSize = 0;
        for (Event value : eventList) {
            if (!value.isCompleted()) {
                eventListSize++;
            }
        }

        int sprintCounter = 0;
        int eventCounter = 0;
        while ((sprintCounter < sprintList.size()) && (eventCounter < eventListSize)) {
            if (!eventList.get(eventCounter).isCompleted()) {
                if (sprintList.get(sprintCounter).getStartDate().before(eventList.get(eventCounter).getEventStartDate())) {
                    importantDates.add(Pair.of(sprintCounter, "Sprint"));
                    sprintCounter++;
                } else {
                    Event event = eventList.get(eventCounter);
                    event.setType("Event");
                    importantDates.add(Pair.of(eventCounter, "Event"));
                    eventCounter++;
                }
            }
        }
        if (sprintCounter < sprintList.size()) {
            while (sprintCounter < sprintList.size()) {
                Sprint sprint = sprintList.get(sprintCounter);
                sprint.setType("Sprint");
                importantDates.add(Pair.of(sprintCounter, "Sprint"));
                sprintCounter++;
            }
        } else {
            while (eventCounter < eventList.size()) {
                if (!eventList.get(eventCounter).isCompleted()) {
                    Event event = eventList.get(eventCounter);
                    event.setType("Event");
                    importantDates.add(Pair.of(eventCounter, "Event"));
                    eventCounter++;
                }
            }
        }


        model.addAttribute("eventList", eventList);
        model.addAttribute("importantDates", importantDates);
        int numObjects = importantDates.size();
        model.addAttribute("numImportantDates", numObjects);
        /* Return the name of the Thymeleaf template
        detects the role of the current user and returns appropriate page */
        if (userAccountClientService.isTeacher(principal)) {
            return "teacherProjectDetails";

        } else {
            return "userProjectDetails";
        }
    }

}
