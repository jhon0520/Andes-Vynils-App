package com.dev.wine.music.app.e2e

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dev.wine.music.app.MainActivity
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

/** HU02 error paths: the catalog loads, then the album detail fails on purpose. */
@OptIn(ExperimentalTestApi::class)
@HiltAndroidTest
@UninstallModules(ApiConfigModule::class)
@RunWith(AndroidJUnit4::class)
class HU02AlbumDetailErrorE2ETest {

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

    private var scenario: ActivityScenario<MainActivity>? = null

    private fun openAlbumDetail() {
        // Without an answer for /collectors the fake backend sends 404; the detail still loads.
        backend.respondJson("/albums", FakeBackendData.albums)
        scenario = ActivityScenario.launch(MainActivity::class.java)
        composeTestRule.enterAsVisitor()

        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("album_list"), timeoutMillis = TIMEOUT)
        composeTestRule.onNodeWithText(FakeBackendData.ALBUM_NAME).performClick()
    }

    @After
    fun closeApp() {
        scenario?.close()
    }

    @Test
    fun serverErrorShowsTheErrorAndRetryLoadsTheDetail() {
        backend.respondStatus(DETAIL_PATH, 500)
        openAlbumDetail()

        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("error_view"), timeoutMillis = TIMEOUT)

        backend.respondJson(DETAIL_PATH, FakeBackendData.album)
        composeTestRule.onNodeWithTag("retry_button").performClick()

        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("album_detail_body"), timeoutMillis = TIMEOUT)
        composeTestRule.onNodeWithTag("album_record_label").assertTextEquals(FakeBackendData.ALBUM_LABEL.uppercase())
    }

    @Test
    fun missingAlbumShowsNotFoundWithoutRetry() {
        backend.respondStatus(DETAIL_PATH, 404, "The album with the given id was not found")
        openAlbumDetail()

        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("not_found_view"), timeoutMillis = TIMEOUT)
        composeTestRule.onNodeWithTag("retry_button").assertDoesNotExist()
    }

    @Test
    fun albumStillOpensWhenCollectorsFail() {
        // Collectors only add review authors; the album must show without them.
        backend.respondJson(DETAIL_PATH, FakeBackendData.album)
        backend.respondStatus("/collectors", 500)
        openAlbumDetail()

        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("album_detail_body"), timeoutMillis = TIMEOUT)
        composeTestRule.onNodeWithTag("album_record_label").assertTextEquals(FakeBackendData.ALBUM_LABEL.uppercase())
    }

    private companion object {
        const val TIMEOUT = 15_000L
        const val DETAIL_PATH = "/albums/${FakeBackendData.ALBUM_ID}"
    }
}
