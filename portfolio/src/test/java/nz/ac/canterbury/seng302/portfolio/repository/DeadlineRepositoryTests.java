package nz.ac.canterbury.seng302.portfolio.repository;

import nz.ac.canterbury.seng302.portfolio.model.project.Deadline;
import nz.ac.canterbury.seng302.portfolio.model.project.Project;
import nz.ac.canterbury.seng302.portfolio.repository.project.DeadlineRepository;
import nz.ac.canterbury.seng302.portfolio.repository.project.ProjectRepository;
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
class DeadlineRepositoryTests {
    
    @Autowired private DataSource dataSource;
    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private EntityManager entityManager;
    @Autowired private DeadlineRepository deadlineRepository;
    @Autowired private ProjectRepository projectRepository;
    
    @BeforeEach
    void cleanDatabase() {
        deadlineRepository.deleteAll();
    }
    
    @Test
    void injectedComponentsAreNotNull() {
        assertThat(dataSource).isNotNull();
        assertThat(jdbcTemplate).isNotNull();
        assertThat(entityManager).isNotNull();
        assertThat(deadlineRepository).isNotNull();
    }
    
    @Test
    void givenMultipleDeadlinesExist_findAllDeadlines() {
        Project project1 = new Project("Project1", "Test Project", Date.valueOf("2022-05-09"), Date.valueOf("2022-06-10"));
        Deadline deadline1 = new Deadline(project1.getId(), "Deadline 1",  Date.valueOf("2022-06-24"));
        Deadline deadline2 = new Deadline(project1.getId(), "Deadline 2", Date.valueOf("2022-06-10"));
        List<Deadline> deadlines = new ArrayList<>();
        deadlines.add(deadline1);
        deadlines.add(deadline2);
        projectRepository.save(project1);
        deadlineRepository.saveAll(deadlines);

        List<Deadline> deadlinesFromDatabase = StreamSupport.stream(deadlineRepository.findAll().spliterator(), false).toList();
        assertThat(deadlinesFromDatabase.get(0)).isNotNull();
        assertThat(deadlinesFromDatabase.get(0).getDeadlineId()).isEqualTo(deadlines.get(0).getDeadlineId());
        assertThat(deadlinesFromDatabase.get(0).getDeadlineName()).isEqualTo(deadlines.get(0).getDeadlineName());
        assertThat(deadlinesFromDatabase.get(0).getDeadlineDate()).isEqualTo(deadlines.get(0).getDeadlineDate());

        assertThat(deadlinesFromDatabase.get(1)).isNotNull();
        assertThat(deadlinesFromDatabase.get(1).getDeadlineId()).isEqualTo(deadlines.get(1).getDeadlineId());
        assertThat(deadlinesFromDatabase.get(1).getDeadlineName()).isEqualTo(deadlines.get(1).getDeadlineName());
        assertThat(deadlinesFromDatabase.get(1).getDeadlineDate()).isEqualTo(deadlines.get(1).getDeadlineDate());
    }
    
    @Test
    void givenValidId_findDeadlineById() {
        Project project1 = new Project("Project1", "Test Project", Date.valueOf("2022-05-09"), Date.valueOf("2022-06-10"));
        Deadline deadline1 = new Deadline(project1.getId(), "Deadline 1",  Date.valueOf("2022-06-24"));
        Deadline deadline2 = new Deadline(project1.getId(), "Deadline 2", Date.valueOf("2022-06-10"));
        List<Deadline> deadlines = new ArrayList<>();
        deadlines.add(deadline1);
        deadlines.add(deadline2);
        projectRepository.save(project1);
        deadlineRepository.saveAll(deadlines);
        
        Deadline retrievedDeadline1 = deadlineRepository.findById(deadline1.getDeadlineId());
        Deadline retrievedDeadline2 = deadlineRepository.findById(deadline2.getDeadlineId());

        assertThat(retrievedDeadline1).isNotNull();
        assertThat(retrievedDeadline1.getDeadlineId()).isEqualTo(deadlines.get(0).getDeadlineId());
        assertThat(retrievedDeadline1.getDeadlineName()).isEqualTo(deadlines.get(0).getDeadlineName());
        assertThat(retrievedDeadline1.getDeadlineDate()).isEqualTo(deadlines.get(0).getDeadlineDate());

        assertThat(retrievedDeadline2).isNotNull();
        assertThat(retrievedDeadline2.getDeadlineId()).isEqualTo(deadlines.get(1).getDeadlineId());
        assertThat(retrievedDeadline2.getDeadlineName()).isEqualTo(deadlines.get(1).getDeadlineName());
        assertThat(retrievedDeadline2.getDeadlineDate()).isEqualTo(deadlines.get(1).getDeadlineDate());
    }
    
    @Test
    void givenValidDeadline_addDeadlineViaRepository() {
        Project project1 = new Project("Project1", "Test Project", Date.valueOf("2022-05-09"), Date.valueOf("2022-06-10"));
        Deadline deadline = new Deadline(project1.getId(), "Deadline", Date.valueOf("2022-06-24"));
        projectRepository.save(project1);
        deadlineRepository.save(deadline);
        
        //Check that the deadline was inserted correctly
        Deadline retrievedDeadline = deadlineRepository.findById(deadline.getDeadlineId());
        assertThat(retrievedDeadline).isNotNull();
        assertThat(retrievedDeadline.getDeadlineId()).isEqualTo(deadline.getDeadlineId());
        assertThat(retrievedDeadline.getDeadlineName()).isEqualTo(deadline.getDeadlineName());
        assertThat(retrievedDeadline.getDeadlineDate()).isEqualTo(deadline.getDeadlineDate());
    }
}
