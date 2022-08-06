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
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

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
        Evidence evidence = new Evidence(0, projects.get(1).getId(), "!!&683 7;'.} {-++++", TEST_DESCRIPTION, Date.valueOf("2022-05-14"));
        assertThrows(IllegalArgumentException.class, () -> evidenceService.saveEvidence(evidence), "Title not valid");
        List<Evidence> evidenceList = evidenceService.getEvidenceForPortfolio(0, projects.get(1).getId());
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
    void testSetCategoriesOfEvidenceAllThree() {
        Evidence evidence = new Evidence(0, projects.get(1).getId(), "Test Evidence", TEST_DESCRIPTION, Date.valueOf("2022-05-14"));
        evidenceService.saveEvidence(evidence);
        Set<Categories> categoriesSet = new HashSet<>();
        categoriesSet.add(Categories.QUALITATIVE);
        categoriesSet.add(Categories.QUANTITATIVE);
        categoriesSet.add(Categories.SERVICE);
        evidenceService.setCategories(categoriesSet, evidenceService.getEvidenceById(evidence.getId()));
        evidence = evidenceRepository.findByProjectId(projects.get(1).getId()).get(0);
        assertEquals(categoriesSet, evidence.getCategories());
    }

    @Test
    void testSetCategoriesOfEvidenceChangeCategories() {
        Evidence evidence = new Evidence(0, projects.get(1).getId(), "Test Evidence", TEST_DESCRIPTION, Date.valueOf("2022-05-14"));
        evidenceService.saveEvidence(evidence);
        evidence = evidenceRepository.findByProjectId(projects.get(1).getId()).get(0);

        Set<Categories> categoriesSet1 = new HashSet<>();
        categoriesSet1.add(Categories.QUALITATIVE);
        evidenceService.setCategories(categoriesSet1, evidence);
        evidence = evidenceRepository.findByProjectId(projects.get(1).getId()).get(0);
        assertEquals(categoriesSet1, evidence.getCategories());

        Set<Categories> categoriesSet2 = new HashSet<>();
        categoriesSet2.add(Categories.SERVICE);
        evidenceService.setCategories(categoriesSet2, evidence);
        evidence = evidenceRepository.findByProjectId(projects.get(1).getId()).get(0);
        assertEquals(categoriesSet2, evidence.getCategories());
    }

    @Test
    void givenNoEvidenceWithCategoryMatch_testGetEvidenceByCategoryForPortfolio() {
        Evidence evidence = new Evidence(0, projects.get(1).getId(), "Test Evidence", TEST_DESCRIPTION, Date.valueOf("2022-05-14"));
        Set<Categories> categoriesSet = new HashSet<>();
        categoriesSet.add(Categories.QUANTITATIVE);
        evidence.setCategories(categoriesSet);
        evidenceRepository.save(evidence);
        List<Evidence> evidenceList = evidenceService.getEvidenceByCategoryForPortfolio(0, projects.get(1).getId(), Categories.SERVICE);
        assertEquals(0, evidenceList.size());
    }

    @Test
    void givenOneEvidenceWithCategoryMatch_testGetEvidenceByCategoryForPortfolio() {
        Evidence evidence = new Evidence(0, projects.get(1).getId(), "Test Evidence", TEST_DESCRIPTION, Date.valueOf("2022-05-14"));
        Set<Categories> categoriesSet = new HashSet<>();
        categoriesSet.add(Categories.QUANTITATIVE);
        evidence.setCategories(categoriesSet);
        evidenceRepository.save(evidence);
        List<Evidence> evidenceList = evidenceService.getEvidenceByCategoryForPortfolio(0, projects.get(1).getId(), Categories.QUANTITATIVE);
        assertEquals(1, evidenceList.size());
        assertTrue(evidenceList.get(0).getCategories().contains(Categories.QUANTITATIVE));
    }

    @Test
    void givenTwoEvidenceWithCategoryMatch_testGetEvidenceByCategoryForPortfolio() {
        Evidence evidence = new Evidence(0, projects.get(1).getId(), "Test Evidence", TEST_DESCRIPTION, Date.valueOf("2022-05-14"));
        Evidence evidence2 = new Evidence(0, projects.get(1).getId(), "Test Evidence", TEST_DESCRIPTION, Date.valueOf("2022-05-14"));
        Set<Categories> categoriesSet = new HashSet<>();
        categoriesSet.add(Categories.QUANTITATIVE);
        evidence2.setCategories(categoriesSet);
        evidence.setCategories(categoriesSet);
        evidenceRepository.save(evidence);
        evidenceRepository.save(evidence2);
        List<Evidence> evidenceList = evidenceService.getEvidenceByCategoryForPortfolio(0, projects.get(1).getId(), Categories.QUANTITATIVE);
        assertEquals(2, evidenceList.size());
        assertTrue(evidenceList.get(0).getCategories().contains(Categories.QUANTITATIVE));
    }

    @Test
    void givenOneEvidenceWithMultipleCategoryWithCategoryMatch_testGetEvidenceByCategoryForPortfolio() {
        Evidence evidence = new Evidence(0, projects.get(0).getId(), "Test Evidence", TEST_DESCRIPTION, Date.valueOf("2022-05-14"));
        Set<Categories> categoriesSet = new HashSet<>();
        categoriesSet.add(Categories.QUANTITATIVE);
        categoriesSet.add(Categories.QUALITATIVE);
        evidence.setCategories(categoriesSet);
        evidenceRepository.save(evidence);
        List<Evidence> evidenceList = evidenceService.getEvidenceByCategoryForPortfolio(0, projects.get(0).getId(), Categories.QUANTITATIVE);
        assertEquals(1, evidenceList.size());
    }

    @Test
    void givenOneEvidenceInWrongProject_testGetEvidenceByCategoryForPortfolio() {
        Evidence evidence = new Evidence(0, projects.get(0).getId(), "Test Evidence", TEST_DESCRIPTION, Date.valueOf("2022-05-14"));
        Set<Categories> categoriesSet = new HashSet<>();
        categoriesSet.add(Categories.QUANTITATIVE);
        evidence.setCategories(categoriesSet);
        evidenceRepository.save(evidence);
        List<Evidence> evidenceList = evidenceService.getEvidenceByCategoryForPortfolio(0, projects.get(1).getId(), Categories.QUANTITATIVE);
        assertEquals(0, evidenceList.size());
    }

}