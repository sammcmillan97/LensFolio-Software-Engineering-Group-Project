package nz.ac.canterbury.seng302.identityprovider.service;

import com.google.protobuf.Timestamp;
import nz.ac.canterbury.seng302.identityprovider.entity.User;
import nz.ac.canterbury.seng302.identityprovider.repository.UserRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import nz.ac.canterbury.seng302.shared.util.ValidationError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static nz.ac.canterbury.seng302.shared.identityprovider.UserRole.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@SpringBootTest
class UserAccountsServerServiceTests {

    @Autowired
    private UserRepository repository;

    @Spy
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
    private final UserRole studentRole = STUDENT;
    private final UserRole teacherRole = TEACHER;
    private final UserRole adminRole = COURSE_ADMINISTRATOR;

    private int testId;
    private Timestamp testCreated;

    @BeforeEach
    public void setup() {
        repository.deleteAll();
        User testUser = repository.save(new User(testUsername, testFirstName, testMiddleName, testLastName, testNickname, testBio, testPronouns, testEmail, testPassword));
        testId = testUser.getUserId();
        testCreated = testUser.getTimeCreated();
    }

    //Method for adding users to be used in the 'getPaginatedUser' tests
    public void addUsers() {
        repository.deleteAll();
        User testUser1 = new User("test1", "Adam", "", "Adam", "A", "", "test/tester", "Test@emai.com", "password");
        User testUser2 = new User("test2", "Bruce", "Bruce", "Bruce", "B", "", "test/tester", "Test@email.com", "password");
        User testUser3 = new User("test3", "Adam", "", "Adama", "3", "", "test/tester", "Test@email.com", "password");
        testUser1.addRole(UserRole.COURSE_ADMINISTRATOR);
        testUser2.addRole(UserRole.TEACHER);
        testUser2.addRole(UserRole.STUDENT);
        testUser3.addRole(UserRole.STUDENT);
        repository.save(testUser1);
        repository.save(testUser2);
        repository.save(testUser3);
    }

    //Tests that sort by name works correctly on the get paginated users service
    @Test
    void getPaginatedUsersSortByName(){
        addUsers();
        GetPaginatedUsersRequest getPaginatedUsersRequest = GetPaginatedUsersRequest.newBuilder()
                .setOffset(0)
                .setLimit(9999)
                .setOrderBy("name")
                .setIsAscendingOrder(true)
                .build();
        PaginatedUsersResponse response = userService.getPaginatedUsersHandler(getPaginatedUsersRequest);
        assertEquals("Adam", response.getUsersList().get(0).getLastName());
        assertEquals("Adam", response.getUsersList().get(0).getFirstName());
        assertEquals("Bruce", response.getUsersList().get(response.getUsersList().size() - 1).getFirstName());
    }

    //Tests that sort by username works correctly on the get paginated users service
    @Test
    void getPaginatedUsersSortByUsername(){
        addUsers();
        GetPaginatedUsersRequest getPaginatedUsersRequest = GetPaginatedUsersRequest.newBuilder()
                .setOffset(0)
                .setLimit(9999)
                .setOrderBy("username")
                .setIsAscendingOrder(true)
                .build();
        PaginatedUsersResponse response = userService.getPaginatedUsersHandler(getPaginatedUsersRequest);
        assertEquals("test1", response.getUsersList().get(0).getUsername());
        assertEquals("test3", response.getUsersList().get(response.getUsersList().size() - 1).getUsername());
    }

    //Tests that sort by alias works correctly on the get paginated users service
    @Test
    void getPaginatedUsersSortByAlias() {
        addUsers();
        GetPaginatedUsersRequest getPaginatedUsersRequest = GetPaginatedUsersRequest.newBuilder()
                .setOffset(0)
                .setLimit(9999)
                .setOrderBy("alias")
                .setIsAscendingOrder(true)
                .build();
        PaginatedUsersResponse response = userService.getPaginatedUsersHandler(getPaginatedUsersRequest);
        assertEquals("3", response.getUsersList().get(0).getNickname());
        assertEquals("B", response.getUsersList().get(response.getUsersList().size() - 1).getNickname());
    }

    //Tests that sort by role works correctly on the get paginated users service
    @Test
    void getPaginatedUsersSortByRoles() {
        addUsers();
        GetPaginatedUsersRequest getPaginatedUsersRequest = GetPaginatedUsersRequest.newBuilder()
                .setOffset(0)
                .setLimit(9999)
                .setOrderBy("roles")
                .setIsAscendingOrder(true)
                .build();
        PaginatedUsersResponse response = userService.getPaginatedUsersHandler(getPaginatedUsersRequest);
        assertEquals("test1", response.getUsersList().get(0).getUsername());
        assertEquals("test3", response.getUsersList().get(response.getUsersList().size() - 1).getUsername());
    }

