package com.dev.wine.music.app.ui.a11y

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.dev.wine.music.app.R
import com.dev.wine.music.app.domain.model.Album
import com.dev.wine.music.app.domain.model.AlbumGenre
import com.dev.wine.music.app.domain.model.Artist
import com.dev.wine.music.app.domain.model.ArtistType
import com.dev.wine.music.app.domain.model.Collector
import com.dev.wine.music.app.domain.model.Track
import com.dev.wine.music.app.domain.model.UserSession
import com.dev.wine.music.app.ui.album.create.AlbumCreateActions
import com.dev.wine.music.app.ui.album.create.AlbumCreateContent
import com.dev.wine.music.app.ui.album.create.AlbumCreateUiState
import com.dev.wine.music.app.ui.album.create.AlbumField
import com.dev.wine.music.app.ui.album.detail.AlbumDetailContent
import com.dev.wine.music.app.ui.album.list.AlbumCatalog
import com.dev.wine.music.app.ui.album.list.AlbumListContent
import com.dev.wine.music.app.ui.collector.CollectorListContent
import com.dev.wine.music.app.ui.collector.CollectorListUiState
import com.dev.wine.music.app.ui.common.Announcer
import com.dev.wine.music.app.ui.common.LocalAnnouncer
import com.dev.wine.music.app.ui.common.UiState
import com.dev.wine.music.app.ui.theme.WineMusicAppTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** What TalkBack hears beyond the focused element: status messages (D-18), roles (D-19) and grouping (D-20). */
@RunWith(AndroidJUnit4::class)
class TalkBackFeedbackTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val announced = mutableListOf<String>()
    private val recorder = Announcer { announced += it }

    private val siembra = Album(1, "Siembra", "", "1978-08-01T05:00:00.000Z", "", "Salsa", "Fania Records")
    private val abbey = Album(2, "Abbey Road", "", "1969-09-26T05:00:00.000Z", "", "Rock", "EMI")
    private val jaime = Collector(1, "Jaime Monsalve", "3012357936", "jmonsalve@rtvc.com.co")
    private val manolo = Collector(2, "Manolo Bellon", "3502457896", "manollo@caracol.com.co")

    /** Lets the debounce of [com.dev.wine.music.app.ui.common.AnnounceOnChange] run. */
    private fun settle() {
        composeTestRule.mainClock.advanceTimeBy(1_000)
        composeTestRule.waitForIdle()
    }

    @Test
    fun filteringTheCatalogAnnouncesHowManyAlbumsAreLeft() {
        var genre by mutableStateOf<AlbumGenre?>(null)
        composeTestRule.setContent {
            CompositionLocalProvider(LocalAnnouncer provides recorder) {
                WineMusicAppTheme {
                    val shown = listOfNotNull(siembra.takeIf { genre != AlbumGenre.ROCK }, abbey.takeIf { genre != AlbumGenre.SALSA })
                    AlbumListContent(
                        uiState = UiState.Success(AlbumCatalog(shown, totalCount = 2)),
                        query = "",
                        selectedGenre = genre,
                        onQueryChange = {},
                        onGenreSelect = {},
                        onClearFilters = {},
                        onAlbumClick = {},
                        onRetry = {},
                    )
                }
            }
        }
        settle()
        assertEquals("The catalog as it loads is not announced", emptyList<String>(), announced)

        genre = AlbumGenre.ROCK
        settle()
        assertEquals(listOf(context.resources.getQuantityString(R.plurals.results_count, 1, 1)), announced)
    }

    @Test
    fun aSearchWithoutCollectorsAnnouncesThatTheSleeveIsEmpty() {
        var state by mutableStateOf(CollectorListUiState(UiState.Success(listOf(jaime, manolo)), totalCount = 2))
        composeTestRule.setContent {
            CompositionLocalProvider(LocalAnnouncer provides recorder) {
                WineMusicAppTheme {
                    CollectorListContent(uiState = state, onQueryChange = {}, onCollectorClick = {}, onRetry = {})
                }
            }
        }
        settle()

        state = state.copy(collectors = UiState.Success(emptyList()), query = "zq")
        settle()
        val expected = context.getString(R.string.no_results_count) + ". " + context.getString(R.string.no_results_title)
        assertEquals(listOf(expected), announced)
    }

    @Test
    fun theFormNoticeIsAnnouncedWhenErrorsAppear() {
        var state by mutableStateOf(AlbumCreateUiState())
        composeTestRule.setContent {
            CompositionLocalProvider(LocalAnnouncer provides recorder) {
                WineMusicAppTheme { AlbumCreateContent(uiState = state, actions = AlbumCreateActions()) }
            }
        }
        settle()
        assertEquals(emptyList<String>(), announced)

        state = AlbumCreateUiState(
            fieldErrors = mapOf(AlbumField.NAME to R.string.album_error_required),
            touched = setOf(AlbumField.NAME),
        )
        settle()
        assertEquals(listOf(context.resources.getQuantityString(R.plurals.album_form_review, 1, 1)), announced)
    }

    private fun setAlbumDetail(album: Album) {
        composeTestRule.setContent {
            WineMusicAppTheme {
                AlbumDetailContent(uiState = UiState.Success(album), onBack = {}, onRetry = {}, session = UserSession.Visitor)
            }
        }
    }

    /** The click is on a box around the text, so the node TalkBack focuses is a button, not a text (D-19). */
    @Test
    fun thePerformerAndReadMoreAreButtons() {
        val long = "Un álbum de salsa grabado en Nueva York. ".repeat(12)
        setAlbumDetail(siembra.copy(description = long, performers = listOf(Artist(7, "Rubén Blades", "", "", ArtistType.MUSICIAN))))

        listOf("album_performer_7", "read_more_button").forEach { tag ->
            composeTestRule.onNodeWithTag(tag, useUnmergedTree = true)
                .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button))
                .assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.Text))
        }
        composeTestRule.onNodeWithTag("album_performer_7").assertTextEquals("Rubén Blades")
    }

    /** One stop per track and one for the facts line, instead of three and four (D-20). */
    @Test
    fun eachTrackAndTheFactsAreReadAsOneElement() {
        setAlbumDetail(siembra.copy(tracks = listOf(Track(1, "Plástico", "6:46"))))

        composeTestRule.onNodeWithTag("track_item_1").assertTextEquals("A1", "Plástico", "6:46")
        composeTestRule.onNodeWithTag("album_genre", useUnmergedTree = true).assertTextEquals("SALSA")
        composeTestRule.onNodeWithTag("album_track_count", useUnmergedTree = true)
            .assert(SemanticsMatcher("merged into the facts line") { it.parent?.config?.isMergingSemanticsOfDescendants == true })
    }

    /** Without a name yet, the cover preview no longer says "Portada de ." (D-20). */
    @Test
    fun theCoverPreviewWithoutANameHasItsOwnDescription() {
        composeTestRule.setContent {
            WineMusicAppTheme { AlbumCreateContent(uiState = AlbumCreateUiState(cover = "https://example.com/a.jpg"), actions = AlbumCreateActions()) }
        }

        composeTestRule.onNodeWithTag("album_cover_preview")
            .assertContentDescriptionEquals(context.getString(R.string.album_cover_preview_description))
    }
}
