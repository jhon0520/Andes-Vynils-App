package com.dev.wine.music.app.ui.artist

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.dev.wine.music.app.R
import com.dev.wine.music.app.domain.model.Artist
import com.dev.wine.music.app.domain.model.ArtistType
import com.dev.wine.music.app.ui.common.UiState
import com.dev.wine.music.app.ui.theme.WineMusicAppTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** HU03 screen states, without Hilt or network. */
@RunWith(AndroidJUnit4::class)
class ArtistListContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    private val ruben = Artist(100, "Rubén Blades Bellido de Luna", "", "", ArtistType.MUSICIAN)
    private val freddie = Artist(102, "Freddie Mercury", "", "", ArtistType.MUSICIAN, bandName = "Queen")
    private val queen = Artist(101, "Queen", "", "", ArtistType.BAND, memberIds = listOf(102))

    private fun setContent(
        uiState: UiState<ArtistCatalog>,
        selectedType: ArtistType? = null,
        onTypeSelect: (ArtistType?) -> Unit = {},
        onRetry: () -> Unit = {},
    ) {
        composeTestRule.setContent {
            WineMusicAppTheme {
                ArtistListContent(
                    uiState = uiState,
                    selectedType = selectedType,
                    onTypeSelect = onTypeSelect,
                    onRetry = onRetry,
                )
            }
        }
    }

    private fun catalog(vararg artists: Artist) = UiState.Success(ArtistCatalog(artists.toList(), totalCount = 3))

    @Test
    fun musiciansShowTheirBandOrNoBand() {
        setContent(catalog(freddie, queen, ruben))

        composeTestRule.onNodeWithText(context.getString(R.string.musician_of_band, "Queen")).assertIsDisplayed()
        composeTestRule.onNodeWithText(
            context.getString(R.string.musician_of_band, context.getString(R.string.artist_no_band)),
        ).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.band)).assertIsDisplayed()
    }

    @Test
    fun avatarShapeTellsBandsFromMusicians() {
        setContent(catalog(queen, ruben))

        composeTestRule.onNodeWithTag("artist_avatar_band", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithTag("artist_avatar_musician", useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    fun typeTabsShowTheSelectionAndReportClicks() {
        var selected: ArtistType? = ArtistType.MUSICIAN
        setContent(catalog(queen), selectedType = ArtistType.BAND, onTypeSelect = { selected = it })

        composeTestRule.onNodeWithTag("artist_type_tab_band").assertIsSelected()
        composeTestRule.onNodeWithTag("artist_type_tab_all").performClick()

        assertEquals(null, selected)
    }

    @Test
    fun filterWithoutArtistsShowsNoResultsAndShowsAllAgain() {
        var selected: ArtistType? = ArtistType.BAND
        setContent(
            UiState.Success(ArtistCatalog(emptyList(), totalCount = 1)),
            selectedType = ArtistType.BAND,
            onTypeSelect = { selected = it },
        )

        composeTestRule.onNodeWithText(context.getString(R.string.artist_no_results_type)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.artist_show_all)).performClick()

        assertEquals(null, selected)
    }

    @Test
    fun emptyListShowsEmptyView() {
        setContent(UiState.Success(ArtistCatalog(emptyList(), totalCount = 0)))

        composeTestRule.onNodeWithTag("empty_view").assertIsDisplayed()
    }

    @Test
    fun errorStateShowsRetryButton() {
        var retried = false
        setContent(UiState.Error(), onRetry = { retried = true })

        composeTestRule.onNodeWithTag("retry_button").performClick()

        assertTrue(retried)
    }

    @Test
    fun loadingStateShowsLoadingView() {
        setContent(UiState.Loading)

        composeTestRule.onNodeWithTag("loading_view").assertIsDisplayed()
    }
}
