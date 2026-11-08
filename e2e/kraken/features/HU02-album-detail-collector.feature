Feature: HU02 - View the detailed information of an album as a collector

  @user1 @mobile
  Scenario: As a collector I open the detail of an album and see the add track button
    Given I wait for 5 seconds
    # The app always opens on the role selection screen
    When I tap the element with id "role_collector"
    And I type "<COLLECTOR_QUERY>" into the element with id "collector_search"
    And I hide the keyboard
    And I tap the first element with id starting with "collector_row_"
    And I tap the element with id "collector_continue"
    And I type "<ALBUM_QUERY>" into the element with id "album_search_input"
    And I hide the keyboard
    And I scroll to the text "<SALSA_ALBUM>" in the element with id "album_list"
    And I tap the text "<SALSA_ALBUM>"
    Then I should see the element with id "album_detail_body"
    And I should see the element with id "add_track_button"
