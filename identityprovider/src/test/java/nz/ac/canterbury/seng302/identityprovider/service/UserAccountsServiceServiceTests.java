package nz.ac.canterbury.seng302.identityprovider.service;

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

    @BeforeEach
    public void setup() {
        repository.deleteAll();
        User testUser = repository.save(new User(testUsername, testFirstName, testMiddleName, testLastName, testNickname, testBio, testPronouns, testEmail, testPassword));
        testId = testUser.getUserId();
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

    /*
    public EditUserResponse editUser(final int userId, final String firstName, final String middleName,
                                     final String lastName, final String nickname, final String bio,
                                     final String personalPronouns, final String email)  {
        EditUserRequest editUserRequest = EditUserRequest.newBuilder()
                .setUserId(userId)
                .setFirstName(firstName)
                .setMiddleName(middleName)
                .setLastName(lastName)
                .setNickname(nickname)
                .setBio(bio)
                .setPersonalPronouns(personalPronouns)
                .setEmail(email)
                .build();
        return userStub.editUser(editUserRequest);
    }

    public UserResponse getUserAccountById(final int userId)  {
        GetUserByIdRequest getUserByIdRequest = GetUserByIdRequest.newBuilder()
                .setId(userId)
                .build();
        return userStub.getUserAccountById(getUserByIdRequest);
    }

    public UserRegisterResponse register(final String username, final String password, final String firstName,
                                         final String middleName, final String lastName, final String nickname,
                                         final String bio, final String personalPronouns, final String email)  {
        UserRegisterRequest userRegisterRequest = UserRegisterRequest.newBuilder()
                .setUsername(username)
                .setPassword(password)
                .setFirstName(firstName)
                .setMiddleName(middleName)
                .setLastName(lastName)
                .setNickname(nickname)
                .setBio(bio)
                .setPersonalPronouns(personalPronouns)
                .setEmail(email)
                .build();
        return userStub.register(userRegisterRequest);
    }
    */

}
