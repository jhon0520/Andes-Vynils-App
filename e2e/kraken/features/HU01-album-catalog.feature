Feature: HU01 - Browse the album catalog

  @user1 @mobile
  Scenario: As a visitor I search by performer, filter by genre and clear the search
    Given I wait for 5 seconds
    Then I should see the element with id "album_list"
    When I scroll to the text "<CATALOG_ALBUM>" in the element with id "album_list"
    Then I should see the text "<CATALOG_ALBUM>"
    # The rock album is on screen first so that its absence after searching is meaningful
    When I scroll to the text "<ROCK_ALBUM>" in the element with id "album_list"
    Then I should see the text "<ROCK_ALBUM>"
    When I type "<PERFORMER_QUERY>" into the element with id "album_search_input"
    And I hide the keyboard
    Then I should not see the text "<ROCK_ALBUM>"
    When I tap the element with id "album_genre_tab_salsa"
    Then the element with id "album_genre_tab_salsa" should be selected
    When I scroll to the text "<SALSA_ALBUM>" in the element with id "album_list"
    Then I should see the text "<SALSA_ALBUM>"
    # A query no album can match: the empty result does not depend on the backend data
    When I type "<NO_MATCH_QUERY>" into the element with id "album_search_input"
    And I hide the keyboard
    Then I should see the element with id "no_results_view"
    When I tap the element with id "clear_search_button"
    Then the element with id "album_genre_tab_all" should be selected
    And I should see the element with id "album_list"
    When I scroll to the text "<CATALOG_ALBUM>" in the element with id "album_list"
    Then I should see the text "<CATALOG_ALBUM>"
