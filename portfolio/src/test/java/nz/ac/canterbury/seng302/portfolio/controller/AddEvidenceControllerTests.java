package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.controller.evidence.AddEvidenceController;
import nz.ac.canterbury.seng302.portfolio.model.user.PortfolioUser;
import nz.ac.canterbury.seng302.portfolio.model.project.Project;
import nz.ac.canterbury.seng302.portfolio.model.user.User;
import nz.ac.canterbury.seng302.portfolio.service.evidence.EvidenceService;
import nz.ac.canterbury.seng302.portfolio.service.user.PortfolioUserService;
import nz.ac.canterbury.seng302.portfolio.service.project.ProjectService;
import nz.ac.canterbury.seng302.portfolio.service.user.UserAccountClientService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AddEvidenceController.class)
@AutoConfigureMockMvc(addFilters = false)
class AddEvidenceControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    UserAccountClientService userService;

    @MockBean
    ProjectService projectService;

    @MockBean
    PortfolioUserService portfolioUserService;

    @MockBean
    EvidenceService evidenceService;

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
    void whenGetAddEvidencePage_testReturnsAddEvidence() throws Exception {
        AuthState validAuthState = setupSecurity();
        Mockito.when(userService.getUserAccountByPrincipal(validAuthState)).thenReturn(new User(UserResponse.newBuilder().build()));
        PortfolioUser portfolioUser = new PortfolioUser(1, "name", true);
        portfolioUser.setCurrentProject(1);
        Mockito.when(portfolioUserService.getUserById(any(Integer.class))).thenReturn(portfolioUser);
        Mockito.when(projectService.getProjectById(any(Integer.class))).thenReturn(new Project());
        Mockito.when(globalControllerAdvice.getCurrentProject(validAuthState)).thenReturn(new Project());
        Mockito.when(globalControllerAdvice.getAllProjects()).thenReturn(List.of(new Project()));

        mockMvc.perform(get("/addEvidence"))
                .andExpect(status().isOk())
                .andExpect(redirectedUrl(null));
    }

    // Check that saving evidence properly redirects to the portfolio page.
    @Test
    void whenSaveEvidenceWithGoodData_testReturnsPortfolio() throws Exception {
        AuthState validAuthState = setupSecurity();
        Mockito.when(userService.getUserAccountByPrincipal(validAuthState)).thenReturn(new User(UserResponse.newBuilder().setId(1).build()));
        Mockito.when(userService.getUserId(validAuthState)).thenReturn(1);
        Mockito.when(portfolioUserService.getUserById(1)).thenReturn(new PortfolioUser(1, "name", true));
        Mockito.when(globalControllerAdvice.getCurrentProject(validAuthState)).thenReturn(new Project());
        Mockito.when(globalControllerAdvice.getAllProjects()).thenReturn(List.of(new Project()));
        Mockito.when(projectService.getProjectById(any(Integer.class))).thenReturn(new Project());

        mockMvc.perform(post("/addEvidence")
                        .param("evidenceTitle", "test title")
                        .param("evidenceDescription", "test description")
                        .param("evidenceDate", "2002-02-16")
                        .param("evidenceSkills", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/portfolio"));
    }

    // Check that saving a piece of evidence properly redirects to the portfolio page.
    @Test
    void whenSaveEvidenceWithBadData_testReturnsAddEvidence() throws Exception {
        AuthState validAuthState = setupSecurity();
        Mockito.when(userService.getUserAccountByPrincipal(validAuthState)).thenReturn(new User(UserResponse.newBuilder().build()));
        Mockito.when(userService.getUserId(validAuthState)).thenReturn(1);
        Mockito.when(portfolioUserService.getUserById(any(Integer.class))).thenReturn(new PortfolioUser(1, "name", true));
        Mockito.doThrow(IllegalArgumentException.class).when(evidenceService).saveEvidence(any());
        Mockito.when(globalControllerAdvice.getCurrentProject(validAuthState)).thenReturn(new Project());
        Mockito.when(globalControllerAdvice.getAllProjects()).thenReturn(List.of(new Project()));
        Mockito.when(projectService.getProjectById(any(Integer.class))).thenReturn(new Project());

        mockMvc.perform(post("/addEvidence")
                        .param("evidenceTitle", "test title")
                        .param("evidenceDescription", "test description")
                        .param("evidenceDate", "2002-02-16")
                        .param("evidenceSkills", ""))
                .andExpect(status().isOk())
                .andExpect(redirectedUrl(null));
    }

    @Test
    void whenSaveEvidenceWithBadDate_testReturnsAddEvidence() throws Exception {
        AuthState validAuthState = setupSecurity();
        Mockito.when(userService.getUserAccountByPrincipal(validAuthState)).thenReturn(new User(UserResponse.newBuilder().build()));
        Mockito.when(userService.getUserId(validAuthState)).thenReturn(1);
        Mockito.when(portfolioUserService.getUserById(any(Integer.class))).thenReturn(new PortfolioUser(1, "name", true));
        Mockito.when(globalControllerAdvice.getCurrentProject(validAuthState)).thenReturn(new Project());
        Mockito.when(globalControllerAdvice.getAllProjects()).thenReturn(List.of(new Project()));
        Mockito.when(projectService.getProjectById(any(Integer.class))).thenReturn(new Project());

        mockMvc.perform(post("/addEvidence")
                        .param("evidenceTitle", "test title")
                        .param("evidenceDescription", "test description")
                        .param("evidenceDate", "bad date")
                        .param("evidenceSkills", ""))
                .andExpect(status().isOk())
                .andExpect(redirectedUrl(null));
    }



}
