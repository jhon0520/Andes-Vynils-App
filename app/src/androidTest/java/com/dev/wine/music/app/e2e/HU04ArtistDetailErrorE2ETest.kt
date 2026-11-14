package com.dev.wine.music.app.e2e

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
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

/**
 * HU04 paths that need exact backend answers: no connection with retry, a missing artist,
 * prize names crossed from GET /performerprizes, and the empty sections.
 */
@OptIn(ExperimentalTestApi::class)
@HiltAndroidTest
@UninstallModules(ApiConfigModule::class)
@RunWith(AndroidJUnit4::class)
class HU04ArtistDetailErrorE2ETest {

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

    private val musicianPath = "/musicians/${FakeBackendData.MUSICIAN_ID}"
    private val bandPath = "/bands/${FakeBackendData.BAND_ID}"

    private fun openArtist(id: Int) {
        backend.respondJson("/albums", FakeBackendData.EMPTY_LIST)
        backend.respondJson("/musicians", FakeBackendData.musicians)
        backend.respondJson("/bands", FakeBackendData.bands)
        backend.respondJson("/performerprizes", FakeBackendData.performerPrizes)
        scenario = ActivityScenario.launch(MainActivity::class.java)
        composeTestRule.enterAsVisitor()

        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("nav_artists"), timeoutMillis = TIMEOUT)
        composeTestRule.onNodeWithTag("nav_artists").performClick()
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("artist_list"), timeoutMillis = TIMEOUT)
        composeTestRule.onNodeWithTag("artist_list").performScrollToNode(hasTestTag("artist_item_$id"))
        composeTestRule.onNodeWithTag("artist_item_$id").performClick()
    }

    @After
    fun closeApp() {
        scenario?.close()
    }

    @Test
    fun networkErrorShowsTheNoConnectionMessageAndRetryLoadsTheArtist() {
        backend.respondNetworkError(musicianPath)
        openArtist(FakeBackendData.MUSICIAN_ID)

        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("error_view"), timeoutMillis = TIMEOUT)
        composeTestRule.onNodeWithText(context.getString(R.string.error_network)).assertIsDisplayed()

        backend.respondJson(musicianPath, FakeBackendData.musicianDetail)
        composeTestRule.onNodeWithTag("retry_button").performClick()

        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("artist_detail_body"), timeoutMillis = TIMEOUT)
        composeTestRule.onNodeWithText(FakeBackendData.MUSICIAN_NAME).assertIsDisplayed()
    }

    @Test
    fun aMissingArtistShowsNotFoundWithoutRetry() {
        backend.respondStatus(musicianPath, 404, "The musician with the given id was not found")
        openArtist(FakeBackendData.MUSICIAN_ID)

        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("not_found_view"), timeoutMillis = TIMEOUT)
        composeTestRule.onNodeWithTag("retry_button").assertDoesNotExist()
    }

    @Test
    fun prizesShowTheNameFromThePerformerPrizesAndTheMusicianWithoutBandSaysSo() {
        backend.respondJson(musicianPath, FakeBackendData.musicianDetail)
        openArtist(FakeBackendData.MUSICIAN_ID)

        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("artist_detail_body"), timeoutMillis = TIMEOUT)
        composeTestRule.onNodeWithTag("prize_item_${FakeBackendData.PRIZE_ID}").performScrollTo()
        composeTestRule.onNodeWithText(FakeBackendData.PRIZE_NAME).assertIsDisplayed()
        // The fake band has no members, so the musician belongs to no band.
        composeTestRule.onNodeWithTag("artist_band_empty").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun aBandWithoutMembersAlbumsOrPrizesShowsTheEmptySections() {
        backend.respondJson(bandPath, FakeBackendData.emptyBandDetail)
        openArtist(FakeBackendData.BAND_ID)

        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("artist_detail_body"), timeoutMillis = TIMEOUT)
        composeTestRule.onNodeWithTag("artist_members_empty").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("artist_albums_empty").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("artist_prizes_empty").performScrollTo().assertIsDisplayed()
    }

    private companion object {
        const val TIMEOUT = 15_000L
    }
}
