package com.dev.wine.music.app.ui.collector

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
import com.dev.wine.music.app.domain.model.Collector
import com.dev.wine.music.app.domain.model.CollectorAlbum
import com.dev.wine.music.app.domain.model.CollectorAlbumStatus
import com.dev.wine.music.app.domain.model.CollectorDetail
import com.dev.wine.music.app.domain.model.CollectorReview
import com.dev.wine.music.app.ui.collector.detail.CollectorDetailContent
import com.dev.wine.music.app.ui.common.ErrorType
import com.dev.wine.music.app.ui.common.UiState
import com.dev.wine.music.app.ui.theme.WineMusicAppTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** HU06 screen states, without Hilt or network. */
@RunWith(AndroidJUnit4::class)
class CollectorDetailContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    private val buscando = Album(100, "Buscando América", "", "1984-08-01T05:00:00.000Z", "", "Salsa", "Elektra")
    private val opera = Album(102, "A Night at the Opera", "", "1975-11-21T05:00:00.000Z", "", "Rock", "EMI")

    private val manolo = CollectorDetail(
        collector = Collector(100, "Manolo Bellon", "3502457896", "manollo@caracol.com.co"),
        favoriteArtists = listOf(
            Artist(100, "Rubén Blades Bellido de Luna", "", "", ArtistType.MUSICIAN),
            Artist(101, "Queen", "", "", ArtistType.BAND),
        ),
        albums = listOf(
            CollectorAlbum(1, 35, CollectorAlbumStatus.ACTIVE, buscando),
            CollectorAlbum(2, 20, CollectorAlbumStatus.INACTIVE, opera),
        ),
        reviews = listOf(
            CollectorReview(100, "The most relevant album of Ruben Blades", 5, buscando),
            CollectorReview(101, "Without album", 3),
        ),
    )

    private val empty = CollectorDetail(collector = Collector(101, "Jaime Monsalve", "3012357936", "jmonsalve@rtvc.com.co"))

    private fun setContent(
        uiState: UiState<CollectorDetail>,
        onRetry: () -> Unit = {},
        onArtistClick: (Int, ArtistType) -> Unit = { _, _ -> },
        onAlbumClick: (Int) -> Unit = {},
    ) {
        composeTestRule.setContent {
            WineMusicAppTheme {
                CollectorDetailContent(
                    uiState = uiState,
                    onBack = {},
                    onRetry = onRetry,
                    onArtistClick = onArtistClick,
                    onAlbumClick = onAlbumClick,
                )
            }
        }
    }

    @Test
    fun showsTheNameTelephoneAndEmail() {
        setContent(UiState.Success(manolo))

        composeTestRule.onNodeWithTag("collector_name").assertTextEquals("Manolo Bellon")
        composeTestRule.onNodeWithText("3502457896").assertIsDisplayed()
        composeTestRule.onNodeWithText("manollo@caracol.com.co").assertIsDisplayed()
    }

    @Test
    fun theCollectionShowsPriceAndStatus() {
        setContent(UiState.Success(manolo))

        // The texts are merged into the clickable row: scroll to the row, then read them unmerged.
        composeTestRule.onNodeWithTag("collector_album_100").performScrollTo()
        composeTestRule.onNodeWithTag("collector_album_meta_100", useUnmergedTree = true).assertTextEquals("PRECIO: 35 · ACTIVO")
        composeTestRule.onNodeWithTag("collector_album_102").performScrollTo()
        composeTestRule.onNodeWithTag("collector_album_meta_102", useUnmergedTree = true).assertTextEquals("PRECIO: 20 · INACTIVO")
    }

    @Test
    fun reviewsShowTheirAlbumAndRating() {
        setContent(UiState.Success(manolo))

        composeTestRule.onNodeWithTag("collector_review_100").performScrollTo()
        composeTestRule.onNodeWithTag("collector_review_album_100", useUnmergedTree = true).assertTextEquals("BUSCANDO AMÉRICA")
        composeTestRule.onNodeWithText("“The most relevant album of Ruben Blades”", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(
            context.getString(R.string.review_rating_description, 5, 5),
            useUnmergedTree = true,
        ).assertExists()
        // Without album the review is not clickable, so its texts are not merged.
        composeTestRule.onNodeWithTag("collector_review_album_101").performScrollTo()
            .assertTextEquals(context.getString(R.string.collector_review_unknown_album).uppercase())
    }

    @Test
    fun tappingAFavoriteOpensTheArtistWithItsType() {
        var opened: Pair<Int, ArtistType>? = null
        setContent(UiState.Success(manolo), onArtistClick = { id, type -> opened = id to type })

        composeTestRule.onNodeWithTag("favorite_item_101").performScrollTo().performClick()

        assertEquals(101 to ArtistType.BAND, opened)
    }

    @Test
    fun tappingAnAlbumOfTheCollectionOrAReviewOpensTheAlbum() {
        val opened = mutableListOf<Int>()
        setContent(UiState.Success(manolo), onAlbumClick = { opened += it })

        composeTestRule.onNodeWithTag("collector_album_102").performScrollTo().performClick()
        composeTestRule.onNodeWithTag("collector_review_100").performScrollTo().performClick()

        assertEquals(listOf(102, 100), opened)
    }

    @Test
    fun aCollectorWithoutDataShowsTheEmptySections() {
        setContent(UiState.Success(empty))

        composeTestRule.onNodeWithTag("collector_favorites_empty").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("collector_albums_empty").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("collector_reviews_empty").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun loadingShowsTheLoadingView() {
        setContent(UiState.Loading)

        composeTestRule.onNodeWithTag("loading_view").assertIsDisplayed()
    }

    @Test
    fun anErrorShowsRetry() {
        var retried = false
        setContent(UiState.Error(type = ErrorType.SERVER), onRetry = { retried = true })

        composeTestRule.onNodeWithTag("retry_button").performClick()

        assertTrue(retried)
    }

    @Test
    fun aMissingCollectorShowsNotFound() {
        setContent(UiState.Error(type = ErrorType.NOT_FOUND))

        composeTestRule.onNodeWithTag("not_found_view").assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.error_not_found)).assertIsDisplayed()
    }
}
