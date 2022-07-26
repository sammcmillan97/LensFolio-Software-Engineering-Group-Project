package nz.ac.canterbury.seng302.portfolio.service;

import io.cucumber.java.an.E;
import nz.ac.canterbury.seng302.portfolio.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureTestDatabase
@SpringBootTest
class EvidenceServiceTests {

    @Autowired
    EvidenceService evidenceService;

    @Autowired
    ProjectService projectService;

    @Autowired
    EvidenceRepository evidenceRepository;

    static List<Project> projects;

    //Initialise the database with projects before each test.
    @BeforeEach
    void storeProjects() {
        projectService.saveProject(new Project("Project Name", "Test Project", Date.valueOf("2022-04-9"), Date.valueOf("2022-06-16")));
        projectService.saveProject(new Project("Project Name", "Test Project", Date.valueOf("2022-05-9"), Date.valueOf("2022-05-16")));
        projects = projectService.getAllProjects();
    }

    //Refresh the database after each test.
    @AfterEach
    void cleanDatabase() {
        for (Project project : projects) {
            projectService.deleteProjectById(project.getId());
        }
        evidenceRepository.deleteAll();
    }

    //When the date of the evidence is out of range of the project, test it is rejected.
    @Test
    void whenDateOutOfRangeTestEvidenceRejected() {
        Evidence evidence = new Evidence(0, projects.get(1).getId(), "Test", "Test Evidence", Date.valueOf("2022-05-8"));
        assertThrows(IllegalArgumentException.class, () -> evidenceService.saveEvidence(evidence), "Date not valid");
        List<Evidence> evidenceList = evidenceService.getEvidenceForPortfolio(0, projects.get(1).getId());
        assertEquals(0, evidenceList.size());
    }

    //When the date of the evidence is in the range of the project, test it is accpeted.
    @Test
    void whenDateInRangeTestEvidenceSaved() {
        Evidence evidence = new Evidence(0, projects.get(1).getId(), "Test", "Test Evidence", Date.valueOf("2022-05-9"));
        evidenceService.saveEvidence(evidence);
        List<Evidence> evidenceList = evidenceService.getEvidenceForPortfolio(0, projects.get(1).getId());
        assertEquals(1, evidenceList.size());
        assertEquals(evidence.getDescription(), evidenceList.get(0).getDescription());
    }

}