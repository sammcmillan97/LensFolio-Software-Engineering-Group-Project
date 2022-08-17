package nz.ac.canterbury.seng302.portfolio.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.portfolio.model.evidence.Evidence;
import nz.ac.canterbury.seng302.portfolio.model.evidence.WebLink;
import nz.ac.canterbury.seng302.portfolio.service.evidence.EvidenceService;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Date;

public class AddingWebLinkToEvidence {

    EvidenceService evidenceService = new EvidenceService();
    Evidence evidence;
    private static final String TEST_DESCRIPTION = "According to all know laws of aviation, there is no way a bee should be able to fly.";

    @Given("I have created a piece of evidence")
    public void i_have_created_a_piece_of_evidence() {
        evidence = new Evidence(0, 0, "Test Evidence", TEST_DESCRIPTION, Date.valueOf("2022-05-8"));
    }
    @When("I enter {string}")
    public void i_enter(String string) {
        try {
            evidenceService.validateWebLink(string);
            WebLink webLink = new  WebLink(string, "test weblink");
            evidence.addWebLink(webLink);
        } catch (IllegalArgumentException ignored) {}
    }

    @Then("The number of weblinks on that piece of evidence is {int}")
    public void the_number_of_weblinks_on_that_piece_of_evidence_is(Integer int1) {
        assertEquals(int1, evidence.getWebLinks().size());
    }

    @Then("The weblink is displayed as {string}")
    public void the_weblink_is_displayed_as(String string) {
        WebLink webLink = evidence.getWebLinks().get(0);
        assertEquals(string, webLink.getLink());
    }
}
