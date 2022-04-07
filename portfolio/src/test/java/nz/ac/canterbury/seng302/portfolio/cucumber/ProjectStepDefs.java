package nz.ac.canterbury.seng302.portfolio.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.portfolio.model.ProjectRepository;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProjectStepDefs{
    int id;

    @Given("no projects exist")
    public void noProjectsExist() {
        id = 0;
    }

    @When("project is created")
    public void projectIsCreated() {
        return;
    }

    @Then("project exists in database")
    public void projectExistsInDatabase() {
        assertEquals(id, 0);
    }
}
