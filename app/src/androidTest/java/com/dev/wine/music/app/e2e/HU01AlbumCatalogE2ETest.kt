package com.dev.wine.music.app.e2e

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasAnyDescendant
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.dev.wine.music.app.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import com.dev.wine.music.app.R
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * HU01 against the real BackVynils (api.base.url in gradle.properties) and its seed data:
 * Rubén Blades has "Buscando América" (Salsa) and Queen has "A Night at the Opera" (Rock).
 * The checks only ask for these albums to exist, never for counts or order, so albums created by
 * other tests do not break them.
 */
@OptIn(ExperimentalTestApi::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class HU01AlbumCatalogE2ETest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun enterApp() {
        composeTestRule.enterAsVisitor()
    }

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    private fun waitForCatalog() {
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("album_list"), timeoutMillis = TIMEOUT)
    }

    private fun scrollToAlbum(name: String) {
        composeTestRule.onNodeWithTag("album_list").performScrollToNode(hasText(name))
    }

    @Test
    fun catalogShowsTheAlbumsFromTheBackendWithTheirPerformer() {
        waitForCatalog()

        scrollToAlbum(SALSA_ALBUM)
        composeTestRule.onNodeWithText(SALSA_ALBUM).assertIsDisplayed()
        scrollToAlbum(ROCK_ALBUM)
        composeTestRule.onNodeWithText(ROCK_ALBUM).assertIsDisplayed()
    }

    @Test
    fun searchByPerformerShowsOnlyTheirAlbums() {
        waitForCatalog()

        composeTestRule.onNodeWithTag("album_search_input").performTextInput("queen")
        composeTestRule.onNodeWithTag("album_search_input").performImeAction()

        // Every card left must be by Queen; only then is the absence of the salsa album meaningful.
        composeTestRule.waitUntilEveryItemMatches(
            itemTagPrefix = "album_item_",
            matcher = hasAnyDescendant(hasText(ROCK_PERFORMER, substring = true)),
            timeoutMillis = TIMEOUT,
        )
        composeTestRule.onNodeWithText(SALSA_ALBUM).assertDoesNotExist()
        scrollToAlbum(ROCK_ALBUM)
        composeTestRule.onNodeWithText(ROCK_ALBUM).assertIsDisplayed()
    }

    @Test
    fun searchCombinedWithGenreKeepsOnlyTheAlbumsThatMatchBoth() {
        waitForCatalog()

        composeTestRule.onNodeWithTag("album_search_input").performTextInput("ruben")
        composeTestRule.onNodeWithTag("album_search_input").performImeAction()
        composeTestRule.onNodeWithTag("album_genre_tab_salsa").performClick()
        composeTestRule.onNodeWithTag("album_genre_tab_salsa").assertIsSelected()

        composeTestRule.waitUntilEveryItemMatches(
            itemTagPrefix = "album_item_",
            matcher = hasAnyDescendant(hasText(SALSA_PERFORMER, substring = true)),
            timeoutMillis = TIMEOUT,
        )
        scrollToAlbum(SALSA_ALBUM)
        composeTestRule.onNodeWithText(SALSA_ALBUM).assertIsDisplayed()
    }

    @Test
    fun searchWithoutMatchesShowsNoResultsAndCanBeCleared() {
        waitForCatalog()

        // A query no album can match, so the empty result does not depend on what the backend holds.
        composeTestRule.onNodeWithTag("album_search_input").performTextInput(NO_MATCH_QUERY)
        composeTestRule.onNodeWithTag("album_search_input").performImeAction()

        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("no_results_view"), timeoutMillis = TIMEOUT)
        composeTestRule.onNodeWithText(context.getString(R.string.album_no_results_query, NO_MATCH_QUERY)).assertIsDisplayed()

        composeTestRule.onNodeWithTag("clear_search_button").performClick()
        composeTestRule.onNodeWithTag("album_genre_tab_all").assertIsSelected()

        waitForCatalog()
        scrollToAlbum(SALSA_ALBUM)
        composeTestRule.onNodeWithText(SALSA_ALBUM).assertIsDisplayed()
    }

    @Test
    fun tappingAnAlbumOpensItsDetailAndBackReturnsToTheCatalog() {
        waitForCatalog()

        scrollToAlbum(SALSA_ALBUM)
        composeTestRule.onNodeWithText(SALSA_ALBUM).performClick()

        composeTestRule.waitUntilAtLeastOneExists(hasText(context.getString(R.string.tracks)), timeoutMillis = TIMEOUT)
        composeTestRule.onNodeWithTag("album_detail").assertIsDisplayed()

        composeTestRule.onNodeWithTag("back_button").performClick()
        waitForCatalog()
    }

    private companion object {
        const val TIMEOUT = 15_000L
        const val SALSA_ALBUM = "Buscando América"
        const val SALSA_PERFORMER = "Rubén Blades"
        const val ROCK_ALBUM = "A Night at the Opera"
        const val ROCK_PERFORMER = "Queen"
        const val NO_MATCH_QUERY = "zzz-e2e-no-results"
    }
}
