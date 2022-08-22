package nz.ac.canterbury.seng302.portfolio.service.user;

import nz.ac.canterbury.seng302.portfolio.model.user.User;
import nz.ac.canterbury.seng302.portfolio.model.user.UserListResponse;
import nz.ac.canterbury.seng302.portfolio.service.user.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.*;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
class UserAccountClientServiceTests {

    @Autowired
    UserAccountClientService userAccountClientService;

    @Test
    void testLoggedIn_PrincipalIsNull(){
        AuthState principal = null;
        assertFalse(userAccountClientService.isLoggedIn(principal));
    }

    @Test
    void testLoggedIn_PrincipalIsNotNull(){
        AuthState principal = AuthState.newBuilder()
                .addClaims(ClaimDTO.newBuilder().setType("role").setValue("student").build())
                .build();
        assertTrue(userAccountClientService.isLoggedIn(principal));
    }

    @Test
    void testIsTeacherHandler_rolesContainsStudent(){
        ArrayList<UserRole> roles = new ArrayList<>();
        roles.add(UserRole.STUDENT);
        assertFalse(userAccountClientService.isTeacherHandler(roles));
    }

    @Test
    void testIsTeacherHandler_rolesContainsTeacher(){
        ArrayList<UserRole> roles = new ArrayList<>();
        roles.add(UserRole.TEACHER);
        assertTrue(userAccountClientService.isTeacherHandler(roles));
    }

    @Test
    void testIsTeacherHandler_rolesContainsAdmin(){
        ArrayList<UserRole> roles = new ArrayList<>();
        roles.add(UserRole.COURSE_ADMINISTRATOR);
        assertTrue(userAccountClientService.isTeacherHandler(roles));
    }

    @Test
    void testIsAdminHandler_rolesContainsStudent(){
        ArrayList<UserRole> roles = new ArrayList<>();
        roles.add(UserRole.STUDENT);
        assertFalse(userAccountClientService.isAdminHandler(roles));
    }

    @Test
    void testIsAdminHandler_rolesContainsTeacher(){
        ArrayList<UserRole> roles = new ArrayList<>();
        roles.add(UserRole.TEACHER);
        assertFalse(userAccountClientService.isAdminHandler(roles));
    }

    @Test
    void testIsAdminHandler_rolesContainsAdmin(){
        ArrayList<UserRole> roles = new ArrayList<>();
        roles.add(UserRole.COURSE_ADMINISTRATOR);
        assertTrue(userAccountClientService.isAdminHandler(roles));
    }

    @Test
    void whenNoUsers_testGetAllUsers() {
        List<UserResponse> users = new ArrayList<>();
        PaginatedUsersResponse paginatedUsersResponse = PaginatedUsersResponse.newBuilder()
                .addAllUsers(users)
                .setResultSetSize(0)
                .build();

        UserAccountServiceGrpc.UserAccountServiceBlockingStub userServiceBlockingStub = Mockito.spy(userAccountClientService.getUserStub());
        Mockito.doReturn(paginatedUsersResponse).when(userServiceBlockingStub).getPaginatedUsers(any(GetPaginatedUsersRequest.class));
        userAccountClientService.setUserStub(userServiceBlockingStub);

        UserListResponse response = userAccountClientService.getAllUsers();
        assertEquals(0, response.getResultSetSize());
        assertEquals(0, response.getUsers().size());
    }

