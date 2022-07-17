package nz.ac.canterbury.seng302.portfolio.controller;

import com.google.protobuf.Timestamp;
import nz.ac.canterbury.seng302.portfolio.model.User;
import nz.ac.canterbury.seng302.portfolio.model.UserListResponse;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.PaginatedUsersResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GroupsControllerTests {

    @Autowired
    GroupsController groupsController;

    @InjectMocks
    UserAccountClientService userAccountClientService = new UserAccountClientService();

    User student;
    User teacher;
    User admin;
    UserResponse studentResponse;
    UserResponse teacherResponse;
    UserResponse adminResponse;


    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this); // This is required for Mockito annotations to work

        String testUsername = "test user";
        String testFirstName = "test fname";
        String testMiddleName = "test mname";
        String testLastName = "test lname";
        String testNickname = "test nname";
        String testBio = "test bio";
        String testPronouns = "test/tester";
        String testEmail = "test@email.com";
        String testProfileImagePath = "test/image/path";
        Timestamp testCreated = Timestamp.newBuilder().build();

        ArrayList<UserRole> teacherRoles = new ArrayList<>();
        teacherRoles.add(UserRole.TEACHER);
        teacherResponse = UserResponse.newBuilder()
                .setUsername(testUsername)
                .setFirstName(testFirstName)
                .setMiddleName(testMiddleName)
                .setLastName(testLastName)
                .setProfileImagePath(testProfileImagePath)
                .setCreated(testCreated)
                .setEmail(testEmail)
                .setPersonalPronouns(testPronouns)
                .setBio(testBio)
                .setNickname(testNickname)
                .addAllRoles(teacherRoles).build();
        teacher = new User(teacherResponse);


        ArrayList<UserRole> studentRoles = new ArrayList<>();
        studentRoles.add(UserRole.STUDENT);
        studentResponse = UserResponse.newBuilder()
                .setUsername(testUsername)
                .setFirstName(testFirstName)
                .setMiddleName(testMiddleName)
                .setLastName(testLastName)
                .setProfileImagePath(testProfileImagePath)
                .setCreated(testCreated)
                .setEmail(testEmail)
                .setPersonalPronouns(testPronouns)
                .setBio(testBio)
                .setNickname(testNickname)
                .addAllRoles(studentRoles).build();
        student = new User(studentResponse);

        ArrayList<UserRole> adminRoles = new ArrayList<>();
        adminRoles.add(UserRole.COURSE_ADMINISTRATOR);
        adminResponse = UserResponse.newBuilder()
                .setUsername(testUsername)
                .setFirstName(testFirstName)
                .setMiddleName(testMiddleName)
                .setLastName(testLastName)
                .setProfileImagePath(testProfileImagePath)
                .setCreated(testCreated)
                .setEmail(testEmail)
                .setPersonalPronouns(testPronouns)
                .setBio(testBio)
                .setNickname(testNickname)
                .addAllRoles(adminRoles).build();
        admin = new User(adminResponse);

    }

    @Test
    void whenUserIsTeacher_isTeacherReturnsTrue() {
        assertTrue(groupsController.isTeacher(teacher));
    }

    @Test
    void whenUserIsStudent_isTeacherReturnsFalse() {
        assertFalse(groupsController.isTeacher(student));
    }

    @Test
    void givenUsersExist_getAllUsers(){
        PaginatedUsersResponse paginatedUsersResponse = PaginatedUsersResponse.newBuilder()
                        .addUsers(0, studentResponse)
                        .addUsers(1, teacherResponse)
                        .addUsers(2, adminResponse)
                        .setResultSetSize(3)
                        .build();
        Mockito.when(userAccountClientService.getPaginatedUsers(0, 50, "userId", true)).thenReturn(new UserListResponse(paginatedUsersResponse));

        Set<User> users = groupsController.getAllUsers();
        assertEquals(3, users.size());
    }


}
