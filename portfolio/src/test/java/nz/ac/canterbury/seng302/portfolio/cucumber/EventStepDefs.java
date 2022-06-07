package nz.ac.canterbury.seng302.portfolio.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.portfolio.model.Event;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EventStepDefs {
    Event event;

    @Given("an event has name {string}")
    public void anEventHasName(String name) {
        event = new Event();
        event.setEventName(name);
    }

    @When("name is changed to {string}")
    public void nameIsChangedTo(String name) {
        event.setEventName(name);
    }


    @Then("name is {string}")
    public void nameIs(String name) {
        assertEquals(name, event.getEventName());
    }
}
