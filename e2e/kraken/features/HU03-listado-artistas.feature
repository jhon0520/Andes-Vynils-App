Feature: HU03 - Consultar el listado de artistas

  @user1 @mobile
  Scenario: Como visitante veo los artistas y los filtro por tipo
    Given I wait for 5 seconds
    When I tap the element with id "nav_artists"
    Then I should see the element with id "artist_list"
    # Los dos artistas quedan en pantalla para que su ausencia al filtrar signifique algo
    When I scroll to the text "<MUSICIAN>" in the element with id "artist_list"
    Then I should see the text "<MUSICIAN>"
    When I scroll to the text "<BAND>" in the element with id "artist_list"
    Then I should see the text "<BAND>"
    When I tap the element with id "artist_type_tab_band"
    Then the element with id "artist_type_tab_band" should be selected
    And I should see the text "<BAND>"
    And I should not see the text "<MUSICIAN>"
    And I should not see the element with id "artist_avatar_musician"
    When I tap the element with id "artist_type_tab_musician"
    Then the element with id "artist_type_tab_musician" should be selected
    And I should see the text "<MUSICIAN>"
    And I should not see the text "<BAND>"
    And I should not see the element with id "artist_avatar_band"
