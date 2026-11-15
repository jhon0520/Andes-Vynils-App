Feature: P-HU03 - Profiling of the artist list

  @user1 @mobile
  Scenario: Open the artists, filter them and come back (the return tests the cache)
    Given I wait for 5 seconds
    When I start profiling "P-HU03"
    And I tap the element with id "role_visitor"
    And I should see the element with id "album_list"
    And I mark the network checkpoint "primera-apertura"
    And I tap the element with id "nav_artists" and measure until I see the element with id "artist_list" as "artistas-primera-vez"
    And I wait for 2 seconds
    And I tap the element with id "artist_type_tab_band"
    And I tap the element with id "artist_type_tab_musician"
    And I tap the element with id "artist_type_tab_all"
    And I tap the element with id "nav_albums"
    And I should see the element with id "album_list"
    And I mark the network checkpoint "regreso"
    And I tap the element with id "nav_artists" and measure until I see the element with id "artist_list" as "artistas-regreso"
    And I wait for 2 seconds
    Then I save the profiling
