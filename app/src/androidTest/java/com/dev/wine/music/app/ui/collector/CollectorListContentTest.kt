package com.dev.wine.music.app.ui.collector

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.dev.wine.music.app.R
import com.dev.wine.music.app.domain.model.Collector
import com.dev.wine.music.app.ui.common.ErrorType
import com.dev.wine.music.app.ui.common.UiState
import com.dev.wine.music.app.ui.theme.WineMusicAppTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** HU05 screen states, without Hilt or network. */
@RunWith(AndroidJUnit4::class)
class CollectorListContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    private val manolo = Collector(100, "Manolo Bellon", "3502457896", "manollo@caracol.com.co")
    private val jaime = Collector(101, "Jaime Monsalve", "3012357936", "jmonsalve@rtvc.com.co")

    private fun setContent(
        uiState: CollectorListUiState,
        onQueryChange: (String) -> Unit = {},
        onCollectorClick: (Int) -> Unit = {},
        onRetry: () -> Unit = {},
    ) {
        composeTestRule.setContent {
            WineMusicAppTheme {
                CollectorListContent(
                    uiState = uiState,
                    onQueryChange = onQueryChange,
                    onCollectorClick = onCollectorClick,
                    onRetry = onRetry,
                )
            }
        }
    }

    private fun loaded(shown: List<Collector> = listOf(jaime, manolo), query: String = "") =
        CollectorListUiState(collectors = UiState.Success(shown), totalCount = 2, query = query)

    @Test
    fun showsEachCollectorWithInitialsNameAndEmail() {
        setContent(loaded())

        composeTestRule.onNodeWithText(context.getString(R.string.collectors)).assertIsDisplayed()
        composeTestRule.onNodeWithText("Manolo Bellon").assertIsDisplayed()
        composeTestRule.onNodeWithText("manollo@caracol.com.co").assertIsDisplayed()
        composeTestRule.onNodeWithText("MB").assertIsDisplayed()
        composeTestRule.onNodeWithText("Jaime Monsalve").assertIsDisplayed()
        composeTestRule.onNodeWithText("JM").assertIsDisplayed()
    }

    @Test
    fun typingInTheSearchSendsTheQuery() {
        var query = ""
        setContent(loaded(), onQueryChange = { query = it })

        composeTestRule.onNodeWithTag("collectors_search").performTextInput("monsalve")

        assertEquals("monsalve", query)
    }

    @Test
    fun aSearchWithoutResultsShowsTheMessageAndClearResetsIt() {
        var query = "zzz"
        setContent(loaded(shown = emptyList(), query = "zzz"), onQueryChange = { query = it })

        composeTestRule.onNodeWithTag("no_results_view").assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.collector_no_results_query, "zzz")).assertIsDisplayed()

        composeTestRule.onNodeWithTag("clear_search_button").performClick()

        assertEquals("", query)
    }

    @Test
    fun theClearIconOfTheSearchResetsTheQuery() {
        var query = "monsalve"
        setContent(loaded(shown = listOf(jaime), query = "monsalve"), onQueryChange = { query = it })

        composeTestRule.onNodeWithTag("collectors_search_clear_button").performClick()

        assertEquals("", query)
    }

    @Test
    fun tappingACollectorSendsItsId() {
        var clicked: Int? = null
        setContent(loaded(), onCollectorClick = { clicked = it })

        composeTestRule.onNodeWithTag("collector_item_101").performClick()

        assertEquals(101, clicked)
    }

    @Test
    fun loadingShowsTheLoadingView() {
        setContent(CollectorListUiState())

        composeTestRule.onNodeWithTag("loading_view").assertIsDisplayed()
    }

    @Test
    fun anErrorShowsRetry() {
        var retried = false
        setContent(CollectorListUiState(collectors = UiState.Error(type = ErrorType.NETWORK)), onRetry = { retried = true })

        composeTestRule.onNodeWithText(context.getString(R.string.error_network)).assertIsDisplayed()
        composeTestRule.onNodeWithTag("retry_button").performClick()

        assertTrue(retried)
    }

    @Test
    fun noCollectorsShowsTheEmptyView() {
        setContent(CollectorListUiState(collectors = UiState.Success(emptyList()), totalCount = 0))

        composeTestRule.onNodeWithTag("empty_view").assertIsDisplayed()
    }
}
