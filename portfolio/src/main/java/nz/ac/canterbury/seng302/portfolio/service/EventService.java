package nz.ac.canterbury.seng302.portfolio.service;


import nz.ac.canterbury.seng302.portfolio.model.Event;
import nz.ac.canterbury.seng302.portfolio.model.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private ProjectEditsService projectEditsService;
    private static final Logger PORTFOLIO_LOGGER = LoggerFactory.getLogger("com.portfolio");

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
     * Save the event to the repository
     */
    public void saveEvent(Event event) {
        projectEditsService.refreshProject(event.getEventParentProjectId());
        eventRepository.save(event);
    }

    /**
     * Delete the event by id
     * @param eventId the id of the event
     */
    public void deleteEventById(int eventId) {
        projectEditsService.refreshProject(eventRepository.findById(eventId).getEventParentProjectId());
        eventRepository.deleteById(eventId);
    }


    /**
     * Updates the event start and end dates
     * @param eventId The event being edited
     * @param newStartDate The new event start date
     * @param newEndDate The new event end date
     * @throws UnsupportedOperationException The exception thrown if the start date proceeds the end date or the start or end dates
     * do not fall within the project dates.
     */
    public void updateEventDates(int eventId, Date newStartDate, Date newEndDate) throws UnsupportedOperationException {
        Event eventToChange = getEventById(eventId);
        Date projectStartDate = eventProjectService.getProjectById(eventToChange.getEventParentProjectId()).getStartDate();
        Date projectEndDate = eventProjectService.getProjectById(eventToChange.getEventParentProjectId()).getEndDate();
        if (newStartDate.compareTo(newEndDate) > 0) {
            throw new UnsupportedOperationException("Event start date must not proceed the end date");
        } else if (newStartDate.compareTo(projectStartDate) < 0 || newStartDate.compareTo(projectEndDate) > 0) {
            throw new UnsupportedOperationException("Event start date must be within the project dates");
        } else if (newEndDate.compareTo(projectStartDate) < 0 || newEndDate.compareTo(projectEndDate) > 0) {
            throw new UnsupportedOperationException("Event end date must be within the project dates");
        } else {
            eventToChange.setEventStartDate(newStartDate);
            eventToChange.setEventEndDate(newEndDate);
            saveEvent(eventToChange);
        }
    }
}
