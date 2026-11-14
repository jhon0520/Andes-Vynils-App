Feature: P-HU05 - Profiling of the collector list

  @user1 @mobile
  Scenario: Open the collectors and come back (the return tests the cache)
    Given I wait for 5 seconds
    When I start profiling "P-HU05"
    And I tap the element with id "role_visitor"
    And I should see the element with id "album_list"
    And I mark the network checkpoint "primera-apertura"
    # The list has no test tag yet: every row shows an email, so "@" means the list is on screen
    And I tap the element with id "nav_collectors" and measure until I see a text containing "@" as "coleccionistas-primera-vez"
    And I wait for 2 seconds
    And I tap the element with id "nav_albums"
    And I should see the element with id "album_list"
    And I mark the network checkpoint "regreso"
    And I tap the element with id "nav_collectors" and measure until I see a text containing "@" as "coleccionistas-regreso"
    And I wait for 2 seconds
    Then I save the profiling
