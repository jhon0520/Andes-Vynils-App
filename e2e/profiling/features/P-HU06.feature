Feature: P-HU06 - Profiling of the collector detail

  @user1 @mobile
  Scenario: Open a collector, go through their sections, open a favorite artist and open the collector again
    # The build before the optimizations has no collector detail yet
    Given I skip this scenario on the build "antes"
    And I wait for 5 seconds
    When I start profiling "P-HU06"
    And I tap the element with id "role_visitor"
    And I should see the element with id "album_list"
    And I tap the element with id "nav_collectors"
    And I should see the element with id "collectors_list"
    And I scroll to the element with id "<DETAIL_COLLECTOR_ITEM_ID>" in the element with id "collectors_list"
    And I mark the network checkpoint "detalle-primera-vez"
    And I tap the element with id "<DETAIL_COLLECTOR_ITEM_ID>" and measure until I see the element with id "collector_detail_body" as "detalle-primera-vez"
    And I wait for 2 seconds
    And I scroll to the element with id "<COLLECTOR_ALBUM_ID>" in the element with id "collector_detail_body"
    And I scroll to the element with id "<COLLECTOR_REVIEW_ID>" in the element with id "collector_detail_body"
    And I scroll to the element with id "<FAVORITE_ITEM_ID>" in the element with id "collector_detail_body"
    And I mark the network checkpoint "artista-favorito"
    And I tap the element with id "<FAVORITE_ITEM_ID>" and measure until I see the element with id "artist_detail_body" as "artista-favorito"
    And I wait for 2 seconds
    And I tap the element with id "back_button"
    And I should see the element with id "collector_detail_body"
    And I tap the element with id "back_button"
    And I should see the element with id "collectors_list"
    And I scroll to the element with id "<DETAIL_COLLECTOR_ITEM_ID>" in the element with id "collectors_list"
    And I mark the network checkpoint "detalle-segunda-vez"
    And I tap the element with id "<DETAIL_COLLECTOR_ITEM_ID>" and measure until I see the element with id "collector_detail_body" as "detalle-segunda-vez"
    And I wait for 2 seconds
    Then I save the profiling
