package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.shared.identityprovider.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

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
}
