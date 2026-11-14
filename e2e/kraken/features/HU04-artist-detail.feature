Feature: HU04 - See the detail of an artist

  @user1 @mobile
  Scenario: As a visitor I open a musician from the artist list and then one of their albums
    Given I wait for 5 seconds
    # The app always opens on the role selection screen
    When I tap the element with id "role_visitor"
    When I tap the element with id "nav_artists"
    Then I should see the element with id "artist_list"
    # The seed musician and album are found by their id: their names differ between backends
    When I scroll to the element with id "<MUSICIAN_ITEM_ID>" in the element with id "artist_list"
    And I tap the element with id "<MUSICIAN_ITEM_ID>"
    Then I should see the element with id "artist_detail_body"
    And I should see the element with id "artist_image_musician"
    When I scroll to the element with id "<MUSICIAN_ALBUM_ID>" in the element with id "artist_detail_body"
    And I tap the element with id "<MUSICIAN_ALBUM_ID>"
    Then I should see the element with id "album_detail_body"
    And I should see the text "<SALSA_LABEL>"
    When I tap the element with id "back_button"
    Then I should see the element with id "artist_detail_body"
