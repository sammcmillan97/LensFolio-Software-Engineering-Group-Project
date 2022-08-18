package nz.ac.canterbury.seng302.portfolio.cucumber;


import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.portfolio.model.evidence.Evidence;

import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class EvidenceStepDefs {
    Evidence evidence;

    @Given("a piece of evidence is created with details {int}, {int}, {string}, {string}, null")
    public void aPieceOfEvidenceIsCreatedWithNull(int ownerId, int projectId, String title, String description) {
        evidence = new Evidence(ownerId, projectId, title, description, null);
    }

    @When("the skill {string} is added to evidence")
    public void aSkillIsAddedToEvidence(String skill) { evidence.addSkill(skill); }

    @Then("skills becomes$")
    public void skillsBecomes(List<String> skills) {
        assertEquals(skills, evidence.getSkills());
    }
}
