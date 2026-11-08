package com.dev.wine.music.app.e2e

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.dev.wine.music.app.MainActivity
import com.dev.wine.music.app.R
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** Onboarding as a collector, against the real BackVynils and its seed collector Jaime Monsalve. */
@OptIn(ExperimentalTestApi::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class OnboardingCollectorE2ETest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    private fun openCollectorPicker() {
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("role_collector"), timeoutMillis = TIMEOUT)
        composeTestRule.onNodeWithTag("role_collector").performClick()
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("collector_list"), timeoutMillis = TIMEOUT)
    }

    @Test
    fun searchSelectAndContinueAsCollector() {
        openCollectorPicker()
        composeTestRule.onNodeWithTag("collector_continue").assertIsNotEnabled()

        composeTestRule.onNodeWithTag("collector_search").performTextInput(QUERY)
        composeTestRule.waitUntilAtLeastOneExists(hasText(COLLECTOR), timeoutMillis = TIMEOUT)
        composeTestRule.onNodeWithText(COLLECTOR).performClick()

        composeTestRule.onNodeWithText(COLLECTOR).assertIsSelected()
        composeTestRule.onNodeWithTag("collector_continue")
            .assertIsEnabled()
            .assertTextEquals(context.getString(R.string.collector_continue_as, COLLECTOR))
        composeTestRule.onNodeWithTag("collector_continue").performClick()

        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("album_list"), timeoutMillis = TIMEOUT)
    }

    @Test
    fun backFromCollectorPickerReturnsToRoleSelection() {
        openCollectorPicker()

        composeTestRule.onNodeWithTag("back_button").performClick()

        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("role_selection"), timeoutMillis = TIMEOUT)
        composeTestRule.onNodeWithTag("collector_picker").assertDoesNotExist()
    }

    private companion object {
        const val TIMEOUT = 15_000L
        const val COLLECTOR = "Jaime Monsalve"
        const val QUERY = "monsalve"
    }
}
