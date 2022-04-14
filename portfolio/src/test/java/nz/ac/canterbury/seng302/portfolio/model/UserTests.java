package nz.ac.canterbury.seng302.portfolio.model;

import com.google.protobuf.Timestamp;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class UserTests {

    private final String testUsername = "test user";
    private final String testFirstName = "test fname";
    private final String testMiddleName = "test mname";
    private final String testLastName = "test lname";
    private final String testNickname = "test nname";
    private final String testBio = "test bio";
    private final String testPronouns = "test/tester";
    private final String testEmail = "test@email.com";
    private final String testProfileImagePath = "test/image/path";
    private Timestamp testCreated = Timestamp.newBuilder().build();

    User createDefaultUser() {
        ArrayList<UserRole> roles = new ArrayList<>();
        roles.add(UserRole.STUDENT);
        roles.add(UserRole.COURSE_ADMINISTRATOR);
        UserResponse source = UserResponse.newBuilder()
                .setUsername(testUsername)
                .setFirstName(testFirstName)
                .setMiddleName(testMiddleName)
                .setLastName(testLastName)
                .setProfileImagePath(testProfileImagePath)
                .setCreated(testCreated)
                .setEmail(testEmail)
                .setPersonalPronouns(testPronouns)
                .setBio(testBio)
                .setNickname(testNickname)
                .addAllRoles(roles).build();
        return new User(source);
    }

    @Test
    void testCreateUser() {
        User testUser = createDefaultUser();
        assertEquals(testUsername, testUser.getUsername());
        assertEquals(testFirstName, testUser.getFirstName());
        assertEquals(testMiddleName, testUser.getMiddleName());
        assertEquals(testLastName, testUser.getLastName());
        assertEquals(testNickname, testUser.getNickname());
        assertEquals(testEmail, testUser.getEmail());
        assertEquals(testPronouns, testUser.getPersonalPronouns());
        assertEquals(testBio, testUser.getBio());
        assertEquals(testProfileImagePath, testUser.getProfileImagePath());
        assertEquals(Timestamp.newBuilder().build(), testUser.getCreated());
        assertEquals(2, testUser.getRoles().size());
        assertTrue(testUser.getRoles().contains(UserRole.STUDENT));
        assertTrue(testUser.getRoles().contains(UserRole.COURSE_ADMINISTRATOR));
    }

}
