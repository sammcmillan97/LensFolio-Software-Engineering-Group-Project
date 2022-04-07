Feature: Create a project

  Scenario:
    Given no projects exist
    When project is created
    Then project exists in database