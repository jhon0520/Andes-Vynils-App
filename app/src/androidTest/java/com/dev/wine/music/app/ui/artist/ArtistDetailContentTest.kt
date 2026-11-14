package com.dev.wine.music.app.ui.artist

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.dev.wine.music.app.R
import com.dev.wine.music.app.domain.model.Album
import com.dev.wine.music.app.domain.model.Artist
import com.dev.wine.music.app.domain.model.ArtistDetail
import com.dev.wine.music.app.domain.model.ArtistPrize
import com.dev.wine.music.app.domain.model.ArtistType
import com.dev.wine.music.app.ui.artist.detail.ArtistDetailContent
import com.dev.wine.music.app.ui.common.ErrorType
import com.dev.wine.music.app.ui.common.UiState
import com.dev.wine.music.app.ui.theme.WineMusicAppTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** HU04 screen states, without Hilt or network. */
@RunWith(AndroidJUnit4::class)
class ArtistDetailContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    private val ruben = Artist(100, "Rubén Blades Bellido de Luna", "", "Cantante panameño.", ArtistType.MUSICIAN)

    private val musician = ArtistDetail(
        artist = ruben,
        date = "1948-07-16T05:00:00.000Z",
        albums = listOf(
            Album(100, "Buscando América", "", "1984-08-01T05:00:00.000Z", "", "Salsa", "Elektra"),
        ),
        prizes = listOf(
            ArtistPrize(100, "1978-12-10T05:00:00.000Z", name = "Premio Grammy", organization = "Recording Academy"),
        ),
    )

    private val band = ArtistDetail(
        artist = Artist(101, "Queen", "", "Banda británica de rock.", ArtistType.BAND),
        date = "1970-01-01T05:00:00.000Z",
    )

    private fun setContent(
        uiState: UiState<ArtistDetail>,
        onRetry: () -> Unit = {},
        onAlbumClick: (Int) -> Unit = {},
    ) {
        composeTestRule.setContent {
            WineMusicAppTheme {
                ArtistDetailContent(uiState = uiState, onBack = {}, onRetry = onRetry, onAlbumClick = onAlbumClick)
            }
        }
    }

    @Test
    fun aMusicianShowsRoundImageBirthDateAlbumsAndPrizes() {
        setContent(UiState.Success(musician))

        composeTestRule.onNodeWithTag("artist_image_musician").assertIsDisplayed()
        composeTestRule.onNodeWithTag("artist_name").assertTextEquals("Rubén Blades Bellido de Luna")
        composeTestRule.onNodeWithTag("artist_facts").assertTextEquals("MÚSICO · NACIÓ EL 16/07/1948")
        composeTestRule.onNodeWithTag("artist_album_100").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("prize_item_100").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("RECORDING ACADEMY · 10/12/1978").assertIsDisplayed()
    }

    @Test
    fun aMusicianWithoutBandSaysSo() {
        setContent(UiState.Success(musician))

        composeTestRule.onNodeWithTag("artist_band_empty").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun aBandShowsSquareImageCreationDateAndEmptySections() {
        setContent(UiState.Success(band))

        composeTestRule.onNodeWithTag("artist_image_band").assertIsDisplayed()
        composeTestRule.onNodeWithTag("artist_facts").assertTextEquals("BANDA · CREADA EL 01/01/1970")
        composeTestRule.onNodeWithTag("artist_members_empty").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("artist_albums_empty").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("artist_prizes_empty").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun tappingAnAlbumOpensItsDetail() {
        var openedAlbum = 0
        setContent(UiState.Success(musician), onAlbumClick = { openedAlbum = it })

        composeTestRule.onNodeWithTag("artist_album_100").performScrollTo().performClick()

        assertEquals(100, openedAlbum)
    }

    @Test
    fun anErrorShowsRetry() {
        var retried = false
        setContent(UiState.Error(type = ErrorType.SERVER), onRetry = { retried = true })

        composeTestRule.onNodeWithTag("retry_button").performClick()

        assertTrue(retried)
    }

    @Test
    fun aMissingArtistShowsNotFound() {
        setContent(UiState.Error(type = ErrorType.NOT_FOUND))

        composeTestRule.onNodeWithTag("not_found_view").assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.error_not_found)).assertIsDisplayed()
    }
}
