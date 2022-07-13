package nz.ac.canterbury.seng302.portfolio.model;

import com.google.protobuf.Timestamp;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@SpringBootTest
class UserTests {

    // Test that creating a User from a UserResponse puts all the fields in the correct places.
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

    // Test that when a middle name is missing the full name renders correctly
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

    // Test that when a middle name is present the full name renders correctly
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

    // Test that when no roles are present no roles are returned from getRoleStrings
    @Test
    void testGetRoleStringsWithNoRoles() {
        UserResponse source = UserResponse.newBuilder().build();
        User testUser = new User(source);
        assertEquals(0, testUser.getRoleStrings().size());
    }

    // Test that when all roles are present all roles are returned from getRoleStrings
    @Test
    void testGetRoleStringsWithAllRoles() {
        ArrayList<UserRole> roles = new ArrayList<>();
        roles.add(UserRole.STUDENT);
        roles.add(UserRole.TEACHER);
        roles.add(UserRole.COURSE_ADMINISTRATOR);
        UserResponse source = UserResponse.newBuilder()
                .addAllRoles(roles).build();
        User testUser = new User(source);
        assertEquals(3, testUser.getRoleStrings().size());
        ArrayList<String> expectedRoles = new ArrayList<>();
        expectedRoles.add("Course Administrator");
        expectedRoles.add("Student");
        expectedRoles.add("Teacher");
        assertEquals(expectedRoles, testUser.getRoleStrings());
    }

    // Test that when a user has just been created the end of getMemberSince says 0 months
    @Test
    void testGetMemberSinceWithNewUser() {
        Instant time = Instant.now();
        Timestamp timestamp = Timestamp.newBuilder().setSeconds(time.getEpochSecond()).setNanos(time.getNano()).build();
        UserResponse source = UserResponse.newBuilder()
                .setCreated(timestamp).build();
        User testUser = new User(source);

        Date dateCreated = java.util.Date.from(time);
        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMMM yyyy");
        String expected = "Member Since: " + dateFormat.format(dateCreated) + " (0 months)";
        assertEquals(expected, testUser.getMemberSince());
    }

    // Provides arguments for the parameterized tests for getting membership time
    static Stream<Arguments> getMemberSinceTestParamProvider() {
        return Stream.of(
                arguments(40, " (1 month)"), // Tests one month ago
                arguments(100, " (3 months)"), // Tests 3 months ago
                arguments(370, " (1 year 0 months)"), // Tests 1 year ago
                arguments(770, " (2 years 1 month)") // Tests 2 years 1 month ago
        );
    }

    // Tests that the user's membership length is correctly converted into string format
    // Uses parameters from the above method
    @ParameterizedTest
    @MethodSource("getMemberSinceTestParamProvider")
    void testGetMemberSince(int daysToSubtract, String expectedTime) {
        Instant time = Instant.now().minus(daysToSubtract, ChronoUnit.DAYS);
        Timestamp timestamp = Timestamp.newBuilder().setSeconds(time.getEpochSecond()).setNanos(time.getNano()).build();
        UserResponse source = UserResponse.newBuilder()
                .setCreated(timestamp).build();
        User testUser = new User(source);

        Date dateCreated = java.util.Date.from(time);
        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMMM yyyy");
        String expected = "Member Since: " + dateFormat.format(dateCreated) + expectedTime;
        assertEquals(expected, testUser.getMemberSince());
    }
}
