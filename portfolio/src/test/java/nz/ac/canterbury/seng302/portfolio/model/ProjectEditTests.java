package nz.ac.canterbury.seng302.portfolio.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class ProjectEditTests {

    @Test
    void testProjectEditNotTimedOutAfter4Seconds() throws InterruptedException {
        ProjectEdit edit = new ProjectEdit(0, 0, "");
        TimeUnit.SECONDS.sleep(4);
        assertThat(edit.hasTimedOut()).isFalse();
    }

    @Test
    void testProjectEditTimedOutAfter6Seconds() throws InterruptedException {
        ProjectEdit edit = new ProjectEdit(0, 0, "");
        TimeUnit.SECONDS.sleep(6);
        assertThat(edit.hasTimedOut()).isTrue();
    }

    @Test
    void testProjectEditIsRelevantWithSameProjectIdAndDifferentUserId() {
        int projectId = 1;
        int userId = 1;
        int otherUserId = 2;
        ProjectEdit edit = new ProjectEdit(projectId, userId, "");
        assertThat(edit.isRelevant(projectId, otherUserId)).isTrue();
    }

    @Test
    void testProjectEditIsNotRelevantWithSameProjectIdAndSameUserId() {
        int projectId = 1;
        int userId = 1;
        ProjectEdit edit = new ProjectEdit(projectId, userId, "");
        assertThat(edit.isRelevant(projectId, userId)).isFalse();
    }

    @Test
    void testProjectEditIsNotRelevantWithDifferentProjectIdAndDifferentUserId() {
        int projectId = 1;
        int userId = 1;
        int otherProjectId = 2;
        ProjectEdit edit = new ProjectEdit(projectId, userId, "");
        assertThat(edit.isRelevant(otherProjectId, userId)).isFalse();
    }

    @Test
    void testProjectEditIsFromUserWhenSameUser() {
        int userId = 1;
        ProjectEdit edit = new ProjectEdit(0, userId, "");
        assertThat(edit.isFromUser(userId)).isTrue();
    }

    @Test
    void testProjectEditIsNotFromUserWhenDifferentUser() {
        int userId = 1;
        int otherUserId = 2;
        ProjectEdit edit = new ProjectEdit(0, userId, "");
        assertThat(edit.isFromUser(otherUserId)).isFalse();
    }

}
