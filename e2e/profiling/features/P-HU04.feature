Feature: P-HU04 - Profiling of the artist detail

  @user1 @mobile
  Scenario: Open a musician, one of their albums and then a band
    # The build before the optimizations has no artist detail yet
    Given I skip this scenario on the build "antes"
    And I wait for 5 seconds
    When I start profiling "P-HU04"
    And I tap the element with id "role_visitor"
    And I tap the element with id "nav_artists"
    And I should see the element with id "artist_list"
    And I scroll to the element with id "<MUSICIAN_ITEM_ID>" in the element with id "artist_list"
    And I mark the network checkpoint "detalle-musico"
    And I tap the element with id "<MUSICIAN_ITEM_ID>" and measure until I see the element with id "artist_detail_body" as "detalle-musico"
    And I wait for 2 seconds
    And I scroll to the element with id "<MUSICIAN_ALBUM_ID>" in the element with id "artist_detail_body"
    And I mark the network checkpoint "album-desde-artista"
    And I tap the element with id "<MUSICIAN_ALBUM_ID>" and measure until I see the element with id "album_detail_body" as "album-desde-artista"
    And I wait for 2 seconds
    And I tap the element with id "back_button"
    And I should see the element with id "artist_detail_body"
    And I tap the element with id "back_button"
    And I tap the element with id "artist_type_tab_band"
    And I mark the network checkpoint "detalle-banda"
    And I tap the first element with id starting with "artist_item_" and measure until I see the element with id "artist_detail_body" as "detalle-banda"
    And I wait for 2 seconds
    Then I save the profiling
