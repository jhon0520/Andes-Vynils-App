Feature: HU06 - See the detail of a collector

  @user1 @mobile
  Scenario: As a visitor I open a collector, see their contact, favorites, collection and reviews, and open a favorite artist
    Given I wait for 5 seconds
    # The app always opens on the role selection screen
    When I tap the element with id "role_visitor"
    When I tap the element with id "nav_collectors"
    Then I should see the element with id "collectors_list"
    # The seed collector, their favorite, album and review are found by their id
    When I scroll to the element with id "<DETAIL_COLLECTOR_ITEM_ID>" in the element with id "collectors_list"
    And I tap the element with id "<DETAIL_COLLECTOR_ITEM_ID>"
    Then I should see the element with id "collector_detail_body"
    And I should see the text "<DETAIL_COLLECTOR_NAME>"
    And I should see the element with id "collector_telephone"
    And I should see the element with id "collector_email"
    When I scroll to the element with id "<COLLECTOR_ALBUM_ID>" in the element with id "collector_detail_body"
    Then I should see the element with id "<COLLECTOR_ALBUM_ID>"
    When I scroll to the element with id "<COLLECTOR_REVIEW_ID>" in the element with id "collector_detail_body"
    Then I should see the element with id "<COLLECTOR_REVIEW_ID>"
    When I scroll to the element with id "<FAVORITE_ITEM_ID>" in the element with id "collector_detail_body"
    And I tap the element with id "<FAVORITE_ITEM_ID>"
    Then I should see the element with id "artist_detail_body"
    When I tap the element with id "back_button"
    Then I should see the element with id "collector_detail_body"
    When I tap the element with id "back_button"
    Then I should see the element with id "collectors_list"
