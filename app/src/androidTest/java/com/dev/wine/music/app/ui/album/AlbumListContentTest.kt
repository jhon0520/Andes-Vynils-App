package com.dev.wine.music.app.ui.album

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.dev.wine.music.app.R
import com.dev.wine.music.app.domain.model.Album
import com.dev.wine.music.app.domain.model.AlbumGenre
import com.dev.wine.music.app.domain.model.Artist
import com.dev.wine.music.app.domain.model.ArtistType
import com.dev.wine.music.app.domain.model.UserSession
import com.dev.wine.music.app.ui.a11y.MAX_FONT_SCALE
import com.dev.wine.music.app.ui.a11y.PhoneWithFontScale
import com.dev.wine.music.app.ui.a11y.assertFullyInside
import com.dev.wine.music.app.ui.a11y.bounds
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
        session: UserSession? = null,
        onCreateAlbumClick: () -> Unit = {},
        fontScale: Float? = null,
    ) {
        composeTestRule.setContent {
            WineMusicAppTheme {
                val content = @Composable {
                    AlbumListContent(
                        uiState = uiState,
                        query = query,
                        selectedGenre = selectedGenre,
                        onQueryChange = onQueryChange,
                        onGenreSelect = onGenreSelect,
                        onClearFilters = onClearFilters,
                        onAlbumClick = onAlbumClick,
                        onRetry = onRetry,
                        session = session,
                        onCreateAlbumClick = onCreateAlbumClick,
                    )
                }
                if (fontScale == null) content() else PhoneWithFontScale(fontScale) { content() }
            }
        }
    }

    @Test
    fun showsAlbumWithPerformerYearAndGenreAndReportsClicks() {
        var clickedId: Int? = null
        setContent(UiState.Success(AlbumCatalog(listOf(album), totalCount = 1)), onAlbumClick = { clickedId = it })

        composeTestRule.onNodeWithText("Rubén Blades").assertIsDisplayed()
        composeTestRule.onNodeWithText("1984 · ${context.getString(R.string.genre_classical).uppercase()}").assertIsDisplayed()
        composeTestRule.onNodeWithTag("album_item_7").performClick()

        assertEquals(7, clickedId)
    }

    @Test
    fun albumWithoutCoverShowsTheSleeve() {
        setContent(UiState.Success(AlbumCatalog(listOf(album), totalCount = 1)), session = UserSession.Visitor)

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

    @Test
    fun collectorSeesTheCreateAlbumButtonAndItReportsClicks() {
        var clicked = false
        setContent(
            UiState.Success(AlbumCatalog(listOf(album), totalCount = 1)),
            session = UserSession.Collector(id = 100, name = "Manolo Bellon", email = "manollo@caracol.com.co"),
            onCreateAlbumClick = { clicked = true },
        )

        composeTestRule.onNodeWithText(context.getString(R.string.create_album)).assertIsDisplayed().performClick()

        assertTrue(clicked)
    }

    @Test
    fun visitorDoesNotSeeTheCreateAlbumButton() {
        setContent(UiState.Success(AlbumCatalog(listOf(album), totalCount = 1)), session = UserSession.Visitor)

        composeTestRule.onNodeWithTag("create_album_button").assertDoesNotExist()
    }

    @Test
    fun headerShowsTheCollectorBadge() {
        setContent(
            UiState.Success(AlbumCatalog(listOf(album), totalCount = 1)),
            session = UserSession.Collector(id = 100, name = "Manolo Bellon", email = "manollo@caracol.com.co"),
        )

        composeTestRule.onNodeWithTag("profile_badge")
            .assertContentDescriptionEquals(context.getString(R.string.profile_badge_collector_description, "Manolo Bellon"))
    }

    @Test
    fun headerShowsTheVisitorBadge() {
        setContent(UiState.Success(AlbumCatalog(listOf(album), totalCount = 1)), session = UserSession.Visitor)

        composeTestRule.onNodeWithTag("profile_badge")
            .assertContentDescriptionEquals(context.getString(R.string.profile_badge_visitor_description))
    }

    private val collector = UserSession.Collector(id = 100, name = "Manolo Bellon", email = "manollo@caracol.com.co")

    private fun manyAlbums() = (1..6).map { album.copy(id = it, name = "Álbum $it") }

    /** D-17: with the font at 200 % the header scrolls away and a whole album card fits on screen. */
    @Test
    fun withTheLargestFontAWholeAlbumFitsOnScreen() {
        val albums = manyAlbums()
        setContent(UiState.Success(AlbumCatalog(albums, totalCount = albums.size)), fontScale = MAX_FONT_SCALE)

        composeTestRule.onNodeWithTag("album_list").performScrollToNode(hasTestTag("album_item_1"))
        composeTestRule.onNodeWithTag("album_item_1").assertFullyInside(composeTestRule.onNodeWithTag("album_list"))
    }

    /** D-16: at the end of the list the "Crear álbum" button does not cover the last album. */
    @Test
    fun withTheLargestFontTheCreateButtonDoesNotCoverTheLastAlbum() {
        val albums = manyAlbums()
        setContent(
            UiState.Success(AlbumCatalog(albums, totalCount = albums.size)),
            session = collector,
            fontScale = MAX_FONT_SCALE,
        )

        // Header, search and tabs are the first three items of the grid.
        composeTestRule.onNodeWithTag("album_list").performScrollToIndex(albums.size + 2)
        composeTestRule.waitForIdle()
        val lastAlbum = composeTestRule.onNodeWithTag("album_item_${albums.last().id}").bounds()
        val button = composeTestRule.onNodeWithTag("create_album_button").bounds()
        assertTrue("Last album $lastAlbum is under the button $button", lastAlbum.bottom <= button.top)
    }

    /** D-17: the profile badge grows with the font instead of cutting its initials. */
    @Test
    fun withTheLargestFontTheProfileBadgeGrowsAndStaysRound() {
        setContent(UiState.Success(AlbumCatalog(listOf(album), totalCount = 1)), session = collector, fontScale = MAX_FONT_SCALE)

        val badge = composeTestRule.onNodeWithTag("profile_badge").fetchSemanticsNode().size
        val minSize = with(composeTestRule.density) { 48.dp.roundToPx() }
        assertEquals(badge.width, badge.height)
        assertTrue("Badge ${badge.width}px should be wider than 48 dp ($minSize px)", badge.width > minSize)
    }
}
