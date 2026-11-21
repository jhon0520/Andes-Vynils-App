package com.dev.wine.music.app.ui.album

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dev.wine.music.app.domain.model.Album
import com.dev.wine.music.app.domain.model.Track
import com.dev.wine.music.app.ui.album.track.AddTrackContent
import com.dev.wine.music.app.ui.album.track.AddTrackUiState
import com.dev.wine.music.app.ui.common.ErrorType
import com.dev.wine.music.app.ui.common.UiState
import com.dev.wine.music.app.ui.theme.WineMusicAppTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** HU08 sheet states, without Hilt or network. */
@RunWith(AndroidJUnit4::class)
class AddTrackContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val album = Album(
        id = 100,
        name = "Buscando América",
        cover = "",
        releaseDate = "1984-08-01T05:00:00.000Z",
        description = "",
        genre = "Salsa",
        recordLabel = "Elektra",
        tracks = listOf(Track(1, "Decisiones", "5:05"), Track(2, "Desapariciones", "6:29")),
    )

    private fun setContent(state: AddTrackUiState, onSave: () -> Unit = {}, onCancel: () -> Unit = {}) {
        composeTestRule.setContent {
            WineMusicAppTheme {
                AddTrackContent(
                    album = album,
                    uiState = state,
                    onNameChange = {},
                    onDurationChange = {},
                    onNameBlur = {},
                    onDurationBlur = {},
                    onSave = onSave,
                    onCancel = onCancel,
                )
            }
        }
    }

    @Test
    fun showsTheAlbumItsTracksAndThePositionOfTheNewOne() {
        setContent(AddTrackUiState())

        composeTestRule.onNodeWithTag("add_track_album").assertTextEquals("Buscando América")
        composeTestRule.onNodeWithText("Desapariciones").assertIsDisplayed()
        composeTestRule.onNodeWithTag("add_track_position").assertTextEquals("Será el track B1")
    }

    @Test
    fun theButtonIsDisabledUntilTheDataIsValid() {
        setContent(AddTrackUiState(name = "Decisiones", duration = "0560"))

        composeTestRule.onNodeWithTag("add_track_submit").assertIsNotEnabled()
    }

    @Test
    fun validDataCanBeSaved() {
        var saved = false
        setContent(AddTrackUiState(name = "Decisiones", duration = "0505"), onSave = { saved = true })

        composeTestRule.onNodeWithTag("add_track_submit").assertIsEnabled().performClick()

        assertTrue(saved)
    }

    @Test
    fun aWrongDurationShowsItsError() {
        setContent(AddTrackUiState(name = "Decisiones", duration = "0560", durationTouched = true))

        composeTestRule.onNodeWithTag("track_duration_input_error", useUnmergedTree = true)
            .assertTextEquals("Usa el formato mm:ss, con segundos entre 00 y 59.")
    }

    @Test
    fun aServiceFailureShowsTheMessage() {
        setContent(AddTrackUiState(name = "Decisiones", duration = "0505", submitError = UiState.Error(type = ErrorType.SERVER)))

        composeTestRule.onNodeWithTag("add_track_error").assertIsDisplayed()
        composeTestRule.onNodeWithTag("add_track_submit").assertIsEnabled()
    }

    @Test
    fun cancelClosesTheSheet() {
        var cancelled = false
        setContent(AddTrackUiState(), onCancel = { cancelled = true })

        composeTestRule.onNodeWithTag("add_track_cancel").performClick()

        assertTrue(cancelled)
    }
}
