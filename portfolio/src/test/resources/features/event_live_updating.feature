Feature: Users should be updated when a project is being modified

  Scenario: User is updated when an edit is happening
    Given a project exists
    When a user is editing the project
    Then another user viewing the project should be notified

  Scenario: User is no longer updated when an edit stops happening
    Given a project exists
    When a user is editing the project
    And the user stops editing the project
    Then another user viewing the project should not be notified

  Scenario: User is told to reload when an edit is completed
    Given a project exists
    When a user is editing the project
    And the user saves their edits
    Then another user viewing the project should told to reload
