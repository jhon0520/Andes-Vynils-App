package com.dev.wine.music.app.e2e

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dev.wine.music.app.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * HU04 against the real backend: from the artist list to the detail of a musician and a band,
 * and from the discography to the album detail. The seed artists and albums are found by their
 * id (testTag), because their names differ between backends.
 */
@OptIn(ExperimentalTestApi::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class HU04ArtistDetailE2ETest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun enterApp() {
        composeTestRule.enterAsVisitor()
    }

    private fun openArtist(itemTag: String) {
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("nav_artists"), timeoutMillis = TIMEOUT)
        composeTestRule.onNodeWithTag("nav_artists").performClick()
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("artist_list"), timeoutMillis = TIMEOUT)
        composeTestRule.onNodeWithTag("artist_list").performScrollToNode(hasTestTag(itemTag))
        composeTestRule.onNodeWithTag(itemTag).performClick()
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("artist_detail_body"), timeoutMillis = TIMEOUT)
    }

    @Test
    fun musicianDetailShowsRoundImageBirthDateDiscographyAndPrizes() {
        openArtist(MUSICIAN_TAG)

        composeTestRule.onNodeWithTag("artist_image_musician").assertIsDisplayed()
        composeTestRule.onNodeWithTag("artist_facts").assertTextContains("MÚSICO", substring = true)
        composeTestRule.onNodeWithTag(MUSICIAN_ALBUM_TAG).performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag(MUSICIAN_PRIZE_TAG).performScrollTo().assertIsDisplayed()
    }

    @Test
    fun bandDetailShowsSquareImageCreationDateAndDiscography() {
        openArtist(BAND_TAG)

        composeTestRule.onNodeWithTag("artist_image_band").assertIsDisplayed()
        composeTestRule.onNodeWithTag("artist_facts").assertTextContains("BANDA", substring = true)
        composeTestRule.onNodeWithTag(BAND_ALBUM_TAG).performScrollTo().assertIsDisplayed()
    }

    @Test
    fun tappingAnAlbumOfTheDiscographyOpensItsDetailAndBackReturns() {
        openArtist(MUSICIAN_TAG)

        composeTestRule.onNodeWithTag(MUSICIAN_ALBUM_TAG).performScrollTo().performClick()
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("album_detail_body"), timeoutMillis = TIMEOUT)
        composeTestRule.onNodeWithTag("album_record_label", useUnmergedTree = true).assertTextContains("ELEKTRA")

        composeTestRule.onNodeWithTag("back_button").performClick()
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("artist_detail_body"), timeoutMillis = TIMEOUT)
        composeTestRule.onNodeWithTag("artist_image_musician").assertIsDisplayed()
    }

    @Test
    fun backFromTheDetailReturnsToTheArtistList() {
        openArtist(MUSICIAN_TAG)

        composeTestRule.onNodeWithTag("back_button").performClick()

        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("artist_list"), timeoutMillis = TIMEOUT)
    }

    private companion object {
        const val TIMEOUT = 15_000L

        // BackVynils seed data: Rubén Blades (100) with Buscando América (100) and the Grammy (100);
        // Queen (101) with A Night at the Opera (102).
        const val MUSICIAN_TAG = "artist_item_100"
        const val MUSICIAN_ALBUM_TAG = "artist_album_100"
        const val MUSICIAN_PRIZE_TAG = "prize_item_100"
        const val BAND_TAG = "artist_item_101"
        const val BAND_ALBUM_TAG = "artist_album_102"
    }
}
