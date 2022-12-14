
package nz.ac.canterbury.seng302.identityprovider.entity;

import com.google.protobuf.Timestamp;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class UserTests {

    User user1;

    @BeforeEach
    void setup() {
        Instant time = Instant.now();
        Timestamp timestamp = Timestamp.newBuilder().setSeconds(time.getEpochSecond())
                .setNanos(time.getNano()).build();
        user1 = new User(1, "bauerjac", "Jack", "Brown", "Bauer", "Jack-Jack", "howdy", "HE/HIM", "jack@gmail.com", "password", timestamp);
    }
    //Tests that the toString function works
    @Test
    void givenUserExists_testToString(){
        assertEquals("User[userId=1, username=bauerjac, firstName=Jack, middleName=Brown, lastName=Bauer, nickname=Jack-Jack, bio=howdy, preferredPronouns=HE/HIM, email=jack@gmail.com]", user1.toString());
    }

    //Tests that the getUserId function works
    @Test
    void givenUserExists_testUserIdMethod(){
        assertEquals(1, user1.getUserId());
    }

    //Tests that the getUsername function works
    @Test
    void givenUserExists_testUsernameMethods() {
        assertEquals("bauerjac", user1.getUsername());
        //not setter for username
    }

    //Tests that the getFirstName and setFirstName functions work
    @Test
    void givenUserExists_testFirstNameMethods() {
        assertEquals("Jack", user1.getFirstName());
        user1.setFirstName("Jacko");
        assertEquals("Jacko", user1.getFirstName());
    }

    //Tests that the getMiddleName and setMiddleName functions work
    @Test
    void givenUserExists_testMiddleNameMethods() {
        assertEquals("Brown", user1.getMiddleName());
        user1.setMiddleName("Browno");
        assertEquals("Browno", user1.getMiddleName());
    }

    //Tests that the getLastName and setLastName functions work
    @Test
    void givenUserExists_testLastNameMethods() {
        assertEquals("Bauer", user1.getLastName());
        user1.setLastName("Bauero");
        assertEquals("Bauero", user1.getLastName());
    }

    //Tests that the getNickname and setNickname functions work
    @Test
    void givenUserExists_testNicknameMethods() {
        assertEquals("Jack-Jack", user1.getNickname());
        user1.setNickname("Jacko-Jacko");
        assertEquals("Jacko-Jacko", user1.getNickname());
    }

    //Tests that the getBio and setBio functions work
    @Test
    void givenUserExists_testBioMethods() {
        assertEquals("howdy", user1.getBio());
        user1.setBio("howdyo");
        assertEquals("howdyo", user1.getBio());
    }

    //Tests that the getPronouns and setPronouns functions work
    @Test
    void givenUserExists_testPronounMethods() {
        assertEquals("HE/HIM", user1.getPersonalPronouns());
        user1.setPersonalPronouns("SHE/HER");
        assertEquals("SHE/HER", user1.getPersonalPronouns());
    }

    //Tests that the getEmail and setEmail functions work
    @Test
    void givenUserExists_testEmailMethods() {
        assertEquals("jack@gmail.com", user1.getEmail());
        user1.setEmail("jacko@gmail.com");
        assertEquals("jacko@gmail.com", user1.getEmail());
    }

    //Tests that the getProfileImagePath and setProfileImagePath functions work
    @Test
    void givenUserExists_testProfileImagePathMethods() {
        assertNull(user1.getProfileImagePath());
        user1.setProfileImagePath("http://localhost:8080/resources/test.jpg");
        assertEquals("http://localhost:8080/resources/test.jpg", user1.getProfileImagePath());
    }

    //Tests that the password is not stored in plain text
    @Test
    void givenUserExists_testHashingOccurs(){
        assertNotEquals("password", user1.getPassword());
    }

    //Tests that the checkPassword function works with the correct password
    @Test
    void givenUserExists_testCorrectPassword() {
        assertTrue(user1.checkPassword("password"));
    }

    //Tests that the checkPassword function fails with the incorrect password
    @Test
    void givenUserExists_testIncorrectPassword() {
        assertFalse(user1.checkPassword("theWrongPassword"));
    }

    // Tests that every user has a student role by default
    @Test
    void givenUserExists_testDefaultStudentRole() {
        assertTrue(user1.getRoles().contains(UserRole.STUDENT));
    }

    // Tests that adding a second role adds the new role
    @Test
    void givenUserExists_testAddingSecondRoleContainsNewRole() {
        user1.addRole(UserRole.TEACHER);
        // Check teacher role has been added
        assertTrue(user1.getRoles().contains(UserRole.TEACHER));
    }

    // Tests that adding a second role keeps the old role
    @Test
    void givenUserExists_testAddingSecondRoleContainsOldRole() {
        user1.addRole(UserRole.TEACHER);
        // Check student role is still there
        assertTrue(user1.getRoles().contains(UserRole.STUDENT));
    }

    // Tests that adding a second role only adds one role to the list
    @Test
    void givenUserExists_testAddingSecondRoleListCorrectSize() {
        user1.addRole(UserRole.TEACHER);
        user1.addRole(UserRole.TEACHER);
        // Check list of roles is the correct size
        assertEquals(2, user1.getRoles().size());
    }

    // Tests that adding the same role again doesn't do anything
    @Test
    void givenUserExists_testAddingRepeatedRoleListCorrectSize() {
        user1.addRole(UserRole.TEACHER);
        // Check list of roles is the correct size
        assertEquals(2, user1.getRoles().size());
    }

    // Tests that removing roles works
    @Test
    void givenUserExists_testRemoveGoodRole() {
        user1.addRole(UserRole.TEACHER);
        user1.removeRole(UserRole.STUDENT);
        assertEquals(1, user1.getRoles().size());
        assertTrue(user1.getRoles().contains(UserRole.TEACHER));
    }

    // Tests that removing a non-existent role removes no roles
    @Test
    void givenUserExists_testRemoveBadRole() {
        user1.addRole(UserRole.TEACHER);
        user1.removeRole(UserRole.COURSE_ADMINISTRATOR);
        assertEquals(2, user1.getRoles().size());
        assertTrue(user1.getRoles().contains(UserRole.TEACHER));
        assertTrue(user1.getRoles().contains(UserRole.STUDENT));
    }

    @Test
    void givenUserExists_setAndGetTimeCreated(){
        Instant time = Instant.now();
        Timestamp timestamp = Timestamp.newBuilder().setSeconds(time.getEpochSecond())
                .setNanos(time.getNano()).build();
        user1.setTimeCreated(timestamp);
        assertEquals(timestamp, user1.getTimeCreated());
    }




}