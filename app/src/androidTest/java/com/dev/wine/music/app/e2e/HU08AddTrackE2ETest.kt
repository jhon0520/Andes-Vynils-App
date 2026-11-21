package com.dev.wine.music.app.e2e

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dev.wine.music.app.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** HU08 against the real backend. Each run adds a track with a unique name and checks only that one. */
@OptIn(ExperimentalTestApi::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class HU08AddTrackE2ETest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun openSheet() {
        composeTestRule.enterAsCollector()
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("album_list"), timeoutMillis = TIMEOUT)
        composeTestRule.onNodeWithTag("album_list").performScrollToNode(hasTestTag(SEED_ALBUM_TAG))
        composeTestRule.onNodeWithTag(SEED_ALBUM_TAG).performClick()
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("album_detail_body"), timeoutMillis = TIMEOUT)
        composeTestRule.onNodeWithTag("add_track_button").performScrollTo().performClick()
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("add_track_sheet"), timeoutMillis = TIMEOUT)
    }

    @Test
    fun anAddedTrackAppearsInTheTracklist() {
        val name = "E2E ${System.currentTimeMillis()}"

        composeTestRule.onNodeWithTag("track_name_input").performTextInput(name)
        composeTestRule.onNodeWithTag("track_duration_input").performTextInput("0330")
        composeTestRule.onNodeWithTag("add_track_submit").assertIsEnabled().performClick()

        composeTestRule.waitUntil(TIMEOUT) {
            composeTestRule.onAllNodes(hasTestTag("add_track_sheet")).fetchSemanticsNodes().isEmpty()
        }
        composeTestRule.waitUntilAtLeastOneExists(hasText(name), timeoutMillis = TIMEOUT)
        composeTestRule.firstNodeWithText(name).performScrollTo().assertIsDisplayed()
    }

    @Test
    fun cancelClosesTheSheetWithoutChanges() {
        val name = "E2E cancelled ${System.currentTimeMillis()}"
        composeTestRule.onNodeWithTag("track_name_input").performTextInput(name)

        composeTestRule.onNodeWithTag("add_track_cancel").performClick()

        composeTestRule.waitUntil(TIMEOUT) {
            composeTestRule.onAllNodes(hasTestTag("add_track_sheet")).fetchSemanticsNodes().isEmpty()
        }
        composeTestRule.onNodeWithTag("album_detail_body").assertIsDisplayed()
        composeTestRule.onAllNodes(hasText(name)).assertCountEquals(0)
    }

    private companion object {
        const val TIMEOUT = 15_000L
        const val SEED_ALBUM_TAG = "album_item_100"
    }
}
