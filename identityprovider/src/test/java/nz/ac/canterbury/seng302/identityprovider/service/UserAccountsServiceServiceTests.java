package nz.ac.canterbury.seng302.identityprovider.service;

import com.google.protobuf.Timestamp;
import nz.ac.canterbury.seng302.identityprovider.entity.User;
import nz.ac.canterbury.seng302.identityprovider.repository.UserRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserAccountsServiceServiceTests {

    @Autowired
    private UserRepository repository;

    @Autowired
    private UserAccountsServerService userService;

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
    private Timestamp testCreated;

    @BeforeEach
    public void setup() {
        repository.deleteAll();
        User testUser = repository.save(new User(testUsername, testFirstName, testMiddleName, testLastName, testNickname, testBio, testPronouns, testEmail, testPassword));
        testId = testUser.getUserId();
        testCreated = testUser.getTimeCreated();
    }

    @Test
    void changePasswordBadUserTest() {
        ChangePasswordRequest changePasswordRequest = ChangePasswordRequest.newBuilder()
                .setUserId(-1)
                .setCurrentPassword("Not a password")
                .setNewPassword("Also not a password")
                .build();

        ChangePasswordResponse response = userService.changeUserPasswordHandler(changePasswordRequest);
        assertEquals("Password change failed: user does not exist", response.getMessage());
        assertFalse(response.getIsSuccess());
        assertTrue(repository.findByUserId(testId).checkPassword(testPassword));
    }

    @Test
    void changePasswordBadPasswordTest() {
        ChangePasswordRequest changePasswordRequest = ChangePasswordRequest.newBuilder()
                .setUserId(testId)
                .setCurrentPassword("Not a password")
                .setNewPassword("Also not a password")
                .build();

        ChangePasswordResponse response = userService.changeUserPasswordHandler(changePasswordRequest);
        assertEquals("Password change failed: current password is incorrect", response.getMessage());
        assertFalse(response.getIsSuccess());
        assertTrue(repository.findByUserId(testId).checkPassword(testPassword));
    }

    @Test
    void changePasswordGoodPasswordTest() {
        final String newPassword = "new password";
        ChangePasswordRequest changePasswordRequest = ChangePasswordRequest.newBuilder()
                .setUserId(testId)
                .setCurrentPassword(testPassword)
                .setNewPassword(newPassword)
                .build();
        ChangePasswordResponse response = userService.changeUserPasswordHandler(changePasswordRequest);
        assertEquals("Successfully changed password", response.getMessage());
        assertTrue(response.getIsSuccess());
        assertTrue(repository.findByUserId(testId).checkPassword(newPassword));
    }

    @Test
    void editUserBadUserTest() {
        EditUserRequest editUserRequest = EditUserRequest.newBuilder()
                .setUserId(-1)
                .setFirstName(testFirstName + "new")
                .setMiddleName(testMiddleName + "new")
                .setLastName(testLastName + "new")
                .setNickname(testNickname + "new")
                .setBio(testBio + "new")
                .setPersonalPronouns(testPronouns + "new")
                .setEmail(testEmail + "new")
                .build();
        EditUserResponse response = userService.editUserHandler(editUserRequest);
        assertEquals("Edit user failed: user does not exist", response.getMessage());
        assertFalse(response.getIsSuccess());

        User testUser = repository.findByUserId(testId);
        assertEquals(testUsername, testUser.getUsername());
        assertEquals(testFirstName, testUser.getFirstName());
        assertEquals(testMiddleName, testUser.getMiddleName());
        assertEquals(testLastName, testUser.getLastName());
        assertEquals(testNickname, testUser.getNickname());
        assertEquals(testBio, testUser.getBio());
        assertEquals(testPronouns, testUser.getPersonalPronouns());
        assertEquals(testEmail, testUser.getEmail());
    }

    @Test
    void editUserGoodUserTest() {
        EditUserRequest editUserRequest = EditUserRequest.newBuilder()
                .setUserId(testId)
                .setFirstName(testFirstName + "new")
                .setMiddleName(testMiddleName + "new")
                .setLastName(testLastName + "new")
                .setNickname(testNickname + "new")
                .setBio(testBio + "new")
                .setPersonalPronouns(testPronouns + "new")
                .setEmail(testEmail + "new")
                .build();
        EditUserResponse response = userService.editUserHandler(editUserRequest);
        assertEquals("Edit user succeeded", response.getMessage());
        assertTrue(response.getIsSuccess());

        User testUser = repository.findByUserId(testId);
        assertEquals(testUsername, testUser.getUsername());
        assertEquals(testFirstName + "new", testUser.getFirstName());
        assertEquals(testMiddleName + "new", testUser.getMiddleName());
        assertEquals(testLastName + "new", testUser.getLastName());
        assertEquals(testNickname + "new", testUser.getNickname());
        assertEquals(testBio + "new", testUser.getBio());
        assertEquals(testPronouns + "new", testUser.getPersonalPronouns());
        assertEquals(testEmail + "new", testUser.getEmail());
    }

    @Test
    void getUserByIdBadUserTest() {
        GetUserByIdRequest getUserByIdRequest = GetUserByIdRequest.newBuilder()
                .setId(-1)
                .build();
        UserResponse reply = userService.getUserAccountByIdHandler(getUserByIdRequest);
        assertEquals("", reply.getUsername());
        assertEquals("", reply.getFirstName());
        assertEquals("", reply.getMiddleName());
        assertEquals("", reply.getLastName());
        assertEquals("", reply.getNickname());
        assertEquals("", reply.getBio());
        assertEquals("", reply.getPersonalPronouns());
        assertEquals("", reply.getEmail());
        assertEquals(Timestamp.newBuilder().build(), reply.getCreated());
    }

    @Test
    void getUserByIdGoodUserTest() {
        GetUserByIdRequest getUserByIdRequest = GetUserByIdRequest.newBuilder()
                .setId(testId)
                .build();
        UserResponse reply = userService.getUserAccountByIdHandler(getUserByIdRequest);
        assertEquals(testUsername, reply.getUsername());
        assertEquals(testFirstName, reply.getFirstName());
        assertEquals(testMiddleName, reply.getMiddleName());
        assertEquals(testLastName, reply.getLastName());
        assertEquals(testNickname, reply.getNickname());
        assertEquals(testBio, reply.getBio());
        assertEquals(testPronouns, reply.getPersonalPronouns());
        assertEquals(testEmail, reply.getEmail());
        assertEquals(testCreated, reply.getCreated());
    }

    @Test
    void userRegisterBadUsernameTest() {
        UserRegisterRequest userRegisterRequest = UserRegisterRequest.newBuilder()
                .setUsername(testUsername)
                .setPassword(testPassword + "2")
                .setFirstName(testFirstName + "2")
                .setMiddleName(testMiddleName + "2")
                .setLastName(testLastName + "2")
                .setNickname(testNickname + "2")
                .setBio(testBio + "2")
                .setPersonalPronouns(testPronouns + "2")
                .setEmail(testEmail + "2")
                .build();
        UserRegisterResponse reply = userService.registerHandler(userRegisterRequest);
        assertEquals("Register attempt failed: Username already taken", reply.getMessage());
        assertFalse(reply.getIsSuccess());
    }

    @Test
    void userRegisterGoodUsernameTest() {
        UserRegisterRequest userRegisterRequest = UserRegisterRequest.newBuilder()
                .setUsername(testUsername + "2")
                .setPassword(testPassword + "2")
                .setFirstName(testFirstName + "2")
                .setMiddleName(testMiddleName + "2")
                .setLastName(testLastName + "2")
                .setNickname(testNickname + "2")
                .setBio(testBio + "2")
                .setPersonalPronouns(testPronouns + "2")
                .setEmail(testEmail + "2")
                .build();
        UserRegisterResponse reply = userService.registerHandler(userRegisterRequest);
        assertEquals("Register attempt succeeded", reply.getMessage());
        assertTrue(reply.getIsSuccess());
        int newTestId = reply.getNewUserId();
        User testUser = repository.findByUserId(newTestId);
        assertEquals(testUsername + "2", testUser.getUsername());
        assertEquals(testFirstName + "2", testUser.getFirstName());
        assertEquals(testMiddleName + "2", testUser.getMiddleName());
        assertEquals(testLastName + "2", testUser.getLastName());
        assertEquals(testNickname + "2", testUser.getNickname());
        assertEquals(testBio + "2", testUser.getBio());
        assertEquals(testPronouns + "2", testUser.getPersonalPronouns());
        assertEquals(testEmail + "2", testUser.getEmail());
        //as the password is stored encrypted, it needs to be checked differently
        assertTrue(testUser.checkPassword(testPassword + "2"));
    }

}
