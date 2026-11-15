Feature: P-ONB - Profiling of the onboarding

  @user1 @mobile
  Scenario: Cold start and entering as a collector
    Given I wait for 5 seconds
    And I record the device information
    When I measure the cold start 5 times
    And I start profiling "P-ONB"
    And I tap the element with id "role_collector" and measure until I see the element with id "collector_list" as "lista-de-coleccionistas"
    And I type "<COLLECTOR_QUERY>" into the element with id "collector_search"
    And I hide the keyboard
    And I tap the first element with id starting with "collector_row_"
    And I tap the element with id "collector_continue" and measure until I see the element with id "album_list" as "catalogo"
    And I wait for 3 seconds
    Then I save the profiling
