package com.dev.wine.music.app.e2e

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dev.wine.music.app.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * HU05 against the real BackVynils (api.base.url) and its seed data:
 * Manolo Bellon (manollo@caracol.com.co) and Jaime Monsalve (jmonsalve@rtvc.com.co).
 */
@OptIn(ExperimentalTestApi::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class HU05CollectorListE2ETest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun openCollectors() {
        composeTestRule.enterAsVisitor()
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("nav_collectors"), timeoutMillis = TIMEOUT)
        composeTestRule.onNodeWithTag("nav_collectors").performClick()
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("collectors_list"), timeoutMillis = TIMEOUT)
    }

    private fun scrollTo(name: String) {
        composeTestRule.onNodeWithTag("collectors_list").performScrollToNode(hasText(name))
    }

    @Test
    fun listShowsTheCollectorsWithTheirEmail() {
        scrollTo(JAIME)
        composeTestRule.onNodeWithText(JAIME).assertIsDisplayed()
        composeTestRule.onNodeWithText(JAIME_EMAIL).assertIsDisplayed()
    }

    @Test
    fun searchByNameKeepsOnlyTheMatchingCollector() {
        // Manolo is on screen first, so his absence after searching is meaningful.
        scrollTo(MANOLO)
        composeTestRule.onNodeWithText(MANOLO).assertIsDisplayed()

        composeTestRule.onNodeWithTag("collectors_search").performTextInput("monsalve")

        composeTestRule.waitUntilDoesNotExist(hasText(MANOLO), timeoutMillis = TIMEOUT)
        composeTestRule.onNodeWithText(JAIME).assertIsDisplayed()
    }

    @Test
    fun searchByEmailFindsTheCollector() {
        composeTestRule.onNodeWithTag("collectors_search").performTextInput("caracol")

        composeTestRule.waitUntilDoesNotExist(hasText(JAIME), timeoutMillis = TIMEOUT)
        composeTestRule.onNodeWithText(MANOLO).assertIsDisplayed()
    }

    @Test
    fun searchWithoutMatchesShowsTheMessageAndClearShowsEveryoneAgain() {
        composeTestRule.onNodeWithTag("collectors_search").performTextReplacement(NO_MATCH)

        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("no_results_view"), timeoutMillis = TIMEOUT)
        composeTestRule.onNodeWithTag("clear_search_button").performClick()

        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("collectors_list"), timeoutMillis = TIMEOUT)
        scrollTo(MANOLO)
        composeTestRule.onNodeWithText(MANOLO).assertIsDisplayed()
    }

    private companion object {
        const val TIMEOUT = 15_000L
        const val MANOLO = "Manolo Bellon"
        const val JAIME = "Jaime Monsalve"
        const val JAIME_EMAIL = "jmonsalve@rtvc.com.co"
        const val NO_MATCH = "zzz-e2e-no-results"
    }
}
