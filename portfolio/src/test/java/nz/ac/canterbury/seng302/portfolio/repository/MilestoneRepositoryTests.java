package nz.ac.canterbury.seng302.portfolio.repository;

import nz.ac.canterbury.seng302.portfolio.model.project.Milestone;
import nz.ac.canterbury.seng302.portfolio.model.project.Project;
import nz.ac.canterbury.seng302.portfolio.repository.project.MilestoneRepository;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class MilestoneRepositoryTests {

    @Autowired
    private DataSource dataSource;
    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private EntityManager entityManager;
    @Autowired private MilestoneRepository milestoneRepository;
    @Autowired private ProjectRepository projectRepository;

    @BeforeEach
    void cleanDatabase() {
        milestoneRepository.deleteAll();
    }

    @Test
    void injectedComponentsAreNotNull() {
        assertThat(dataSource).isNotNull();
        assertThat(jdbcTemplate).isNotNull();
        assertThat(entityManager).isNotNull();
        assertThat(milestoneRepository).isNotNull();
    }

    @Test
    void givenMultipleMilestoneExist_findAllMilestones() {
        Project project1 = new Project("Project1", "Test Project", Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06"));
        Milestone milestone1 = new Milestone(project1.getId(), "Milestone 1", Date.valueOf("2022-05-20"));
        Milestone milestone2 = new Milestone(project1.getId(), "Milestone 2", Date.valueOf("2022-05-22"));
        List<Milestone> milestones = new ArrayList<>();
        milestones.add(milestone1);
        milestones.add(milestone2);
        projectRepository.save(project1);
        milestoneRepository.saveAll(milestones);

        List<Milestone> milestonesFromDatabase = StreamSupport.stream(milestoneRepository.findAll().spliterator(), false).toList();
        assertThat(milestonesFromDatabase.get(0)).isNotNull();
        assertThat(milestonesFromDatabase.get(0).getId()).isEqualTo(milestones.get(0).getId());
        assertThat(milestonesFromDatabase.get(0).getMilestoneName()).isEqualTo(milestones.get(0).getMilestoneName());
        assertThat(milestonesFromDatabase.get(0).getMilestoneDate()).isEqualTo(milestones.get(0).getMilestoneDate());

        assertThat(milestonesFromDatabase.get(1)).isNotNull();
        assertThat(milestonesFromDatabase.get(1).getId()).isEqualTo(milestones.get(1).getId());
        assertThat(milestonesFromDatabase.get(1).getMilestoneName()).isEqualTo(milestones.get(1).getMilestoneName());
        assertThat(milestonesFromDatabase.get(1).getMilestoneDate()).isEqualTo(milestones.get(1).getMilestoneDate());
    }

    @Test
    void givenValidId_findMilestoneById() {
        Project project1 = new Project("Project1", "Test Project", Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06"));
        Milestone milestone1 = new Milestone(project1.getId(), "Milestone 1", Date.valueOf("2022-05-20"));
        Milestone milestone2 = new Milestone(project1.getId(), "Milestone 2", Date.valueOf("2022-05-22"));
        List<Milestone> milestones = new ArrayList<>();
        milestones.add(milestone1);
        milestones.add(milestone2);
        projectRepository.save(project1);
        milestoneRepository.saveAll(milestones);

        Milestone retrievedMilestone1 = milestoneRepository.findById(milestones.get(0).getId());
        Milestone retrievedMilestone2 = milestoneRepository.findById(milestones.get(1).getId());

        assertThat(retrievedMilestone1).isNotNull();
        assertThat(retrievedMilestone1.getId()).isEqualTo(milestones.get(0).getId());
        assertThat(retrievedMilestone1.getMilestoneName()).isEqualTo(milestones.get(0).getMilestoneName());
        assertThat(retrievedMilestone1.getMilestoneDate()).isEqualTo(milestones.get(0).getMilestoneDate());

        assertThat(retrievedMilestone2).isNotNull();
        assertThat(retrievedMilestone2.getId()).isEqualTo(milestones.get(1).getId());
        assertThat(retrievedMilestone2.getMilestoneName()).isEqualTo(milestones.get(1).getMilestoneName());
        assertThat(retrievedMilestone2.getMilestoneDate()).isEqualTo(milestones.get(1).getMilestoneDate());
    }

    @Test
    void givenValidDetails_addMilestoneViaRepository() {
        Project project1 = new Project("Project1", "Test Project", Date.valueOf("2022-05-05"), Date.valueOf("2022-06-06"));
        Milestone milestone1 = new Milestone(project1.getId(), "Milestone 1", Date.valueOf("2022-05-20"));
        projectRepository.save(project1);
        milestoneRepository.save(milestone1);

        //Check that the event was inserted correctly
        Milestone retrievedMilestone = milestoneRepository.findById(milestone1.getId());
        assertThat(retrievedMilestone).isNotNull();
        assertThat(retrievedMilestone.getId()).isEqualTo(milestone1.getId());
        assertThat(retrievedMilestone.getMilestoneName()).isEqualTo(milestone1.getMilestoneName());
        assertThat(retrievedMilestone.getMilestoneDate()).isEqualTo(milestone1.getMilestoneDate());
    }
}
