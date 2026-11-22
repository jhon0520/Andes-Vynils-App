package com.dev.wine.music.app.ui.album

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dev.wine.music.app.R
import com.dev.wine.music.app.domain.model.AlbumGenre
import com.dev.wine.music.app.domain.model.RecordLabel
import com.dev.wine.music.app.ui.album.create.AlbumCreateActions
import com.dev.wine.music.app.ui.album.create.AlbumCreateContent
import com.dev.wine.music.app.ui.album.create.AlbumCreateUiState
import com.dev.wine.music.app.ui.album.create.AlbumField
import com.dev.wine.music.app.ui.common.ErrorType
import com.dev.wine.music.app.ui.common.UiState
import com.dev.wine.music.app.ui.theme.WineMusicAppTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** HU07 form states, without Hilt or network. */
@RunWith(AndroidJUnit4::class)
class AlbumCreateContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val valid = AlbumCreateUiState(
        cover = "https://example.com/a.jpg",
        name = "Siembra",
        releaseDate = "01081978",
        genre = AlbumGenre.SALSA,
        recordLabel = RecordLabel.FANIA_RECORDS,
        description = "Un clásico",
    )

    private fun setContent(state: AlbumCreateUiState, actions: AlbumCreateActions = AlbumCreateActions()) {
        composeTestRule.setContent {
            WineMusicAppTheme { AlbumCreateContent(uiState = state, actions = actions) }
        }
    }

    @Test
    fun theButtonIsDisabledWhileTheFormIsInvalid() {
        setContent(AlbumCreateUiState(fieldErrors = mapOf(AlbumField.NAME to R.string.album_error_required)))

        composeTestRule.onNodeWithTag("album_create_submit").performScrollTo().assertIsNotEnabled()
    }

    @Test
    fun aValidFormCanBeSubmitted() {
        var submitted = false
        setContent(valid, AlbumCreateActions(onSubmit = { submitted = true }))

        composeTestRule.onNodeWithTag("album_create_submit").performScrollTo().assertIsEnabled().performClick()

        assertTrue(submitted)
    }

    @Test
    fun errorsShowUnderTheFieldWithANoticeAtTheTop() {
        setContent(
            AlbumCreateUiState(
                fieldErrors = mapOf(AlbumField.NAME to R.string.album_error_required, AlbumField.COVER to R.string.album_error_cover),
                touched = setOf(AlbumField.NAME, AlbumField.COVER),
            ),
        )

        composeTestRule.onNodeWithTag("album_form_notice").assertTextEquals("Revisa 2 campos.")
        composeTestRule.onNodeWithTag("album_name_input_error", useUnmergedTree = true).assertTextEquals("Este campo es obligatorio.")
    }

    @Test
    fun theCoverPreviewShowsWithAValidUrl() {
        setContent(valid)

        composeTestRule.onNodeWithTag("album_cover_preview").assertIsDisplayed()
    }

    @Test
    fun theDescriptionShowsItsCounter() {
        setContent(valid)

        composeTestRule.onNodeWithTag("album_description_input_counter", useUnmergedTree = true).assertTextEquals("10/500")
    }

    @Test
    fun whileSendingTheButtonSaysSoAndIsDisabled() {
        setContent(valid.copy(isSubmitting = true))

        composeTestRule.onNodeWithTag("album_create_submit").performScrollTo().assertIsNotEnabled()
    }

    @Test
    fun aRejectionShowsTheMessageAndKeepsTheData() {
        setContent(valid.copy(submitError = UiState.Error(type = ErrorType.VALIDATION)))

        composeTestRule.onNodeWithTag("album_create_error").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("album_create_submit").performScrollTo().assertIsEnabled()
    }

    @Test
    fun theConfirmationOffersTheThreeOptions() {
        var added = 0
        var viewed = 0
        var dismissed = false
        setContent(
            valid.copy(createdAlbumId = 104),
            AlbumCreateActions(onAddTracks = { added = it }, onViewAlbum = { viewed = it }, onNotNow = { dismissed = true }),
        )

        composeTestRule.onNodeWithTag("album_created_add_tracks").performClick()
        composeTestRule.onNodeWithTag("album_created_view").performClick()
        composeTestRule.onNodeWithTag("album_created_not_now").performClick()

        assertEquals(104, added)
        assertEquals(104, viewed)
        assertTrue(dismissed)
    }
}
