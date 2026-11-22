Feature: P-HU07 - Profiling of the album creation

  @user1 @mobile
  Scenario: As a collector open the form, create an album and open it
    # The build before the optimizations has no album creation yet
    Given I skip this scenario on the build "antes"
    And I wait for 5 seconds
    When I start profiling "P-HU07"
    And I tap the element with id "role_collector"
    And I type "<COLLECTOR_QUERY>" into the element with id "collector_search"
    And I hide the keyboard
    And I tap the first element with id starting with "collector_row_"
    And I tap the element with id "collector_continue"
    And I should see the element with id "album_list"
    And I mark the network checkpoint "formulario"
    And I tap the element with id "create_album_button" and measure until I see the element with id "album_create_form" as "formulario"
    And I type "<NEW_ALBUM_COVER>" into the element with id "album_cover_input"
    And I hide the keyboard
    # A generated name: every run creates its own album
    And I type "$name_1" into the element with id "album_name_input"
    And I hide the keyboard
    And I type "<NEW_ALBUM_DATE>" into the element with id "album_release_date_input"
    And I hide the keyboard
    And I tap the element with id "album_genre_input"
    And I tap the element with id "album_genre_option_salsa"
    And I tap the element with id "album_label_input"
    And I tap the element with id "album_label_option_elektra"
    And I scroll to the element with id "album_description_input" in the element with id "album_create_form"
    And I type "<NEW_ALBUM_DESCRIPTION>" into the element with id "album_description_input"
    And I hide the keyboard
    And I scroll to the element with id "album_create_submit" in the element with id "album_create_form"
    And I mark the network checkpoint "crear"
    And I tap the element with id "album_create_submit" and measure until I see the element with id "album_created_dialog" as "album-creado"
    And I mark the network checkpoint "ver-album"
    And I tap the element with id "album_created_view" and measure until I see the element with id "album_detail_body" as "detalle-album-creado"
    And I should see the text "$$name_1"
    And I wait for 2 seconds
    Then I save the profiling
