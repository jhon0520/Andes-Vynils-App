Feature: HU07 - Create an album

  @user1 @mobile
  Scenario: As a collector I create an album and find it in the catalog
    Given I wait for 5 seconds
    # The app always opens on the role selection screen
    When I tap the element with id "role_collector"
    And I type "<COLLECTOR_QUERY>" into the element with id "collector_search"
    And I hide the keyboard
    And I tap the first element with id starting with "collector_row_"
    And I tap the element with id "collector_continue"
    And I tap the element with id "create_album_button"
    Then I should see the element with id "album_create_form"
    When I type "<NEW_ALBUM_COVER>" into the element with id "album_cover_input"
    And I hide the keyboard
    # A generated name: this run only checks the album it created
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
    And I tap the element with id "album_create_submit"
    Then I should see the element with id "album_created_dialog"
    When I tap the element with id "album_created_view"
    Then I should see the element with id "album_detail_body"
    And I should see the text "$$name_1"
