package com.dev.wine.music.app.e2e

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dev.wine.music.app.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * HU02 against the real BackVynils (http://10.0.2.2:3000/) and its seed data:
 * "Buscando América" (Elektra, 01/08/1984) opens with "Decisiones" (5:05) and a review by Manolo Bellon.
 */
@OptIn(ExperimentalTestApi::class)
@RunWith(AndroidJUnit4::class)
class HU02AlbumDetailE2ETest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private fun openSalsaAlbum() {
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("album_list"), timeoutMillis = TIMEOUT)
        composeTestRule.onNodeWithTag("album_list").performScrollToNode(hasText(SALSA_ALBUM))
        composeTestRule.onNodeWithText(SALSA_ALBUM).performClick()
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("album_detail_body"), timeoutMillis = TIMEOUT)
    }

    @Test
    fun detailShowsLabelAndReleaseDateFromTheBackend() {
        openSalsaAlbum()

        composeTestRule.onNodeWithTag("album_record_label").assertTextEquals("Elektra")
        composeTestRule.onNodeWithTag("album_release_date").assertTextEquals("01/08/1984")
        composeTestRule.onNodeWithTag("album_performers").assertIsDisplayed()
    }

    @Test
    fun tracklistShowsTheTracksWithSideAndDuration() {
        openSalsaAlbum()

        composeTestRule.onNodeWithText(FIRST_TRACK).performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("A1").assertIsDisplayed()
    }

    @Test
    fun reviewsShowTheCollectorWhoWroteThem() {
        openSalsaAlbum()

        composeTestRule.onNodeWithTag("album_reviews").performScrollTo()
        composeTestRule.onNodeWithText(REVIEW_AUTHOR).performScrollTo().assertIsDisplayed()
    }

    @Test
    fun backReturnsToTheCatalog() {
        openSalsaAlbum()

        composeTestRule.onNodeWithTag("back_button").performClick()

        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("album_list"), timeoutMillis = TIMEOUT)
    }

    private companion object {
        const val TIMEOUT = 15_000L
        const val SALSA_ALBUM = "Buscando América"
        const val FIRST_TRACK = "Decisiones"
        const val REVIEW_AUTHOR = "Manolo Bellon"
    }
}
