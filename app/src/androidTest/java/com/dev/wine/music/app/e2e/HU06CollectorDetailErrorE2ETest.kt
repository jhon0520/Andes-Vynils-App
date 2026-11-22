package com.dev.wine.music.app.e2e

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
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
 * HU06 paths that need exact backend answers: no connection with retry, a missing collector,
 * the album of a review crossed from GET /albums, and the empty sections.
 */
@OptIn(ExperimentalTestApi::class)
@HiltAndroidTest
@UninstallModules(ApiConfigModule::class)
@RunWith(AndroidJUnit4::class)
class HU06CollectorDetailErrorE2ETest {

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

    private val collectorPath = "/collectors/${FakeBackendData.COLLECTOR_ID}"
    private val collectionPath = "$collectorPath/albums"

    private fun openCollector() {
        backend.respondJson("/albums", FakeBackendData.albumsWithReview)
        backend.respondJson("/collectors", FakeBackendData.collectors)
        scenario = ActivityScenario.launch(MainActivity::class.java)
        composeTestRule.enterAsVisitor()

        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("nav_collectors"), timeoutMillis = TIMEOUT)
        composeTestRule.onNodeWithTag("nav_collectors").performClick()
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("collectors_list"), timeoutMillis = TIMEOUT)
        val item = "collector_item_${FakeBackendData.COLLECTOR_ID}"
        composeTestRule.onNodeWithTag("collectors_list").performScrollToNode(hasTestTag(item))
        composeTestRule.onNodeWithTag(item).performClick()
    }

    @After
    fun closeApp() {
        scenario?.close()
    }

    @Test
    fun networkErrorShowsTheNoConnectionMessageAndRetryLoadsTheCollector() {
        backend.respondNetworkError(collectorPath)
        backend.respondJson(collectionPath, FakeBackendData.collectorAlbums)
        openCollector()

        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("error_view"), timeoutMillis = TIMEOUT)
        composeTestRule.onNodeWithText(context.getString(R.string.error_network)).assertIsDisplayed()

        backend.respondJson(collectorPath, FakeBackendData.collectorDetail)
        composeTestRule.onNodeWithTag("retry_button").performClick()

        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("collector_detail_body"), timeoutMillis = TIMEOUT)
        composeTestRule.onNodeWithTag("collector_name").assertTextEquals(FakeBackendData.COLLECTOR_NAME)
    }

    @Test
    fun aMissingCollectorShowsNotFoundWithoutRetry() {
        backend.respondStatus(collectorPath, 404, "The collector with the given id was not found")
        backend.respondStatus(collectionPath, 404, "The collector with the given id was not found")
        openCollector()

        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("not_found_view"), timeoutMillis = TIMEOUT)
        composeTestRule.onNodeWithTag("retry_button").assertDoesNotExist()
    }

    @Test
    fun theReviewShowsTheAlbumFoundInTheCatalogAndTheCollectionItsPriceAndStatus() {
        backend.respondJson(collectorPath, FakeBackendData.collectorDetail)
        backend.respondJson(collectionPath, FakeBackendData.collectorAlbums)
        openCollector()

        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("collector_detail_body"), timeoutMillis = TIMEOUT)
        // The texts are merged into the clickable rows: scroll to the row, then read them unmerged.
        composeTestRule.onNodeWithTag("collector_album_${FakeBackendData.ALBUM_ID}").performScrollTo()
        composeTestRule.onNodeWithTag("collector_album_meta_${FakeBackendData.ALBUM_ID}", useUnmergedTree = true)
            .assertTextEquals("PRECIO: 35 · ACTIVO")
        composeTestRule.onNodeWithTag("collector_review_${FakeBackendData.REVIEW_ID}").performScrollTo()
        composeTestRule.onNodeWithTag("collector_review_album_${FakeBackendData.REVIEW_ID}", useUnmergedTree = true)
            .assertTextEquals(FakeBackendData.ALBUM_NAME.uppercase())
    }

    @Test
    fun aCollectorWithoutFavoritesCollectionOrReviewsShowsTheEmptySections() {
        backend.respondJson(collectorPath, FakeBackendData.emptyCollectorDetail)
        backend.respondJson(collectionPath, FakeBackendData.EMPTY_LIST)
        openCollector()

        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("collector_detail_body"), timeoutMillis = TIMEOUT)
        composeTestRule.onNodeWithTag("collector_favorites_empty").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("collector_albums_empty").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("collector_reviews_empty").performScrollTo().assertIsDisplayed()
    }

    private companion object {
        const val TIMEOUT = 15_000L
    }
}
