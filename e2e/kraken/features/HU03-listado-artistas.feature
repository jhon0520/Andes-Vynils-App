Feature: HU03 - Consultar el listado de artistas

  @user1 @mobile
  Scenario: Como visitante veo los artistas y los filtro por tipo
    Given I wait for 5 seconds
    When I tap the element with id "nav_artists"
    Then I should see the element with id "artist_list"
    When I tap the element with id "artist_type_tab_band"
    Then I should see the text "<BAND>"
    And I should not see the text "<MUSICIAN>"
    When I tap the element with id "artist_type_tab_musician"
    Then I should see the text "<MUSICIAN>"
    And I should not see the text "<BAND>"
