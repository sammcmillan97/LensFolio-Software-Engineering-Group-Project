package nz.ac.canterbury.seng302.portfolio.service.evidence;

import nz.ac.canterbury.seng302.portfolio.model.evidence.*;
import nz.ac.canterbury.seng302.portfolio.model.project.Project;
import nz.ac.canterbury.seng302.portfolio.repository.evidence.EvidenceRepository;
import nz.ac.canterbury.seng302.portfolio.service.project.ProjectService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureTestDatabase
@SpringBootTest
class EvidenceServiceTests {

    private static final String TEST_DESCRIPTION = "According to all known laws of aviation, there is no way a bee should be able to fly.";

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
    @Transactional
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
        evidenceService.saveWebLink(evidence1.getId(), new WebLink("http://localhost:9000/portfolio"));
        Evidence receivedEvidence = evidenceService.getEvidenceById(evidence1.getId());
        assertEquals("localhost:9000/portfolio", receivedEvidence.getWebLinks().get(0).getLink());
    }

    @Test
    @Transactional
    void whenEvidenceAdded_testSaveMultipleWebLinks() {
        Evidence evidence1 = new Evidence(0, projects.get(1).getId(), "Three", TEST_DESCRIPTION, Date.valueOf("2022-05-14"));
        evidenceService.saveEvidence(evidence1);
        evidenceService.saveWebLink(evidence1.getId(), new WebLink("http://localhost:9000/portfolio"));
        evidenceService.saveWebLink(evidence1.getId(), new WebLink("http://localhost:9000/portfolio"));
        evidenceService.saveWebLink(evidence1.getId(), new WebLink("http://localhost:9000/portfolio"));
        evidenceService.saveWebLink(evidence1.getId(), new WebLink("http://localhost:9000/portfolio"));
        Evidence receivedEvidence = evidenceService.getEvidenceById(evidence1.getId());
        for (WebLink webLink: receivedEvidence.getWebLinks()) {
            assertEquals("localhost:9000/portfolio", webLink.getLink());
        }
    }

    @Test
    @Transactional
    void whenNoEvidenceAdded_testWebLinkNotSaved() {
        Exception exception = assertThrows(Exception.class,
                () -> evidenceService.saveWebLink(0, new WebLink("http://localhost:9000/portfolio")));
        String expectedMessage = "Evidence not found: web link not saved";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    /////////////////////////////////
    /////////Delete Evidence/////////
    /////////////////////////////////

    @Test
    @Transactional
    void givenOneEvidenceExists_testDeleteById() {
        Evidence evidence = new Evidence(0, projects.get(1).getId(), "Test", TEST_DESCRIPTION, Date.valueOf("2022-05-14"));
        evidenceService.saveEvidence(evidence);
        List<Evidence> evidenceList = evidenceService.getEvidenceForPortfolio(0, projects.get(1).getId());
        assertEquals(1, evidenceList.size());
        evidenceService.deleteById(evidence.getId());
        evidenceList = evidenceService.getEvidenceForPortfolio(0, projects.get(1).getId());
        assertEquals(0, evidenceList.size());
    }

    @Test
    @Transactional
    void givenEvidenceDoesNotExist_testDeleteById() {
        assertThrows(IllegalArgumentException.class, () -> evidenceService.deleteById(1), "Evidence " + 1 + " not found");
    }

    @Test
    @Transactional
    void givenEvidenceExistWithSkills_testRemovesSkillsFromUser() {
        Evidence evidence = new Evidence(0, projects.get(1).getId(), "Test", TEST_DESCRIPTION, Date.valueOf("2022-05-14"), "a b c");
        evidenceService.saveEvidence(evidence);
        List<Evidence> evidenceList = evidenceService.getEvidenceForPortfolio(0, projects.get(1).getId());
        List<String> skills = (List<String>) evidenceService.getSkillsFromEvidence(evidenceList);
        List<String> expectedSkills = new ArrayList<>();
        expectedSkills.add("a");
        expectedSkills.add("b");
        expectedSkills.add("c");
        assertEquals(expectedSkills, skills);
        evidenceService.deleteById(evidence.getId());
        evidenceList = evidenceService.getEvidenceForPortfolio(0, projects.get(1).getId());
        skills = (List<String>) evidenceService.getSkillsFromEvidence(evidenceList);
        expectedSkills.clear();
        assertEquals(expectedSkills, skills);
    }

    @Test
    @Transactional
    void givenTwoEvidenceExistWithOverlappingSkills_testRemovesCorrectSkillsFromUser() {
        Evidence evidence1 = new Evidence(0, projects.get(1).getId(), "Test1", TEST_DESCRIPTION, Date.valueOf("2022-05-14"), "a b");
        Evidence evidence2 = new Evidence(0, projects.get(1).getId(), "Test2", TEST_DESCRIPTION, Date.valueOf("2022-05-14"), "b c");
        evidenceService.saveEvidence(evidence1);
        evidenceService.saveEvidence(evidence2);
        List<Evidence> evidenceList = evidenceService.getEvidenceForPortfolio(0, projects.get(1).getId());
        List<String> skills = (List<String>) evidenceService.getSkillsFromEvidence(evidenceList);
        List<String> expectedSkills = new ArrayList<>();
        expectedSkills.add("a");
        expectedSkills.add("b");
        expectedSkills.add("c");
        assertEquals(expectedSkills, skills);
        evidenceService.deleteById(evidence2.getId());
        evidenceList = evidenceService.getEvidenceForPortfolio(0, projects.get(1).getId());
        skills = (List<String>) evidenceService.getSkillsFromEvidence(evidenceList);
        expectedSkills.remove("c");
        assertEquals(expectedSkills, skills);
    }

    //When skills are added, test that they split up as expected.
    @Test
    @Transactional
    void whenSkillsAdded_testSkillsSplitProperly() {
        Evidence evidence = new Evidence(0, projects.get(1).getId(), "Test", TEST_DESCRIPTION, Date.valueOf("2022-05-14"), "   skill1 skill_2 {skill}  a     b  ");
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

    //When skills are added, test duplicates are removed.
    @Test
    @Transactional
    void whenSkillsAdded_testDuplicatesRemoved() {
        Evidence evidence = new Evidence(0, projects.get(1).getId(), "Test", TEST_DESCRIPTION, Date.valueOf("2022-05-14"), "a b c b a");
        evidenceService.saveEvidence(evidence);
        List<Evidence> evidenceList = evidenceService.getEvidenceForPortfolio(0, projects.get(1).getId());
        assertEquals(1, evidenceList.size());
        List<String> skills = evidenceList.get(0).getSkills();
        List<String> expectedSkills = new ArrayList<>();
        expectedSkills.add("a");
        expectedSkills.add("b");
        expectedSkills.add("c");
        assertEquals(expectedSkills, skills);
    }

    //Test long skills cause an error to be thrown
    @Test
    @Transactional
    void when51CharSkillAdded_testErrorThrown() {
        Evidence evidence = new Evidence(0, projects.get(1).getId(), "Test", TEST_DESCRIPTION, Date.valueOf("2022-05-14"), "a".repeat(51));
        assertThrows(IllegalArgumentException.class, () -> evidenceService.saveEvidence(evidence), "Skills not valid");
        List<Evidence> evidenceList = evidenceService.getEvidenceForPortfolio(1, projects.get(1).getId());
        assertEquals(0, evidenceList.size());
    }

    //Test skills under the limit do not cause errors to be thrown
    @Test
    @Transactional
    void when50CharSkillAdded_testErrorNotThrown() {
        Evidence evidence = new Evidence(0, projects.get(1).getId(), "Test", TEST_DESCRIPTION, Date.valueOf("2022-05-14"), "a".repeat(50));
        evidenceService.saveEvidence(evidence);
        List<Evidence> evidenceList = evidenceService.getEvidenceForPortfolio(0, projects.get(1).getId());
        assertEquals(1, evidenceList.size());
    }

    //Test that capitalization of strings carries through in a user's evidence
    @Test
    @Transactional
    void whenSkillsAdded_testCorrectCapitalization() {
        Evidence evidence1 = new Evidence(0, projects.get(1).getId(), "Test", TEST_DESCRIPTION, Date.valueOf("2022-05-14"), "blah tESt");
        evidenceService.saveEvidence(evidence1);
        Evidence evidence2 = new Evidence(0, projects.get(1).getId(), "Test", TEST_DESCRIPTION, Date.valueOf("2022-05-13"), "TesT testing");
        evidenceService.saveEvidence(evidence2);
        List<Evidence> evidenceList = evidenceService.getEvidenceForPortfolio(0, projects.get(1).getId());
        assertEquals(2, evidenceList.size());
        List<String> skills1 = evidenceList.get(0).getSkills();
        List<String> expectedSkills1 = new ArrayList<>();
        expectedSkills1.add("blah");
        expectedSkills1.add("tESt");
        assertEquals(expectedSkills1, skills1);
        List<String> skills2 = evidenceList.get(1).getSkills();
        List<String> expectedSkills2 = new ArrayList<>();
        expectedSkills2.add("tESt");
        expectedSkills2.add("testing");
        assertEquals(expectedSkills2, skills2);
    }


    /////////////////////////////////
    ///GET EVIDENCE BY SKILL TESTS///
    /////////////////////////////////

    @Test
    @Transactional
    void givenNoEvidenceExists_findBySkill(){
        assertTrue(evidenceService.retrieveEvidenceBySkill("skill", projects.get(1).getId()).isEmpty());
    }

    @Test
    @Transactional
    void givenOneEvidenceExistsWithSkills_findByMatchingSkill(){
        Evidence evidence = new Evidence(0, projects.get(1).getId(), "Evidence One", TEST_DESCRIPTION, Date.valueOf("2022-05-14"), "skill1 skill_2 {skill}  a     b  ");
        evidenceService.saveEvidence(evidence);

        assertEquals("Evidence One", evidenceService.retrieveEvidenceBySkill("skill1", projects.get(1).getId()).get(0).getTitle());
    }

    @Test
    @Transactional
    void givenOneEvidenceExistsWithSkills_findByNonMatchingSkill(){
        Evidence evidence = new Evidence(0, projects.get(1).getId(), "Evidence One", TEST_DESCRIPTION, Date.valueOf("2022-05-14"), "skill1 skill_2 {skill}  a     b  ");
        evidenceService.saveEvidence(evidence);

        assertTrue(evidenceService.retrieveEvidenceBySkill("skill", projects.get(1).getId()).isEmpty());
    }

    @Test
    @Transactional
    void givenMultipleEvidencesExistsWithSkills_findByMatchingSkill_MatchOne(){
        Evidence evidence = new Evidence(0, projects.get(1).getId(), "Evidence One", TEST_DESCRIPTION, Date.valueOf("2022-05-14"), "skill1 skill_2 {skill}  a     b  ");
        Evidence evidence1 = new Evidence(1, projects.get(1).getId(), "Evidence Two", TEST_DESCRIPTION, Date.valueOf("2022-05-14"), "skill1 skill_2 {skill}");
        evidenceService.saveEvidence(evidence);
        evidenceService.saveEvidence(evidence1);

        assertEquals("Evidence One", evidenceService.retrieveEvidenceBySkill("b", projects.get(1).getId()).get(0).getTitle());
    }

    @Test
    @Transactional
    void givenMultipleEvidencesExistsWithSkills_findByMatchingSkill_MatchAll(){
        Evidence evidence = new Evidence(0, projects.get(1).getId(), "Evidence One", TEST_DESCRIPTION, Date.valueOf("2022-05-14"), "skill1 skill_2 {skill}  a     b  ");
        Evidence evidence1 = new Evidence(1, projects.get(1).getId(), "Evidence Two", TEST_DESCRIPTION, Date.valueOf("2022-05-14"), "skill1 skill_2 {skill}");
        evidenceService.saveEvidence(evidence);
        evidenceService.saveEvidence(evidence1);

        assertEquals(2, evidenceService.retrieveEvidenceBySkill("skill_2", projects.get(1).getId()).size());
    }

    @Test
    @Transactional
    void givenMultipleEvidencesExistsWithSkills_findByNonMatchingSkill(){
        Evidence evidence = new Evidence(0, projects.get(1).getId(), "Evidence One", TEST_DESCRIPTION, Date.valueOf("2022-05-14"), "skill1 skill_2 {skill}  a     b  ");
        Evidence evidence1 = new Evidence(1, projects.get(1).getId(), "Evidence Two", TEST_DESCRIPTION, Date.valueOf("2022-05-14"), "skill1 skill_2 {skill}");
        evidenceService.saveEvidence(evidence);
        evidenceService.saveEvidence(evidence1);

        assertTrue(evidenceService.retrieveEvidenceBySkill("skill", projects.get(1).getId()).isEmpty());
    }

    ///////////////////////////////////////
    ///GET EVIDENCE WITH NO SKILL TESTS////
    ///////////////////////////////////////

    @Test
    @Transactional
    void givenNoEvidenceExists_findEvidenceWithNoSkill(){
        int testUserId = 0;
        assertTrue(evidenceService.retrieveEvidenceWithNoSkill(testUserId, projects.get(1).getId()).isEmpty());
    }

    @Test
    @Transactional
    void givenOneEvidenceExistsWithSkills_findEvidenceWithNoSkill(){
        int testUserId = 0;
        Evidence evidence = new Evidence(testUserId, projects.get(1).getId(), "Evidence One", TEST_DESCRIPTION, Date.valueOf("2022-05-14"), "skill1 skill_2 {skill}  a     b  ");
        evidenceService.saveEvidence(evidence);
        assertTrue(evidenceService.retrieveEvidenceWithNoSkill(testUserId, projects.get(1).getId()).isEmpty());
    }

    @Test
    @Transactional
    void givenOneEvidenceExistsWithoutSkills_findEvidenceWithNoSkill(){
        int testUserId = 0;
        Evidence evidence = new Evidence(testUserId, projects.get(1).getId(), "Evidence One", TEST_DESCRIPTION, Date.valueOf("2022-05-14"), "");
        evidenceService.saveEvidence(evidence);
        assertEquals(1, evidenceService.retrieveEvidenceWithNoSkill(testUserId, projects.get(1).getId()).size());
    }

    @Test
    @Transactional
    void givenMultipleEvidencesExistsWithSkills_findEvidenceWithNoSkill(){
        int testUserId = 0;
        Evidence evidence = new Evidence(testUserId, projects.get(1).getId(), "Evidence One", TEST_DESCRIPTION, Date.valueOf("2022-05-14"), "skill1 skill_2 {skill}  a     b  ");
        Evidence evidence1 = new Evidence(testUserId, projects.get(1).getId(), "Evidence Two", TEST_DESCRIPTION, Date.valueOf("2022-05-14"), "skill1 skill_2 {skill}");
        evidenceService.saveEvidence(evidence);
        evidenceService.saveEvidence(evidence1);
        assertTrue(evidenceService.retrieveEvidenceWithNoSkill(testUserId, projects.get(1).getId()).isEmpty());
    }

    @Test
    @Transactional
    void givenMultipleEvidencesExistsOneWithSkills_findEvidenceWithNoSkill(){
        int testUserId = 0;
        Evidence evidence = new Evidence(testUserId, projects.get(1).getId(), "Evidence One", TEST_DESCRIPTION, Date.valueOf("2022-05-14"), "skill1 skill_2 {skill}  a     b  ");
        Evidence evidence1 = new Evidence(testUserId, projects.get(1).getId(), "Evidence Two", TEST_DESCRIPTION, Date.valueOf("2022-05-14"), "");
        evidenceService.saveEvidence(evidence);
        evidenceService.saveEvidence(evidence1);
        assertEquals(1, evidenceService.retrieveEvidenceWithNoSkill(testUserId, projects.get(1).getId()).size());
    }

    @Test
    @Transactional
    void givenMultipleEvidencesExistsWithoutSkills_findEvidenceWithNoSkill(){
        int testUserId = 0;
        Evidence evidence = new Evidence(testUserId, projects.get(1).getId(), "Evidence One", TEST_DESCRIPTION, Date.valueOf("2022-05-14"), "");
        Evidence evidence1 = new Evidence(testUserId, projects.get(1).getId(), "Evidence Two", TEST_DESCRIPTION, Date.valueOf("2022-05-14"), "");
        evidenceService.saveEvidence(evidence);
        evidenceService.saveEvidence(evidence1);
        assertEquals(2, evidenceService.retrieveEvidenceWithNoSkill(testUserId, projects.get(1).getId()).size());
    }

    @Test
    @Transactional
    void givenMultipleEvidencesExistsWithSkills_matchAll_testSortOrder() {
        Evidence evidence = new Evidence(1, projects.get(1).getId(), "Evidence One", TEST_DESCRIPTION, Date.valueOf("2022-05-12"), "skill1 skill_2 {skill}  a     b  ");
        Evidence evidence1 = new Evidence(1, projects.get(1).getId(), "Evidence Two", TEST_DESCRIPTION, Date.valueOf("2022-05-14"), "skill1 skill_2 {skill}");
        Evidence evidence2 = new Evidence(1, projects.get(1).getId(), "Evidence Three", TEST_DESCRIPTION, Date.valueOf("2022-05-10"), "skill1 skill_2 {skill}");
        evidenceService.saveEvidence(evidence);
        evidenceService.saveEvidence(evidence1);
        evidenceService.saveEvidence(evidence2);

        List<Evidence> searchResults = evidenceService.retrieveEvidenceBySkill("skill1", projects.get(1).getId());
        assertEquals(3, searchResults.size());
        assertEquals("Evidence Two", searchResults.get(0).getTitle());
        assertEquals("Evidence One", searchResults.get(1).getTitle());
        assertEquals("Evidence Three", searchResults.get(2).getTitle());
    }

    //////////////////////////////////////////
    ///GET EVIDENCE BY SKILL AND USER TESTS///
    //////////////////////////////////////////

    @Test
    @Transactional
    void givenNoEvidenceExistsWithSkillAndUser_findBySkillAndUser() {
        assertTrue(evidenceService.retrieveEvidenceBySkillAndUser("skill", 1, projects.get(1).getId()).isEmpty());
    }

    @Test
    @Transactional
    void givenOneEvidenceExistsWithSkillAndUser_findBySkillAndNonMatchingUser() {
        int testUserId1 = 1;
        int testUserId2 = 2;
        String testSkill = "skill1";
        Evidence evidence = new Evidence(testUserId2, projects.get(1).getId(), "Evidence One", TEST_DESCRIPTION, Date.valueOf("2022-05-14"), "skill1 skill_2 {skill}  a     b  ");
        evidenceService.saveEvidence(evidence);

        assertTrue(evidenceService.retrieveEvidenceBySkillAndUser(testSkill, testUserId1, projects.get(1).getId()).isEmpty());
    }

    @Test
    @Transactional
    void givenOneEvidenceExistsForSkillAndUser_findByUserAndNonMatchingSkill() {
        int testUserId = 1;
        String testSkill = "skill1234";
        Evidence evidence = new Evidence(testUserId, projects.get(1).getId(), "Evidence One", TEST_DESCRIPTION, Date.valueOf("2022-05-14"), "skill1 skill_2 {skill}  a     b  ");
        evidenceService.saveEvidence(evidence);

        assertTrue(evidenceService.retrieveEvidenceBySkillAndUser(testSkill, testUserId, projects.get(1).getId()).isEmpty());
    }

    @Test
    @Transactional
    void givenOneEvidenceExistsWithSkillAndUser_findBySkillAndUser() {
        int testUserId = 1;
        String testSkill = "skill1";
        Evidence evidence = new Evidence(testUserId, projects.get(1).getId(), "Evidence One", TEST_DESCRIPTION, Date.valueOf("2022-05-14"), "skill1 skill_2 {skill}  a     b  ");
        evidenceService.saveEvidence(evidence);

        assertEquals("Evidence One", evidenceService.retrieveEvidenceBySkillAndUser(testSkill, 1, projects.get(1).getId()).get(0).getTitle());
    }

    @Test
    @Transactional
    void givenMultipleEvidenceExistsWithSkillAndUser_findBySkillAndNonMatchingUser() {
        int testUserId1 = 1;
        int testUserId2 = 2;
        String testSkill = "skill1";
        Evidence evidence = new Evidence(testUserId2, projects.get(1).getId(), "Evidence One", TEST_DESCRIPTION, Date.valueOf("2022-05-14"), "skill1 skill_2 {skill}  a     b  ");
        Evidence evidence1 = new Evidence(testUserId2, projects.get(1).getId(), "Evidence Two", TEST_DESCRIPTION, Date.valueOf("2022-05-14"), "skill1 skill_2 {skill}");
        evidenceService.saveEvidence(evidence);
        evidenceService.saveEvidence(evidence1);

        assertTrue(evidenceService.retrieveEvidenceBySkillAndUser(testSkill, testUserId1, projects.get(1).getId()).isEmpty());
    }

    @Test
    @Transactional
    void givenMultipleEvidenceExistsForSkillAndUser_findByUserAndNonMatchingSkill_NoMatches() {
        int testUserId = 1;
        String testSkill = "skill1234";
        Evidence evidence = new Evidence(testUserId, projects.get(1).getId(), "Evidence One", TEST_DESCRIPTION, Date.valueOf("2022-05-14"), "skill1 skill_2 {skill}  a     b  ");
        Evidence evidence1 = new Evidence(1, projects.get(1).getId(), "Evidence Two", TEST_DESCRIPTION, Date.valueOf("2022-05-14"), "skill1 skill_2 {skill}");
        evidenceService.saveEvidence(evidence);
        evidenceService.saveEvidence(evidence1);


        assertTrue(evidenceService.retrieveEvidenceBySkillAndUser(testSkill, testUserId, projects.get(1).getId()).isEmpty());
    }

    @Test
    @Transactional
    void givenMultipleEvidenceExistsWithSkillAndUser_findBySkillAndUser_MatchOne() {
        int testUserId = 1;
        int testUserId2 = 2;
        String testSkill = "skill1";
        Evidence evidence = new Evidence(testUserId, projects.get(1).getId(), "Evidence One", TEST_DESCRIPTION, Date.valueOf("2022-05-14"), "skill1 skill_2 {skill}  a     b  ");
        Evidence evidence1 = new Evidence(testUserId2, projects.get(1).getId(), "Evidence Two", TEST_DESCRIPTION, Date.valueOf("2022-05-14"), "skill1 skill_2 {skill}");
        evidenceService.saveEvidence(evidence);
        evidenceService.saveEvidence(evidence1);

        assertEquals("Evidence One", evidenceService.retrieveEvidenceBySkillAndUser(testSkill, 1, projects.get(1).getId()).get(0).getTitle());
    }

    @Test
    @Transactional
    void givenMultipleEvidenceExistsWithSkillAndUser_findBySkillAndUser_MatchAll() {
        int testUserId = 1;
        String testSkill = "skill1";
        Evidence evidence = new Evidence(testUserId, projects.get(1).getId(), "Evidence One", TEST_DESCRIPTION, Date.valueOf("2022-05-14"), "skill1 skill_2 {skill}  a     b  ");
        Evidence evidence1 = new Evidence(testUserId, projects.get(1).getId(), "Evidence Two", TEST_DESCRIPTION, Date.valueOf("2022-05-14"), "skill1 skill_2 {skill}");
        evidenceService.saveEvidence(evidence);
        evidenceService.saveEvidence(evidence1);

        assertEquals(2, evidenceService.retrieveEvidenceBySkillAndUser(testSkill, 1, projects.get(1).getId()).size());
    }

    @Test
    @Transactional
    void givenMultipleEvidencesExistsWithSkillAndUser_matchAll_testSortOrder() {
        int testUserId = 1;
        String testSkill = "skill1";
        Evidence evidence = new Evidence(testUserId, projects.get(1).getId(), "Evidence One", TEST_DESCRIPTION, Date.valueOf("2022-05-12"), "skill1 skill_2 {skill}  a     b  ");
        Evidence evidence1 = new Evidence(testUserId, projects.get(1).getId(), "Evidence Two", TEST_DESCRIPTION, Date.valueOf("2022-05-14"), "skill1 skill_2 {skill}");
        Evidence evidence2 = new Evidence(testUserId, projects.get(1).getId(), "Evidence Three", TEST_DESCRIPTION, Date.valueOf("2022-05-10"), "skill1 skill_2 {skill}");
        evidenceService.saveEvidence(evidence);
        evidenceService.saveEvidence(evidence1);
        evidenceService.saveEvidence(evidence2);

        List<Evidence> searchResults = evidenceService.retrieveEvidenceBySkill(testSkill, projects.get(1).getId());
        assertEquals(3, searchResults.size());
        assertEquals("Evidence Two", searchResults.get(0).getTitle());
        assertEquals("Evidence One", searchResults.get(1).getTitle());
        assertEquals("Evidence Three", searchResults.get(2).getTitle());
    }




    @Test
    @Transactional
    void whenEvidenceAdded_testWebLinkSavedWithName() {
        Evidence evidence1 = new Evidence(0, projects.get(1).getId(), "Three", TEST_DESCRIPTION, Date.valueOf("2022-05-14"));
        evidenceService.saveEvidence(evidence1);
        evidenceService.saveWebLink(evidence1.getId(), new WebLink("http://localhost:9000/portfolio", "My web link"));
        Evidence receivedEvidence = evidenceService.getEvidenceById(evidence1.getId());
        assertEquals("My web link", receivedEvidence.getWebLinks().get(0).getName());
    }

    @Test
    @Transactional
    void whenEvidenceAdded_testWebLinkSavedWithoutName() {
        Evidence evidence1 = new Evidence(0, projects.get(1).getId(), "Three", TEST_DESCRIPTION, Date.valueOf("2022-05-14"));
        evidenceService.saveEvidence(evidence1);
        evidenceService.saveWebLink(evidence1.getId(), new WebLink("http://localhost:9000/portfolio"));
        Evidence receivedEvidence = evidenceService.getEvidenceById(evidence1.getId());
        assertNull(receivedEvidence.getWebLinks().get(0).getName());
    }

    @Test
    @Transactional
    void whenEvidenceAdded_testWebLinkSavedWithLongLink() {
        Evidence evidence1 = new Evidence(0, projects.get(1).getId(), "Three", TEST_DESCRIPTION, Date.valueOf("2022-05-14"));
        evidenceService.saveEvidence(evidence1);
        evidenceService.saveWebLink(evidence1.getId(), new WebLink("http://www.reallylong.link/rll/eUGA_yTayv_cC_wxQ4no95oM7sInpIWAmUDDX/aWB6WdAu9Ks55ewCBl0LCVtDza1OPMkjaRBzb_TDOwyFZPQUYfrWMYNbSxO3iAKougBKA3n5B2_6H3h_8x9kLvOWcEoP3oJrnxQcNtoBP2HFv69NQoJ_LFDtI/b5MRiG2txjP24pTixTTK9p0ysxJF/dO6pm/9LgyQpYRD_f2TRG9tlt4iQj_vs4Bj_m7BBhgYyad6ddZBufUDWR2Faif_7sGmnqwUPTlBTYBDs2gk8vj6/CRJk1U4SN73S4hEpBDe3Cm9t35d7AvSrRtAsZyrrIJT3rF1a_EBcObrmxlyJKvhMQhrV1LZ7xXlfJTUg0_BnMAbu7KWTUkmAEa5utaEHBiZ0woiT8dXK1XCCjDis71YNUTegNd9tE_AgNEDIWRvd121hKRLzYaeoC4N4iPewVCu1VuTJQj1xJFo_ps4XaZlUWkooSdSgkqmy7RphWfpurA6G52Rf37hVKxb0uiNX2r3nuIs8SLlL2mseFWVDhLIn26sNxnyoPlDh9xLwfj9XrM2kFDsDUEVzv9K/x94TewZ/S_N1_cGn4uLPatxYdIFQ855/WRJ7R__85AOHUYNpCk98yBAui36Fp_chWYInwCEyNGAvDZg/x14fKCgImDqw19LuFoD/Z3ClRJJToX04EgUxdrFdvx5pgw66sjjrmMWeIfkf8LXioKES2YZ8GQ2vPpHVZvCR/ndwmSHRpYkZ4Z2v3KVNG4qowBLYqjLdkqa73yIiVXX94APd/HYqQJRQ4O0OGJO8ZbUWhpWGBlnw3zgruf/vCY_bYWT/O12/K0t/9zkWCyXOKTYHXhdMHhtwG_QJOshk0OsqsQaBPaTJsrYkDI1VOlYaGs30/Owu9vWDFdR9X28Bj79_4zTUFy9QuJe4gahNPjK7xu1kErp92NJfBN_FGCQ/2ieNVM2XCFaTLcC5305akM48kscBHDScUL7GvhjxRW/EGD8K7p3LE1CD4G3fbw5/PrwQkGfGjmy0FyNCYHeD0ClxUJ/j3EdR3mjinw/mGO5_cEnJTafryMGIYKSQER4uFmMo8bjwR/D6eC8HJz8ApQdeT8Hql_FGRc2bfTqMhvdRm9ScdpFEsiLUVvx37iZKB6J1nU36QrulAsFyWRrOzLsR9dnhwJiqk5bvZVmZyT504Hym7Z1zS9dqTxS0jM3nfEHu0RLq3qw8YRr7MuN_h0vvG2QzpgSYyXzUcxOfacKMztDgrjllPaYlpoE54d7ANyCbxaExMnJ1CU1YZmLYFWyA/6BMZYKTRX0TIlyVv96qxSRoAcdZ1v0s/WwmR2/xOBfA2yoZ98FRWFBM8E1IPQCYKkOMNxMl3lLrntdCVnKNCXGYtulQlPzfRjcsThfqQEXRLPziaAwuwdeOwCCJDouQ9GCPSN3e0fX25PUFTyFyHQlWbb6TRDu4sVSWMxs8d85sjo_4hwstQLK691_RScuI8KZOJNQ1MtpQJpNB2UKNkFm6PGAykpu/WgBQ3WMj5vXP0HOko5TAGLdlVvLbod2ypsGhAhZNUB87eHOaj3axAFjwSc2FE7vgWhSHOvVsVvlNShR/k7AvKz68rFVrgqz2RrBhF9roa2A6WC8XNae8DaMm7nE7nVz1CZOaK6TMvATA_IZzUC/AEgcDAszyJ8h4AMNiIipm0xJTtNTU4N3GkND/nSK_qG_6F0_5se622EmDyZ7jgn7C90UPlGVCLJMriVBvK53aeji4YAgUn5s0ceGaKV/E8FHQadt1w8TJtDRXAkHANQxeXcPdRV9Tq0aawxnJ0LaWXjTH6Na5/uK1e4Yllr03KAKD8cpLGL4kDyF1ktxlEH_L_ykQHQEb4qoFCFIC7yaC7BYJy8F0rmjCk0uo8iS_4s08nX96WFs3ywsmCHjoLroZZvJ1AuOrxgP0DacJ2bqX9MHprUpDfvNEr4wfLAQ1shEpIw"));
        Evidence receivedEvidence = evidenceService.getEvidenceById(evidence1.getId());
        assertEquals("www.reallylong.link/rll/eUGA_yTayv_cC_wxQ4no95oM7sInpIWAmUDDX/aWB6WdAu9Ks55ewCBl0LCVtDza1OPMkjaRBzb_TDOwyFZPQUYfrWMYNbSxO3iAKougBKA3n5B2_6H3h_8x9kLvOWcEoP3oJrnxQcNtoBP2HFv69NQoJ_LFDtI/b5MRiG2txjP24pTixTTK9p0ysxJF/dO6pm/9LgyQpYRD_f2TRG9tlt4iQj_vs4Bj_m7BBhgYyad6ddZBufUDWR2Faif_7sGmnqwUPTlBTYBDs2gk8vj6/CRJk1U4SN73S4hEpBDe3Cm9t35d7AvSrRtAsZyrrIJT3rF1a_EBcObrmxlyJKvhMQhrV1LZ7xXlfJTUg0_BnMAbu7KWTUkmAEa5utaEHBiZ0woiT8dXK1XCCjDis71YNUTegNd9tE_AgNEDIWRvd121hKRLzYaeoC4N4iPewVCu1VuTJQj1xJFo_ps4XaZlUWkooSdSgkqmy7RphWfpurA6G52Rf37hVKxb0uiNX2r3nuIs8SLlL2mseFWVDhLIn26sNxnyoPlDh9xLwfj9XrM2kFDsDUEVzv9K/x94TewZ/S_N1_cGn4uLPatxYdIFQ855/WRJ7R__85AOHUYNpCk98yBAui36Fp_chWYInwCEyNGAvDZg/x14fKCgImDqw19LuFoD/Z3ClRJJToX04EgUxdrFdvx5pgw66sjjrmMWeIfkf8LXioKES2YZ8GQ2vPpHVZvCR/ndwmSHRpYkZ4Z2v3KVNG4qowBLYqjLdkqa73yIiVXX94APd/HYqQJRQ4O0OGJO8ZbUWhpWGBlnw3zgruf/vCY_bYWT/O12/K0t/9zkWCyXOKTYHXhdMHhtwG_QJOshk0OsqsQaBPaTJsrYkDI1VOlYaGs30/Owu9vWDFdR9X28Bj79_4zTUFy9QuJe4gahNPjK7xu1kErp92NJfBN_FGCQ/2ieNVM2XCFaTLcC5305akM48kscBHDScUL7GvhjxRW/EGD8K7p3LE1CD4G3fbw5/PrwQkGfGjmy0FyNCYHeD0ClxUJ/j3EdR3mjinw/mGO5_cEnJTafryMGIYKSQER4uFmMo8bjwR/D6eC8HJz8ApQdeT8Hql_FGRc2bfTqMhvdRm9ScdpFEsiLUVvx37iZKB6J1nU36QrulAsFyWRrOzLsR9dnhwJiqk5bvZVmZyT504Hym7Z1zS9dqTxS0jM3nfEHu0RLq3qw8YRr7MuN_h0vvG2QzpgSYyXzUcxOfacKMztDgrjllPaYlpoE54d7ANyCbxaExMnJ1CU1YZmLYFWyA/6BMZYKTRX0TIlyVv96qxSRoAcdZ1v0s/WwmR2/xOBfA2yoZ98FRWFBM8E1IPQCYKkOMNxMl3lLrntdCVnKNCXGYtulQlPzfRjcsThfqQEXRLPziaAwuwdeOwCCJDouQ9GCPSN3e0fX25PUFTyFyHQlWbb6TRDu4sVSWMxs8d85sjo_4hwstQLK691_RScuI8KZOJNQ1MtpQJpNB2UKNkFm6PGAykpu/WgBQ3WMj5vXP0HOko5TAGLdlVvLbod2ypsGhAhZNUB87eHOaj3axAFjwSc2FE7vgWhSHOvVsVvlNShR/k7AvKz68rFVrgqz2RrBhF9roa2A6WC8XNae8DaMm7nE7nVz1CZOaK6TMvATA_IZzUC/AEgcDAszyJ8h4AMNiIipm0xJTtNTU4N3GkND/nSK_qG_6F0_5se622EmDyZ7jgn7C90UPlGVCLJMriVBvK53aeji4YAgUn5s0ceGaKV/E8FHQadt1w8TJtDRXAkHANQxeXcPdRV9Tq0aawxnJ0LaWXjTH6Na5/uK1e4Yllr03KAKD8cpLGL4kDyF1ktxlEH_L_ykQHQEb4qoFCFIC7yaC7BYJy8F0rmjCk0uo8iS_4s08nX96WFs3ywsmCHjoLroZZvJ1AuOrxgP0DacJ2bqX9MHprUpDfvNEr4wfLAQ1shEpIw",
                receivedEvidence.getWebLinks().get(0).getLink());
    }

    @Test
    @Transactional
    void whenEvidenceAdded_testSafeWebLinkSaved() {
        Evidence evidence1 = new Evidence(0, projects.get(1).getId(), "Three", TEST_DESCRIPTION, Date.valueOf("2022-05-14"));
        evidenceService.saveEvidence(evidence1);
        evidenceService.saveWebLink(evidence1.getId(), new WebLink("https://localhost:9000/portfolio", "My web link"));
        Evidence receivedEvidence = evidenceService.getEvidenceById(evidence1.getId());
        assertTrue(receivedEvidence.getWebLinks().get(0).isSafe());
    }

    @Test
    @Transactional
    void whenEvidenceAdded_testNotSafeWebLinkSaved() {
        Evidence evidence1 = new Evidence(0, projects.get(1).getId(), "Three", TEST_DESCRIPTION, Date.valueOf("2022-05-14"));
        evidenceService.saveEvidence(evidence1);
        evidenceService.saveWebLink(evidence1.getId(), new WebLink("http://localhost:9000/portfolio", "My web link"));
        Evidence receivedEvidence = evidenceService.getEvidenceById(evidence1.getId());
        assertFalse(receivedEvidence.getWebLinks().get(0).isSafe());
    }
    @Test
    @Transactional
    void testSetCategoriesOfEvidenceAllThree() {
        Evidence evidence = new Evidence(0, projects.get(1).getId(), "Test Evidence", TEST_DESCRIPTION, Date.valueOf("2022-05-14"));
        evidenceService.saveEvidence(evidence);
        List<Categories> categoriesList = new ArrayList<>();
        categoriesList.add(Categories.Quantitative);
        categoriesList.add(Categories.Qualitative);
        categoriesList.add(Categories.Service);
        evidenceService.setCategories(new HashSet<>(categoriesList), evidenceService.getEvidenceById(evidence.getId()));
        evidence = evidenceRepository.findByProjectId(projects.get(1).getId()).get(0);
        assertEquals(categoriesList, evidence.getCategories());
    }

    @Test
    @Transactional
    void testSetCategoriesOfEvidenceChangeCategories() {
        Evidence evidence = new Evidence(0, projects.get(1).getId(), "Test Evidence", TEST_DESCRIPTION, Date.valueOf("2022-05-14"));
        evidenceService.saveEvidence(evidence);
        evidence = evidenceRepository.findByProjectId(projects.get(1).getId()).get(0);

        List<Categories> categoriesList1 = new ArrayList<>();
        categoriesList1.add(Categories.Qualitative);
        evidenceService.setCategories(new HashSet<>(categoriesList1), evidence);
        evidence = evidenceRepository.findByProjectId(projects.get(1).getId()).get(0);
        assertEquals(categoriesList1, evidence.getCategories());

        List<Categories> categoriesList2 = new ArrayList<>();
        categoriesList2.add(Categories.Service);
        evidenceService.setCategories(new HashSet<>(categoriesList2), evidence);
        evidence = evidenceRepository.findByProjectId(projects.get(1).getId()).get(0);
        assertEquals(categoriesList2, evidence.getCategories());
    }

    @Test
    void givenNoEvidenceWithCategoryMatch_testGetEvidenceByCategoryForPortfolio() {
        Evidence evidence = new Evidence(0, projects.get(1).getId(), "Test Evidence", TEST_DESCRIPTION, Date.valueOf("2022-05-14"));
        Set<Categories> categoriesSet = new HashSet<>();
        categoriesSet.add(Categories.Quantitative);
        evidence.setCategories(categoriesSet);
        evidenceRepository.save(evidence);
        List<Evidence> evidenceList = evidenceService.getEvidenceByCategoryForPortfolio(0, projects.get(1).getId(), Categories.Service);
        assertEquals(0, evidenceList.size());
    }

    @Test
    void givenOneEvidenceWithCategoryMatch_testGetEvidenceByCategoryForPortfolio() {
        Evidence evidence = new Evidence(0, projects.get(1).getId(), "Test Evidence", TEST_DESCRIPTION, Date.valueOf("2022-05-14"));
        Set<Categories> categoriesSet = new HashSet<>();
        categoriesSet.add(Categories.Quantitative);
        evidence.setCategories(categoriesSet);
        evidenceRepository.save(evidence);
        List<Evidence> evidenceList = evidenceService.getEvidenceByCategoryForPortfolio(0, projects.get(1).getId(), Categories.Quantitative);
        assertEquals(1, evidenceList.size());
        assertTrue(evidenceList.get(0).getCategories().contains(Categories.Quantitative));
    }

    @Test
    void givenTwoEvidenceWithCategoryMatch_testGetEvidenceByCategoryForPortfolio() {
        Evidence evidence = new Evidence(0, projects.get(1).getId(), "Test Evidence", TEST_DESCRIPTION, Date.valueOf("2022-05-14"));
        Evidence evidence2 = new Evidence(0, projects.get(1).getId(), "Test Evidence", TEST_DESCRIPTION, Date.valueOf("2022-05-14"));
        Set<Categories> categoriesSet = new HashSet<>();
        categoriesSet.add(Categories.Quantitative);
        evidence2.setCategories(categoriesSet);
        evidence.setCategories(categoriesSet);
        evidenceRepository.save(evidence);
        evidenceRepository.save(evidence2);
        List<Evidence> evidenceList = evidenceService.getEvidenceByCategoryForPortfolio(0, projects.get(1).getId(), Categories.Quantitative);
        assertEquals(2, evidenceList.size());
        assertTrue(evidenceList.get(0).getCategories().contains(Categories.Quantitative));
    }

    @Test
    void givenOneEvidenceWithMultipleCategoryWithCategoryMatch_testGetEvidenceByCategoryForPortfolio() {
        Evidence evidence = new Evidence(0, projects.get(0).getId(), "Test Evidence", TEST_DESCRIPTION, Date.valueOf("2022-05-14"));
        Set<Categories> categoriesSet = new HashSet<>();
        categoriesSet.add(Categories.Quantitative);
        categoriesSet.add(Categories.Qualitative);
        evidence.setCategories(categoriesSet);
        evidenceRepository.save(evidence);
        List<Evidence> evidenceList = evidenceService.getEvidenceByCategoryForPortfolio(0, projects.get(0).getId(), Categories.Quantitative);
        assertEquals(1, evidenceList.size());
    }

    @Test
    void givenOneEvidenceInWrongProject_testGetEvidenceByCategoryForPortfolio() {
        Evidence evidence = new Evidence(0, projects.get(0).getId(), "Test Evidence", TEST_DESCRIPTION, Date.valueOf("2022-05-14"));
        Set<Categories> categoriesSet = new HashSet<>();
        categoriesSet.add(Categories.Quantitative);
        evidence.setCategories(categoriesSet);
        evidenceRepository.save(evidence);
        List<Evidence> evidenceList = evidenceService.getEvidenceByCategoryForPortfolio(0, projects.get(1).getId(), Categories.Quantitative);
        assertEquals(0, evidenceList.size());
    }

    @Test
    void givenTwoEvidenceWithCategoryMatch_testGetEvidenceByCategoryForPortfolioCorrectOrdering() {
        Evidence evidence = new Evidence(0, projects.get(1).getId(), "Test Evidence1", TEST_DESCRIPTION, Date.valueOf("2022-05-14"));
        Evidence evidence2 = new Evidence(0, projects.get(1).getId(), "Test Evidence2", TEST_DESCRIPTION, Date.valueOf("2022-05-13"));
        Set<Categories> categoriesSet = new HashSet<>();
        categoriesSet.add(Categories.Quantitative);
        evidence2.setCategories(categoriesSet);
        evidence.setCategories(categoriesSet);
        evidenceRepository.save(evidence);
        evidenceRepository.save(evidence2);
        List<Evidence> evidenceList = evidenceService.getEvidenceByCategoryForPortfolio(0, projects.get(1).getId(), Categories.Quantitative);
        assertEquals("Test Evidence1", evidenceList.get(0).getTitle());
    }

    @Test
    void givenThreeEvidenceWithCategoryMatch_testGetEvidenceByCategoryForPortfolioCorrectOrdering() {
        Evidence evidence = new Evidence(0, projects.get(1).getId(), "Test Evidence1", TEST_DESCRIPTION, Date.valueOf("2022-05-13"));
        Evidence evidence2 = new Evidence(0, projects.get(1).getId(), "Test Evidence2", TEST_DESCRIPTION, Date.valueOf("2022-05-11"));
        Evidence evidence3 = new Evidence(0, projects.get(1).getId(), "Test Evidence3", TEST_DESCRIPTION, Date.valueOf("2022-05-18"));
        Set<Categories> categoriesSet = new HashSet<>();
        categoriesSet.add(Categories.Quantitative);
        evidence2.setCategories(categoriesSet);
        evidence.setCategories(categoriesSet);
        evidence3.setCategories(categoriesSet);
        evidenceRepository.save(evidence);
        evidenceRepository.save(evidence2);
        evidenceRepository.save(evidence3);
        List<Evidence> evidenceList = evidenceService.getEvidenceByCategoryForPortfolio(0, projects.get(1).getId(), Categories.Quantitative);
        assertEquals("Test Evidence3", evidenceList.get(0).getTitle());
        assertEquals("Test Evidence1", evidenceList.get(1).getTitle());
        assertEquals("Test Evidence2", evidenceList.get(2).getTitle());
    }

    ///////////////////////////////////////
    ///GET EVIDENCE WITH NO SKILL TESTS////
    ///////////////////////////////////////

    @Test
    @Transactional
    void givenNoEvidenceExists_findEvidenceWithNoCategories(){
        int testUserId = 0;
        assertTrue(evidenceService.retrieveEvidenceWithNoCategory(testUserId, projects.get(1).getId()).isEmpty());
    }

    @Test
    @Transactional
    void givenOneEvidenceExistsWithCategories_findEvidenceWithNoCategories(){
        int testUserId = 0;
        Evidence evidence = new Evidence(testUserId, projects.get(1).getId(), "Evidence One", TEST_DESCRIPTION, Date.valueOf("2022-05-14"), "skill1 skill_2 {skill}  a     b  ");
        Set<Categories> categoriesSet = new HashSet<>();
        categoriesSet.add(Categories.Quantitative);
        evidence.setCategories(categoriesSet);
        evidenceService.saveEvidence(evidence);
        assertTrue(evidenceService.retrieveEvidenceWithNoCategory(testUserId, projects.get(1).getId()).isEmpty());
    }

    @Test
    @Transactional
    void givenOneEvidenceExistsWithoutCategories_findEvidenceWithNoCategories(){
        int testUserId = 0;
        Evidence evidence = new Evidence(testUserId, projects.get(1).getId(), "Evidence One", TEST_DESCRIPTION, Date.valueOf("2022-05-14"), "");
        evidenceService.saveEvidence(evidence);
        assertEquals(1, evidenceService.retrieveEvidenceWithNoCategory(testUserId, projects.get(1).getId()).size());
    }

    @Test
    @Transactional
    void givenMultipleEvidencesExistsWithCategories_findEvidenceWithNoCategories(){
        int testUserId = 0;
        Evidence evidence = new Evidence(testUserId, projects.get(1).getId(), "Evidence One", TEST_DESCRIPTION, Date.valueOf("2022-05-14"), "skill1 skill_2 {skill}  a     b  ");
        Evidence evidence1 = new Evidence(testUserId, projects.get(1).getId(), "Evidence Two", TEST_DESCRIPTION, Date.valueOf("2022-05-14"), "skill1 skill_2 {skill}");
        Set<Categories> categoriesSet = new HashSet<>();
        categoriesSet.add(Categories.Quantitative);
        evidence.setCategories(categoriesSet);
        evidence1.setCategories(categoriesSet);
        evidenceService.saveEvidence(evidence);
        evidenceService.saveEvidence(evidence1);
        assertTrue(evidenceService.retrieveEvidenceWithNoCategory(testUserId, projects.get(1).getId()).isEmpty());
    }

    @Test
    @Transactional
    void givenMultipleEvidencesExistsOneWithCategories_findEvidenceWithNoCategories(){
        int testUserId = 0;
        Evidence evidence = new Evidence(testUserId, projects.get(1).getId(), "Evidence One", TEST_DESCRIPTION, Date.valueOf("2022-05-14"), "skill1 skill_2 {skill}  a     b  ");
        Evidence evidence1 = new Evidence(testUserId, projects.get(1).getId(), "Evidence Two", TEST_DESCRIPTION, Date.valueOf("2022-05-14"), "");
        Set<Categories> categoriesSet = new HashSet<>();
        categoriesSet.add(Categories.Quantitative);
        evidence.setCategories(categoriesSet);
        evidenceService.saveEvidence(evidence);
        evidenceService.saveEvidence(evidence1);
        assertEquals(1, evidenceService.retrieveEvidenceWithNoCategory(testUserId, projects.get(1).getId()).size());
    }

    @Test
    @Transactional
    void givenMultipleEvidencesExistsWithoutCategories_findEvidenceWithNoCategories(){
        int testUserId = 0;
        Evidence evidence = new Evidence(testUserId, projects.get(1).getId(), "Evidence One", TEST_DESCRIPTION, Date.valueOf("2022-05-14"), "");
        Evidence evidence1 = new Evidence(testUserId, projects.get(1).getId(), "Evidence Two", TEST_DESCRIPTION, Date.valueOf("2022-05-14"), "");
        evidenceService.saveEvidence(evidence);
        evidenceService.saveEvidence(evidence1);
        assertEquals(2, evidenceService.retrieveEvidenceWithNoCategory(testUserId, projects.get(1).getId()).size());
    }

    @Test
    @Transactional
    void givenMultipleEvidencesExistsWithNoCategories_testSortOrder() {
        int testUserId = 0;
        Evidence evidence = new Evidence(testUserId, projects.get(1).getId(), "Evidence One", TEST_DESCRIPTION, Date.valueOf("2022-05-12"), "skill1 skill_2 {skill}  a     b  ");
        Evidence evidence1 = new Evidence(testUserId, projects.get(1).getId(), "Evidence Two", TEST_DESCRIPTION, Date.valueOf("2022-05-14"), "skill1 skill_2 {skill}");
        Evidence evidence2 = new Evidence(testUserId, projects.get(1).getId(), "Evidence Three", TEST_DESCRIPTION, Date.valueOf("2022-05-10"), "skill1 skill_2 {skill}");
        evidenceService.saveEvidence(evidence);
        evidenceService.saveEvidence(evidence1);
        evidenceService.saveEvidence(evidence2);

        List<Evidence> searchResults = evidenceService.retrieveEvidenceWithNoCategory(testUserId, projects.get(1).getId());
        assertEquals(3, searchResults.size());
        assertEquals("Evidence Two", searchResults.get(0).getTitle());
        assertEquals("Evidence One", searchResults.get(1).getTitle());
        assertEquals("Evidence Three", searchResults.get(2).getTitle());
    }

    ////////////////////////////////////////////////
    ///COPY EVIDENCE TO ANOTHER USER'S PORTFOLIO////
    ////////////////////////////////////////////////

    @Test
    @Transactional
    void whenEvidenceExists_testCopyToAnotherUserPortfolio() {
        int testUserId1 = 0;
        int testUserId2 = 1;
        Evidence evidence = new Evidence(testUserId1, projects.get(1).getId(), "Evidence One", TEST_DESCRIPTION, Date.valueOf("2022-05-14"), "");
        evidenceService.saveEvidence(evidence);
        evidenceService.copyEvidenceToNewUser(evidence.getId(), List.of(testUserId2));

        List<Evidence> evidenceList = (List<Evidence>) evidenceRepository.findAll();
        assertEquals(2, evidenceList.size());
        assertNotEquals(evidenceList.get(0).getId(), evidenceList.get(1).getId());
        assertNotEquals(evidenceList.get(0).getOwnerId(), evidenceList.get(1).getOwnerId());
        assertEquals(evidenceList.get(0).getTitle(), evidenceList.get(1).getTitle());
        assertEquals(evidenceList.get(0).getDescription(), evidenceList.get(1).getDescription());
        assertEquals(evidenceList.get(0).getDate(), evidenceList.get(1).getDate());
    }

    @Test
    @Transactional
    void whenEvidenceExistsWithSkillsAndCategories_testCopyToAnotherUserPortfolio() {
        int testUserId1 = 0;
        int testUserId2 = 1;
        Evidence evidence = new Evidence(testUserId1, projects.get(1).getId(), "Evidence One", TEST_DESCRIPTION, Date.valueOf("2022-05-14"), "skill1 skill_2 {skill}");
        Set<Categories> categoriesSet = new HashSet<>();
        categoriesSet.add(Categories.Quantitative);
        categoriesSet.add(Categories.Qualitative);
        evidence.setCategories(categoriesSet);
        evidenceService.saveEvidence(evidence);
        evidenceService.copyEvidenceToNewUser(evidence.getId(), List.of(testUserId2));

        List<Evidence> evidenceList = (List<Evidence>) evidenceRepository.findAll();
        assertEquals(2, evidenceList.size());
        assertNotEquals(evidenceList.get(0).getId(), evidenceList.get(1).getId());
        assertEquals("skill1 skill_2 {skill}", String.join(" ", evidenceList.get(1).getSkills()));
        assertEquals(evidenceList.get(0).getCategories(), evidenceList.get(1).getCategories());
    }

    @Test
    @Transactional
    void whenEvidenceExistsWithSkillsAndCategories_testCopyToMultipleUserPortfolio() {
        int testUserId0 = 0;
        int testUserId1 = 1;
        int testUserId2 = 2;
        int testUserId3 = 3;

        Evidence evidence = new Evidence(testUserId0, projects.get(1).getId(), "Evidence One", TEST_DESCRIPTION, Date.valueOf("2022-05-12"), "skill1 skill_2 {skill}");
        Set<Categories> categoriesSet = new HashSet<>();
        categoriesSet.add(Categories.Quantitative);
        categoriesSet.add(Categories.Qualitative);
        evidence.setCategories(categoriesSet);
        evidenceService.saveEvidence(evidence);
        evidenceService.copyEvidenceToNewUser(evidence.getId(), List.of(testUserId1));
        evidenceService.copyEvidenceToNewUser(evidence.getId(), List.of(testUserId2));
        evidenceService.copyEvidenceToNewUser(evidence.getId(), List.of(testUserId3));

        List<Evidence> evidenceList = (List<Evidence>) evidenceRepository.findAll();
        Evidence evidence0 = evidenceList.get(0);
        Evidence evidence1 = evidenceList.get(1);
        Evidence evidence2 = evidenceList.get(2);
        Evidence evidence3 = evidenceList.get(3);

        assertEquals(4, evidenceList.size());
        assertThat(evidence0.getId()).isNotIn(evidence1.getId(), evidence2.getId(), evidence3.getId());
        assertThat(evidenceList.get(0).getTitle()).isIn(evidenceList.get(1).getTitle(), evidenceList.get(2).getTitle(), evidenceList.get(3).getTitle());
        Stream.of(evidence1, evidence2, evidence3).forEach(evidenceSkill -> assertEquals("skill1 skill_2 {skill}", String.join(" ", evidenceSkill.getSkills())));
        Stream.of(evidence1, evidence2, evidence3).forEach(evidenceCategory -> assertEquals(evidence0.getCategories(), evidenceCategory.getCategories()));
    }

    @Test
    void whenEvidenceDoesNotExist_testCopyToAnotherUserPortfolioThrowsError() {
        int testUserId = 0;
        List<Integer> userIdList = List.of(testUserId);
        assertThrows(NoSuchElementException.class, () -> evidenceService.copyEvidenceToNewUser(0, userIdList),
                "Evidence does not exist");
        List<Evidence> evidenceList = (List<Evidence>) evidenceRepository.findAll();
        assertEquals(0, evidenceList.size());

    }

    @Test
    @Transactional
    void whenEvidenceCopiedToMultipleUsers_testGetUserIdFromEachCopiedEvidence() {
        int testUserId0 = 0;
        int testUserId1 = 1;
        int testUserId2 = 2;
        int testUserId3 = 3;
        Set<Integer> userIdSet = new HashSet<>();
        userIdSet.add(testUserId0);
        userIdSet.add(testUserId1);
        userIdSet.add(testUserId2);
        userIdSet.add(testUserId3);

        Evidence evidence = new Evidence(testUserId0, projects.get(1).getId(), "Evidence One", TEST_DESCRIPTION, Date.valueOf("2022-05-12"), "skill1 skill_2 {skill}  a     b  ");
        evidenceService.saveEvidence(evidence);
        evidenceService.copyEvidenceToNewUser(evidence.getId(), List.of(testUserId1, testUserId2, testUserId3));

        List<Evidence> evidenceList = (List<Evidence>) evidenceRepository.findAll();
        assertEquals(4, evidenceList.size());
        assertThat(evidenceList.get(0).getId()).isNotIn(evidenceList.get(1).getId(), evidenceList.get(2).getId(), evidenceList.get(3).getId());
        assertEquals(userIdSet, evidenceRepository.findById(evidence.getId()).getUsers());
        assertEquals(userIdSet, evidenceList.get(1).getUsers());
        assertEquals(userIdSet, evidenceList.get(2).getUsers());
        assertEquals(userIdSet, evidenceList.get(3).getUsers());
    }

    @Test
    @Transactional
    void whenEvidenceExistsWithWeblinks_testCopyToAnotherUserPortfolio() {
        int testUser0 = 0;
        int testUser1 = 1;
        Evidence evidence1 = new Evidence(testUser0, projects.get(1).getId(), "Three", TEST_DESCRIPTION, Date.valueOf("2022-05-14"));
        evidenceService.saveEvidence(evidence1);
        evidenceService.saveWebLink(evidence1.getId(), new WebLink("http://localhost:9000/portfolio", "My web link"));
        evidenceService.copyEvidenceToNewUser(evidence1.getId(), List.of(testUser1));

        List<Evidence> evidenceList = (List<Evidence>) evidenceRepository.findAll();
        assertEquals(2, evidenceList.size());
        assertNotEquals(evidenceList.get(0).getId(), evidenceList.get(1).getId());
        assertEquals("My web link", evidenceList.get(1).getWebLinks().get(0).getName());
    }

    @Test
    @Transactional
    void whenOtherUserAlreadyHasSkills_testCopiedEvidenceConformSkills() {
        int testUser0 = 0;
        int testUser1 = 1;
        Evidence evidence1 = new Evidence(testUser0, projects.get(1).getId(), "Three", TEST_DESCRIPTION, Date.valueOf("2022-05-14"), "skills");
        Evidence evidence2 = new Evidence(testUser1, projects.get(1).getId(), "One", TEST_DESCRIPTION, Date.valueOf("2022-05-9"), "SKILLS");
        evidenceService.saveEvidence(evidence1);
        evidenceService.saveEvidence(evidence2);
        evidenceService.copyEvidenceToNewUser(evidence1.getId(), List.of(testUser1));

        List<Evidence> evidenceList = (List<Evidence>) evidenceRepository.findAll();
        List<Evidence> allUsersEvidenceList = evidenceService.getEvidenceForPortfolio(testUser1, projects.get(1).getId());
        assertEquals(3, evidenceList.size());
        assertEquals(1, evidenceService.getSkillsFromEvidence(allUsersEvidenceList).size());
    }

    // Test a skill can be changed on multiple pieces of evidence
    @Test
    @Transactional
    void givenEvidencesExistsWithSkills_testChangeOneSkill(){
        Evidence evidence = new Evidence(1, projects.get(1).getId(), "Evidence One", TEST_DESCRIPTION, Date.valueOf("2022-05-14"), "skill1 skill_2 skill_3");
        Evidence evidence1 = new Evidence(1, projects.get(1).getId(), "Evidence Two", TEST_DESCRIPTION, Date.valueOf("2022-05-14"), "skill1 skill_2 skill4");
        evidenceService.saveEvidence(evidence);
        evidenceService.saveEvidence(evidence1);
        evidenceService.updateEvidenceSkills(1, projects.get(1).getId(), "skill1 skill_1");
        List<Evidence> evidenceList = evidenceService.retrieveEvidenceBySkill("skill_1", projects.get(1).getId());
        assertEquals(2, evidenceList.size());
        List<Evidence> badEvidenceList = evidenceService.retrieveEvidenceBySkill("skill1", projects.get(1).getId());
        assertEquals(0, badEvidenceList.size());
    }

    // Test skills can be changed to the same skill but capitalized
    @Test
    @Transactional
    void givenEvidenceExistsWithSkills_testChangeSkillCapitalization(){
        Evidence evidence = new Evidence(1, projects.get(1).getId(), "Evidence One", TEST_DESCRIPTION, Date.valueOf("2022-05-14"), "skill1");
        evidenceService.saveEvidence(evidence);
        evidenceService.updateEvidenceSkills(1, projects.get(1).getId(), "skill1 SKILL1");
        List<Evidence> evidenceList = evidenceService.retrieveEvidenceBySkill("SKILL1", projects.get(1).getId());
        assertEquals(1, evidenceList.size());
        List<Evidence> badEvidenceList = evidenceService.retrieveEvidenceBySkill("skill1", projects.get(1).getId());
        assertEquals(0, badEvidenceList.size());
    }

    // Test multiple skills can be changed
    @Test
    @Transactional
    void givenEvidenceExistsWithSkills_testChangeManySkills(){
        Evidence evidence = new Evidence(1, projects.get(1).getId(), "Evidence One", TEST_DESCRIPTION, Date.valueOf("2022-05-14"), "skill1 skill2 skill3");
        evidenceService.saveEvidence(evidence);
        evidenceService.updateEvidenceSkills(1, projects.get(1).getId(), "skill1 SKILL1 skill2 SKILL2 skill4 SKILL4");
        List<Evidence> evidenceList = evidenceService.retrieveEvidenceBySkill("SKILL2", projects.get(1).getId());
        List<String> skills = evidenceList.get(0).getSkills();
        assertTrue(skills.contains("SKILL1"));
        assertTrue(skills.contains("SKILL2"));
        assertTrue(skills.contains("skill3"));
    }

    // If no skills are changed, test nothing changes
    @Test
    @Transactional
    void givenEvidenceExistsWithSkills_testChangeNoSkills(){
        Evidence evidence = new Evidence(1, projects.get(1).getId(), "Evidence One", TEST_DESCRIPTION, Date.valueOf("2022-05-14"), "skill1");
        evidenceService.saveEvidence(evidence);
        evidenceService.updateEvidenceSkills(1, projects.get(1).getId(), "");
        List<Evidence> evidenceList = evidenceService.retrieveEvidenceBySkill("skill1", projects.get(1).getId());
        List<String> skills = evidenceList.get(0).getSkills();
        assertTrue(skills.contains("skill1"));
    }

    // If a skill is changed to an existing skill, check that it is not changed.
    @Test
    @Transactional
    void givenEvidenceExistsWithSkills_testChangeSkillToExistingSkill(){
        Evidence evidence = new Evidence(1, projects.get(1).getId(), "Evidence One", TEST_DESCRIPTION, Date.valueOf("2022-05-14"), "skill1 skill2");
        evidenceService.saveEvidence(evidence);
        evidenceService.updateEvidenceSkills(1, projects.get(1).getId(), "skill1 SKILL2");
        List<Evidence> evidenceList = evidenceService.retrieveEvidenceBySkill("skill1", projects.get(1).getId());
        List<String> skills = evidenceList.get(0).getSkills();
        System.out.println(skills);
        assertTrue(skills.contains("skill1"));
        assertTrue(skills.contains("skill2"));
    }

    // If a skill is changed that does not exist, check the rest of the app functions properly.
    @Test
    @Transactional
    void givenEvidenceExistsWithSkills_testChangeNonexistentSkill(){
        Evidence evidence = new Evidence(1, projects.get(1).getId(), "Evidence One", TEST_DESCRIPTION, Date.valueOf("2022-05-14"), "skill1 skill2");
        evidenceService.saveEvidence(evidence);
        evidenceService.updateEvidenceSkills(1, projects.get(1).getId(), "non-existent_skill another_non-existent_skill");
        List<Evidence> evidenceList = evidenceService.retrieveEvidenceBySkill("skill1", projects.get(1).getId());
        List<String> skills = evidenceList.get(0).getSkills();
        System.out.println(skills);
        assertTrue(skills.contains("skill1"));
        assertTrue(skills.contains("skill2"));
    }

    // If a skill is changed and no evidence exists, check that nothing breaks.
    @Test
    @Transactional
    void givenEvidenceExistsWithSkills_testChangeSkillWithoutEvidence(){
        evidenceService.updateEvidenceSkills(1, projects.get(1).getId(), "skill1 skill2");
        List<Evidence> evidenceList = evidenceService.retrieveEvidenceBySkill("skill1", projects.get(1).getId());
        assertEquals(0, evidenceList.size());
    }

    ////////////////////////////////////////////////
    //////////////Modify Weblinks///////////////////
    ////////////////////////////////////////////////

    @Test
    @Transactional
    void whenEvidenceExistsWithOneWebLink_testModifyWeblink() {
        int testUser0 = 0;
        Evidence evidence1 = new Evidence(testUser0, projects.get(1).getId(), "Three", TEST_DESCRIPTION, Date.valueOf("2022-05-14"));
        evidenceService.saveEvidence(evidence1);
        evidenceService.saveWebLink(evidence1.getId(), new WebLink("http://localhost:9000/portfolio", "My web link"));
        WebLink webLink = new WebLink("http://localhost:9000/planner", "Changed");
        evidenceService.modifyWebLink(evidence1.getId(), webLink, 0);
        Evidence evidence = evidenceService.getEvidenceById(evidence1.getId());
        assertEquals(webLink.getLink(), evidence.getWebLinks().get(0).getLink());
        assertEquals(webLink.getName(), evidence.getWebLinks().get(0).getName());
    }

    @Test
    @Transactional
    void whenEvidenceExistsWithMultipleWebLink_testModifyWeblink() {
        int testUser0 = 0;
        Evidence evidence1 = new Evidence(testUser0, projects.get(1).getId(), "Three", TEST_DESCRIPTION, Date.valueOf("2022-05-14"));
        evidenceService.saveEvidence(evidence1);
        evidenceService.saveWebLink(evidence1.getId(), new WebLink("http://localhost:9000/portfolio", "My web link"));
        evidenceService.saveWebLink(evidence1.getId(), new WebLink("http://localhost:9000/planner", "My web link1"));
        evidenceService.saveWebLink(evidence1.getId(), new WebLink("http://localhost:9000/profile", "My web link2"));
        evidenceService.saveWebLink(evidence1.getId(), new WebLink("http://localhost:9000/projects", "My web link3"));
        WebLink webLink = new WebLink("http://localhost:9000/planner", "Changed");
        evidenceService.modifyWebLink(evidence1.getId(), webLink, 2);
        Evidence evidence = evidenceService.getEvidenceById(evidence1.getId());
        assertEquals(webLink.getLink(), evidence.getWebLinks().get(2).getLink());
        assertEquals(webLink.getName(), evidence.getWebLinks().get(2).getName());
    }

}