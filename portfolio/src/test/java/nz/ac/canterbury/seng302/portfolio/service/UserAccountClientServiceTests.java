package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserAccountClientServiceTests {

    @Autowired
    UserAccountClientService userAccountClientService;

    //Test a roleless user is not a teacher
    @Test
    void testIsTeacherWithNone() {
        AuthState principal = AuthState.newBuilder().build();
        assertFalse(userAccountClientService.isTeacher(principal));
    }

    //Test a teacher is a teacher
    @Test
    void testIsTeacherWithTeacher() {
        AuthState principal = AuthState.newBuilder()
                .addClaims(ClaimDTO.newBuilder().setType("role").setValue("teacher").build())
                .build();
        assertTrue(userAccountClientService.isTeacher(principal));
    }

    //Test a student is not a teacher
    @Test
    void testIsTeacherWithStudent() {
        AuthState principal = AuthState.newBuilder()
                .addClaims(ClaimDTO.newBuilder().setType("role").setValue("student").build())
                .build();
        assertFalse(userAccountClientService.isTeacher(principal));
    }

    //Test a many-roled course admin is a teacher
    @Test
    void testIsTeacherWithAdmin() {
        AuthState principal = AuthState.newBuilder()
                .addClaims(ClaimDTO.newBuilder().setType("role").setValue("student,courseadministrator,tutor").build())
                .build();
        assertTrue(userAccountClientService.isTeacher(principal));
    }

}