    @Test
    void whenFiveUsers_testGetAllUsers() {
        // Create a PaginatedUserResponse with no UserResponses in it
        List<UserResponse> users = new ArrayList<>();
        PaginatedUsersResponse firstPaginatedUserResponse = PaginatedUsersResponse.newBuilder()
                .addAllUsers(users)
                .setResultSetSize(5)
                .build();

        // Create a PaginatedUserResponse with five empty UserResponses in it
        UserResponse emptyUserResponse = UserResponse.newBuilder().build();
        users = Arrays.asList(emptyUserResponse, emptyUserResponse, emptyUserResponse, emptyUserResponse, emptyUserResponse);
        PaginatedUsersResponse secondPaginatedUserResponse = PaginatedUsersResponse.newBuilder()
                .addAllUsers(users)
                .setResultSetSize(5)
                .build();

        UserAccountServiceGrpc.UserAccountServiceBlockingStub userServiceBlockingStub = Mockito.spy(userAccountClientService.getUserStub());
        Mockito.doReturn(firstPaginatedUserResponse, secondPaginatedUserResponse).when(userServiceBlockingStub).getPaginatedUsers(any(GetPaginatedUsersRequest.class));
        userAccountClientService.setUserStub(userServiceBlockingStub);

        UserListResponse response = userAccountClientService.getAllUsers();
        assertEquals(5, response.getResultSetSize());
        assertEquals(5, response.getUsers().size());
    }

    @Test
    void whenNoUsers_testGetAllUsersExcept() {
        int testUserId = 0;
        List<UserResponse> users = new ArrayList<>();
        PaginatedUsersResponse paginatedUsersResponse = PaginatedUsersResponse.newBuilder()
                .addAllUsers(users)
                .setResultSetSize(0)
                .build();

        UserListResponse userListResponse = new UserListResponse(paginatedUsersResponse);

        UserAccountClientService mockUserService = Mockito.spy(userAccountClientService);
        Mockito.doReturn(userListResponse).when(mockUserService).getAllUsers();

        List<User> responseUsers = mockUserService.getAllUsersExcept(testUserId);
        assertEquals(0, responseUsers.size());
    }

    @Test
    void whenFiveUsersAndOneMatches_testGetAllUsersExcept() {
        int testUserId = 0;
        int testUserId2 = 1;

        // Create a PaginatedUserResponse with five UserResponses in it with one matching the requested id
        UserResponse matchedUserResponse = UserResponse.newBuilder().setId(testUserId).build();
        UserResponse notMatchedUserResponse = UserResponse.newBuilder().setId(testUserId2).build();
        List<UserResponse> users = Arrays.asList(notMatchedUserResponse, matchedUserResponse, notMatchedUserResponse, notMatchedUserResponse, notMatchedUserResponse);
        PaginatedUsersResponse paginatedUsersResponse = PaginatedUsersResponse.newBuilder()
                .addAllUsers(users)
                .setResultSetSize(5)
                .build();

        UserListResponse userListResponse = new UserListResponse(paginatedUsersResponse);

        UserAccountClientService mockUserService = Mockito.spy(userAccountClientService);
        Mockito.doReturn(userListResponse).when(mockUserService).getAllUsers();

        List<User> responseUsers = mockUserService.getAllUsersExcept(testUserId);
        assertEquals(4, responseUsers.size());
        for (User user : responseUsers) {
            assertNotEquals(testUserId, user.getId());
        }
    }

    @Test
    void whenFiveUsersAndNoneMatch_testGetAllUsersExcept() {
        int testUserId = 0;
        int testUserId2 = 1;

        // Create a PaginatedUserResponse with five UserResponses in it that don't match the requested id
        UserResponse notMatchedUserResponse = UserResponse.newBuilder().setId(testUserId2).build();
        List<UserResponse> users = Arrays.asList(notMatchedUserResponse, notMatchedUserResponse, notMatchedUserResponse, notMatchedUserResponse, notMatchedUserResponse);
        PaginatedUsersResponse paginatedUsersResponse = PaginatedUsersResponse.newBuilder()
                .addAllUsers(users)
                .setResultSetSize(5)
                .build();

        UserListResponse userListResponse = new UserListResponse(paginatedUsersResponse);

        UserAccountClientService mockUserService = Mockito.spy(userAccountClientService);
        Mockito.doReturn(userListResponse).when(mockUserService).getAllUsers();

        List<User> responseUsers = mockUserService.getAllUsersExcept(testUserId);
        assertEquals(5, responseUsers.size());
        for (User user : responseUsers) {
            assertNotEquals(testUserId, user.getId());
        }
    }



}
