package nz.ac.canterbury.seng302.portfolio.repository;

import nz.ac.canterbury.seng302.portfolio.model.project.Event;
import nz.ac.canterbury.seng302.portfolio.model.project.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.AssertionsForClassTypes.*;

@DataJpaTest
class EventRepositoryTests {

    @Autowired private DataSource dataSource;
    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private EntityManager entityManager;
    @Autowired private EventRepository eventRepository;
    @Autowired private ProjectRepository projectRepository;

    @BeforeEach
    void cleanDatabase() {
        eventRepository.deleteAll();
    }

    @Test
    void injectedComponentsAreNotNull() {
        assertThat(dataSource).isNotNull();
        assertThat(jdbcTemplate).isNotNull();
        assertThat(entityManager).isNotNull();
        assertThat(eventRepository).isNotNull();
    }

    @Test
    void givenMultipleEventsExist_findAllEvents() {
        Project project1 = new Project("Project1", "Test Project", Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06"));
        Event event1 = new Event(project1.getId(), "Event 1", Date.valueOf("2022-05-05"), Date.valueOf("2022-05-20"));
        Event event2 = new Event(project1.getId(), "Event 2", Date.valueOf("2022-05-21"), Date.valueOf("2022-06-05"));
        List<Event> events = new ArrayList<>();
        events.add(event1);
        events.add(event2);
        projectRepository.save(project1);
        eventRepository.saveAll(events);

        List<Event> eventsFromDatabase = StreamSupport.stream(eventRepository.findAll().spliterator(), false).toList();
        assertThat(eventsFromDatabase.get(0)).isNotNull();
        assertThat(eventsFromDatabase.get(0).getEventId()).isEqualTo(events.get(0).getEventId());
        assertThat(eventsFromDatabase.get(0).getEventName()).isEqualTo(events.get(0).getEventName());
        assertThat(eventsFromDatabase.get(0).getEventStartDate()).isEqualTo(events.get(0).getEventStartDate());
        assertThat(eventsFromDatabase.get(0).getEventEndDate()).isEqualTo(events.get(0).getEventEndDate());

        assertThat(eventsFromDatabase.get(1)).isNotNull();
        assertThat(eventsFromDatabase.get(1).getEventId()).isEqualTo(events.get(1).getEventId());
        assertThat(eventsFromDatabase.get(1).getEventName()).isEqualTo(events.get(1).getEventName());
        assertThat(eventsFromDatabase.get(1).getEventStartDate()).isEqualTo(events.get(1).getEventStartDate());
        assertThat(eventsFromDatabase.get(1).getEventEndDate()).isEqualTo(events.get(1).getEventEndDate());
    }

    @Test
    void givenValidId_findEventById() {
        Project project1 = new Project("Project1", "Test Project", Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06"));
        Event event1 = new Event(project1.getId(), "Event 1", Date.valueOf("2022-05-05"), Date.valueOf("2022-05-20"));
        Event event2 = new Event(project1.getId(), "Event 2", Date.valueOf("2022-05-21"), Date.valueOf("2022-06-05"));
        List<Event> events = new ArrayList<>();
        events.add(event1);
        events.add(event2);
        projectRepository.save(project1);
        eventRepository.saveAll(events);

        Event retrievedEvent1 = eventRepository.findById(events.get(0).getEventId());
        Event retrievedEvent2 = eventRepository.findById(events.get(1).getEventId());

        assertThat(retrievedEvent1).isNotNull();
        assertThat(retrievedEvent1.getEventId()).isEqualTo(events.get(0).getEventId());
        assertThat(retrievedEvent1.getEventName()).isEqualTo(events.get(0).getEventName());
        assertThat(retrievedEvent1.getEventStartDate()).isEqualTo(events.get(0).getEventStartDate());
        assertThat(retrievedEvent1.getEventEndDate()).isEqualTo(events.get(0).getEventEndDate());

        assertThat(retrievedEvent2).isNotNull();
        assertThat(retrievedEvent2.getEventId()).isEqualTo(events.get(1).getEventId());
        assertThat(retrievedEvent2.getEventName()).isEqualTo(events.get(1).getEventName());
        assertThat(retrievedEvent2.getEventStartDate()).isEqualTo(events.get(1).getEventStartDate());
        assertThat(retrievedEvent2.getEventEndDate()).isEqualTo(events.get(1).getEventEndDate());
    }

    @Test
    void givenValidEventDetals_addEventViaRepository() {
        Project project1 = new Project("Project1", "Test Project", Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06"));
        Event event1 = new Event(project1.getId(), "Event 1", Date.valueOf("2022-05-05"), Date.valueOf("2022-05-20"));
        projectRepository.save(project1);
        eventRepository.save(event1);

        //Check that the event was inserted correctly
        Event retrievedEvent = eventRepository.findById(event1.getEventId());
        assertThat(retrievedEvent).isNotNull();
        assertThat(retrievedEvent.getEventId()).isEqualTo(event1.getEventId());
        assertThat(retrievedEvent.getEventName()).isEqualTo(event1.getEventName());
        assertThat(retrievedEvent.getEventStartDate()).isEqualTo(event1.getEventStartDate());
        assertThat(retrievedEvent.getEventEndDate()).isEqualTo(event1.getEventEndDate());
    }
}
