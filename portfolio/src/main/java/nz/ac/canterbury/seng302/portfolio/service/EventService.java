package nz.ac.canterbury.seng302.portfolio.service;


import nz.ac.canterbury.seng302.portfolio.model.Event;
import nz.ac.canterbury.seng302.portfolio.model.EventRepository;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

// more info here https://codebun.com/spring-boot-crud-application-using-thymeleaf-and-spring-data-jpa/

@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private ProjectService eventProjectService;

    @Autowired
    private ProjectEdits projectEdits;

    /**
     * Get a list of all events
     * @return the list of all existing events
     */
    public List<Event> getAllEvents() {
        return (List<Event>) eventRepository.findAll();
    }

    /**
     * Get the event by id
     * @param eventId the id of the event
     * @return the event which has the required id
     * @throws IllegalArgumentException when event is not found
     */
    public Event getEventById(Integer eventId) throws IllegalArgumentException {
        Optional<Event> event = eventRepository.findById(eventId);
        if (event.isPresent()) {
            return event.get();
        } else {
            throw new IllegalArgumentException("Event not found");
        }
    }

    /**
     * Get event by parent project id
     * @param projectId the id of the project
     * @return the list of events by the project id
     */
    public List<Event> getByEventParentProjectId(int projectId) {
        return eventRepository.findByEventParentProjectIdOrderByEventStartDate(projectId);
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
        projectEdits.refreshProject(event.getEventParentProjectId());
        return eventRepository.save(event);
    }

    /**
     * Delete the event by id
     * @param eventId the id of the event
     */
    public void deleteEventById(int eventId) {
        projectEdits.refreshProject(eventRepository.findById(eventId).getEventParentProjectId());
        eventRepository.deleteById(eventId);
    }

    /**
     * Update the start date of the event
     * @param eventId the id of the event to be updated
     * @param newStartDate the new date the start date should be updated to
     * @throws UnsupportedOperationException when the date is changed to a date outside the scope
     */
    public void updateStartDate(int eventId, Date newStartDate) throws UnsupportedOperationException {
        Event eventToChange = getEventById(eventId);
        Date projectStartDate = eventProjectService.getProjectById(eventToChange.getEventParentProjectId()).getStartDate();
        Date projectEndDate = eventProjectService.getProjectById(eventToChange.getEventParentProjectId()).getEndDate();

        if (newStartDate.compareTo(eventToChange.getEventEndDate()) > 0) {
            throw new UnsupportedOperationException("Event start date must not be after end date");
        } else if (newStartDate.compareTo(projectStartDate) < 0 || newStartDate.compareTo(projectEndDate) > 0) {
            throw new UnsupportedOperationException("Event start date must be within project dates");
        } else {
            eventToChange.setEventStartDate(newStartDate);
            saveEvent(eventToChange);
        }
    }

    /**
     * Updates the end date of the event
     * @param eventId the id of the event to be updated
     * @param newEndDate the new end date it should be updated to
     * @throws UnsupportedOperationException when the date it should update to is outside the scope
     */
    public void updateEndDate(int eventId, Date newEndDate) throws UnsupportedOperationException {
        Event eventToChange = getEventById(eventId);
        Date projectStartDate = eventProjectService.getProjectById(eventToChange.getEventParentProjectId()).getStartDate();
        Date projectEndDate = eventProjectService.getProjectById(eventToChange.getEventParentProjectId()).getEndDate();

        if (newEndDate.compareTo(eventToChange.getEventStartDate()) < 0) {
            throw new UnsupportedOperationException("Event end date must not be before start date");
        } else if (newEndDate.compareTo(projectStartDate) < 0 || newEndDate.compareTo(projectEndDate) > 0) {
            throw new UnsupportedOperationException("Event end date must be within project dates");
        } else {
            eventToChange.setEventEndDate(newEndDate);
            saveEvent(eventToChange);
        }
    }
}
