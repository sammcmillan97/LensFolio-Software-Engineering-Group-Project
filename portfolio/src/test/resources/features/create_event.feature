Feature: Create an event model

  Scenario:
    Given an event has name "April Fools"
    When name is changed to "New Years"
    Then name is "New Years"
