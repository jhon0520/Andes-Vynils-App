package com.dev.wine.music.app.e2e

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
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

/** HU03 error paths: artists come from two endpoints, and either one failing is an error. */
@OptIn(ExperimentalTestApi::class)
@HiltAndroidTest
@UninstallModules(ApiConfigModule::class)
@RunWith(AndroidJUnit4::class)
class HU03ArtistListErrorE2ETest {

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

    private fun openArtists() {
        backend.respondJson("/albums", FakeBackendData.EMPTY_LIST)
        scenario = ActivityScenario.launch(MainActivity::class.java)
        composeTestRule.enterAsVisitor()

        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("nav_artists"), timeoutMillis = TIMEOUT)
        composeTestRule.onNodeWithTag("nav_artists").performClick()
    }

    @After
    fun closeApp() {
        scenario?.close()
    }

    @Test
    fun networkErrorShowsTheNoConnectionMessageAndRetryLoadsTheArtists() {
        backend.respondNetworkError("/musicians")
        backend.respondJson("/bands", FakeBackendData.bands)
        openArtists()

        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("error_view"), timeoutMillis = TIMEOUT)
        composeTestRule.onNodeWithText(context.getString(R.string.error_network)).assertIsDisplayed()

        backend.respondJson("/musicians", FakeBackendData.musicians)
        composeTestRule.onNodeWithTag("retry_button").performClick()

        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("artist_list"), timeoutMillis = TIMEOUT)
        composeTestRule.onNodeWithTag("artist_list").performScrollToNode(hasText(FakeBackendData.MUSICIAN_NAME))
        composeTestRule.onNodeWithText(FakeBackendData.MUSICIAN_NAME).assertIsDisplayed()
        composeTestRule.onNodeWithTag("artist_list").performScrollToNode(hasText(FakeBackendData.BAND_NAME))
        composeTestRule.onNodeWithText(FakeBackendData.BAND_NAME).assertIsDisplayed()
    }

    @Test
    fun noArtistsShowsTheEmptyView() {
        backend.respondJson("/musicians", FakeBackendData.EMPTY_LIST)
        backend.respondJson("/bands", FakeBackendData.EMPTY_LIST)
        openArtists()

        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("empty_view"), timeoutMillis = TIMEOUT)
    }

    private companion object {
        const val TIMEOUT = 15_000L
    }
}
