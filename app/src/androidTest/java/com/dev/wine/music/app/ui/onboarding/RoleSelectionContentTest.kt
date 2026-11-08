package com.dev.wine.music.app.ui.onboarding

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.dev.wine.music.app.R
import com.dev.wine.music.app.ui.theme.WineMusicAppTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RoleSelectionContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val clicks = mutableListOf<String>()

    private fun setContent() {
        composeTestRule.setContent {
            WineMusicAppTheme {
                RoleSelectionContent(
                    onVisitorClick = { clicks += "visitor" },
                    onCollectorClick = { clicks += "collector" },
                )
            }
        }
    }

    @Test
    fun showsTaglineAndBothRoles() {
        setContent()

        composeTestRule.onNodeWithText(context.getString(R.string.onboarding_tagline)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.onboarding_how_to_enter)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.role_visitor)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.role_collector_description)).assertIsDisplayed()
    }

    @Test
    fun eachOptionReportsItsClick() {
        setContent()

        composeTestRule.onNodeWithTag("role_visitor").performClick()
        composeTestRule.onNodeWithTag("role_collector").performClick()

        assertEquals(listOf("visitor", "collector"), clicks)
    }
}
