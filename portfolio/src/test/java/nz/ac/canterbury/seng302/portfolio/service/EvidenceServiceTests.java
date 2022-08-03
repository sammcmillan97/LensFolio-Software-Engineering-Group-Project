package nz.ac.canterbury.seng302.portfolio.service;

import io.cucumber.java.an.E;
import nz.ac.canterbury.seng302.portfolio.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureTestDatabase
@SpringBootTest
class EvidenceServiceTests {

    private static final String TEST_DESCRIPTION = "According to all know laws of aviation, there is no way a bee should be able to fly.";

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

    //When the date of the evidence is out of range of the project near the start, test it is rejected.
    @Test
    void whenDateOutOfRangeAtStart_testEvidenceRejected() {
        Evidence evidence = new Evidence(0, projects.get(1).getId(), "Test", TEST_DESCRIPTION, Date.valueOf("2022-05-8"));
        assertThrows(IllegalArgumentException.class, () -> evidenceService.saveEvidence(evidence), "Date not valid");
        List<Evidence> evidenceList = evidenceService.getEvidenceForPortfolio(0, projects.get(1).getId());
        assertEquals(0, evidenceList.size());
    }

    //When the date of the evidence is in the range of the project near the start, test it is accepted.
    @Test
    void whenDateInRangeAtStart_testEvidenceSaved() {
        Evidence evidence = new Evidence(0, projects.get(1).getId(), "Test", TEST_DESCRIPTION, Date.valueOf("2022-05-9"));
        evidenceService.saveEvidence(evidence);
        List<Evidence> evidenceList = evidenceService.getEvidenceForPortfolio(0, projects.get(1).getId());
        assertEquals(1, evidenceList.size());
        assertEquals(evidence.getDescription(), evidenceList.get(0).getDescription());
    }

    //When the date of the evidence is out of range of the project near the end, test it is rejected.
    @Test
    void whenDateOutOfRangeAtEnd_testEvidenceRejected() {
        Evidence evidence = new Evidence(0, projects.get(1).getId(), "Test", TEST_DESCRIPTION, Date.valueOf("2022-05-17"));
        assertThrows(IllegalArgumentException.class, () -> evidenceService.saveEvidence(evidence), "Date not valid");
        List<Evidence> evidenceList = evidenceService.getEvidenceForPortfolio(0, projects.get(1).getId());
        assertEquals(0, evidenceList.size());
    }

    //When the date of the evidence is in the range of the project near the end, test it is accepted.
    @Test
    void whenDateInRangeAtEnd_testEvidenceSaved() {
        Evidence evidence = new Evidence(0, projects.get(1).getId(), "Test", TEST_DESCRIPTION, Date.valueOf("2022-05-16"));
        evidenceService.saveEvidence(evidence);
        List<Evidence> evidenceList = evidenceService.getEvidenceForPortfolio(0, projects.get(1).getId());
        assertEquals(1, evidenceList.size());
        assertEquals(evidence.getOwnerId(), evidenceList.get(0).getOwnerId());
    }

    //When evidence has an invalid project, check that it does
    @Test
    void whenProjectDoesNotExist_testEvidenceRejected() {
        Evidence evidence = new Evidence(0, -1, "Test", TEST_DESCRIPTION, Date.valueOf("2022-05-17"));
        assertThrows(IllegalArgumentException.class, () -> evidenceService.saveEvidence(evidence), "Project does not exist");
        List<Evidence> evidenceList = evidenceService.getEvidenceForPortfolio(0, projects.get(1).getId());
        assertEquals(0, evidenceList.size());
    }

    //When fields are made of only special characters, they should be rejected.
    @Test
    void whenTitleOnlySpecialCharacters_testEvidenceRejected() {
        Evidence evidence = new Evidence(1, projects.get(1).getId(), "!!&683 7;'.} {-++++", TEST_DESCRIPTION, Date.valueOf("2022-05-14"));
        assertThrows(IllegalArgumentException.class, () -> evidenceService.saveEvidence(evidence), "Title not valid");
        List<Evidence> evidenceList = evidenceService.getEvidenceForPortfolio(1, projects.get(1).getId());
        assertEquals(0, evidenceList.size());
    }

    //When fields are only one letter long, they should be rejected.
    @Test
    void whenDescriptionOneLetterLong_testEvidenceRejected() {
        Evidence evidence = new Evidence(0, projects.get(1).getId(), "Test Title", "T", Date.valueOf("2022-05-14"));
        assertThrows(IllegalArgumentException.class, () -> evidenceService.saveEvidence(evidence), "Description not valid");
        List<Evidence> evidenceList = evidenceService.getEvidenceForPortfolio(0, projects.get(1).getId());
        assertEquals(0, evidenceList.size());
    }

    //When evidence is deleted, check that it has been.
    @Test
    void whenEvidenceDeleted_testIsDeleted() {
        Evidence evidence = new Evidence(0, projects.get(1).getId(), "Test", TEST_DESCRIPTION, Date.valueOf("2022-05-16"));
        evidenceService.saveEvidence(evidence);
        List<Evidence> evidenceList = evidenceService.getEvidenceForPortfolio(0, projects.get(1).getId());
        assertEquals(1, evidenceList.size());
        evidenceService.deleteById(evidenceList.get(0).getId());
        List<Evidence> deletedEvidenceList = evidenceService.getEvidenceForPortfolio(0, projects.get(1).getId());
        assertEquals(0, deletedEvidenceList.size());
    }

