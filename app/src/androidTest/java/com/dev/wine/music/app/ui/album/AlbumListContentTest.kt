package com.dev.wine.music.app.ui.album

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.dev.wine.music.app.R
import com.dev.wine.music.app.domain.model.Album
import com.dev.wine.music.app.domain.model.AlbumGenre
import com.dev.wine.music.app.domain.model.Artist
import com.dev.wine.music.app.domain.model.ArtistType
import com.dev.wine.music.app.ui.album.list.AlbumCatalog
import com.dev.wine.music.app.ui.album.list.AlbumListContent
import com.dev.wine.music.app.ui.common.UiState
import com.dev.wine.music.app.ui.theme.WineMusicAppTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** Tests the stateless screen directly, so no Hilt or network is needed. */
@RunWith(AndroidJUnit4::class)
class AlbumListContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    private val album = Album(
        id = 7,
        name = "Buscando América",
        cover = "",
        releaseDate = "1984-08-01T00:00:00.000Z",
        description = "",
        genre = "Classical",
        recordLabel = "Elektra",
        performers = listOf(Artist(1, "Rubén Blades", "", "", ArtistType.MUSICIAN)),
    )

    private fun setContent(
        uiState: UiState<AlbumCatalog>,
        query: String = "",
        selectedGenre: AlbumGenre? = null,
        onQueryChange: (String) -> Unit = {},
        onGenreSelect: (AlbumGenre?) -> Unit = {},
        onClearFilters: () -> Unit = {},
        onAlbumClick: (Int) -> Unit = {},
        onRetry: () -> Unit = {},
    ) {
        composeTestRule.setContent {
            WineMusicAppTheme {
                AlbumListContent(
                    uiState = uiState,
                    query = query,
                    selectedGenre = selectedGenre,
                    onQueryChange = onQueryChange,
                    onGenreSelect = onGenreSelect,
                    onClearFilters = onClearFilters,
                    onAlbumClick = onAlbumClick,
                    onRetry = onRetry,
                )
            }
        }
    }

    @Test
    fun showsAlbumWithPerformerYearAndGenreAndReportsClicks() {
        var clickedId: Int? = null
        setContent(UiState.Success(AlbumCatalog(listOf(album), totalCount = 1)), onAlbumClick = { clickedId = it })

        composeTestRule.onNodeWithText("Rubén Blades").assertIsDisplayed()
        composeTestRule.onNodeWithText("1984 · ${context.getString(R.string.genre_classical)}").assertIsDisplayed()
        composeTestRule.onNodeWithTag("album_item_7").performClick()

        assertEquals(7, clickedId)
    }

    @Test
    fun albumWithoutCoverShowsTheSleeve() {
        setContent(UiState.Success(AlbumCatalog(listOf(album), totalCount = 1)))

        // The clickable card merges its children, so the sleeve lives in the unmerged tree.
        composeTestRule.onNodeWithTag("album_sleeve_7", useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    fun typingInTheSearchFieldReportsTheQuery() {
        var query by mutableStateOf("")
        composeTestRule.setContent {
            WineMusicAppTheme {
                AlbumListContent(
                    uiState = UiState.Success(AlbumCatalog(listOf(album), totalCount = 1)),
                    query = query,
                    selectedGenre = null,
                    onQueryChange = { query = it },
                    onGenreSelect = {},
                    onClearFilters = {},
                    onAlbumClick = {},
                    onRetry = {},
                )
            }
        }

        composeTestRule.onNodeWithTag("album_search_input").performTextInput("ruben")

        assertEquals("ruben", query)
    }

    @Test
    fun genreTabsShowTheSelectionAndReportClicks() {
        var selected: AlbumGenre? = null
        setContent(
            UiState.Success(AlbumCatalog(listOf(album), totalCount = 1)),
            selectedGenre = AlbumGenre.SALSA,
            onGenreSelect = { selected = it },
        )

        composeTestRule.onNodeWithTag("album_genre_tab_salsa").assertIsSelected()
        composeTestRule.onNodeWithTag("album_genre_tab_rock").performClick()

        assertEquals(AlbumGenre.ROCK, selected)
    }

    @Test
    fun searchWithoutMatchesShowsNoResultsAndClearsFilters() {
        var cleared = false
        setContent(
            UiState.Success(AlbumCatalog(emptyList(), totalCount = 1)),
            query = "Soda Stereo",
            onClearFilters = { cleared = true },
        )

        composeTestRule.onNodeWithText(context.getString(R.string.album_no_results_query, "Soda Stereo")).assertIsDisplayed()
        composeTestRule.onNodeWithTag("clear_search_button").performClick()

        assertTrue(cleared)
    }

    @Test
    fun emptyCatalogShowsEmptyView() {
        setContent(UiState.Success(AlbumCatalog(emptyList(), totalCount = 0)))

        composeTestRule.onNodeWithTag("empty_view").assertIsDisplayed()
    }

    @Test
    fun loadingStateShowsLoadingView() {
        setContent(UiState.Loading)

        composeTestRule.onNodeWithTag("loading_view").assertIsDisplayed()
    }

    @Test
    fun errorStateShowsRetryButton() {
        var retried = false
        setContent(UiState.Error(), onRetry = { retried = true })

        composeTestRule.onNodeWithText(context.getString(R.string.retry)).performClick()

        assertTrue(retried)
    }
}
