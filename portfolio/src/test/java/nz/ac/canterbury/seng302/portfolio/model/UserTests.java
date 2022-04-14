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

    @Test
    void testCreateUser() {
        ArrayList<UserRole> roles = new ArrayList<>();
        roles.add(UserRole.STUDENT);
        roles.add(UserRole.COURSE_ADMINISTRATOR);
        String testUsername = "test user";
        String testFirstName = "test fname";
        String testMiddleName = "test mname";
        String testLastName = "test lname";
        String testNickname = "test nname";
        String testBio = "test bio";
        String testPronouns = "test/tester";
        String testEmail = "test@email.com";
        String testProfileImagePath = "test/image/path";
        Timestamp testCreated = Timestamp.newBuilder().build();
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
        User testUser = new User(source);
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

    @Test
    void testGetFullNameNoMiddleName() {
        String testFirstName = "fname";
        String testLastName = "lname";
        UserResponse source = UserResponse.newBuilder()
                .setFirstName(testFirstName)
                .setLastName(testLastName).build();
        User testUser = new User(source);
        assertEquals(testFirstName + " " + testLastName, testUser.getFullName());
    }

    @Test
    void testGetFullNameWithMiddleName() {
        String testFirstName = "fname";
        String testMiddleName = "mname";
        String testLastName = "lname";
        UserResponse source = UserResponse.newBuilder()
                .setFirstName(testFirstName)
                .setMiddleName(testMiddleName)
                .setLastName(testLastName).build();
        User testUser = new User(source);
        assertEquals(testFirstName + " " + testMiddleName + " " + testLastName, testUser.getFullName());
    }

}
