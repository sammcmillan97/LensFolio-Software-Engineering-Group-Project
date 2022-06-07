package nz.ac.canterbury.seng302.portfolio.model;

import com.google.protobuf.Timestamp;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

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

    // Test that when a user is one month old the end of getMemberSince says 1 month
    @Test
    void testGetMemberSinceWithOneMonth() {
        Instant time = Instant.now().minus(40, ChronoUnit.DAYS);
        Timestamp timestamp = Timestamp.newBuilder().setSeconds(time.getEpochSecond()).setNanos(time.getNano()).build();
        UserResponse source = UserResponse.newBuilder()
                .setCreated(timestamp).build();
        User testUser = new User(source);

        Date dateCreated = java.util.Date.from(time);
        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMMM yyyy");
        String expected = "Member Since: " + dateFormat.format(dateCreated) + " (1 month)";
        assertEquals(expected, testUser.getMemberSince());
    }

    // Test that when a user is three months old the end of getMemberSince says 3 months
    @Test
    void testGetMemberSinceWithThreeMonths() {
        Instant time = Instant.now().minus(100, ChronoUnit.DAYS);
        Timestamp timestamp = Timestamp.newBuilder().setSeconds(time.getEpochSecond()).setNanos(time.getNano()).build();
        UserResponse source = UserResponse.newBuilder()
                .setCreated(timestamp).build();
        User testUser = new User(source);

        Date dateCreated = java.util.Date.from(time);
        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMMM yyyy");
        String expected = "Member Since: " + dateFormat.format(dateCreated) + " (3 months)";
        assertEquals(expected, testUser.getMemberSince());
    }

    // Test that when a user is one year old the end of getMemberSince says 1 year 0 months
    @Test
    void testGetMemberSinceWithOneYear() {
        Instant time = Instant.now().minus(370, ChronoUnit.DAYS);
        Timestamp timestamp = Timestamp.newBuilder().setSeconds(time.getEpochSecond()).setNanos(time.getNano()).build();
        UserResponse source = UserResponse.newBuilder()
                .setCreated(timestamp).build();
        User testUser = new User(source);

        Date dateCreated = java.util.Date.from(time);
        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMMM yyyy");
        String expected = "Member Since: " + dateFormat.format(dateCreated) + " (1 year 0 months)";
        assertEquals(expected, testUser.getMemberSince());
    }

    // Test that when a user is two years and one month old, the end of getMemberSince says 2 years 1 month
    @Test
    void testGetMemberSinceWithTwoYearsOneMonth() {
        Instant time = Instant.now().minus(770, ChronoUnit.DAYS);
        Timestamp timestamp = Timestamp.newBuilder().setSeconds(time.getEpochSecond()).setNanos(time.getNano()).build();
        UserResponse source = UserResponse.newBuilder()
                .setCreated(timestamp).build();
        User testUser = new User(source);

        Date dateCreated = java.util.Date.from(time);
        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMMM yyyy");
        String expected = "Member Since: " + dateFormat.format(dateCreated) + " (2 years 1 month)";
        assertEquals(expected, testUser.getMemberSince());
    }

    // Test that the users aren't equal when their first names don't match
    @Test
    void testUsersUnequalWhenFirstNamesDifferent() {
        UserResponse response = UserResponse.newBuilder().setFirstName("Frank").build();
        UserResponse response2 = UserResponse.newBuilder().setFirstName("Franklin").build();
        User user = new User(response);
        User user2 = new User(response2);
        assertNotEquals(user, user2);
    }

    // Test that the users are equal when their first names match
    @Test
    void testUsersEqualWhenFirstNamesSame() {
        UserResponse response = UserResponse.newBuilder().setFirstName("Frank").build();
        User user = new User(response);
        User user2 = new User(response);
        assertEquals(user, user2);
    }

    // Test that the users aren't equal when their middle names don't match
    @Test
    void testUsersUnequalWhenMiddleNamesDifferent() {
        UserResponse response = UserResponse.newBuilder().setMiddleName("Frank").build();
        UserResponse response2 = UserResponse.newBuilder().setMiddleName("Franklin").build();
        User user = new User(response);
        User user2 = new User(response2);
        assertNotEquals(user, user2);
    }

    // Test that the users are equal when their middle names match
    @Test
    void testUsersEqualWhenMiddleNamesSame() {
        UserResponse response = UserResponse.newBuilder().setMiddleName("Frank").build();
        User user = new User(response);
        User user2 = new User(response);
        assertEquals(user, user2);
    }

    // Test that the users aren't equal when their last names don't match
    @Test
    void testUsersUnequalWhenLastNamesDifferent() {
        UserResponse response = UserResponse.newBuilder().setLastName("Frank").build();
        UserResponse response2 = UserResponse.newBuilder().setLastName("Franklin").build();
        User user = new User(response);
        User user2 = new User(response2);
        assertNotEquals(user, user2);
    }

    // Test that the users are equal when their last names match
    @Test
    void testUsersEqualWhenLastNamesSame() {
        UserResponse response = UserResponse.newBuilder().setLastName("Frank").build();
        User user = new User(response);
        User user2 = new User(response);
        assertEquals(user, user2);
    }
}