package com.dev.wine.music.app.e2e

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.dev.wine.music.app.MainActivity
import com.dev.wine.music.app.R
import com.dev.wine.music.app.di.ApiBaseUrl
import com.dev.wine.music.app.di.ApiConfigModule
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** HU08 when the service fails: the message shows, the data stays and retrying saves the track. */
@OptIn(ExperimentalTestApi::class)
@HiltAndroidTest
@UninstallModules(ApiConfigModule::class)
@RunWith(AndroidJUnit4::class)
class HU08AddTrackErrorE2ETest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val backend = FakeBackendRule()

    @get:Rule(order = 2)
    val composeTestRule = createEmptyComposeRule()

    @BindValue
    @field:ApiBaseUrl
    @JvmField
    val baseUrl: String = backend.baseUrl

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private var scenario: ActivityScenario<MainActivity>? = null

    private val albumPath = "/albums/${FakeBackendData.ALBUM_ID}"
    private val tracksPath = "$albumPath/tracks"

    private fun openSheetAndFill() {
        backend.respondJson("/albums", FakeBackendData.albums)
        backend.respondJson(albumPath, FakeBackendData.album)
        backend.respondJson("/collectors", FakeBackendData.collectors)
        scenario = ActivityScenario.launch(MainActivity::class.java)
        composeTestRule.enterAsCollector()
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("album_item_${FakeBackendData.ALBUM_ID}"), timeoutMillis = TIMEOUT)
        composeTestRule.onNodeWithTag("album_item_${FakeBackendData.ALBUM_ID}").performClick()
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("add_track_button"), timeoutMillis = TIMEOUT)
        composeTestRule.onNodeWithTag("add_track_button").performScrollTo().performClick()
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("add_track_sheet"), timeoutMillis = TIMEOUT)
        composeTestRule.onNodeWithTag("track_name_input").performTextInput(FakeBackendData.NEW_TRACK_NAME)
        composeTestRule.onNodeWithTag("track_duration_input").performTextInput("0330")
    }

    @After
    fun closeApp() {
        scenario?.close()
    }

    @Test
    fun aServerErrorKeepsTheDataAndRetryShowsTheNewTrack() {
        backend.respondStatus(tracksPath, 500)
        openSheetAndFill()

        composeTestRule.onNodeWithTag("add_track_submit").performClick()
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("add_track_error"), timeoutMillis = TIMEOUT)
        composeTestRule.onNodeWithText(context.getString(R.string.error_loading)).assertIsDisplayed()
        composeTestRule.onNodeWithTag("track_name_input").assertTextContains(FakeBackendData.NEW_TRACK_NAME)

        backend.respondJson(tracksPath, FakeBackendData.newTrack)
        backend.respondJson(albumPath, FakeBackendData.albumWithNewTrack)
        composeTestRule.onNodeWithTag("add_track_submit").performClick()

        composeTestRule.waitUntil(TIMEOUT) {
            composeTestRule.onAllNodes(hasTestTag("add_track_sheet")).fetchSemanticsNodes().isEmpty()
        }
        composeTestRule.waitUntilAtLeastOneExists(hasText(FakeBackendData.NEW_TRACK_NAME), timeoutMillis = TIMEOUT)
    }

    @Test
    fun noConnectionShowsTheMessage() {
        backend.respondNetworkError(tracksPath)
        openSheetAndFill()

        composeTestRule.onNodeWithTag("add_track_submit").performClick()

        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("add_track_error"), timeoutMillis = TIMEOUT)
        composeTestRule.onNodeWithText(context.getString(R.string.error_network)).assertIsDisplayed()
    }

    @Test
    fun aRejectionShowsItsMessage() {
        backend.respondStatus(tracksPath, 400, "\"duration\" is required")
        openSheetAndFill()

        composeTestRule.onNodeWithTag("add_track_submit").performClick()

        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("add_track_error"), timeoutMillis = TIMEOUT)
        composeTestRule.onNodeWithText(context.getString(R.string.add_track_rejected)).assertIsDisplayed()
    }

    private companion object {
        const val TIMEOUT = 15_000L
    }
}
