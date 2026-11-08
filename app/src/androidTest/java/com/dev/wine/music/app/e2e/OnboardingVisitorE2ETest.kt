package com.dev.wine.music.app.e2e

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.dev.wine.music.app.MainActivity
import com.dev.wine.music.app.R
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** Onboarding as a visitor, against the real BackVynils. */
@OptIn(ExperimentalTestApi::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class OnboardingVisitorE2ETest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun appOpensOnRoleSelectionWithoutBottomBar() {
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("role_selection"), timeoutMillis = TIMEOUT)

        composeTestRule.onNodeWithText(context.getString(R.string.onboarding_tagline)).assertIsDisplayed()
        composeTestRule.onNodeWithTag("role_visitor").assertIsDisplayed()
        composeTestRule.onNodeWithTag("role_collector").assertIsDisplayed()
        composeTestRule.onNodeWithTag("nav_albums").assertDoesNotExist()
    }

    @Test
    fun visitorLandsOnTheAlbumCatalog() {
        composeTestRule.enterAsVisitor()

        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("album_list"), timeoutMillis = TIMEOUT)
        composeTestRule.onNodeWithTag("nav_albums").assertIsDisplayed()
        composeTestRule.onNodeWithTag("role_selection").assertDoesNotExist()
    }

    private companion object {
        const val TIMEOUT = 15_000L
    }
}
