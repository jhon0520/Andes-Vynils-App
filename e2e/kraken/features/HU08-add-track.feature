Feature: HU08 - Add a track to an album

  @user1 @mobile
  Scenario: As a collector I add a track to an album and see it in the tracklist
    Given I wait for 5 seconds
    # The app always opens on the role selection screen
    When I tap the element with id "role_collector"
    And I type "<COLLECTOR_QUERY>" into the element with id "collector_search"
    And I hide the keyboard
    And I tap the first element with id starting with "collector_row_"
    And I tap the element with id "collector_continue"
    And I type "<ALBUM_QUERY>" into the element with id "album_search_input"
    And I hide the keyboard
    # The seed album is found by its id: its name differs between backends
    And I scroll to the element with id "<SALSA_ALBUM_ID>" in the element with id "album_list"
    And I tap the element with id "<SALSA_ALBUM_ID>"
    Then I should see the element with id "album_detail_body"
    When I scroll to the element with id "add_track_button" in the element with id "album_detail_body"
    And I tap the element with id "add_track_button"
    Then I should see the element with id "add_track_sheet"
    # A generated name: this run only checks the track it created
    When I type "$name_1" into the element with id "track_name_input"
    And I hide the keyboard
    And I type "<NEW_TRACK_DURATION>" into the element with id "track_duration_input"
    And I hide the keyboard
    And I tap the element with id "add_track_submit"
    Then I should not see the element with id "add_track_sheet"
    When I scroll to the text "$$name_1" in the element with id "album_detail_body"
    Then I should see the text "$$name_1"
