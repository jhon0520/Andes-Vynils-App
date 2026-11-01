Feature: HU01 - Consultar catálogo de álbumes

  @user1 @mobile
  Scenario: Como visitante busco por intérprete, filtro por género y limpio la búsqueda
    Given I wait for 5 seconds
    Then I should see the element with id "album_list"
    When I scroll to the text "<CATALOG_ALBUM>" in the element with id "album_list"
    Then I should see the text "<CATALOG_ALBUM>"
    # El álbum de rock queda en pantalla para que su ausencia después de buscar signifique algo
    When I scroll to the text "<ROCK_ALBUM>" in the element with id "album_list"
    Then I should see the text "<ROCK_ALBUM>"
    When I type "<PERFORMER_QUERY>" into the element with id "album_search_input"
    And I hide the keyboard
    Then I should not see the text "<ROCK_ALBUM>"
    When I tap the element with id "album_genre_tab_salsa"
    Then the element with id "album_genre_tab_salsa" should be selected
    When I scroll to the text "<SALSA_ALBUM>" in the element with id "album_list"
    Then I should see the text "<SALSA_ALBUM>"
    # Una búsqueda que ningún álbum puede cumplir: el vacío no depende de lo que haya en el backend
    When I type "<NO_MATCH_QUERY>" into the element with id "album_search_input"
    And I hide the keyboard
    Then I should see the element with id "no_results_view"
    When I tap the element with id "clear_search_button"
    Then the element with id "album_genre_tab_all" should be selected
    And I should see the element with id "album_list"
    When I scroll to the text "<CATALOG_ALBUM>" in the element with id "album_list"
    Then I should see the text "<CATALOG_ALBUM>"
