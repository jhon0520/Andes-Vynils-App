package com.dev.wine.music.app.e2e

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
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
import com.dev.wine.music.app.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * HU01 against the real BackVynils (http://10.0.2.2:3000/) and its seed data:
 * Rubén Blades has "Buscando América" (Salsa) and Queen has "A Night at the Opera" (Rock).
 */
@OptIn(ExperimentalTestApi::class)
@RunWith(AndroidJUnit4::class)
class HU01AlbumCatalogE2ETest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

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

        composeTestRule.waitUntilDoesNotExist(hasText(SALSA_ALBUM), timeoutMillis = TIMEOUT)
        scrollToAlbum(ROCK_ALBUM)
        composeTestRule.onNodeWithText(ROCK_ALBUM).assertIsDisplayed()
    }

    @Test
    fun searchCombinedWithGenreWithoutMatchesShowsNoResultsAndCanBeCleared() {
        waitForCatalog()

        composeTestRule.onNodeWithTag("album_search_input").performTextInput("queen")
        composeTestRule.onNodeWithTag("album_search_input").performImeAction()
        composeTestRule.onNodeWithTag("album_genre_tab_salsa").performClick()

        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("no_results_view"), timeoutMillis = TIMEOUT)
        composeTestRule.onNodeWithText(context.getString(R.string.album_no_results_query, "queen")).assertIsDisplayed()

        composeTestRule.onNodeWithTag("clear_search_button").performClick()

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
        const val ROCK_ALBUM = "A Night at the Opera"
    }
}
