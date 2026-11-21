package com.dev.wine.music.app.e2e

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dev.wine.music.app.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** HU07 against the real backend. Each run creates an album with a unique name and checks only that one. */
@OptIn(ExperimentalTestApi::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class HU07AlbumCreateE2ETest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private fun openForm() {
        composeTestRule.enterAsCollector()
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("create_album_button"), timeoutMillis = TIMEOUT)
        composeTestRule.onNodeWithTag("create_album_button").performClick()
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("album_create_form"), timeoutMillis = TIMEOUT)
    }

    private fun fillForm(name: String) {
        composeTestRule.fillAlbumForm(name)
        composeTestRule.onNodeWithTag("album_create_submit").performScrollTo().assertIsEnabled().performClick()
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("album_created_dialog"), timeoutMillis = TIMEOUT)
    }

    @Test
    fun theButtonStaysDisabledUntilTheFormIsComplete() {
        openForm()

        composeTestRule.onNodeWithTag("album_create_submit").performScrollTo().assertIsNotEnabled()
        composeTestRule.onNodeWithTag("album_name_input").performScrollTo().performTextInput("Incomplete")
        composeTestRule.onNodeWithTag("album_create_submit").performScrollTo().assertIsNotEnabled()
    }

    @Test
    fun aCreatedAlbumAppearsInTheCatalog() {
        val name = "E2E ${System.currentTimeMillis()}"
        openForm()
        fillForm(name)

        composeTestRule.onNodeWithTag("album_created_not_now").performClick()

        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("album_search_input"), timeoutMillis = TIMEOUT)
        composeTestRule.onNodeWithTag("album_search_input").performTextInput(name)
        composeTestRule.waitUntilAtLeastOneExists(hasText(name), timeoutMillis = TIMEOUT)
    }

    @Test
    fun viewAlbumOpensTheNewAlbumAndBackReturnsToTheCatalog() {
        val name = "E2E ${System.currentTimeMillis()}"
        openForm()
        fillForm(name)

        composeTestRule.onNodeWithTag("album_created_view").performClick()

        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("album_detail_body"), timeoutMillis = TIMEOUT)
        composeTestRule.firstNodeWithText(name).assertIsDisplayed()
        composeTestRule.onNodeWithTag("back_button").performClick()
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("album_list"), timeoutMillis = TIMEOUT)
    }

    private companion object {
        const val TIMEOUT = 15_000L
    }
}
