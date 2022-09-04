package nz.ac.canterbury.seng302.portfolio.model;


import nz.ac.canterbury.seng302.portfolio.model.evidence.Commit;
import nz.ac.canterbury.seng302.portfolio.model.evidence.Evidence;
import nz.ac.canterbury.seng302.portfolio.model.project.Project;
import nz.ac.canterbury.seng302.portfolio.repository.evidence.EvidenceRepository;
import nz.ac.canterbury.seng302.portfolio.service.evidence.EvidenceService;
import nz.ac.canterbury.seng302.portfolio.service.project.ProjectService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@AutoConfigureTestDatabase
@SpringBootTest
class CommitTests {

    private static final String EVIDENCE_DESCRIPTION = "According to all known laws of aviation, there is no way a bee should be able to fly.";

    @Autowired
    EvidenceService evidenceService;

    @Autowired
    ProjectService projectService;

    @Autowired
    EvidenceRepository evidenceRepository;

    static Project project;

    //Initialise the database with projects before each test.
    @BeforeEach
    void storeProjects() {
        projectService.saveProject(new Project("Project Name", "Test Project", Date.valueOf("2022-04-9"), Date.valueOf("2022-06-16")));
        project = projectService.getAllProjects().get(0);
    }

    //Refresh the database after each test.
    @AfterEach
    void cleanDatabase() {
        projectService.deleteProjectById(project.getId());
        evidenceRepository.deleteAll();
    }

    @Test
    @Transactional
    void whenEvidenceAdded_testSaveOneCommit() {
        Evidence evidence = new Evidence(0, project.getId(), "Test Evidence", EVIDENCE_DESCRIPTION, Date.valueOf("2022-05-14"));
        evidenceService.saveEvidence(evidence);
        evidenceService.saveCommit(evidence.getId(), new Commit("Tester", Date.valueOf("2022-05-14"), "www.testCommit.com", "#Test test commit" ));
        Evidence receivedEvidence = evidenceService.getEvidenceById(evidence.getId());
        assertEquals("Tester", receivedEvidence.getCommits().get(0).getAuthor());
    }

    @Test
    @Transactional
    void whenEvidenceAdded_testSaveMultipleCommits() {
        Evidence evidence = new Evidence(0, project.getId(), "Test Evidence", EVIDENCE_DESCRIPTION, Date.valueOf("2022-05-14"));
        evidenceService.saveEvidence(evidence);
        evidenceService.saveCommit(evidence.getId(), new Commit("Tester", Date.valueOf("2022-05-14"), "www.testCommit.com", "#Test test 1 commit" ));
        evidenceService.saveCommit(evidence.getId(), new Commit("Tester", Date.valueOf("2022-05-14"), "www.testCommit.com", "#Test test 2 commit" ));
        evidenceService.saveCommit(evidence.getId(), new Commit("Tester", Date.valueOf("2022-05-14"), "www.testCommit.com", "#Test test 3 commit" ));
        Evidence receivedEvidence = evidenceService.getEvidenceById(evidence.getId());
        assertEquals(3, receivedEvidence.getNumberCommits());
    }

    @Test
    @Transactional
    void whenNoEvidenceAdded_testCommitNotSaved() {
        Exception exception = assertThrows(Exception.class,
                () -> evidenceService.saveCommit(0, new Commit("Tester", Date.valueOf("2022-05-14"), "www.testCommit.com", "#Test test 1 commit" )));
        String expectedMessage = "Evidence not found: commit not saved";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

}
