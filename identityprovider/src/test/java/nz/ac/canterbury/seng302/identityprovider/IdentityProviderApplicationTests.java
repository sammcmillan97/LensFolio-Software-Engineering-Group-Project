package nz.ac.canterbury.seng302.identityprovider;

import nz.ac.canterbury.seng302.identityprovider.entity.User;
import nz.ac.canterbury.seng302.identityprovider.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Objects;

@SpringBootTest
class IdentityProviderApplicationTests {

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

    @Test
    public void testGetById() {
        assert Objects.equals(testUsername, repository.findByUserId(testId).getUsername());
    }

    @Test
    public void testGetByUsername() {
        assert Objects.equals(testUsername, repository.findByUsername(testUsername).getUsername());
    }

    @Test
    public void testAllFieldsInPlace() {
        User testUser = repository.findByUserId(testId);
        assert Objects.equals(testUser.getUsername(), testUsername);
        assert Objects.equals(testUser.getFirstName(), testFirstName);
        assert Objects.equals(testUser.getMiddleName(), testMiddleName);
        assert Objects.equals(testUser.getLastName(), testLastName);
        assert Objects.equals(testUser.getNickname(), testNickname);
        assert Objects.equals(testUser.getBio(), testBio);
        assert Objects.equals(testUser.getPersonalPronouns(), testPronouns);
        assert Objects.equals(testUser.getEmail(), testEmail);
        assert Objects.equals(testUser.getPassword(), testPassword);
    }

}
