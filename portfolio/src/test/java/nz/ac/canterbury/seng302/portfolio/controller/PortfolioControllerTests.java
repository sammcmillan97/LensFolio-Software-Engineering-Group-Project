package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Evidence;
import nz.ac.canterbury.seng302.portfolio.model.PortfolioUser;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.User;
import nz.ac.canterbury.seng302.portfolio.service.EvidenceService;
import nz.ac.canterbury.seng302.portfolio.service.PortfolioUserService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PortfolioController.class)
@AutoConfigureMockMvc(addFilters = false)
class PortfolioControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    UserAccountClientService userService;

    @MockBean
    EvidenceService evidenceService;

    @MockBean
    PortfolioUserService portfolioUserService;

    @MockBean
    GlobalControllerAdvice globalControllerAdvice;


    /**
     * Helper function to create a valid AuthState given an ID
     * Credit to teaching team for this function
     * @param id - The ID of the user specified by this AuthState
     * @return the valid AuthState
     */
    private AuthState createValidAuthStateWithId(String id) {
        return AuthState.newBuilder()
                .setIsAuthenticated(true)
                .setNameClaimType("name")
                .setRoleClaimType("role")
                .addClaims(ClaimDTO.newBuilder().setType("role").setValue("STUDENT").build())
                .addClaims(ClaimDTO.newBuilder().setType("nameid").setValue(id).build())
                .build();
    }

    /**
     * Helper function which sets up the security of the test for simple uses.
     * @return a valid AuthState with ID 1
     */
    private AuthState setupSecurity() {
        AuthState validAuthState = createValidAuthStateWithId("1");
        SecurityContext mockedSecurityContext = Mockito.mock((SecurityContext.class));
        Mockito.when(mockedSecurityContext.getAuthentication()).thenReturn(new PreAuthenticatedAuthenticationToken(validAuthState,""));
        SecurityContextHolder.setContext(mockedSecurityContext);
        return validAuthState;
    }

    // Check that the portfolio endpoint works with a valid user
    @Test
    void getStandardPortfolioWithValidUserReturnPortfolio() throws Exception {

        AuthState validAuthState = setupSecurity();
        Mockito.when(userService.getUserAccountByPrincipal(validAuthState)).thenReturn(new User(UserResponse.newBuilder().build()));
        Mockito.when(portfolioUserService.getUserById(any(Integer.class))).thenReturn(new PortfolioUser(1, "name", true));
        Mockito.when(evidenceService.getEvidenceById(any(Integer.class))).thenReturn(new Evidence());
        Mockito.when(globalControllerAdvice.getCurrentProject(validAuthState)).thenReturn(new Project());
        Mockito.when(globalControllerAdvice.getAllProjects()).thenReturn(List.of(new Project()));

        mockMvc.perform(get("/portfolio"))
                .andExpect(status().isOk())
                .andExpect(redirectedUrl(null));
    }

    // Check that the portfolio endpoint redirects to the requesters profile
    // when viewing a user who does not exist
    @Test
    void getOtherPortfolioWithInvalidUserReturnsRedirectToProfile() throws Exception {

        AuthState validAuthState = setupSecurity();
        Mockito.when(userService.getUserAccountByPrincipal(validAuthState)).thenReturn(new User(UserResponse.newBuilder().build()));
        Mockito.when(userService.getUserAccountById(2)).thenReturn(new User(UserResponse.newBuilder().build()));
        Mockito.when(portfolioUserService.getUserById(any(Integer.class))).thenReturn(new PortfolioUser(1, "name", true));
        Mockito.when(evidenceService.getEvidenceById(any(Integer.class))).thenReturn(new Evidence());
        Mockito.when(globalControllerAdvice.getCurrentProject(validAuthState)).thenReturn(new Project());
        Mockito.when(globalControllerAdvice.getAllProjects()).thenReturn(List.of(new Project()));

        mockMvc.perform(get("/portfolio-2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"));
    }

    // Check that the portfolio endpoint works normally
    // when viewing another user
    @Test
    void getOtherPortfolioWithOtherUserReturnsPortfolio() throws Exception {

        AuthState validAuthState = setupSecurity();
        Mockito.when(userService.getUserAccountByPrincipal(validAuthState)).thenReturn(new User(UserResponse.newBuilder().setId(1).build()));
        Mockito.when(userService.getUserAccountById(2)).thenReturn(new User(UserResponse.newBuilder().setId(2).setUsername("test").build()));
        Mockito.when(portfolioUserService.getUserById(any(Integer.class))).thenReturn(new PortfolioUser(1, "name", true));
        Mockito.when(evidenceService.getEvidenceById(any(Integer.class))).thenReturn(new Evidence());
        Mockito.when(globalControllerAdvice.getCurrentProject(validAuthState)).thenReturn(new Project());
        Mockito.when(globalControllerAdvice.getAllProjects()).thenReturn(List.of(new Project()));

        mockMvc.perform(get("/portfolio-2"))
                .andExpect(status().isOk())
                .andExpect(redirectedUrl(null));
    }

    // Check that the portfolio endpoint redirects to the requesters' portfolio
    // when viewing your own portfolio
    @Test
    void getOtherPortfolioWithSameUserReturnsRedirectToPortfolio() throws Exception {

        AuthState validAuthState = setupSecurity();
        Mockito.when(userService.getUserAccountByPrincipal(validAuthState)).thenReturn(new User(UserResponse.newBuilder().setId(1).build()));
        Mockito.when(userService.getUserAccountById(1)).thenReturn(new User(UserResponse.newBuilder().setId(1).setUsername("test").build()));
        Mockito.when(portfolioUserService.getUserById(any(Integer.class))).thenReturn(new PortfolioUser(1, "name", true));
        Mockito.when(evidenceService.getEvidenceById(any(Integer.class))).thenReturn(new Evidence());
        Mockito.when(globalControllerAdvice.getCurrentProject(validAuthState)).thenReturn(new Project());
        Mockito.when(globalControllerAdvice.getAllProjects()).thenReturn(List.of(new Project()));

        mockMvc.perform(get("/portfolio-1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/portfolio"));
    }

}
