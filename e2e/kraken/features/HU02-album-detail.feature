Feature: HU02 - View the detailed information of an album

  @user1 @mobile
  Scenario: As a visitor I open the detail of an album and see its information
    Given I wait for 5 seconds
    # The app always opens on the role selection screen
    When I tap the element with id "role_visitor"
    When I type "<ALBUM_QUERY>" into the element with id "album_search_input"
    And I hide the keyboard
    And I scroll to the text "<SALSA_ALBUM>" in the element with id "album_list"
    And I tap the text "<SALSA_ALBUM>"
    Then I should see the element with id "album_detail_body"
    And I should see the text "<SALSA_LABEL>"
    And I should see the text "<SALSA_RELEASE_DATE>"
    When I tap the element with id "back_button"
    Then I should see the element with id "album_list"
