Feature: Onboarding - Enter as a visitor

  @user1 @mobile
  Scenario: As a visitor I open the app, choose Visitor and land on the catalog
    Given I wait for 5 seconds
    Then I should see the element with id "role_selection"
    And I should see the element with id "role_collector"
    And I should not see the element with id "nav_albums"
    When I tap the element with id "role_visitor"
    Then I should see the element with id "album_list"
    And I should see the element with id "nav_albums"
