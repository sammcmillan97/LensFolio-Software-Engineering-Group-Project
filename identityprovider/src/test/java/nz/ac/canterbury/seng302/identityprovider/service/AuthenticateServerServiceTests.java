package nz.ac.canterbury.seng302.identityprovider.service;

import nz.ac.canterbury.seng302.identityprovider.entity.User;
import nz.ac.canterbury.seng302.identityprovider.repository.UserRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthenticateRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthenticateResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AuthenticateServerServiceTests {

    @Autowired
    private UserRepository repository;

    @Autowired
    private AuthenticateServerService authService;

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

    //Tests that logging in fails if the username does not exist
    @Test
    void testBadUsername(){
        AuthenticateRequest authRequest = AuthenticateRequest.newBuilder()
                .setUsername("not a username")
                .setPassword(testPassword)
                .build();
        AuthenticateResponse response = authService.authenticateHandler(authRequest);
        System.out.println(response.getMessage());
        assertEquals("Log in attempt failed: username not registered", response.getMessage());
        assertEquals("", response.getToken());
        assertFalse(response.getSuccess());
    }

    //Tests that logging in fails if the password is incorrect
    @Test
    void testBadPassword(){
        AuthenticateRequest authRequest = AuthenticateRequest.newBuilder()
                .setUsername(testUsername)
                .setPassword("not a password")
                .build();
        AuthenticateResponse response = authService.authenticateHandler(authRequest);
        System.out.println(response.getMessage());
        assertEquals("Log in attempt failed: password incorrect", response.getMessage());
        assertEquals("", response.getToken());
        assertFalse(response.getSuccess());
    }

    //Tests that logging in succeeds if the password and username are correct
    @Test
    void testGoodLogin(){
        AuthenticateRequest authRequest = AuthenticateRequest.newBuilder()
                .setUsername(testUsername)
                .setPassword(testPassword)
                .build();
        AuthenticateResponse response = authService.authenticateHandler(authRequest);
        System.out.println(response.getMessage());
        assertEquals("Logged in successfully!", response.getMessage());
        assertEquals(testFirstName, response.getFirstName());
        assertEquals(testLastName, response.getLastName());
        assertEquals(testUsername, response.getUsername());
        assertEquals(response.getUserId(), testId);
        assertEquals(testEmail, response.getEmail());
        assertTrue(response.getSuccess());
    }

}


