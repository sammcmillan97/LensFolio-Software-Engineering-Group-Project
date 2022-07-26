package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.shared.identityprovider.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
}
