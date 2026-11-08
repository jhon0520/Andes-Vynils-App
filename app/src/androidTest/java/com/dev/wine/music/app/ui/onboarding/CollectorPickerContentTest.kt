package com.dev.wine.music.app.ui.onboarding

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.dev.wine.music.app.R
import com.dev.wine.music.app.domain.model.Collector
import com.dev.wine.music.app.ui.common.UiState
import com.dev.wine.music.app.ui.theme.WineMusicAppTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CollectorPickerContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    private val manolo = Collector(100, "Manolo Bellon", "3502457896", "manollo@caracol.com.co")
    private val jaime = Collector(101, "Jaime Monsalve", "3012357936", "jmonsalve@rtvc.com.co")

    private fun setContent(
        uiState: CollectorPickerUiState,
        onCollectorSelect: (Int) -> Unit = {},
        onContinue: () -> Unit = {},
        onQueryChange: (String) -> Unit = {},
    ) {
        composeTestRule.setContent {
            WineMusicAppTheme {
                CollectorPickerContent(
                    uiState = uiState,
                    onBack = {},
                    onQueryChange = onQueryChange,
                    onCollectorSelect = onCollectorSelect,
                    onContinue = onContinue,
                    onRetry = {},
                )
            }
        }
    }

    private fun loaded(selected: Collector? = null) = CollectorPickerUiState(
        collectors = UiState.Success(listOf(jaime, manolo)),
        totalCount = 2,
        selected = selected,
    )

    @Test
    fun showsCollectorsWithInitialsAndEmail() {
        setContent(loaded())

        composeTestRule.onNodeWithText(context.getString(R.string.collector_picker_question)).assertIsDisplayed()
        composeTestRule.onNodeWithText("Manolo Bellon").assertIsDisplayed()
        composeTestRule.onNodeWithText("manollo@caracol.com.co").assertIsDisplayed()
        composeTestRule.onNodeWithText("MB").assertIsDisplayed()
    }

    @Test
    fun continueIsDisabledWithoutSelection() {
        setContent(loaded())

        composeTestRule.onNodeWithTag("collector_continue").assertIsNotEnabled()
        composeTestRule.onNodeWithTag("collector_row_100").assertIsNotSelected()
    }

    @Test
    fun selectedRowIsCheckedAndButtonNamesTheCollector() {
        var continued = false
        setContent(loaded(selected = manolo), onContinue = { continued = true })

        composeTestRule.onNodeWithTag("collector_row_100").assertIsSelected()
        composeTestRule.onNodeWithTag("collector_row_101").assertIsNotSelected()
        composeTestRule.onNodeWithTag("collector_continue")
            .assertIsEnabled()
            .assertTextEquals(context.getString(R.string.collector_continue_as, "Manolo Bellon"))
            .performClick()

        assertEquals(true, continued)
    }

    @Test
    fun tappingARowReportsItsId() {
        var selectedId: Int? = null
        setContent(loaded(), onCollectorSelect = { selectedId = it })

        composeTestRule.onNodeWithTag("collector_row_101").performClick()

        assertEquals(101, selectedId)
    }

    @Test
    fun searchWithoutMatchesShowsNoResultsAndClearResetsTheQuery() {
        var query: String? = null
        setContent(
            CollectorPickerUiState(collectors = UiState.Success(emptyList()), totalCount = 2, query = "zzz"),
            onQueryChange = { query = it },
        )

        composeTestRule.onNodeWithTag("no_results_view").assertIsDisplayed()
        composeTestRule.onNodeWithTag("clear_search_button").performClick()

        assertEquals("", query)
    }

    @Test
    fun noCollectorsShowsTheEmptyView() {
        setContent(CollectorPickerUiState(collectors = UiState.Success(emptyList()), totalCount = 0))

        composeTestRule.onNodeWithTag("empty_view").assertIsDisplayed()
    }
}
