Feature: Onboarding - Enter as a collector

  @user1 @mobile
  Scenario: As a collector I search for my profile, select it and continue
    Given I wait for 5 seconds
    When I tap the element with id "role_collector"
    Then I should see the element with id "collector_picker"
    And I should see the element with id "collector_list"
    When I tap the element with id "back_button"
    Then I should see the element with id "role_selection"
    When I tap the element with id "role_collector"
    And I type "<COLLECTOR_QUERY>" into the element with id "collector_search"
    And I hide the keyboard
    Then I should see the text "<COLLECTOR_NAME>"
    When I tap the first element with id starting with "collector_row_"
    Then I should see the text "<COLLECTOR_CONTINUE>"
    When I tap the element with id "collector_continue"
    Then I should see the element with id "album_list"
