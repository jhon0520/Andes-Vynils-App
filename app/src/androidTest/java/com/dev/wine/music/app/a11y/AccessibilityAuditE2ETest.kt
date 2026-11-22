package com.dev.wine.music.app.a11y

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dev.wine.music.app.MainActivity
import com.dev.wine.music.app.e2e.hasTestTagPrefix
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityCheckResult.AccessibilityCheckResultType
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Accessibility audit of the screens of HU01–HU08 and the onboarding against the real backend,
 * with the same checks as Accessibility Scanner (ATF). Only reads: HU07 and HU08 open their
 * form and sheet but send nothing. The report and screenshots stay on the device under
 * `Android/data/com.dev.wine.music.app/files/a11y/`; the test fails on any ERROR finding.
 */
@OptIn(ExperimentalTestApi::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class AccessibilityAuditE2ETest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private fun waitFor(tag: String) = composeTestRule.waitUntilAtLeastOneExists(hasTestTag(tag), timeoutMillis = TIMEOUT)

    @Test
    fun visitorScreensOfHu01ToHu06() {
        val audit = AccessibilityAudit("visitor")
        waitFor("role_visitor")
        audit.audit("00-role-selection")

        composeTestRule.onNodeWithTag("role_visitor").performClick()
        waitFor("album_list")
        audit.audit("HU01-album-list")

        composeTestRule.onNodeWithTag("album_list").performScrollToNode(hasTestTag(ALBUM_TAG))
        composeTestRule.onNodeWithTag(ALBUM_TAG).performClick()
        waitFor("album_detail_body")
        audit.audit("HU02-album-detail")
        composeTestRule.onNodeWithTag("back_button").performClick()

        waitFor("nav_artists")
        composeTestRule.onNodeWithTag("nav_artists").performClick()
        waitFor("artist_list")
        audit.audit("HU03-artist-list")
        composeTestRule.onNodeWithTag("artist_list").performScrollToNode(hasTestTag(ARTIST_TAG))
        composeTestRule.onNodeWithTag(ARTIST_TAG).performClick()
        waitFor("artist_detail_body")
        audit.audit("HU04-artist-detail")
        composeTestRule.onNodeWithTag("back_button").performClick()

        waitFor("nav_collectors")
        composeTestRule.onNodeWithTag("nav_collectors").performClick()
        waitFor("collectors_list")
        audit.audit("HU05-collector-list")
        composeTestRule.onNodeWithTag("collectors_list").performScrollToNode(hasTestTag(COLLECTOR_TAG))
        composeTestRule.onNodeWithTag(COLLECTOR_TAG).performClick()
        waitFor("collector_detail_body")
        audit.audit("HU06-collector-detail")

        assertNoErrors(audit)
    }

    @Test
    fun collectorScreensOfTheOnboardingHu07AndHu08() {
        val audit = AccessibilityAudit("collector")
        waitFor("role_collector")
        composeTestRule.onNodeWithTag("role_collector").performClick()
        composeTestRule.waitUntilAtLeastOneExists(hasTestTagPrefix("collector_row_"), timeoutMillis = TIMEOUT)
        audit.audit("00-collector-picker")

        composeTestRule.onAllNodes(hasTestTagPrefix("collector_row_")).onFirst().performClick()
        composeTestRule.onNodeWithTag("collector_continue").performClick()
        waitFor("create_album_button")
        composeTestRule.onNodeWithTag("create_album_button").performClick()
        waitFor("album_create_form")
        audit.audit("HU07-album-create")
        Espresso.pressBack()

        waitFor("album_list")
        composeTestRule.onNodeWithTag("album_list").performScrollToNode(hasTestTag(ALBUM_TAG))
        composeTestRule.onNodeWithTag(ALBUM_TAG).performClick()
        waitFor("add_track_button")
        composeTestRule.onAllNodesWithTag("add_track_button").onFirst().performScrollTo().performClick()
        waitFor("add_track_sheet")
        audit.audit("HU08-add-track")

        assertNoErrors(audit)
    }

    private fun assertNoErrors(audit: AccessibilityAudit) {
        val errors = audit.findings.filter { it.type == AccessibilityCheckResultType.ERROR }
        assertTrue("Accessibility errors:\n" + errors.joinToString("\n"), errors.isEmpty())
    }

    private companion object {
        const val TIMEOUT = 15_000L

        // BackVynils seed data: Buscando América (100), Rubén Blades (100) and Manolo Bellon (100).
        const val ALBUM_TAG = "album_item_100"
        const val ARTIST_TAG = "artist_item_100"
        const val COLLECTOR_TAG = "collector_item_100"
    }
}
