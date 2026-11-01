Feature: HU01 - Consultar catálogo de álbumes

  @user1 @mobile
  Scenario: Como visitante busco por intérprete, filtro por género y limpio la búsqueda
    Given I wait for 5 seconds
    Then I should see the element with id "album_list"
    And I should see the text "<FIRST_ALBUM>"
    When I type "<PERFORMER_QUERY>" into the element with id "album_search_input"
    And I hide the keyboard
    Then I should see the text "<SALSA_ALBUM>"
    And I should not see the text "<ROCK_ALBUM>"
    When I tap the element with id "album_genre_tab_rock"
    Then I should see the element with id "no_results_view"
    When I tap the element with id "clear_search_button"
    Then I should see the element with id "album_list"
    And I should see the text "<FIRST_ALBUM>"
