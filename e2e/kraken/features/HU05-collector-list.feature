Feature: HU05 - Browse the list of collectors

  @user1 @mobile
  Scenario: As a visitor I see the collectors, search them by name and email and clear the search
    Given I wait for 5 seconds
    # The app always opens on the role selection screen
    When I tap the element with id "role_visitor"
    When I tap the element with id "nav_collectors"
    Then I should see the element with id "collectors_list"
    # Both collectors are on screen first so that their absence after searching is meaningful
    When I scroll to the text "<COLLECTOR_NAME>" in the element with id "collectors_list"
    Then I should see the text "<COLLECTOR_NAME>"
    When I scroll to the text "<OTHER_COLLECTOR_NAME>" in the element with id "collectors_list"
    Then I should see the text "<OTHER_COLLECTOR_NAME>"
    When I type "<COLLECTOR_QUERY>" into the element with id "collectors_search"
    And I hide the keyboard
    Then I should see the text "<COLLECTOR_NAME>"
    And I should not see the text "<OTHER_COLLECTOR_NAME>"
    # Appium adds the text to what is already in the field: the search is cleared with its ✕ first
    When I tap the element with id "collectors_search_clear_button"
    And I type "<OTHER_COLLECTOR_EMAIL_QUERY>" into the element with id "collectors_search"
    And I hide the keyboard
    Then I should see the text "<OTHER_COLLECTOR_NAME>"
    And I should not see the text "<COLLECTOR_NAME>"
    # A query no collector can match: the empty result does not depend on the backend data
    When I tap the element with id "collectors_search_clear_button"
    And I type "<NO_MATCH_QUERY>" into the element with id "collectors_search"
    And I hide the keyboard
    Then I should see the element with id "no_results_view"
    When I tap the element with id "clear_search_button"
    Then I should see the element with id "collectors_list"
    And I should see the text "<COLLECTOR_NAME>"
