Feature: HU01 - Tocar un álbum del catálogo abre su detalle

  @user1 @mobile
  Scenario: Como visitante abro un álbum del catálogo y vuelvo
    Given I wait for 5 seconds
    When I type "<ALBUM_QUERY>" into the element with id "album_search_input"
    And I hide the keyboard
    And I tap the text "<SALSA_ALBUM>"
    Then I should see the element with id "album_detail"
    When I tap the element with id "back_button"
    Then I should see the element with id "album_list"
