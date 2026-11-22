Feature: P-HU08 - Profiling of adding a track to an album

  @user1 @mobile
  Scenario: As a collector open an album, add a track and see it in the tracklist
    # The build before the optimizations has no track creation yet
    Given I skip this scenario on the build "antes"
    And I wait for 5 seconds
    When I start profiling "P-HU08"
    And I tap the element with id "role_collector"
    And I type "<COLLECTOR_QUERY>" into the element with id "collector_search"
    And I hide the keyboard
    And I tap the first element with id starting with "collector_row_"
    And I tap the element with id "collector_continue"
    And I type "<ALBUM_QUERY>" into the element with id "album_search_input"
    And I hide the keyboard
    And I scroll to the element with id "<SALSA_ALBUM_ID>" in the element with id "album_list"
    And I mark the network checkpoint "detalle-album"
    And I tap the element with id "<SALSA_ALBUM_ID>" and measure until I see the element with id "album_detail_body" as "detalle-album"
    And I wait for 2 seconds
    And I scroll to the element with id "add_track_button" in the element with id "album_detail_body"
    And I tap the element with id "add_track_button" and measure until I see the element with id "add_track_sheet" as "hoja-agregar-track"
    # A generated name: every run adds its own track
    And I type "$name_1" into the element with id "track_name_input"
    And I hide the keyboard
    And I type "<NEW_TRACK_DURATION>" into the element with id "track_duration_input"
    And I hide the keyboard
    And I mark the network checkpoint "guardar-track"
    # The sheet closes once the backend saved the track and the album was reloaded
    And I tap the element with id "add_track_submit" and measure until I stop seeing the element with id "add_track_sheet" as "track-guardado"
    And I scroll to the text "$$name_1" in the element with id "album_detail_body"
    And I should see the text "$$name_1"
    And I wait for 2 seconds
    Then I save the profiling
