Feature: P-HU02 - Profiling of the album detail

  @user1 @mobile
  Scenario: Open the same album twice (the second time tests the cache)
    Given I wait for 5 seconds
    When I start profiling "P-HU02"
    And I tap the element with id "role_visitor"
    And I type "<ALBUM_QUERY>" into the element with id "album_search_input"
    And I hide the keyboard
    And I scroll to the element with id "<SALSA_ALBUM_ID>" in the element with id "album_list"
    And I mark the network checkpoint "primera-apertura"
    And I tap the element with id "<SALSA_ALBUM_ID>" and measure until I see the element with id "album_detail_body" as "detalle-primera-vez"
    And I wait for 2 seconds
    And I tap the element with id "read_more_button" if it is shown
    And I tap the element with id "back_button"
    And I scroll to the element with id "<SALSA_ALBUM_ID>" in the element with id "album_list"
    And I mark the network checkpoint "segunda-apertura"
    And I tap the element with id "<SALSA_ALBUM_ID>" and measure until I see the element with id "album_detail_body" as "detalle-segunda-vez"
    And I wait for 2 seconds
    Then I save the profiling
