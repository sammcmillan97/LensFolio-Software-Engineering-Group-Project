package nz.ac.canterbury.seng302.portfolio.service.project;

import nz.ac.canterbury.seng302.portfolio.service.project.ProjectEditsService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class ProjectEditsServiceTests {

    String editString = "edit string";

    // Tests an edit appears in getEdits after being logged with newEdit
    @Test
    void givenProjectIsUpdated_testNewEditAppears() {
        int projectId = 1;
        int userId = 1;
        int otherUserId = 2;
        ProjectEditsService edits = new ProjectEditsService();
        edits.newEdit(projectId, userId, editString);
        assertThat(edits.getEdits(projectId, otherUserId)).hasToString("{\"edits\": [\"" + editString + "\"], \"refresh\":false}");
    }

    // Tests an edit from the same user overwrites the old edit
    @Test
    void givenEditIsOverwrriten_testDetailsAreOverwritten() {
        int projectId = 1;
        int userId = 1;
        int otherUserId = 2;
        int otherProjectId = 2;
        ProjectEditsService edits = new ProjectEditsService();
        edits.newEdit(projectId, userId, editString);
        edits.newEdit(otherProjectId, userId, editString);
        assertThat(edits.getEdits(projectId, otherUserId)).hasToString("{\"edits\": [], \"refresh\":false}");
        assertThat(edits.getEdits(otherProjectId, otherUserId)).hasToString("{\"edits\": [\"" + editString + "\"], \"refresh\":false}");
    }

    // Tests multiple edits appear in the edit list
    @Test
    void givenTwoEdits_testProjectEdits() {
        int projectId = 1;
        int userId = 1;
        int otherUserId = 2;
        int thirdUserId = 3;
        ProjectEditsService edits = new ProjectEditsService();
        edits.newEdit(projectId, userId, editString);
        edits.newEdit(projectId, otherUserId, editString);
        assertThat(edits.getEdits(projectId, thirdUserId)).hasToString("{\"edits\": [\"" + editString + "\",\"" + editString + "\"], \"refresh\":false}");
    }

    // Tests that the refresh value is true when the getEdits function is called for the first time after refreshing,
    // and false afterwards.
    @Test
    void givenRefresh_testProjectEditsRefreshTrueOnce() {
        int projectId = 1;
        int userId = 1;
        ProjectEditsService edits = new ProjectEditsService();
        edits.refreshProject(projectId);
        assertThat(edits.getEdits(projectId, userId)).hasToString("{\"edits\": [], \"refresh\":true}");
        assertThat(edits.getEdits(projectId, userId)).hasToString("{\"edits\": [], \"refresh\":false}");
    }

    // Tests that the refresh value is false when the getEdits function is called with a project that has not been refreshed
    @Test
    void givenNoRefresh_testProjectEditsRefreshFalse() {
        int projectId = 1;
        int otherProjectId = 2;
        int userId = 1;
        ProjectEditsService edits = new ProjectEditsService();
        edits.refreshProject(projectId);
        assertThat(edits.getEdits(otherProjectId, userId)).hasToString("{\"edits\": [], \"refresh\":false}");
    }

    // Tests that the refresh value is true when the getEdits function is called with a project that has been refreshed,
    // and remains true for a separate user
    @Test
    void givenGetEditsCalled_givenProjectIsRefreshed_testRefreshValueIsTrue() {
        int projectId = 1;
        int userId = 1;
        int otherUserId = 2;
        ProjectEditsService edits = new ProjectEditsService();
        edits.refreshProject(projectId);
        assertThat(edits.getEdits(projectId, userId)).hasToString("{\"edits\": [], \"refresh\":true}");
        assertThat(edits.getEdits(projectId, otherUserId)).hasToString("{\"edits\": [], \"refresh\":true}");
    }

}
