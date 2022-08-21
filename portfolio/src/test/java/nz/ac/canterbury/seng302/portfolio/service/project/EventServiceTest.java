package nz.ac.canterbury.seng302.portfolio.service.project;

import nz.ac.canterbury.seng302.portfolio.model.project.Event;
import nz.ac.canterbury.seng302.portfolio.model.project.Project;
import nz.ac.canterbury.seng302.portfolio.repository.projcet.EventRepository;
import nz.ac.canterbury.seng302.portfolio.repository.projcet.ProjectRepository;
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
import static org.junit.jupiter.api.Assertions.*;

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
        assertTrue(events.isEmpty());
        eventService.saveEvent(new Event(projects.get(0).getId(), "Test Event",
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        events = (List<Event>) eventRepository.findAll();
        assertEquals(1, events.size());
    }

    /**
     * When there is an event in the database, test saving an event using event service
     */
    @Test
    void whenOneEvent_testSaveEventToSameProject() {
        eventService.saveEvent(new Event(projects.get(0).getId(), "Test Event",
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        List<Event> events = (List<Event>) eventRepository.findAll();
        assertEquals(1, events.size());
        eventService.saveEvent(new Event(projects.get(0).getId(), "Test Event Duo",
                Date.valueOf("2022-06-07"), Date.valueOf("2022-07-07")));
        events = (List<Event>) eventRepository.findAll();
        assertEquals(2, events.size());
    }

    /**
     * When there are many events in the database, test saving an event using event service
     */
    @Test
    void whenManyEvents_testSaveEventToSameProject() {
        eventService.saveEvent(new Event(projects.get(0).getId(), "Test Event Uno",
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        eventService.saveEvent(new Event(projects.get(0).getId(), "Test Event Deux",
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        eventService.saveEvent(new Event(projects.get(0).getId(), "Test Event Tri",
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        eventService.saveEvent(new Event(projects.get(0).getId(), "Test Event Quad",
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        List<Event> events = (List<Event>) eventRepository.findAll();
        assertEquals(4, events.size());
        eventService.saveEvent(new Event(projects.get(0).getId(), "Test Event Pent",
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        events = (List<Event>) eventRepository.findAll();
        assertEquals(5, events.size());
    }

    /**
     * When there is one event in a project in the database, test saving an event to a different project.
     */
    @Test
    void whenOneEvent_testSaveEventToDifferentProject() {
        eventService.saveEvent(new Event(projects.get(0).getId(), "Test Event",
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        List<Event> events = (List<Event>) eventRepository.findAll();
        assertEquals(1, events.size());
        assertThat(projects.get(0).getId()).isNotEqualTo(projects.get(1).getId());
        eventService.saveEvent(new Event(projects.get(1).getId(), "Test Event Duo",
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        events = (List<Event>) eventRepository.findAll();
        assertEquals(2, events.size());
    }

    /**
     * When there are many events in each project in the database, test saving events to each different project.
     */
    @Test
    void whenManyEvents_testSaveEventToDifferentProject() {
        eventService.saveEvent(new Event(projects.get(0).getId(), "Test Event 1",
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        eventService.saveEvent(new Event(projects.get(0).getId(), "Test Event 2",
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        eventService.saveEvent(new Event(projects.get(0).getId(), "Test Event 3",
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        eventService.saveEvent(new Event(projects.get(0).getId(), "Test Event 4",
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        List<Event> events = (List<Event>) eventRepository.findAll();
        assertEquals(4, events.size());
        assertThat(projects.get(0).getId()).isNotEqualTo(projects.get(1).getId());
        eventService.saveEvent(new Event(projects.get(1).getId(), "Test Event 5",
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        eventService.saveEvent(new Event(projects.get(1).getId(), "Test Event 6",
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        eventService.saveEvent(new Event(projects.get(1).getId(), "Test Event 7",
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        events = (List<Event>) eventRepository.findAll();
        assertEquals(7, events.size());
    }

    /**
     * When there are no events in the database, test retrieving all events using event service.
     */
    @Test
    void whenNoEventsSaved_testGetAllEvents() {
        List<Event> events = (List<Event>) eventRepository.findAll();
        assertTrue(events.isEmpty());
        assertTrue(eventService.getAllEvents().isEmpty());
    }

    /**
     * When there is one event in the database, test retrieving all events using event service
     */
    @Test
    void whenOneEventSaved_testGetAllEvents() {
        eventService.saveEvent(new Event(projects.get(0).getId(), "Test Event",
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        List<Event> events = (List<Event>) eventRepository.findAll();
        assertEquals(1, events.size());
        assertEquals(1, eventService.getAllEvents().size());
    }

    /**
     * When there are many events in the database, test retrieving all events using event service.
     */
    @Test
    void whenManyEventsSaved_testGetAllEvents() {
        eventService.saveEvent(new Event(projects.get(0).getId(), "Test Event Uno",
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        eventService.saveEvent(new Event(projects.get(0).getId(), "Test Event Duo",
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        eventService.saveEvent(new Event(projects.get(0).getId(), "Test Event Tri",
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        eventService.saveEvent(new Event(projects.get(0).getId(), "Test Event Quad",
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        List<Event> events = (List<Event>) eventRepository.findAll();
        assertEquals(4, events.size());
        assertEquals(4, eventService.getAllEvents().size());
    }

    /**
     * When the event id does not exist, test retrieving the event by its id.
     */
    @Test
    void whenEventIdDoesNotExist_testGetEventById() {
        List<Event> events = (List<Event>) eventRepository.findAll();
        assertTrue(events.isEmpty());
        Exception exception = assertThrows(Exception.class, () -> eventService.getEventById(999999));
        String expectedMessage = "Event " + 999999 + " not found";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    /**
     * When the event id does exist, test retrieving the event by its id.
     */
    @Test
    void whenEventIdExists_testGetEventById() {
        eventService.saveEvent(new Event(projects.get(0).getId(), "Test Event",
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        List<Event> events = (List<Event>) eventRepository.findAll();
        int eventId = events.get(0).getEventId();
        assertNotEquals(0, eventId);
        Event event = eventService.getEventById(eventId);
        assertThat(event.getEventName()).isEqualTo("Test Event");
        assertThat(event.getEventStartDate()).isEqualTo(Timestamp.valueOf("2022-05-05 00:00:00"));
        assertThat(event.getEventEndDate()).isEqualTo(Timestamp.valueOf("2022-06-06 00:00:00"));
    }

    /**
     * When no events are saved to the database, test retrieving events by parent project id.
     */
    @Test
    void whenNoEventsSaved_testGetByParentProjectId() {
        List<Event> events = (List<Event>) eventRepository.findAll();
        assertTrue(events.isEmpty());
        List<Event> eventList = eventService.getByEventParentProjectId(projects.get(0).getId());
        assertTrue(eventList.isEmpty());
    }

    /**
     * When one event is saved to the database, test retrieving events by parent project id.
     */
    @Test
    void whenOneEventSaved_testGetByParentProjectId() {
        eventService.saveEvent(new Event(projects.get(0).getId(), "Test Event",
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        List<Event> events = (List<Event>) eventRepository.findAll();
        assertEquals(1, events.size());
        List<Event> eventList = eventService.getByEventParentProjectId(projects.get(0).getId());
        assertEquals(1, eventList.size());
    }

    /**
     * When many events are saved to the database, test retrieving events by parent project id.
     */
    @Test
    void whenManyEventsSaved_testGetByParentProjectId() {
        eventService.saveEvent(new Event(projects.get(0).getId(), "Test Event 1",
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        eventService.saveEvent(new Event(projects.get(0).getId(), "Test Event 2",
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        eventService.saveEvent(new Event(projects.get(0).getId(), "Test Event 3",
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        eventService.saveEvent(new Event(projects.get(0).getId(), "Test Event 4",
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        List<Event> events = (List<Event>) eventRepository.findAll();
        assertEquals(4, events.size());
        List<Event> eventList = eventService.getByEventParentProjectId(projects.get(0).getId());
        assertEquals(4, eventList.size());
    }

    /**
     * When many events are saved to many projects in the database, test retrieve events by parent project id.
     */
    @Test
    void whenManyEventsSavedToDifferentProjects_testRetrieveByParentProjectId() {
        eventService.saveEvent(new Event(projects.get(0).getId(), "Test Event Uno",
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        eventService.saveEvent(new Event(projects.get(0).getId(), "Test Event Duo",
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        eventService.saveEvent(new Event(projects.get(1).getId(), "Test Event Tri",
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        eventService.saveEvent(new Event(projects.get(1).getId(), "Test Event Quad",
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        List<Event> events = (List<Event>) eventRepository.findAll();
        assertEquals(4, events.size());
        List<Event> eventList = eventService.getByEventParentProjectId(projects.get(0).getId());
        int listSize = eventList.size();
        assertThat(listSize).isEqualTo(2);
        eventList = eventService.getByEventParentProjectId(projects.get(1).getId());
        int listSize2 = eventList.size();
        assertThat(listSize2).isEqualTo(2);
        assertEquals(4, listSize + listSize2);
    }

    /**
     * When event exists and its start date is changed to before the current date, and the new date is within project
     * boundaries, test event date start date is changed.
     */
    @Test
    void whenEventStartDateIsChangedToDateBeforeCurrentDateAndWithinProjectDates_testEventStartDateChanged() {
        eventService.saveEvent(new Event(projects.get(0).getId(), "Test Event",
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        List<Event> events = (List<Event>) eventRepository.findAll();
        int eventId = events.get(0).getEventId();
        eventService.updateEventDates(eventId, Date.valueOf("2022-05-01"), Date.valueOf("2022-06-06"));
        Event event = eventRepository.findById(eventId);
        assertThat(event.getEventStartDate()).isEqualTo(Timestamp.valueOf("2022-05-01 00:00:00"));
    }

    /**
     * When event exists and its start date is changed to date before current and new date is after previous event date,
     * test start date changed
     */
    @Test
    void whenEventStartDateIsChangedToDateBeforeCurrentAndAfterPreviousEventDate_testStartDateChanged() {
        eventService.saveEvent(new Event(projects.get(0).getId(), "Test Event 1",
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        eventService.saveEvent(new Event(projects.get(0).getId(), "Test Event 2",
                Date.valueOf("2022-06-09"), Date.valueOf("2022-06-20")));
        List<Event> events = (List<Event>) eventRepository.findAll();
        int eventId = events.get(1).getEventId();
        eventService.updateEventDates(eventId, Date.valueOf("2022-06-07"), Date.valueOf("2022-06-20"));
        Event event = eventRepository.findById(eventId);
        assertThat(event.getEventStartDate()).isEqualTo(Timestamp.valueOf("2022-06-07 00:00:00"));
    }

    /**
     * When event exists and its start date is changed to before the current date, and the new date is not within project
     * dates, test exception is thrown.
     */
    @Test
    void whenEventStartDateIsChangedToDateBeforeCurrentDateAndNotWithinProjectDates_testExceptionThrown() {
        eventService.saveEvent(new Event(projects.get(0).getId(), "Test Event",
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        List<Event> events = (List<Event>) eventRepository.findAll();
        int eventId = events.get(0).getEventId();
        Exception exception = assertThrows(Exception.class, () -> eventService.updateEventDates(eventId, Date.valueOf("2022-04-20"), Date.valueOf("2022-06-06")));
        String expectedMessage = "start date (2022-04-20) must be within the project dates (2022-05-01 00:00:00.0 - 2022-06-30 00:00:00.0)";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    /**
     * When event exists and its start date is changed to after the current date, and the new date is within project
     * boundaries, test event date start date is changed.
     */
    @Test
    void whenEventStartDateIsChangedToDateAfterCurrentDateAndWithinProjectDates_testEventStartDateChanged() {
        eventService.saveEvent(new Event(projects.get(0).getId(), "Test Event",
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        List<Event> events = (List<Event>) eventRepository.findAll();
        int eventId = events.get(0).getEventId();
        eventService.updateEventDates(eventId, Date.valueOf("2022-05-30"), Date.valueOf("2022-06-06"));
        Event event = eventRepository.findById(eventId);
        assertThat(event.getEventStartDate()).isEqualTo(Timestamp.valueOf("2022-05-30 00:00:00"));
    }

    /**
     * When event exists and its start date is changed to date after current and new date is after event end date,
     * test exception is thrown.
     */
    @Test
    void whenEventStartDateIsChangedToDateAfterCurrentAndAfterEventEndDate_testExceptionThrown() {
        eventService.saveEvent(new Event(projects.get(0).getId(), "Test Event 1",
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        List<Event> events = (List<Event>) eventRepository.findAll();
        int eventId = events.get(0).getEventId();

        Exception exception = assertThrows(Exception.class, () -> eventService.updateEventDates(eventId, Date.valueOf("2022-06-20"), Date.valueOf("2022-06-06")));
        String expectedMessage = "start date (2022-06-20) must not proceed the end date (2022-06-06)";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    /**
     * When event exists and its end date is changed to before the current date, and the new date is within project
     * boundaries, test event date end date is changed.
     */
    @Test
    void whenEventEndDateIsChangedToDateBeforeCurrentDateAndWithinProjectDates_testEventEndDateChanged() {
        eventService.saveEvent(new Event(projects.get(0).getId(), "Test Event",
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        List<Event> events = (List<Event>) eventRepository.findAll();
        int eventId = events.get(0).getEventId();
        eventService.updateEventDates(eventId, Date.valueOf("2022-05-05"), Date.valueOf("2022-05-30"));
        Event event = eventRepository.findById(eventId);
        assertThat(event.getEventEndDate()).isEqualTo(Timestamp.valueOf("2022-05-30 00:00:00"));
    }

    /**
     * When event exists and its end date is changed to date before current and new date is before event start date,
     * test exception is thrown.
     */
    @Test
    void whenEventEndDateIsChangedToDateBeforeCurrentAndBeforeEventStartDate_testExceptionThrown() {
        eventService.saveEvent(new Event(projects.get(0).getId(), "Test Event 1",
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        List<Event> events = (List<Event>) eventRepository.findAll();
        int eventId = events.get(0).getEventId();
        Exception exception = assertThrows(Exception.class, () -> eventService.updateEventDates(eventId, Date.valueOf("2022-05-05"), Date.valueOf("2022-04-20")));
        String expectedMessage = "start date (2022-05-05) must not proceed the end date (2022-04-20)";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    /**
     * When event exists and its end date is changed to after the current date, and the new date is within project
     * boundaries, test event date start date is changed.
     */
    @Test
    void whenEventEndDateIsChangedToDateAfterCurrentDateAndWithinProjectDates_testEventEndDateChanged() {
        eventService.saveEvent(new Event(projects.get(0).getId(), "Test Event",
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        List<Event> events = (List<Event>) eventRepository.findAll();
        int eventId = events.get(0).getEventId();
        eventService.updateEventDates(eventId, Date.valueOf("2022-06-05"), Date.valueOf("2022-06-06"));
        Event event = eventRepository.findById(eventId);
        assertThat(event.getEventEndDate()).isEqualTo(Timestamp.valueOf("2022-06-06 00:00:00"));
    }

    /**
     * When event exists and its end date is changed to date after current and new date is not within project boundaries,
     * test exception is thrown.
     */
    @Test
    void whenEventEndDateIsChangedToDateAfterCurrentAndNotWithinProjectDates_testExceptionThrown() {
        eventService.saveEvent(new Event(projects.get(0).getId(), "Test Event 1",
                Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06")));
        List<Event> events = (List<Event>) eventRepository.findAll();
        int eventId = events.get(0).getEventId();

        Exception exception = assertThrows(Exception.class, () -> eventService.updateEventDates(eventId, Date.valueOf("2022-05-05"), Date.valueOf("2022-08-06")));
        String expectedMessage = "end date (2022-08-06) must be within the project dates (2022-05-01 00:00:00.0 - 2022-06-30 00:00:00.0)";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

}