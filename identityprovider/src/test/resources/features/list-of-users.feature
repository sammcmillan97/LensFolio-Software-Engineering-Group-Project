Feature: View a list of users

  Scenario: View the list of users
    Given I am logged in as any user
    When I request the list of users
    Then I receive the list of users

  Scenario: View the list of users when not logged in
    Given I am not logged in
    When I request the list of users
    Then I receive an error "View user list failed: Not authenticated"
