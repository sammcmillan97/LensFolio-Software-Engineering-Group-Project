
package nz.ac.canterbury.seng302.identityprovider.entity;

import com.google.protobuf.Timestamp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class UserTests {

    User user1;

    @BeforeEach
    private void setup() {
        Instant time = Instant.now();
        Timestamp timestamp = Timestamp.newBuilder().setSeconds(time.getEpochSecond())
                .setNanos(time.getNano()).build();
        user1 = new User(1, "bauerjac", "Jack", "Brown", "Bauer", "Jack-Jack", "howdy", "HE/HIM", "jack@gmail.com", "password", timestamp);
    }
    //Tests that the toString function works
    @Test
    void testToString(){
        assertEquals("User[userId=1, username=bauerjac, firstName=Jack, middleName=Brown, lastName=Bauer, nickname=Jack-Jack, bio=howdy, preferredPronouns=HE/HIM, email=jack@gmail.com]", user1.toString());
    }

    //Tests that the getUserId function works
    @Test
    void testUserIdMethod(){
        assertEquals(1, user1.getUserId());
    }

    //Tests that the getUsername function works
    @Test
    void testUsernameMethods() {
        assertEquals("bauerjac", user1.getUsername());
        //not setter for username
    }

    //Tests that the getFirstName and setFirstName functions work
    @Test
    void testFirstNameMethods() {
        assertEquals("Jack", user1.getFirstName());
        user1.setFirstName("Jacko");
        assertEquals("Jacko", user1.getFirstName());
    }

    //Tests that the getMiddleName and setMiddleName functions work
    @Test
    void testMiddleNameMethods() {
        assertEquals("Brown", user1.getMiddleName());
        user1.setMiddleName("Browno");
        assertEquals("Browno", user1.getMiddleName());
    }

    //Tests that the getLastName and setLastName functions work
    @Test
    void testLastNameMethods() {
        assertEquals("Bauer", user1.getLastName());
        user1.setLastName("Bauero");
        assertEquals("Bauero", user1.getLastName());
    }

    //Tests that the getNickname and setNickname functions work
    @Test
    void testNicknameMethods() {
        assertEquals("Jack-Jack", user1.getNickname());
        user1.setNickname("Jacko-Jacko");
        assertEquals("Jacko-Jacko", user1.getNickname());
    }

    //Tests that the getBio and setBio functions work
    @Test
    void testBioMethods() {
        assertEquals("howdy", user1.getBio());
        user1.setBio("howdyo");
        assertEquals("howdyo", user1.getBio());
    }

    //Tests that the getPronouns and setPronouns functions work
    @Test
    void testPronounMethods() {
        assertEquals("HE/HIM", user1.getPersonalPronouns());
        user1.setPersonalPronouns("SHE/HER");
        assertEquals("SHE/HER", user1.getPersonalPronouns());
    }

    //Tests that the getEmail and setEmail functions work
    @Test
    void testEmailMethods() {
        assertEquals("jack@gmail.com", user1.getEmail());
        user1.setEmail("jacko@gmail.com");
        assertEquals("jacko@gmail.com", user1.getEmail());
    }

    //Tests that the password is not stored in plain text
    @Test
    void testEncryptOccurs(){
        assertNotEquals(user1.getPassword(), "password");
    }

    //Tests that the checkPassword function works with the correct password
    @Test
    void testCorrectPassword() {
        assertTrue(user1.checkPassword("password"));
    }

    //Tests that the checkPassword function fails with the incorrect password
    @Test
    void testIncorrectPassword() {
        assertFalse(user1.checkPassword("theWrongPassword"));
    }

    // Tests that every user has a student role by default
    @Test
    void testDefaultStudentRole() {

    }



}