Feature: P-HU05 - Profiling of the collector list

  @user1 @mobile
  Scenario: Open the collectors, search them and come back (the return tests the cache)
    # The build before the optimizations has the old list without search (its results are already saved)
    Given I skip this scenario on the build "antes"
    And I wait for 5 seconds
    When I start profiling "P-HU05"
    And I tap the element with id "role_visitor"
    And I should see the element with id "album_list"
    And I mark the network checkpoint "primera-apertura"
    And I tap the element with id "nav_collectors" and measure until I see the element with id "collectors_list" as "coleccionistas-primera-vez"
    And I wait for 2 seconds
    # HU05 searches the list already in memory: no request is expected from here
    And I mark the network checkpoint "buscar"
    And I type "<COLLECTOR_QUERY>" into the element with id "collectors_search"
    And I hide the keyboard
    And I tap the element with id "collectors_search_clear_button"
    And I tap the element with id "nav_albums"
    And I should see the element with id "album_list"
    And I mark the network checkpoint "regreso"
    And I tap the element with id "nav_collectors" and measure until I see the element with id "collectors_list" as "coleccionistas-regreso"
    And I wait for 2 seconds
    Then I save the profiling
