Feature: U8 web links in evidence

  Scenario Outline: U1&U2 - adding a valid weblink to a piece of evidence
    Given I have created a piece of evidence
    When I enter <webLink>
    Then The number of weblinks on that piece of evidence is <numberOfWebLinks>
    Examples:
    | webLink | numberOfWebLinks |
    | "https://scrumboard.csse.canterbury.ac.nz/" | 1 |
    | "thisIsNotAWebLink"                         | 0 |

  Scenario Outline: U4 - The protocol should not be displayed on a weblink
    Given I have created a piece of evidence
    When I enter <webLink>
    Then The weblink is displayed as <webLinkDisplay>
    Examples:
      | webLink | webLinkDisplay |
    | "https://scrumboard.csse.canterbury.ac.nz/" | "scrumboard.csse.canterbury.ac.nz/" |
    | "http://scrumboard.csse.canterbury.ac.nz/" | "scrumboard.csse.canterbury.ac.nz/" |