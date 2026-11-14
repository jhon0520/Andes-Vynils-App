Feature: P-HU01 - Profiling of the album catalog

  @user1 @mobile
  Scenario: Load the catalog, scroll to the end, search, filter and clear
    Given I wait for 5 seconds
    When I start profiling "P-HU01"
    And I tap the element with id "role_visitor" and measure until I see the element with id "album_list" as "catalogo"
    # Time for the covers on screen to load
    And I wait for 3 seconds
    And I swipe up 8 times in the element with id "album_list"
    And I mark the network checkpoint "buscar-y-filtrar"
    And I type "<PERFORMER_QUERY>" into the element with id "album_search_input"
    And I hide the keyboard
    And I tap the element with id "album_genre_tab_salsa"
    And I scroll to the element with id "<SALSA_ALBUM_ID>" in the element with id "album_list"
    And I tap the element with id "album_search_clear_button"
    And I tap the element with id "album_genre_tab_all"
    Then I should see the element with id "album_list"
    And I wait for 2 seconds
    And I save the profiling
