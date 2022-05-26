package nz.ac.canterbury.seng302.portfolio.model;

import nz.ac.canterbury.seng302.portfolio.service.ProjectEdits;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class ProjectEditsTests {

    String editString = "edit string";

    @Test
    void testProjectEditsNewEditAppears() {
        int projectId = 1;
        int userId = 1;
        int otherUserId = 2;
        ProjectEdits edits = new ProjectEdits();
        edits.newEdit(projectId, userId, editString);
        assertThat(edits.getEdits(projectId, otherUserId)).hasToString("{\"edits\": [\"" + editString + "\"], \"refresh\":false}");
    }

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

}
