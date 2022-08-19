package nz.ac.canterbury.seng302.portfolio.model;

import nz.ac.canterbury.seng302.portfolio.model.project.ProjectEdit;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class ProjectEditTests {

    @Test
    void after4Seconds_testProjectEditNotTimedOut() throws InterruptedException {
        ProjectEdit edit = new ProjectEdit(0, 0, "");
        TimeUnit.SECONDS.sleep(4);
        assertThat(edit.hasTimedOut()).isFalse();
    }

    @Test
    void after6Seconds_testProjectEditTimedOut() throws InterruptedException {
        ProjectEdit edit = new ProjectEdit(0, 0, "");
        TimeUnit.SECONDS.sleep(6);
        assertThat(edit.hasTimedOut()).isTrue();
    }

    @Test
    void withSameProjectIdAndDifferentUserId_testProjectEditIsRelevant() {
        int projectId = 1;
        int userId = 1;
        int otherUserId = 2;
        ProjectEdit edit = new ProjectEdit(projectId, userId, "");
        assertThat(edit.isRelevant(projectId, otherUserId)).isTrue();
    }

    @Test
    void withSameProjectIdAndSameUserId_testProjectEditIsNotRelevant() {
        int projectId = 1;
        int userId = 1;
        ProjectEdit edit = new ProjectEdit(projectId, userId, "");
        assertThat(edit.isRelevant(projectId, userId)).isFalse();
    }

    @Test
    void withDifferentProjectIdAndDifferentUserId_testProjectEditIsNotRelevant() {
        int projectId = 1;
        int userId = 1;
        int otherProjectId = 2;
        ProjectEdit edit = new ProjectEdit(projectId, userId, "");
        assertThat(edit.isRelevant(otherProjectId, userId)).isFalse();
    }

    @Test
    void whenSameUser_testProjectEditIsFromUser() {
        int userId = 1;
        ProjectEdit edit = new ProjectEdit(0, userId, "");
        assertThat(edit.isFromUser(userId)).isTrue();
    }

    @Test
    void whenDifferentUser_testProjectEditIsNotFromUser() {
        int userId = 1;
        int otherUserId = 2;
        ProjectEdit edit = new ProjectEdit(0, userId, "");
        assertThat(edit.isFromUser(otherUserId)).isFalse();
    }

}
