package nz.ac.canterbury.seng302.identityprovider;

import nz.ac.canterbury.seng302.identityprovider.entity.User;
import nz.ac.canterbury.seng302.identityprovider.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class IdentityproviderApplicationTests {

    @Autowired
    private UserRepository repository;

    private final String testUsername = "test user";
    private final String testFirstName = "test fname";
    private final String testMiddleName = "test mname";
    private final String testLastName = "test lname";
    private final String testNickname = "test nname";
    private final String testBio = "test bio";
    private final String testPronouns = "test/tester";
    private final String testEmail = "test@email.com";
    private final String testPassword = "test password";

    private int testId;

    @BeforeEach
    public void setup() {
        repository.deleteAll();
        User testUser = repository.save(new User(testUsername, testFirstName, testMiddleName, testLastName, testNickname, testBio, testPronouns, testEmail, testPassword));
        testId = testUser.getUserId();
    }

    //Tests that the findByUserId function works
    @Test
    void testGetById() {
        assertEquals(testUsername, repository.findByUserId(testId).getUsername());
    }

    //Tests that the findByUsername function works
    @Test
    void testGetByUsername() {
        assertEquals(testUsername, repository.findByUsername(testUsername).getUsername());
    }

    //Tests that when the user is created, all fields are put in the right locations
    @Test
    void testAllFieldsInPlace() {
        User testUser = repository.findByUserId(testId);
        assertEquals(testUsername, testUser.getUsername());
        assertEquals(testFirstName, testUser.getFirstName());
        assertEquals(testMiddleName, testUser.getMiddleName());
        assertEquals(testLastName, testUser.getLastName());
        assertEquals(testNickname, testUser.getNickname());
        assertEquals(testBio, testUser.getBio());
        assertEquals(testPronouns, testUser.getPersonalPronouns());
        assertEquals(testEmail, testUser.getEmail());
        //as the password is stored encrypted, it needs to be checked differently
        assertTrue(testUser.checkPassword(testPassword));
    }

}
