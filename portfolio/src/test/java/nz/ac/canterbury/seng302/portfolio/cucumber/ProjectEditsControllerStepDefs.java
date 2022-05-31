package nz.ac.canterbury.seng302.portfolio.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.portfolio.controller.ProjectEditsController;
import nz.ac.canterbury.seng302.portfolio.model.Event;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.User;
import nz.ac.canterbury.seng302.portfolio.service.AuthenticateClientService;
import nz.ac.canterbury.seng302.portfolio.service.ProjectEdits;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.sql.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProjectEditsControllerStepDefs {

    ProjectEdits projectEdits;

    Project project;
    int projectId = 1;

    int editingUserId = 1;
    int viewingUserId = 2;
    String editString = "Fabian is editing Test Project";

    /**
     * Helper function to create a valid AuthState given an ID
     * @param id - The ID of the user specified by this AuthState
     * @return the valid AuthState
     */
    private AuthState createValidAuthStateWithId(String id) {
        return AuthState.newBuilder()
                .setIsAuthenticated(true)
                .setNameClaimType("name")
                .setRoleClaimType("role")
                .addClaims(ClaimDTO.newBuilder().setType("role").setValue("TEACHER").build())
                .addClaims(ClaimDTO.newBuilder().setType("nameid").setValue(id).build())
                .build();
    }


    @Given("a project exists")
    public void a_project_exists() {
        projectEdits = new ProjectEdits();
        project = new Project("Project Name", "Test Project", java.sql.Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16"));
    }

    @When("a user is editing the project")
    public void a_user_is_editing_the_project() {
        projectEdits.newEdit(projectId, editingUserId, editString);
    }
    @Then("another user viewing the project should be notified")
    public void another_user_viewing_the_project_should_be_notified() {
        String editNotification = projectEdits.getEdits(projectId, viewingUserId);
        assertEquals("{\"edits\": [\"" + editString + "\"], \"refresh\":false}", editNotification);
    }

    @When("the user stops editing the project")
    public void the_user_stops_editing_the_project() {
        projectEdits.newEdit(-1, editingUserId, editString);
    }

    @Then("another user viewing the project should not be notified")
    public void another_user_viewing_the_project_should_not_be_notified() {
        String editNotification = projectEdits.getEdits(projectId, viewingUserId);
        assertEquals("{\"edits\": [], \"refresh\":false}", editNotification);
    }

    @When("the user saves their edits")
    public void the_user_saves_their_edits() {
        projectEdits.newEdit(-1, editingUserId, editString);
        projectEdits.refreshProject(projectId);
    }

    @Then("another user viewing the project should get a notification to reload")
    public void another_user_viewing_the_project_should_get_a_notification_to_reload() {
        // Write code here that turns the phrase above into concrete actions
        String editNotification = projectEdits.getEdits(projectId, viewingUserId);
        assertEquals("{\"edits\": [], \"refresh\":true}", editNotification);
    }

}
