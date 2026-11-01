package com.dev.wine.music.app.ui.album

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.dev.wine.music.app.R
import com.dev.wine.music.app.domain.model.Album
import com.dev.wine.music.app.domain.model.Artist
import com.dev.wine.music.app.domain.model.ArtistType
import com.dev.wine.music.app.domain.model.Comment
import com.dev.wine.music.app.domain.model.Track
import com.dev.wine.music.app.ui.album.detail.AlbumDetailContent
import com.dev.wine.music.app.ui.common.ErrorType
import com.dev.wine.music.app.ui.common.UiState
import com.dev.wine.music.app.ui.theme.WineMusicAppTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** HU02 screen states, without Hilt or network. */
@RunWith(AndroidJUnit4::class)
class AlbumDetailContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    private val album = Album(
        id = 100,
        name = "Buscando América",
        cover = "",
        releaseDate = "1984-08-01T05:00:00.000Z",
        description = "Buscando América es el primer álbum de la banda de Rubén Blades y Seis del Solar.",
        genre = "Salsa",
        recordLabel = "Elektra",
        tracks = listOf(
            Track(1, "Decisiones", "5:05"),
            Track(2, "Desapariciones", "6:29"),
            Track(3, "El padre Antonio", "7:01"),
        ),
        performers = listOf(Artist(100, "Rubén Blades", "", "", ArtistType.MUSICIAN)),
        comments = listOf(
            Comment(100, "The most relevant album of Ruben Blades", rating = 4, author = "Manolo Bellon"),
            Comment(101, "Un clásico", rating = 5),
        ),
    )

    private fun setContent(uiState: UiState<Album>, onBack: () -> Unit = {}, onRetry: () -> Unit = {}) {
        composeTestRule.setContent {
            WineMusicAppTheme {
                AlbumDetailContent(uiState = uiState, onBack = onBack, onRetry = onRetry)
            }
        }
    }

    @Test
    fun showsLabelReleaseDateGenreAndTrackCount() {
        setContent(UiState.Success(album))

        composeTestRule.onNodeWithText("Buscando América").assertIsDisplayed()
        composeTestRule.onNodeWithTag("album_performers").assertTextEquals("Rubén Blades")
        composeTestRule.onNodeWithTag("album_record_label").assertTextEquals("Elektra")
        composeTestRule.onNodeWithTag("album_release_date").assertTextEquals("01/08/1984")
        composeTestRule.onNodeWithTag("album_genre").assertTextEquals(context.getString(R.string.genre_salsa))
        composeTestRule.onNodeWithTag("album_track_count").assertTextEquals("3")
    }

    @Test
    fun tracklistNumbersTracksBySideWithTheirDuration() {
        setContent(UiState.Success(album))

        composeTestRule.onNodeWithTag("track_item_1").performScrollTo()
        composeTestRule.onNodeWithText("A1").assertIsDisplayed()
        composeTestRule.onNodeWithText("5:05").assertIsDisplayed()
        composeTestRule.onNodeWithTag("track_item_3").performScrollTo()
        composeTestRule.onNodeWithText("B1").assertIsDisplayed()
    }

    @Test
    fun albumWithoutTracksShowsEmptyTracklist() {
        setContent(UiState.Success(album.copy(tracks = emptyList())))

        composeTestRule.onNodeWithTag("album_tracks_empty").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun reviewsShowTextAuthorAndRating() {
        setContent(UiState.Success(album))

        composeTestRule.onNodeWithTag("review_item_100").performScrollTo()
        composeTestRule.onNodeWithText("The most relevant album of Ruben Blades").assertIsDisplayed()
        composeTestRule.onNodeWithText("Manolo Bellon").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(
            context.getString(R.string.review_rating_description, 4, Comment.MAX_RATING),
        ).assertIsDisplayed()
    }

    @Test
    fun reviewWithoutAuthorShowsAnonymous() {
        setContent(UiState.Success(album))

        composeTestRule.onNodeWithTag("review_item_101").performScrollTo()
        composeTestRule.onNodeWithText(context.getString(R.string.review_anonymous)).assertIsDisplayed()
    }

    @Test
    fun albumWithoutReviewsShowsEmptyReviews() {
        setContent(UiState.Success(album.copy(comments = emptyList())))

        composeTestRule.onNodeWithTag("album_reviews_empty").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun longDescriptionCanBeExpanded() {
        setContent(UiState.Success(album.copy(description = "Una descripción muy larga. ".repeat(40))))

        composeTestRule.onNodeWithText(context.getString(R.string.read_more)).performScrollTo().performClick()

        composeTestRule.onNodeWithText(context.getString(R.string.read_less)).performScrollTo().assertIsDisplayed()
    }

    @Test
    fun missingAlbumShowsNotFoundWithoutRetry() {
        var wentBack = false
        setContent(UiState.Error(type = ErrorType.NOT_FOUND), onBack = { wentBack = true })

        composeTestRule.onNodeWithTag("not_found_view").assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.error_not_found)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.retry)).assertDoesNotExist()
        composeTestRule.onNodeWithTag("back_button").performClick()

        assertTrue(wentBack)
    }

    @Test
    fun errorStateShowsRetryButton() {
        var retried = false
        setContent(UiState.Error(type = ErrorType.NETWORK), onRetry = { retried = true })

        composeTestRule.onNodeWithText(context.getString(R.string.error_network)).assertIsDisplayed()
        composeTestRule.onNodeWithTag("retry_button").performClick()

        assertTrue(retried)
    }

    @Test
    fun loadingStateShowsLoadingView() {
        setContent(UiState.Loading)

        composeTestRule.onNodeWithTag("loading_view").assertIsDisplayed()
    }
}
