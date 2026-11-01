package com.dev.wine.music.app.e2e

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dev.wine.music.app.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * HU03 against the real BackVynils (http://10.0.2.2:3000/) and its seed data:
 * Rubén Blades is a musician and Queen is a band.
 */
@OptIn(ExperimentalTestApi::class)
@RunWith(AndroidJUnit4::class)
class HU03ArtistListE2ETest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private fun openArtists() {
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("nav_artists"), timeoutMillis = TIMEOUT)
        composeTestRule.onNodeWithTag("nav_artists").performClick()
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("artist_list"), timeoutMillis = TIMEOUT)
    }

    private fun scrollToArtist(name: String) {
        composeTestRule.onNodeWithTag("artist_list").performScrollToNode(hasText(name))
    }

    @Test
    fun listShowsMusiciansAndBandsTogether() {
        openArtists()

        scrollToArtist(MUSICIAN)
        composeTestRule.onNodeWithText(MUSICIAN).assertIsDisplayed()
        scrollToArtist(BAND)
        composeTestRule.onNodeWithText(BAND).assertIsDisplayed()
    }

    @Test
    fun bandFilterShowsOnlyBands() {
        openArtists()

        composeTestRule.onNodeWithTag("artist_type_tab_band").performClick()

        composeTestRule.waitUntilDoesNotExist(hasText(MUSICIAN), timeoutMillis = TIMEOUT)
        scrollToArtist(BAND)
        composeTestRule.onNodeWithText(BAND).assertIsDisplayed()
    }

    @Test
    fun musicianFilterShowsOnlyMusiciansAndAllShowsEveryoneAgain() {
        openArtists()

        composeTestRule.onNodeWithTag("artist_type_tab_musician").performClick()
        composeTestRule.waitUntilDoesNotExist(hasText(BAND), timeoutMillis = TIMEOUT)
        scrollToArtist(MUSICIAN)
        composeTestRule.onNodeWithText(MUSICIAN).assertIsDisplayed()

        composeTestRule.onNodeWithTag("artist_type_tab_all").performClick()
        scrollToArtist(BAND)
        composeTestRule.onNodeWithText(BAND).assertIsDisplayed()
    }

    private companion object {
        const val TIMEOUT = 15_000L
        const val MUSICIAN = "Rubén Blades Bellido de Luna"
        const val BAND = "Queen"
    }
}
