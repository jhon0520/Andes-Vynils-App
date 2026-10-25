package com.dev.wine.music.app.ui.album

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dev.wine.music.app.domain.model.Album
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

    private val album = Album(
        id = 7,
        name = "Buscando América",
        cover = "",
        releaseDate = "1984-08-01T00:00:00.000Z",
        description = "",
        genre = "Salsa",
        recordLabel = "Elektra",
    )

    @Test
    fun showsAlbumsAndReportsClicks() {
        var clickedId: Int? = null
        composeTestRule.setContent {
            WineMusicAppTheme {
                AlbumListContent(
                    uiState = UiState.Success(listOf(album)),
                    onAlbumClick = { clickedId = it },
                    onRetry = {},
                )
            }
        }

        composeTestRule.onNodeWithText("Buscando América").assertIsDisplayed().performClick()

        assertEquals(7, clickedId)
    }

    @Test
    fun errorStateShowsRetryButton() {
        var retried = false
        composeTestRule.setContent {
            WineMusicAppTheme {
                AlbumListContent(
                    uiState = UiState.Error(),
                    onAlbumClick = {},
                    onRetry = { retried = true },
                )
            }
        }

        composeTestRule.onNodeWithText("Retry").performClick()

        assertTrue(retried)
    }
}
