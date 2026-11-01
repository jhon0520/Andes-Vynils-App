Feature: HU02 - Consultar la información detallada de un álbum

  @user1 @mobile
  Scenario: Como visitante abro el detalle de un álbum y veo su información
    Given I wait for 5 seconds
    When I type "<ALBUM_QUERY>" into the element with id "album_search_input"
    And I hide the keyboard
    And I scroll to the text "<SALSA_ALBUM>" in the element with id "album_list"
    And I tap the text "<SALSA_ALBUM>"
    Then I should see the element with id "album_detail_body"
    And I should see the text "<SALSA_LABEL>"
    And I should see the text "<SALSA_RELEASE_DATE>"
    When I tap the element with id "back_button"
    Then I should see the element with id "album_list"
