package com.dev.wine.music.app.e2e

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
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
 * HU06 against the real backend (api.base.url) and its seed data: Manolo Bellon (100) has
 * Rubén Blades (100) as favorite, Buscando América (100) in his collection and review 100 on it.
 * Items are found by id (testTag) and only what the seed guarantees is checked.
 */
@OptIn(ExperimentalTestApi::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class HU06CollectorDetailE2ETest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun openCollector() {
        composeTestRule.enterAsVisitor()
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("nav_collectors"), timeoutMillis = TIMEOUT)
        composeTestRule.onNodeWithTag("nav_collectors").performClick()
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("collectors_list"), timeoutMillis = TIMEOUT)
        composeTestRule.onNodeWithTag("collectors_list").performScrollToNode(hasTestTag(COLLECTOR_TAG))
        composeTestRule.onNodeWithTag(COLLECTOR_TAG).performClick()
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("collector_detail_body"), timeoutMillis = TIMEOUT)
    }

    @Test
    fun detailShowsContactFavoritesCollectionAndReviews() {
        composeTestRule.onNodeWithTag("collector_name").assertTextEquals(COLLECTOR_NAME)
        composeTestRule.onNodeWithTag("collector_telephone").assertIsDisplayed()
        composeTestRule.onNodeWithTag("collector_email").assertIsDisplayed()
        composeTestRule.onNodeWithTag(FAVORITE_TAG).performScrollTo().assertIsDisplayed()
        // The texts are merged into the clickable row: scroll to the row, then read them unmerged.
        composeTestRule.onNodeWithTag(COLLECTION_TAG).performScrollTo()
        composeTestRule.onNodeWithTag(COLLECTION_META_TAG, useUnmergedTree = true).assertTextContains("PRECIO", substring = true)
        composeTestRule.onNodeWithTag(REVIEW_TAG).performScrollTo().assertIsDisplayed()
    }

    @Test
    fun tappingAFavoriteOpensTheArtistDetailAndBackReturns() {
        composeTestRule.onNodeWithTag(FAVORITE_TAG).performScrollTo().performClick()

        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("artist_detail_body"), timeoutMillis = TIMEOUT)
        composeTestRule.onNodeWithTag("artist_image_musician").assertIsDisplayed()

        composeTestRule.onNodeWithTag("back_button").performClick()
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("collector_detail_body"), timeoutMillis = TIMEOUT)
    }

    @Test
    fun tappingAnAlbumOfTheCollectionOpensTheAlbumDetail() {
        composeTestRule.onNodeWithTag(COLLECTION_TAG).performScrollTo().performClick()

        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("album_detail_body"), timeoutMillis = TIMEOUT)
        composeTestRule.onNodeWithTag("album_record_label", useUnmergedTree = true).assertTextContains("ELEKTRA")
    }

    @Test
    fun backFromTheDetailReturnsToTheCollectorList() {
        composeTestRule.onNodeWithTag("back_button").performClick()

        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("collectors_list"), timeoutMillis = TIMEOUT)
    }

    private companion object {
        const val TIMEOUT = 15_000L
        const val COLLECTOR_TAG = "collector_item_100"
        const val COLLECTOR_NAME = "Manolo Bellon"
        const val FAVORITE_TAG = "favorite_item_100"
        const val COLLECTION_TAG = "collector_album_100"
        const val COLLECTION_META_TAG = "collector_album_meta_100"
        const val REVIEW_TAG = "collector_review_100"
    }
}
