package com.dev.wine.music.app.e2e

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dev.wine.music.app.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** HU02 as a collector against the real BackVynils: only a collector sees «Agregar track». */
@OptIn(ExperimentalTestApi::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class HU02AlbumDetailCollectorE2ETest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun enterApp() {
        composeTestRule.enterAsCollector()
    }

    @Test
    fun collectorSeesTheAddTrackButton() {
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("album_list"), timeoutMillis = TIMEOUT)
        composeTestRule.onNodeWithTag("album_list").performScrollToNode(hasText(SALSA_ALBUM))
        composeTestRule.firstNodeWithText(SALSA_ALBUM).performClick()
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("album_detail_body"), timeoutMillis = TIMEOUT)

        composeTestRule.onNodeWithTag("add_track_button").performScrollTo().assertIsDisplayed()
    }

    private companion object {
        const val TIMEOUT = 15_000L
        const val SALSA_ALBUM = "Buscando América Prueba"
    }
}
