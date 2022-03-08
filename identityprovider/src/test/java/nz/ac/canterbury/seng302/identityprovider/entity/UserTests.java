
package nz.ac.canterbury.seng302.identityprovider.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class UserTests {

    User user1;

    @BeforeEach
    private void setup() {
        ZoneId timeZone = ZoneId.systemDefault();
        LocalDate localDate = LocalDate.now(timeZone);
        user1 = new User(1, "bauerjac", "Jack", "Brown", "Bauer", "Jack-Jack", "howdy", "HE/HIM", "jack@gmail.com", "password", localDate);
    }

    @Test
    void testToString(){
        assertEquals("User[userId=1, username=bauerjac, firstName=Jack, middleName=Brown, lastName=Bauer, nickname=Jack-Jack, bio=howdy, preferredPronouns=HE/HIM, email=jack@gmail.com]", user1.toString());
    }

    @Test
    void testUserIdMethod(){
        assertEquals(1, user1.getUserId());
    }

    @Test
    void testUsernameMethods() {
        assertEquals("bauerjac", user1.getUsername());
        //not setter for username
    }

    @Test
    void testFirstNameMethods() {
        assertEquals("Jack", user1.getFirstName());
        user1.setFirstName("Jacko");
        assertEquals("Jacko", user1.getFirstName());
    }

    @Test
    void testMiddleNameMethods() {
        assertEquals("Brown", user1.getMiddleName());
        user1.setMiddleName("Browno");
        assertEquals("Browno", user1.getMiddleName());
    }

    @Test
    void testLastNameMethods() {
        assertEquals("Bauer", user1.getLastName());
        user1.setLastName("Bauero");
        assertEquals("Bauero", user1.getLastName());
    }

    @Test
    void testNicknameMethods() {
        assertEquals("Jack-Jack", user1.getNickname());
        user1.setNickname("Jacko-Jacko");
        assertEquals("Jacko-Jacko", user1.getNickname());
    }

    @Test
    void testBioMethods() {
        assertEquals("howdy", user1.getBio());
        user1.setBio("howdyo");
        assertEquals("howdyo", user1.getBio());
    }

    @Test
    void testPronounMethods() {
        assertEquals("HE/HIM", user1.getPersonalPronouns());
        user1.setPersonalPronouns("SHE/HER");
        assertEquals("SHE/HER", user1.getPersonalPronouns());
    }

    @Test
    void testEmailMethods() {
        assertEquals("jack@gmail.com", user1.getEmail());
        user1.setEmail("jacko@gmail.com");
        assertEquals("jacko@gmail.com", user1.getEmail());
    }

    @Test
    void testEncryptOccurs(){
        assertNotEquals(user1.getPassword(), "password");
    }

    @Test
    void testCorrectPassword() {
        assertTrue(user1.checkPassword("password"));
    }

    @Test
    void testIncorrectPassword() {
        assertFalse(user1.checkPassword("theWrongPassword"));
    }


}