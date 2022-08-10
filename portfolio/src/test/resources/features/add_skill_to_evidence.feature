Feature: Add skill to evidence

  Scenario: Adding a single skill to evidence
    Given a piece of evidence is created with details 1, 1, "Evidence of Cucumber", "Here I have provided evidence of testing using Cucumber. I have set up a feature file, and defined the step definitions as required for a cucumber test", null
    When the skill "Cucumber" is added to evidence
    Then skills becomes
      |Cucumber|