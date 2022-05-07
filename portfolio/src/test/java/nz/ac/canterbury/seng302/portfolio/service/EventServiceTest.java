package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Event;
import nz.ac.canterbury.seng302.portfolio.model.EventRepository;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.ProjectRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@AutoConfigureTestDatabase
@SpringBootTest
class EventServiceTest {
    @Autowired
    EventService eventService;

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    EventRepository eventRepository;

    static List<Project> projects;

    /**
     * Initialise the database with projects before each test
     */
    @BeforeEach
    void storeProjects() {
        projectRepository.save(new Project("Project Name", "Test Project", Date.valueOf("2022-05-01"), Date.valueOf("2022-06-30")));
        projectRepository.save(new Project("Project Name", "Test Project", Date.valueOf("2022-05-01"), Date.valueOf("2022-06-30")));
        projects = (List<Project>) projectRepository.findAll();
    }

    /**
     * Refresh the database after each test
     */
    @AfterEach
    void cleanDatabase() {
        projectRepository.deleteAll();
        eventRepository.deleteAll();
    }

    /**
     * When there are no events in the database, test saving an event using event service
     */
    @Test
    void whenNoEvents_testSaveEventToSameProject() {
        List<Event> events = (List<Event>) eventRepository.findAll();
        assertThat(events.size()).isZero();
        eventService.saveEvent(new Event(projects.get(0).getId(), "Test Event", 1,
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        events = (List<Event>) eventRepository.findAll();
        assertThat(events.size()).isEqualTo(1);
    }

    /**
     * When there is an event in the database, test saving an event using event service
     */
    @Test
    void whenOneEvent_testSaveEventToSameProject() {
        eventService.saveEvent(new Event(projects.get(0).getId(), "Test Event", 1,
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        List<Event> events = (List<Event>) eventRepository.findAll();
        assertThat(events.size()).isEqualTo(1);
        eventService.saveEvent(new Event(projects.get(0).getId(), "Test Event Duo", 2,
                Date.valueOf("2022-06-07"), Date.valueOf("2022-07-07")));
        events = (List<Event>) eventRepository.findAll();
        assertThat(events.size()).isEqualTo(2);
    }

    /**
     * When there are many events in the database, test saving an event using event service
     */
    @Test
    void whenManyEvents_testSaveEventToSameProject() {
        eventService.saveEvent(new Event(projects.get(0).getId(), "Test Event Uno", 1,
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        eventService.saveEvent(new Event(projects.get(0).getId(), "Test Event Deux", 2,
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        eventService.saveEvent(new Event(projects.get(0).getId(), "Test Event Tri", 3,
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        eventService.saveEvent(new Event(projects.get(0).getId(), "Test Event Quad", 4,
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        List<Event> events = (List<Event>) eventRepository.findAll();
        assertThat(events.size()).isEqualTo(4);
        eventService.saveEvent(new Event(projects.get(0).getId(), "Test Event Pent", 5,
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        events = (List<Event>) eventRepository.findAll();
        assertThat(events.size()).isEqualTo(5);
    }

    /**
     * When there is one event in a project in the database, test saving an event to a different project.
     */
    @Test
    void whenOneEvent_testSaveEventToDifferentProject() {
        eventService.saveEvent(new Event(projects.get(0).getId(), "Test Event", 1,
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        List<Event> events = (List<Event>) eventRepository.findAll();
        assertThat(events.size()).isEqualTo(1);
        assertThat(projects.get(0).getId()).isNotEqualTo(projects.get(1).getId());
        eventService.saveEvent(new Event(projects.get(1).getId(), "Test Event Duo", 2,
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        events = (List<Event>) eventRepository.findAll();
        assertThat(events.size()).isEqualTo(2);
    }

    /**
     * When there are many events in each project in the database, test saving events to each different project.
     */
    @Test
    void whenManyEvents_testSaveEventToDifferentProject() {
        eventService.saveEvent(new Event(projects.get(0).getId(), "Test Event 1", 1,
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        eventService.saveEvent(new Event(projects.get(0).getId(), "Test Event 2", 2,
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        eventService.saveEvent(new Event(projects.get(0).getId(), "Test Event 3", 3,
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        eventService.saveEvent(new Event(projects.get(0).getId(), "Test Event 4", 4,
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        List<Event> events = (List<Event>) eventRepository.findAll();
        assertThat(events.size()).isEqualTo(4);
        assertThat(projects.get(0).getId()).isNotEqualTo(projects.get(1).getId());
        eventService.saveEvent(new Event(projects.get(1).getId(), "Test Event 5", 5,
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        eventService.saveEvent(new Event(projects.get(1).getId(), "Test Event 6", 6,
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        eventService.saveEvent(new Event(projects.get(1).getId(), "Test Event 7", 7,
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        events = (List<Event>) eventRepository.findAll();
        assertThat(events.size()).isEqualTo(7);
    }

    /**
     * When there are no events in the database, test retrieving all events using event service.
     */
    @Test
    void whenNoEventsSaved_testGetAllEvents() {
        List<Event> events = (List<Event>) eventRepository.findAll();
        assertThat(events.size()).isZero();
        assertThat(eventService.getAllEvents().size()).isZero();
    }

    /**
     * When there is one event in the database, test retrieving all events using event service
     */
    @Test
    void whenOneEventSaved_testGetAllEvents() {
        eventService.saveEvent(new Event(projects.get(0).getId(), "Test Event", 1,
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        List<Event> events = (List<Event>) eventRepository.findAll();
        assertThat(events.size()).isEqualTo(1);
        assertThat(eventService.getAllEvents().size()).isEqualTo(1);
    }

    /**
     * When there are many events in the database, test retrieving all events using event service.
     */
    @Test
    void whenManyEventsSaved_testGetAllEvents() {
        eventService.saveEvent(new Event(projects.get(0).getId(), "Test Event Uno", 1,
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        eventService.saveEvent(new Event(projects.get(0).getId(), "Test Event Duo", 2,
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        eventService.saveEvent(new Event(projects.get(0).getId(), "Test Event Tri", 3,
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        eventService.saveEvent(new Event(projects.get(0).getId(), "Test Event Quad", 4,
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        List<Event> events = (List<Event>) eventRepository.findAll();
        assertThat(events.size()).isEqualTo(4);
        assertThat(eventService.getAllEvents().size()).isEqualTo(4);
    }

    /**
     * When the event id does not exist, test retrieving the event by its id.
     */
    @Test
    void whenEventIdDoesNotExist_testGetEventById() throws Exception {
        List<Event> events = (List<Event>) eventRepository.findAll();
        assertThat(events.size()).isZero();
        Exception exception = assertThrows(Exception.class, () -> eventService.getEventById(999999));
        String expectedMessage = "Event not found";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    /**
     * When the event id does exist, test retrieving the event by its id.
     */
    @Test
    void whenEventIdExists_testGetEventById() throws Exception {
        eventService.saveEvent(new Event(projects.get(0).getId(), "Test Event", 1,
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        List<Event> events = (List<Event>) eventRepository.findAll();
        int eventId = events.get(0).getEventId();
        assertThat(eventId).isNotNull();
        Event event = eventService.getEventById(eventId);
        assertThat(event.getEventName()).isEqualTo("Test Event");
        assertThat(event.getEventNumber()).isEqualTo(1);
        assertThat(event.getEventStartDate()).isEqualTo(Timestamp.valueOf("2022-05-05 00:00:00"));
        assertThat(event.getEventEndDate()).isEqualTo(Timestamp.valueOf("2022-06-06 00:00:00"));
    }

    /**
     * When no events are saved to the database, test retrieving events by parent project id.
     */
    @Test
    void whenNoEventsSaved_testGetByParentProjectId() {
        List<Event> events = (List<Event>) eventRepository.findAll();
        assertThat(events.size()).isZero();
        List<Event> eventList = eventService.getByEventParentProjectId(projects.get(0).getId());
        assertThat(eventList.size()).isZero();
    }

    /**
     * When one event is saved to the database, test retrieving events by parent project id.
     */
    @Test
    void whenOneEventSaved_testGetByParentProjectId() {
        eventService.saveEvent(new Event(projects.get(0).getId(), "Test Event", 1,
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        List<Event> events = (List<Event>) eventRepository.findAll();
        assertThat(events.size()).isEqualTo(1);
        List<Event> eventList = eventService.getByEventParentProjectId(projects.get(0).getId());
        assertThat(eventList.size()).isEqualTo(1);
    }

    /**
     * When many events are saved to the database, test retrieving events by parent project id.
     */
    @Test
    void whenManyEventsSaved_testGetByParentProjectId() {
        eventService.saveEvent(new Event(projects.get(0).getId(), "Test Event 1", 1,
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        eventService.saveEvent(new Event(projects.get(0).getId(), "Test Event 2", 2,
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        eventService.saveEvent(new Event(projects.get(0).getId(), "Test Event 3", 3,
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        eventService.saveEvent(new Event(projects.get(0).getId(), "Test Event 4", 4,
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        List<Event> events = (List<Event>) eventRepository.findAll();
        assertThat(events.size()).isEqualTo(4);
        List<Event> eventList = eventService.getByEventParentProjectId(projects.get(0).getId());
        assertThat(eventList.size()).isEqualTo(4);
    }

    /**
     * When many events are saved to many projects in the database, test retrieve events by parent project id.
     */
    @Test
    void whenManyEventsSavedToDifferentProjects_testRetrieveByParentProjectId() {
        eventService.saveEvent(new Event(projects.get(0).getId(), "Test Event Uno", 1,
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        eventService.saveEvent(new Event(projects.get(0).getId(), "Test Event Duo", 2,
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        eventService.saveEvent(new Event(projects.get(1).getId(), "Test Event Tri", 3,
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        eventService.saveEvent(new Event(projects.get(1).getId(), "Test Event Quad", 4,
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        List<Event> events = (List<Event>) eventRepository.findAll();
        assertThat(events.size()).isEqualTo(4);
        List<Event> eventList = eventService.getByEventParentProjectId(projects.get(0).getId());
        int listSize = eventList.size();
        assertThat(listSize).isEqualTo(2);
        eventList = eventService.getByEventParentProjectId(projects.get(1).getId());
        int listSize2 = eventList.size();
        assertThat(listSize2).isEqualTo(2);
        assertThat(listSize + listSize2).isEqualTo(4);
    }

    /**
     * When event exists and its start date is changed to before the current date, and the new date is within project
     * boundaries, test event date start date is changed.
     */
    @Test
    void whenEventStartDateIsChangedToDateBeforeCurrentDateAndWithinProjectDates_testEventStartDateChanged() throws Exception {
        eventService.saveEvent(new Event(projects.get(0).getId(), "Test Event", 1,
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        List<Event> events = (List<Event>) eventRepository.findAll();
        int eventId = events.get(0).getEventId();
        eventService.updateStartDate(eventId, Date.valueOf("2022-05-01"));
        Event event = eventRepository.findById(eventId);
        assertThat(event.getEventStartDate()).isEqualTo(Timestamp.valueOf("2022-05-01 00:00:00"));
        ;
    }
}