    //Tests that sort in descending order works on the get paginated users service
    @Test
    void getPaginatedUsersSortByUsernameDescending() {
        addUsers();
        GetPaginatedUsersRequest getPaginatedUsersRequest = GetPaginatedUsersRequest.newBuilder()
                .setOffset(0)
                .setLimit(9999)
                .setOrderBy("username")
                .setIsAscendingOrder(false)
                .build();
        PaginatedUsersResponse response = userService.getPaginatedUsersHandler(getPaginatedUsersRequest);
        assertEquals("test3", response.getUsersList().get(0).getUsername());
        assertEquals("test1", response.getUsersList().get(response.getUsersList().size() - 1).getUsername());
    }

    // Provides arguments for the parameterized tests for paginated users
    static Stream<Arguments> paginatedUsersTestParamProvider() {
        return Stream.of(
                // All tests have a list of 3 users
                arguments(1, 9999, 2), // Tests that the offset of 1 returns a list of 2 users
                arguments(0, 0, 0), // Tests that a limit of 0 returns 0 users
                arguments(0, 1, 1), // Tests that a limit of 1 returns 1 user
                arguments(3, 9999, 0) // Tests that an offset higher than the number of users returns 0 users
        );
    }

    // Tests that the offset and limit options for pagination work as expected. See above method for test cases
    @ParameterizedTest
    @MethodSource("paginatedUsersTestParamProvider")
    void getPaginatedUsers(int offset, int limit, int expectedUserListSize) {
        addUsers();
        GetPaginatedUsersRequest getPaginatedUsersRequest = GetPaginatedUsersRequest.newBuilder()
                .setOffset(offset)
                .setLimit(limit)
                .setOrderBy("username")
                .setIsAscendingOrder(true)
                .build();
        PaginatedUsersResponse response = userService.getPaginatedUsersHandler(getPaginatedUsersRequest);
        assertEquals(expectedUserListSize, response.getUsersList().size());
    }

    //Tests that the password change fails if the new password is too short
    @Test
    void changePasswordEmptyPasswordTest() {
        ChangePasswordRequest changePasswordRequest = ChangePasswordRequest.newBuilder()
                .setUserId(testId)
                .setCurrentPassword(testPassword)
                .build();
        ChangePasswordResponse response = userService.changeUserPasswordHandler(changePasswordRequest);
        assertEquals("Password change failed: Validation failed", response.getMessage());
        assertEquals("password", response.getValidationErrors(0).getFieldName());
        assertEquals("Password must be at least 8 characters", response.getValidationErrors(0).getErrorText());
        assertFalse(response.getIsSuccess());
        assertTrue(repository.findByUserId(testId).checkPassword(testPassword));
    }

    //Test the max length for the new password field
    @Test
    void changePasswordLongFieldTest() {
        ChangePasswordRequest changePasswordRequest = ChangePasswordRequest.newBuilder()
                .setUserId(testId)
                .setCurrentPassword(testPassword)
                .setNewPassword("a".repeat(64))
                .build();
        ChangePasswordResponse response = userService.changeUserPasswordHandler(changePasswordRequest);
        assertTrue(response.getIsSuccess());
        assertTrue(repository.findByUserId(testId).checkPassword("a".repeat(64)));
    }

