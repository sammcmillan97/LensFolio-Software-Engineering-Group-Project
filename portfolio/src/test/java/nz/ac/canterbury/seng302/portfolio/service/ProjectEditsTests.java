package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.service.ProjectEdits;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class ProjectEditsTests {

    String editString = "edit string";

    // Tests an edit appears in getEdits after being logged with newEdit
    @Test
    void testProjectEditsNewEditAppears() {
        int projectId = 1;
        int userId = 1;
        int otherUserId = 2;
        ProjectEdits edits = new ProjectEdits();
        edits.newEdit(projectId, userId, editString);
        assertThat(edits.getEdits(projectId, otherUserId)).hasToString("{\"edits\": [\"" + editString + "\"], \"refresh\":false}");
    }

    // Tests an edit from the same user overwrites the old edit
    @Test
    void testProjectEditsOverwriteSameUserEdit() {
        int projectId = 1;
        int userId = 1;
        int otherUserId = 2;
        int otherProjectId = 2;
        ProjectEdits edits = new ProjectEdits();
        edits.newEdit(projectId, userId, editString);
        edits.newEdit(otherProjectId, userId, editString);
        assertThat(edits.getEdits(projectId, otherUserId)).hasToString("{\"edits\": [], \"refresh\":false}");
        assertThat(edits.getEdits(otherProjectId, otherUserId)).hasToString("{\"edits\": [\"" + editString + "\"], \"refresh\":false}");
    }

    // Tests multiple edits appear in the edit list
    @Test
    void testProjectEditsTwoEdits() {
        int projectId = 1;
        int userId = 1;
        int otherUserId = 2;
        int thirdUserId = 3;
        ProjectEdits edits = new ProjectEdits();
        edits.newEdit(projectId, userId, editString);
        edits.newEdit(projectId, otherUserId, editString);
        assertThat(edits.getEdits(projectId, thirdUserId)).hasToString("{\"edits\": [\"" + editString + "\",\"" + editString + "\"], \"refresh\":false}");
    }

    // Tests that the refresh value is true when the getEdits function is called for the first time after refreshing,
    // and false afterwards.
    @Test
    void testProjectEditsRefreshTrueOnceAfterRefresh() {
        int projectId = 1;
        int userId = 1;
        ProjectEdits edits = new ProjectEdits();
        edits.refreshProject(projectId);
        assertThat(edits.getEdits(projectId, userId)).hasToString("{\"edits\": [], \"refresh\":true}");
        assertThat(edits.getEdits(projectId, userId)).hasToString("{\"edits\": [], \"refresh\":false}");
    }

    // Tests that the refresh value is false when the getEdits function is called with a project that has not been refreshed
    @Test
    void testProjectEditsRefreshFalseIfNoRefresh() {
        int projectId = 1;
        int otherProjectId = 2;
        int userId = 1;
        ProjectEdits edits = new ProjectEdits();
        edits.refreshProject(projectId);
        assertThat(edits.getEdits(otherProjectId, userId)).hasToString("{\"edits\": [], \"refresh\":false}");
    }

    // Tests that the refresh value is true when the getEdits function is called with a project that has been refreshed,
    // and remains true for a separate user
    @Test
    void testProjectEditsRefreshTrueForAllUsers() {
        int projectId = 1;
        int userId = 1;
        int otherUserId = 2;
        ProjectEdits edits = new ProjectEdits();
        edits.refreshProject(projectId);
        assertThat(edits.getEdits(projectId, userId)).hasToString("{\"edits\": [], \"refresh\":true}");
        assertThat(edits.getEdits(projectId, otherUserId)).hasToString("{\"edits\": [], \"refresh\":true}");
    }

}
