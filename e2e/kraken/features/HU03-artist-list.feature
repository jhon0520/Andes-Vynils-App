Feature: HU03 - Browse the list of artists

  @user1 @mobile
  Scenario: As a visitor I see the artists and filter them by type
    Given I wait for 5 seconds
    # The app always opens on the role selection screen
    When I tap the element with id "role_visitor"
    When I tap the element with id "nav_artists"
    Then I should see the element with id "artist_list"
    # Both artists are on screen first so that their absence after filtering is meaningful
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