    //When evidence is added, check that can be accessed.
    @Test
    void whenEvidenceAdded_testEvidenceExists() {
        Evidence evidence = new Evidence(0, projects.get(1).getId(), "Test", TEST_DESCRIPTION, Date.valueOf("2022-05-16"));
        evidenceService.saveEvidence(evidence);
        List<Evidence> evidenceList = evidenceService.getEvidenceForPortfolio(0, projects.get(1).getId());
        Evidence receivedEvidence = evidenceService.getEvidenceById(evidenceList.get(0).getId());
        assertEquals(evidence.getDescription(), receivedEvidence.getDescription());
    }

    //When evidence is not added, check that it can't be accessed.
    @Test
    void whenEvidenceNotAdded_testCantGet() {
        assertThrows(NoSuchElementException.class, () -> evidenceService.getEvidenceById(-1), "Evidence not found");
    }

    //When evidence is added, check that it is chronologically ordered.
    @Test
    void whenEvidenceAdded_testIsInOrder() {
        Evidence evidence1 = new Evidence(0, projects.get(1).getId(), "Three", TEST_DESCRIPTION, Date.valueOf("2022-05-14"));
        evidenceService.saveEvidence(evidence1);
        Evidence evidence2 = new Evidence(0, projects.get(1).getId(), "One", TEST_DESCRIPTION, Date.valueOf("2022-05-16"));
        evidenceService.saveEvidence(evidence2);
        Evidence evidence3 = new Evidence(0, projects.get(1).getId(), "Two", TEST_DESCRIPTION, Date.valueOf("2022-05-15"));
        evidenceService.saveEvidence(evidence3);
        List<Evidence> evidenceList = evidenceService.getEvidenceForPortfolio(0, projects.get(1).getId());
        assertEquals(3, evidenceList.size());
        assertEquals("One", evidenceList.get(0).getTitle());
        assertEquals("Two", evidenceList.get(1).getTitle());
        assertEquals("Three", evidenceList.get(2).getTitle());
    }

    @Test
    @Transactional
    void whenEvidenceAdded_testSaveOneWebLink() {
        Evidence evidence1 = new Evidence(0, projects.get(1).getId(), "Three", TEST_DESCRIPTION, Date.valueOf("2022-05-14"));
        evidenceService.saveEvidence(evidence1);
        evidenceService.saveWebLink(evidence1.getId(), "http://localhost:9000/portfolio");
        Evidence receivedEvidence = evidenceService.getEvidenceById(evidence1.getId());
        assertEquals("http://localhost:9000/portfolio", receivedEvidence.getWebLinks().get(0));
    }

    @Test
    @Transactional
    void whenEvidenceAdded_testSaveMultipleWebLinks() {
        Evidence evidence1 = new Evidence(0, projects.get(1).getId(), "Three", TEST_DESCRIPTION, Date.valueOf("2022-05-14"));
        evidenceService.saveEvidence(evidence1);
        evidenceService.saveWebLink(evidence1.getId(), "http://localhost:9000/portfolio");
        evidenceService.saveWebLink(evidence1.getId(), "http://localhost:9000/portfolio");
        evidenceService.saveWebLink(evidence1.getId(), "http://localhost:9000/portfolio");
        evidenceService.saveWebLink(evidence1.getId(), "http://localhost:9000/portfolio");
        Evidence receivedEvidence = evidenceService.getEvidenceById(evidence1.getId());
        for (String s: receivedEvidence.getWebLinks()) {
            assertEquals("http://localhost:9000/portfolio", s);
        }
    }

    @Test
    @Transactional
    void whenNoEvidenceAdded_testWebLinkNotSaved() {
        Exception exception = assertThrows(Exception.class,
                () -> evidenceService.saveWebLink(0, "http://localhost:9000/portfolio"));
        String expectedMessage = "Evidence not found: web link not saved";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    //When the date of the evidence is out of range of the project near the start, test it is rejected.
    @Test
    @Transactional
    void whenSkillsAdded_testSkillsSplitProperly() {
        Evidence evidence = new Evidence(0, projects.get(1).getId(), "Test", TEST_DESCRIPTION, Date.valueOf("2022-05-14"), "skill1 skill_2 {skill}  a     b  ");
        evidenceService.saveEvidence(evidence);
        List<Evidence> evidenceList = evidenceService.getEvidenceForPortfolio(0, projects.get(1).getId());
        assertEquals(1, evidenceList.size());
        List<String> skills = evidenceList.get(0).getSkills();
        List<String> expectedSkills = new ArrayList<>();
        expectedSkills.add("skill1");
        expectedSkills.add("skill_2");
        expectedSkills.add("{skill}");
        expectedSkills.add("a");
        expectedSkills.add("b");
        assertEquals(expectedSkills, skills);
    }

}