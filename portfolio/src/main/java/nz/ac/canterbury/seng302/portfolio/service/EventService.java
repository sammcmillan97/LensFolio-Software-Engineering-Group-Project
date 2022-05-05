package nz.ac.canterbury.seng302.portfolio.service;


import nz.ac.canterbury.seng302.portfolio.model.Event;
import nz.ac.canterbury.seng302.portfolio.model.EventRepository;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// more info here https://codebun.com/spring-boot-crud-application-using-thymeleaf-and-spring-data-jpa/

@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private ProjectService eventProjectService;

    /**
     * Get a list of all events
     */
    public List<Event> getAllEvents() {
        return (List<Event>) eventRepository.findAll();
    }

    /**
     * Get event by id
     */
    public Event getEventById(Integer eventId) throws Exception {
        Optional<Event> event = eventRepository.findById(eventId);
        if (event.isPresent()) {
            return event.get();
        } else {
            throw new Exception("Event not found");
        }
    }

    /**
     * Get event by parent project id
     */
    public List<Event> getByEventParentProjectId(int projectId) {
        return eventRepository.findByEventParentProjectId(projectId);
    }

    /**
     * Get all events by parent project id
     */
    public Map<Integer, List<Event>> getAllByEventParentProjectId() {
        List<Project> eventProjects = eventProjectService.getAllProjects();

        Map<Integer, List<Event>> eventsByParentProject = new HashMap<>();
        for (Project eventProject : eventProjects) {
            int eventId = eventProject.getId();
            eventsByParentProject.put(eventId, getByEventParentProjectId(eventId));
        }
        return eventsByParentProject;
    }

    /**
     * Save the event to the repository
     */
    public Event saveEvent(Event event) {
        return eventRepository.save(event);
    }
    /**
     * Delete the event by id
     */
    public void deleteEventById(int eventId) {
        eventRepository.deleteById(eventId);
    }
}
