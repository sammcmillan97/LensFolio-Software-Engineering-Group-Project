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

    //Tests that password change fails if the user does not exist
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

    //Tests that password change fails if the password is incorrect
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

    //Tests that password change succeeds if password is correct
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

    // Tests that editing user fails if user does not exist
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

    // Tests that editing user succeeds if user exists
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

    // Tests that getting user fails if user does not exist
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

    // Tests that getting user succeeds if user exists
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

    // Tests that creating user fails if no fields are entered
    @Test
    void userRegisterNoFieldsTest() {
        UserRegisterRequest userRegisterRequest = UserRegisterRequest.newBuilder().build();
        UserRegisterResponse reply = userService.registerHandler(userRegisterRequest);
        assertEquals("Register attempt failed: Validation failed", reply.getMessage());
        assertEquals(5, reply.getValidationErrorsCount());
        assertEquals("username", reply.getValidationErrors(0).getFieldName());
        assertEquals("Username is required", reply.getValidationErrors(0).getErrorText());
        assertEquals("firstName", reply.getValidationErrors(1).getFieldName());
        assertEquals("First name is required", reply.getValidationErrors(1).getErrorText());
        assertEquals("lastName", reply.getValidationErrors(2).getFieldName());
        assertEquals("Last name is required", reply.getValidationErrors(2).getErrorText());
        assertEquals("email", reply.getValidationErrors(3).getFieldName());
        assertEquals("Email is required", reply.getValidationErrors(3).getErrorText());
        assertEquals("password", reply.getValidationErrors(4).getFieldName());
        assertEquals("Password must be at least 8 characters", reply.getValidationErrors(4).getErrorText());
        assertFalse(reply.getIsSuccess());
    }

    // Tests the max length for each field
    @Test
    void userRegisterLongFieldsTest() {
        UserRegisterRequest userRegisterRequest = UserRegisterRequest.newBuilder()
                .setUsername("a".repeat(64))
                .setPassword("a".repeat(64))
                .setFirstName("a".repeat(64))
                .setMiddleName("a".repeat(64))
                .setLastName("a".repeat(64))
                .setNickname("a".repeat(64))
                .setBio("a".repeat(1024))
                .setPersonalPronouns("a".repeat(64))
                .setEmail("@".repeat(255))
                .build();
        UserRegisterResponse reply = userService.registerHandler(userRegisterRequest);
        assertEquals("Register attempt succeeded", reply.getMessage());
        assertTrue(reply.getIsSuccess());
    }

    // Tests that if fields are too long, the request is rejected
    @Test
    void userRegisterExtraLongFieldsTest() {
        UserRegisterRequest userRegisterRequest = UserRegisterRequest.newBuilder()
                .setUsername("a".repeat(65))
                .setPassword("a".repeat(65))
                .setFirstName("a".repeat(65))
                .setMiddleName("a".repeat(65))
                .setLastName("a".repeat(65))
                .setNickname("a".repeat(65))
                .setBio("a".repeat(1025))
                .setPersonalPronouns("a".repeat(65))
                .setEmail("@".repeat(256))
                .build();
        UserRegisterResponse reply = userService.registerHandler(userRegisterRequest);
        assertEquals("Register attempt failed: Validation failed", reply.getMessage());
        assertEquals(9, reply.getValidationErrorsCount());
        assertEquals("username", reply.getValidationErrors(0).getFieldName());
        assertEquals("Username must be less than 65 characters", reply.getValidationErrors(0).getErrorText());
        assertEquals("firstName", reply.getValidationErrors(1).getFieldName());
        assertEquals("First name must be less than 65 characters", reply.getValidationErrors(1).getErrorText());
        assertEquals("middleName", reply.getValidationErrors(2).getFieldName());
        assertEquals("Middle name must be less than 65 characters", reply.getValidationErrors(2).getErrorText());
        assertEquals("lastName", reply.getValidationErrors(3).getFieldName());
        assertEquals("Last name must be less than 65 characters", reply.getValidationErrors(3).getErrorText());
        assertEquals("nickname", reply.getValidationErrors(4).getFieldName());
        assertEquals("Nickname must be less than 65 characters", reply.getValidationErrors(4).getErrorText());
        assertEquals("bio", reply.getValidationErrors(5).getFieldName());
        assertEquals("Bio must be less than 1025 characters", reply.getValidationErrors(5).getErrorText());
        assertEquals("personalPronouns", reply.getValidationErrors(6).getFieldName());
        assertEquals("Personal pronouns must be less than 65 characters", reply.getValidationErrors(6).getErrorText());
        assertEquals("email", reply.getValidationErrors(7).getFieldName());
        assertEquals("Email must be less than 256 characters", reply.getValidationErrors(7).getErrorText());
        assertEquals("password", reply.getValidationErrors(8).getFieldName());
        assertEquals("Password must be less than 65 characters", reply.getValidationErrors(8).getErrorText());
        assertFalse(reply.getIsSuccess());
    }

    // Tests that creating user fails if username already exists
    @Test
    void userRegisterRepeatedUsernameTest() {
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
        assertEquals("Register attempt failed: Validation failed", reply.getMessage());
        assertEquals(1, reply.getValidationErrorsCount());
        assertEquals("username", reply.getValidationErrors(0).getFieldName());
        assertEquals("Username already taken", reply.getValidationErrors(0).getErrorText());
        assertFalse(reply.getIsSuccess());
    }

    // Tests that an email that does not contain @ is rejected
    @Test
    void userRegisterBadEmailTest() {
        UserRegisterRequest userRegisterRequest = UserRegisterRequest.newBuilder()
                .setUsername(testUsername + "2")
                .setPassword(testPassword + "2")
                .setFirstName(testFirstName + "2")
                .setMiddleName(testMiddleName + "2")
                .setLastName(testLastName + "2")
                .setNickname(testNickname + "2")
                .setBio(testBio + "2")
                .setPersonalPronouns(testPronouns + "2")
                .setEmail("bad email")
                .build();
        UserRegisterResponse reply = userService.registerHandler(userRegisterRequest);
        assertEquals("Register attempt failed: Validation failed", reply.getMessage());
        assertEquals(1, reply.getValidationErrorsCount());
        assertEquals("email", reply.getValidationErrors(0).getFieldName());
        assertEquals("Email must be valid", reply.getValidationErrors(0).getErrorText());
        assertFalse(reply.getIsSuccess());
    }

    // Tests that a password less than 8 characters is rejected
    @Test
    void userRegisterBadPasswordTest() {
        UserRegisterRequest userRegisterRequest = UserRegisterRequest.newBuilder()
                .setUsername(testUsername + "2")
                .setPassword(":seven:")
                .setFirstName(testFirstName + "2")
                .setMiddleName(testMiddleName + "2")
                .setLastName(testLastName + "2")
                .setNickname(testNickname + "2")
                .setBio(testBio + "2")
                .setPersonalPronouns(testPronouns + "2")
                .setEmail(testEmail + "2")
                .build();
        UserRegisterResponse reply = userService.registerHandler(userRegisterRequest);
        assertEquals("Register attempt failed: Validation failed", reply.getMessage());
        assertEquals(1, reply.getValidationErrorsCount());
        assertEquals("password", reply.getValidationErrors(0).getFieldName());
        assertEquals("Password must be at least 8 characters", reply.getValidationErrors(0).getErrorText());
        assertFalse(reply.getIsSuccess());
    }

    // Tests that creating user succeeds if username does not exist
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