    //Tests that the password change fails if the new password is too long
    @Test
    void changePasswordExtraLongPasswordTest() {
        ChangePasswordRequest changePasswordRequest = ChangePasswordRequest.newBuilder()
                .setUserId(testId)
                .setCurrentPassword(testPassword)
                .setNewPassword("a".repeat(65))
                .build();
        ChangePasswordResponse response = userService.changeUserPasswordHandler(changePasswordRequest);
        assertEquals("Password change failed: Validation failed", response.getMessage());
        assertEquals("password", response.getValidationErrors(0).getFieldName());
        assertEquals("Password must be less than 65 characters", response.getValidationErrors(0).getErrorText());
        assertFalse(response.getIsSuccess());
        assertTrue(repository.findByUserId(testId).checkPassword(testPassword));
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
        assertEquals("Password change failed: Validation failed", response.getMessage());
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
        assertEquals("Password change failed: Validation failed", response.getMessage());
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

    // Tests that editing a user fails if no fields are entered
    @Test
    void editUserEmptyFieldsTest() {
        EditUserRequest editUserRequest = EditUserRequest.newBuilder()
                .setUserId(testId)
                .setFirstName("")
                .setMiddleName("")
                .setLastName("")
                .setNickname("")
                .setBio("")
                .setPersonalPronouns("")
                .setEmail("")
                .build();
        EditUserResponse response = userService.editUserHandler(editUserRequest);

        assertEquals(3, response.getValidationErrorsCount());
        assertEquals("firstName", response.getValidationErrors(0).getFieldName());
        assertEquals("First name is required", response.getValidationErrors(0).getErrorText());
        assertEquals("lastName", response.getValidationErrors(1).getFieldName());
        assertEquals("Last name is required", response.getValidationErrors(1).getErrorText());
        assertEquals("email", response.getValidationErrors(2).getFieldName());
        assertEquals("Email is required", response.getValidationErrors(2).getErrorText());
        assertEquals("Edit user failed: Validation failed", response.getMessage());

        assertFalse(response.getIsSuccess());

        // Check user hasn't been changed
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

    // Tests that editing a user fails if names contain special characters
    @Test
    void editUserBadNamesTest() {
        EditUserRequest editUserRequest = EditUserRequest.newBuilder()
                .setUserId(testId)
                .setFirstName("??")
                .setMiddleName("{{{##}}}")
                .setLastName("Ma8ter")
                .setNickname("")
                .setBio("")
                .setPersonalPronouns("")
                .setEmail("a@a.a")
                .build();
        EditUserResponse response = userService.editUserHandler(editUserRequest);

        assertEquals(3, response.getValidationErrorsCount());
        assertEquals("firstName", response.getValidationErrors(0).getFieldName());
        assertEquals("First name must not contain special characters", response.getValidationErrors(0).getErrorText());
        assertEquals("middleName", response.getValidationErrors(1).getFieldName());
        assertEquals("Middle name must not contain special characters", response.getValidationErrors(1).getErrorText());
        assertEquals("lastName", response.getValidationErrors(2).getFieldName());
        assertEquals("Last name must not contain special characters", response.getValidationErrors(2).getErrorText());
        assertEquals("Edit user failed: Validation failed", response.getMessage());

        assertFalse(response.getIsSuccess());

        // Check user hasn't been changed
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

    // Tests the max field length
    @Test
    void editUserLongFieldsTest() {
        EditUserRequest editUserRequest = EditUserRequest.newBuilder()
                .setUserId(testId)
                .setFirstName("a".repeat(64))
                .setMiddleName("a".repeat(64))
                .setLastName("a".repeat(64))
                .setNickname("a".repeat(64))
                .setBio("a".repeat(1024))
                .setPersonalPronouns("a/" + "a".repeat(62))
                .setEmail("a@a." + "a".repeat(251))
                .build();
        EditUserResponse response = userService.editUserHandler(editUserRequest);
        assertEquals("Edit user succeeded", response.getMessage());
        assertTrue(response.getIsSuccess());

        // Check that user info has been changed to correct values
        User testUser = repository.findByUserId(testId);
        assertEquals(testUsername, testUser.getUsername());
        assertEquals("a".repeat(64), testUser.getFirstName());
        assertEquals("a".repeat(64), testUser.getMiddleName());
        assertEquals("a".repeat(64), testUser.getLastName());
        assertEquals("a".repeat(64), testUser.getNickname());
        assertEquals("a".repeat(1024), testUser.getBio());
        assertEquals("a/" + "a".repeat(62), testUser.getPersonalPronouns());
        assertEquals("a@a." + "a".repeat(251), testUser.getEmail());
    }

    // Tests that if fields are too long, the request is rejected
    @Test
    void editUserExtraLongFieldsTest() {
        EditUserRequest editUserRequest = EditUserRequest.newBuilder()
                .setUserId(testId)
                .setFirstName("a".repeat(65))
                .setMiddleName("a".repeat(65))
                .setLastName("a".repeat(65))
                .setNickname("a".repeat(65))
                .setBio("a".repeat(1025))
                .setPersonalPronouns("a/" + "a".repeat(63))
                .setEmail("a@a." + "a".repeat(252))
                .build();
        EditUserResponse response = userService.editUserHandler(editUserRequest);
        assertEquals("Edit user failed: Validation failed", response.getMessage());
        assertEquals(7, response.getValidationErrorsCount());
        assertEquals("firstName", response.getValidationErrors(0).getFieldName());
        assertEquals("First name must be less than 65 characters", response.getValidationErrors(0).getErrorText());
        assertEquals("middleName", response.getValidationErrors(1).getFieldName());
        assertEquals("Middle name must be less than 65 characters", response.getValidationErrors(1).getErrorText());
        assertEquals("lastName", response.getValidationErrors(2).getFieldName());
        assertEquals("Last name must be less than 65 characters", response.getValidationErrors(2).getErrorText());
        assertEquals("nickname", response.getValidationErrors(3).getFieldName());
        assertEquals("Nickname must be less than 65 characters", response.getValidationErrors(3).getErrorText());
        assertEquals("bio", response.getValidationErrors(4).getFieldName());
        assertEquals("Bio must be less than 1025 characters", response.getValidationErrors(4).getErrorText());
        assertEquals("personalPronouns", response.getValidationErrors(5).getFieldName());
        assertEquals("Personal pronouns must be less than 65 characters", response.getValidationErrors(5).getErrorText());
        assertEquals("email", response.getValidationErrors(6).getFieldName());
        assertEquals("Email must be less than 256 characters", response.getValidationErrors(6).getErrorText());
        assertFalse(response.getIsSuccess());
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
        assertEquals("Edit user failed: Validation failed", response.getMessage());
        assertFalse(response.getIsSuccess());

        // Check user hasn't been changed
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

        // Check that user info has been changed to correct values
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
        UserResponse response = userService.getUserAccountByIdHandler(getUserByIdRequest);
        assertEquals("", response.getUsername());
        assertEquals("", response.getFirstName());
        assertEquals("", response.getMiddleName());
        assertEquals("", response.getLastName());
        assertEquals("", response.getNickname());
        assertEquals("", response.getBio());
        assertEquals("", response.getPersonalPronouns());
        assertEquals("", response.getEmail());
        assertEquals(Timestamp.newBuilder().build(), response.getCreated());
    }

    // Tests that getting user succeeds if user exists
    @Test
    void getUserByIdGoodUserTest() {
        GetUserByIdRequest getUserByIdRequest = GetUserByIdRequest.newBuilder()
                .setId(testId)
                .build();
        UserResponse response = userService.getUserAccountByIdHandler(getUserByIdRequest);
        assertEquals(testUsername, response.getUsername());
        assertEquals(testFirstName, response.getFirstName());
        assertEquals(testMiddleName, response.getMiddleName());
        assertEquals(testLastName, response.getLastName());
        assertEquals(testNickname, response.getNickname());
        assertEquals(testBio, response.getBio());
        assertEquals(testPronouns, response.getPersonalPronouns());
        assertEquals(testEmail, response.getEmail());
        assertEquals(testCreated, response.getCreated());
        //assertEquals("http://localhost:8080/resources/profile-images/default/default.jpg", response.getProfileImagePath());
        assertEquals(UserRole.STUDENT, response.getRoles(0));
        assertEquals(1, response.getRolesCount());
    }

    // Tests that getting roles succeeds when roles have been changed from their defaults
    @Test
    void getUserByIdRolesTest() {
        User testUser = repository.findByUserId(testId);
        testUser.addRole(UserRole.TEACHER);
        testUser.removeRole(UserRole.STUDENT);
        testUser.addRole(UserRole.COURSE_ADMINISTRATOR);
        repository.save(testUser);
        GetUserByIdRequest getUserByIdRequest = GetUserByIdRequest.newBuilder()
                .setId(testId)
                .build();
        UserResponse response = userService.getUserAccountByIdHandler(getUserByIdRequest);
        assertTrue(response.getRolesList().contains(UserRole.TEACHER));
        assertTrue(response.getRolesList().contains(UserRole.COURSE_ADMINISTRATOR));
        assertEquals(2, response.getRolesCount());
    }

    // Tests that getting profile picture path succeeds when it is changed from default
//    @Test
//    void getUserByIdProfileImagePathTest() {
//        String testPath = "test.png";
//        User testUser = repository.findByUserId(testId);
//        testUser.setProfileImagePath(testPath);
//        repository.save(testUser);
//        GetUserByIdRequest getUserByIdRequest = GetUserByIdRequest.newBuilder()
//                .setId(testId)
//                .build();
//        UserResponse response = userService.getUserAccountByIdHandler(getUserByIdRequest);
//        assertEquals("resources/" + testPath, response.getProfileImagePath());
//    }


    // Tests that creating user fails if no fields are entered
    @Test
    void userRegisterEmptyFieldsTest() {
        UserRegisterRequest userRegisterRequest = UserRegisterRequest.newBuilder().build();
        UserRegisterResponse response = userService.registerHandler(userRegisterRequest);
        assertEquals("Register attempt failed: Validation failed", response.getMessage());
        assertEquals(5, response.getValidationErrorsCount());
        assertEquals("username", response.getValidationErrors(0).getFieldName());
        assertEquals("Username is required", response.getValidationErrors(0).getErrorText());
        assertEquals("firstName", response.getValidationErrors(1).getFieldName());
        assertEquals("First name is required", response.getValidationErrors(1).getErrorText());
        assertEquals("lastName", response.getValidationErrors(2).getFieldName());
        assertEquals("Last name is required", response.getValidationErrors(2).getErrorText());
        assertEquals("email", response.getValidationErrors(3).getFieldName());
        assertEquals("Email is required", response.getValidationErrors(3).getErrorText());
        assertEquals("password", response.getValidationErrors(4).getFieldName());
        assertEquals("Password must be at least 8 characters", response.getValidationErrors(4).getErrorText());
        assertFalse(response.getIsSuccess());
    }

    // Tests that creating user fails if nonames contain special characters
    @Test
    void userRegisterBadNamesTest() {
        UserRegisterRequest userRegisterRequest = UserRegisterRequest.newBuilder()
                .setFirstName("I999")
                .setMiddleName("|||")
                .setLastName(";;;")
                .setEmail("a@a.a")
                .setPassword("password")
                .setUsername("test")
                .build();
        UserRegisterResponse response = userService.registerHandler(userRegisterRequest);
        assertEquals("Register attempt failed: Validation failed", response.getMessage());
        assertEquals(3, response.getValidationErrorsCount());
        assertEquals("firstName", response.getValidationErrors(0).getFieldName());
        assertEquals("First name must not contain special characters", response.getValidationErrors(0).getErrorText());
        assertEquals("middleName", response.getValidationErrors(1).getFieldName());
        assertEquals("Middle name must not contain special characters", response.getValidationErrors(1).getErrorText());
        assertEquals("lastName", response.getValidationErrors(2).getFieldName());
        assertEquals("Last name must not contain special characters", response.getValidationErrors(2).getErrorText());

        assertFalse(response.getIsSuccess());
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
                .setPersonalPronouns("a/" + "a".repeat(62))
                .setEmail("a@a." + "a".repeat(251))
                .build();
        UserRegisterResponse response = userService.registerHandler(userRegisterRequest);
        assertEquals("Register attempt succeeded", response.getMessage());
        assertTrue(response.getIsSuccess());
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
                .setPersonalPronouns("a/" + "a".repeat(63))
                .setEmail("a@a." + "a".repeat(252))
                .build();
        UserRegisterResponse response = userService.registerHandler(userRegisterRequest);
        assertEquals("Register attempt failed: Validation failed", response.getMessage());
        assertEquals(9, response.getValidationErrorsCount());
        assertEquals("username", response.getValidationErrors(0).getFieldName());
        assertEquals("Username must be less than 65 characters", response.getValidationErrors(0).getErrorText());
        assertEquals("firstName", response.getValidationErrors(1).getFieldName());
        assertEquals("First name must be less than 65 characters", response.getValidationErrors(1).getErrorText());
        assertEquals("middleName", response.getValidationErrors(2).getFieldName());
        assertEquals("Middle name must be less than 65 characters", response.getValidationErrors(2).getErrorText());
        assertEquals("lastName", response.getValidationErrors(3).getFieldName());
        assertEquals("Last name must be less than 65 characters", response.getValidationErrors(3).getErrorText());
        assertEquals("nickname", response.getValidationErrors(4).getFieldName());
        assertEquals("Nickname must be less than 65 characters", response.getValidationErrors(4).getErrorText());
        assertEquals("bio", response.getValidationErrors(5).getFieldName());
        assertEquals("Bio must be less than 1025 characters", response.getValidationErrors(5).getErrorText());
        assertEquals("personalPronouns", response.getValidationErrors(6).getFieldName());
        assertEquals("Personal pronouns must be less than 65 characters", response.getValidationErrors(6).getErrorText());
        assertEquals("email", response.getValidationErrors(7).getFieldName());
        assertEquals("Email must be less than 256 characters", response.getValidationErrors(7).getErrorText());
        assertEquals("password", response.getValidationErrors(8).getFieldName());
        assertEquals("Password must be less than 65 characters", response.getValidationErrors(8).getErrorText());
        assertFalse(response.getIsSuccess());
    }

    // Tests that creating user fails if username already exists
    @Test
    void userRegisterRepeatedUsernameTest() {
        UserRegisterRequest userRegisterRequest = UserRegisterRequest.newBuilder()
                .setUsername(testUsername)
                .setPassword(testPassword + "2")
                .setFirstName(testFirstName + "a")
                .setMiddleName(testMiddleName + "a")
                .setLastName(testLastName + "a")
                .setNickname(testNickname + "2")
                .setBio(testBio + "2")
                .setPersonalPronouns(testPronouns + "2")
                .setEmail(testEmail + "2")
                .build();
        UserRegisterResponse response = userService.registerHandler(userRegisterRequest);
        assertEquals("Register attempt failed: Validation failed", response.getMessage());
        assertEquals(1, response.getValidationErrorsCount());
        assertEquals("username", response.getValidationErrors(0).getFieldName());
        assertEquals("Username already taken", response.getValidationErrors(0).getErrorText());
        assertFalse(response.getIsSuccess());
    }

    // Tests that an email that does not contain @ and . is rejected
    @Test
    void userRegisterBadEmailTest() {
        UserRegisterRequest userRegisterRequest = UserRegisterRequest.newBuilder()
                .setUsername(testUsername + "2")
                .setPassword(testPassword + "2")
                .setFirstName(testFirstName + "a")
                .setMiddleName(testMiddleName + "a")
                .setLastName(testLastName + "a")
                .setNickname(testNickname + "2")
                .setBio(testBio + "2")
                .setPersonalPronouns(testPronouns + "2")
                .setEmail("bad@email")
                .build();
        UserRegisterResponse response = userService.registerHandler(userRegisterRequest);
        assertEquals("Register attempt failed: Validation failed", response.getMessage());
        List<ValidationError> errors = response.getValidationErrorsList();
        for(ValidationError error: errors) {
            System.out.println(error.getErrorText());
        }
        assertEquals(1, response.getValidationErrorsCount());
        assertEquals("email", response.getValidationErrors(0).getFieldName());
        assertEquals("Email must be of form a@b.c", response.getValidationErrors(0).getErrorText());
        assertFalse(response.getIsSuccess());
    }

    // Tests that a password less than 8 characters is rejected
    @Test
    void userRegisterBadPasswordTest() {
        UserRegisterRequest userRegisterRequest = UserRegisterRequest.newBuilder()
                .setUsername(testUsername + "2")
                .setPassword(":seven:")
                .setFirstName(testFirstName + "a")
                .setMiddleName(testMiddleName + "a")
                .setLastName(testLastName + "a")
                .setNickname(testNickname + "2")
                .setBio(testBio + "2")
                .setPersonalPronouns(testPronouns + "2")
                .setEmail(testEmail + "2")
                .build();
        UserRegisterResponse response = userService.registerHandler(userRegisterRequest);
        assertEquals("Register attempt failed: Validation failed", response.getMessage());
        assertEquals(1, response.getValidationErrorsCount());
        assertEquals("password", response.getValidationErrors(0).getFieldName());
        assertEquals("Password must be at least 8 characters", response.getValidationErrors(0).getErrorText());
        assertFalse(response.getIsSuccess());
    }

    // Tests that creating user succeeds if username does not exist
    @Test
    void userRegisterGoodUsernameTest() {
        UserRegisterRequest userRegisterRequest = UserRegisterRequest.newBuilder()
                .setUsername(testUsername + "2")
                .setPassword(testPassword + "2")
                .setFirstName(testFirstName + "a")
                .setMiddleName(testMiddleName + "a")
                .setLastName(testLastName + "a")
                .setNickname(testNickname + "2")
                .setBio(testBio + "2")
                .setPersonalPronouns(testPronouns + "2")
                .setEmail(testEmail + "2")
                .build();
        UserRegisterResponse response = userService.registerHandler(userRegisterRequest);
        assertEquals("Register attempt succeeded", response.getMessage());
        assertTrue(response.getIsSuccess());
        int newTestId = response.getNewUserId();
        User testUser = repository.findByUserId(newTestId);
        assertEquals(testUsername + "2", testUser.getUsername());
        assertEquals(testFirstName + "a", testUser.getFirstName());
        assertEquals(testMiddleName + "a", testUser.getMiddleName());
        assertEquals(testLastName + "a", testUser.getLastName());
        assertEquals(testNickname + "2", testUser.getNickname());
        assertEquals(testBio + "2", testUser.getBio());
        assertEquals(testPronouns + "2", testUser.getPersonalPronouns());
        assertEquals(testEmail + "2", testUser.getEmail());
        //as the password is stored as a hash, it needs to be checked differently
        assertTrue(testUser.checkPassword(testPassword + "2"));
    }

    //Tests that role can be added to a user
    @Test
    void addRoleToUserTest() {
        ModifyRoleOfUserRequest modifyRoleOfUserRequest = ModifyRoleOfUserRequest.newBuilder()
                .setUserId(testId)
                .setRole(teacherRole)
                .build();
        UserRoleChangeResponse response = userService.addRoleToUserHandler(modifyRoleOfUserRequest);
        User updatedUser = repository.findByUserId(testId);
        assertTrue(response.getIsSuccess());
        assertEquals("Role successfully added", response.getMessage());
        Set<UserRole> roleSet = new HashSet<>();
        roleSet.add(studentRole);
        roleSet.add(teacherRole);
        assertEquals(roleSet, updatedUser.getRoles());
    }

    //Tests that role can be added to a user
    @Test
    void addRoleToUserTwiceTest() {
        ModifyRoleOfUserRequest modifyRoleOfUserRequest = ModifyRoleOfUserRequest.newBuilder()
                .setUserId(testId)
                .setRole(studentRole)
                .build();
        UserRoleChangeResponse response = userService.addRoleToUserHandler(modifyRoleOfUserRequest);
        User updatedUser = repository.findByUserId(testId);
        assertFalse(response.getIsSuccess());
        assertEquals("Unable to add role. User already has given role", response.getMessage());
        Set<UserRole> roleSet = new HashSet<>();
        roleSet.add(studentRole);
        assertEquals(roleSet, updatedUser.getRoles());
    }

    //Tests that role can be removed from a user
    @Test
    void removeRoleFromUserTest() {
        repository.deleteAll();
        User userA = new User(testUsername, testFirstName, testMiddleName, testLastName, testNickname, testBio, testPronouns, testEmail, testPassword);
        userA.addRole(teacherRole);
        User testUser = repository.save(userA);
        testId = testUser.getUserId();
        ModifyRoleOfUserRequest modifyRoleOfUserRequest = ModifyRoleOfUserRequest.newBuilder()
                .setUserId(testId)
                .setRole(teacherRole)
                .build();

        UserAccountsServerService spyUserService = Mockito.spy(userService);
        Mockito.doReturn(1).when(spyUserService).getAuthStateUserId();
        UserRoleChangeResponse response = spyUserService.removeRoleFromUserHandler(modifyRoleOfUserRequest);
        User updatedUser = repository.findByUserId(testId);
        assertTrue(response.getIsSuccess());
        Set<UserRole> roleSet = new HashSet<>();
        roleSet.add(studentRole);
        assertEquals(roleSet, updatedUser.getRoles());
        assertEquals("Role successfully removed", response.getMessage());
    }

    //Tests that an admin can't remove their own admin role
    @Test
    void whenHasAdminRole_testRemoveOwnAdminRole() {
        repository.deleteAll();
        User userA = new User(testUsername, testFirstName, testMiddleName, testLastName, testNickname, testBio, testPronouns, testEmail, testPassword);
        userA.addRole(adminRole);
        User testUser = repository.save(userA);
        testId = testUser.getUserId();
        ModifyRoleOfUserRequest modifyRoleOfUserRequest = ModifyRoleOfUserRequest.newBuilder()
                .setUserId(testId)
                .setRole(adminRole)
                .build();

        UserAccountsServerService spyUserService = Mockito.spy(userService);
        Mockito.doReturn(testId).when(spyUserService).getAuthStateUserId();

        UserRoleChangeResponse response = spyUserService.removeRoleFromUserHandler(modifyRoleOfUserRequest);
        User updatedUser = repository.findByUserId(testId);
        assertFalse(response.getIsSuccess());

        Set<UserRole> roleSet = new HashSet<>();
        roleSet.add(studentRole);
        roleSet.add(adminRole);
        assertEquals(roleSet, updatedUser.getRoles());
        assertEquals("Unable to remove role. Cannot remove own course administrator role", response.getMessage());
    }

    //Tests that an admin can remove someone elses admin role
    @Test
    void whenHasAdminRole_testRemoveOtherAdminRole() {
        repository.deleteAll();
        User userA = new User(testUsername, testFirstName, testMiddleName, testLastName, testNickname, testBio, testPronouns, testEmail, testPassword);
        userA.addRole(adminRole);
        User testUser = repository.save(userA);
        testId = testUser.getUserId();
        ModifyRoleOfUserRequest modifyRoleOfUserRequest = ModifyRoleOfUserRequest.newBuilder()
                .setUserId(testId)
                .setRole(adminRole)
                .build();

        UserAccountsServerService spyUserService = Mockito.spy(userService);
        Mockito.doReturn(1).when(spyUserService).getAuthStateUserId();
        UserRoleChangeResponse response = spyUserService.removeRoleFromUserHandler(modifyRoleOfUserRequest);
        User updatedUser = repository.findByUserId(testId);
        assertTrue(response.getIsSuccess());
        Set<UserRole> roleSet = new HashSet<>();
        roleSet.add(studentRole);
        assertEquals(roleSet, updatedUser.getRoles());
        assertEquals("Role successfully removed", response.getMessage());
    }

    //Tests that you can't remove a role that a user doesn't have
    @Test
    void whenOnlyHasStudentRole_testRemoveTeacherRole() {
        ModifyRoleOfUserRequest modifyRoleOfUserRequest = ModifyRoleOfUserRequest.newBuilder()
                .setUserId(testId)
                .setRole(teacherRole)
                .build();
        UserRoleChangeResponse response = userService.removeRoleFromUserHandler(modifyRoleOfUserRequest);
        User updatedUser = repository.findByUserId(testId);
        assertFalse(response.getIsSuccess());
        assertEquals("Unable to remove role. User doesn't have given role", response.getMessage());
        Set<UserRole> roleSet = new HashSet<>();
        roleSet.add(studentRole);
        assertEquals(roleSet, updatedUser.getRoles());
    }

    //Tests that the users last role can't be removed
    @Test
    void whenOnlyHasStudentRole_testRemoveStudentRole() {
        ModifyRoleOfUserRequest modifyRoleOfUserRequest = ModifyRoleOfUserRequest.newBuilder()
                .setUserId(testId)
                .setRole(studentRole)
                .build();
        UserRoleChangeResponse response = userService.removeRoleFromUserHandler(modifyRoleOfUserRequest);
        User updatedUser = repository.findByUserId(testId);
        assertFalse(response.getIsSuccess());
        assertEquals("Unable to remove role. User only has one role", response.getMessage());
        Set<UserRole> roleSet = new HashSet<>();
        roleSet.add(studentRole);
        assertEquals(roleSet, updatedUser.getRoles());
    }

    // Check that a student does not have permissions to modify a student role
    @Test
    void whenHasStudentRole_testModifyStudentRole() {
        User updatedUser = repository.findByUserId(testId);
        repository.save(updatedUser);
        assertFalse(userService.isValidatedForRole(testId, STUDENT));
    }

    // Check that a student does not have permissions to modify a teacher role
    @Test
    void whenHasStudentRole_testModifyTeacherRole() {
        User updatedUser = repository.findByUserId(testId);
        repository.save(updatedUser);
        assertFalse(userService.isValidatedForRole(testId, TEACHER));
    }

    // Check that a student does not have permissions to modify an admin role
    @Test
    void whenHasStudentRole_testModifyAdminRole() {
        User updatedUser = repository.findByUserId(testId);
        repository.save(updatedUser);
        assertFalse(userService.isValidatedForRole(testId, COURSE_ADMINISTRATOR));
    }

    // Check that a teacher does have permissions to modify a student role
    @Test
    void whenHasTeacherRole_testModifyStudentRole() {
        User updatedUser = repository.findByUserId(testId);
        updatedUser.addRole(TEACHER);
        repository.save(updatedUser);
        assertTrue(userService.isValidatedForRole(testId, STUDENT));
    }

    // Check that a teacher does not have permissions to modify a teacher role
    @Test
    void whenHasTeacherRole_testModifyTeacherRole() {
        User updatedUser = repository.findByUserId(testId);
        updatedUser.addRole(TEACHER);
        repository.save(updatedUser);
        assertFalse(userService.isValidatedForRole(testId, TEACHER));
    }

    // Check that a teacher does not have permissions to modify an admin role
    @Test
    void whenHasTeacherRole_testModifyAdminRole() {
        User updatedUser = repository.findByUserId(testId);
        updatedUser.addRole(TEACHER);
        repository.save(updatedUser);
        assertFalse(userService.isValidatedForRole(testId, COURSE_ADMINISTRATOR));
    }

    // Check that an admin does have permissions to modify a student role
    @Test
    void whenHasAdminRole_testModifyStudentRole() {
        User updatedUser = repository.findByUserId(testId);
        updatedUser.addRole(COURSE_ADMINISTRATOR);
        repository.save(updatedUser);
        assertTrue(userService.isValidatedForRole(testId, STUDENT));
    }

    // Check that an admin does have permissions to modify a teacher role
    @Test
    void whenHasAdminRole_testModifyTeacherRole() {
        User updatedUser = repository.findByUserId(testId);
        updatedUser.addRole(COURSE_ADMINISTRATOR);
        repository.save(updatedUser);
        assertTrue(userService.isValidatedForRole(testId, TEACHER));
    }

    // Check that an admin does have permissions to modify an admin role
    @Test
    void whenHasAdminRole_testModifyAdminRole() {
        User updatedUser = repository.findByUserId(testId);
        updatedUser.addRole(COURSE_ADMINISTRATOR);
        repository.save(updatedUser);
        assertTrue(userService.isValidatedForRole(testId, COURSE_ADMINISTRATOR));
    }

}
