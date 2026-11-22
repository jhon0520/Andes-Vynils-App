package com.dev.wine.music.app.e2e

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.dev.wine.music.app.MainActivity
import com.dev.wine.music.app.R
import com.dev.wine.music.app.di.ApiBaseUrl
import com.dev.wine.music.app.di.ApiConfigModule
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * HU07 when the service fails. The fake backend answers by path, so /albums is changed only after
 * the catalog loaded: from then on it is the answer to the POST.
 */
@OptIn(ExperimentalTestApi::class)
@HiltAndroidTest
@UninstallModules(ApiConfigModule::class)
@RunWith(AndroidJUnit4::class)
class HU07AlbumCreateErrorE2ETest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val backend = FakeBackendRule()

    @get:Rule(order = 2)
    val composeTestRule = createEmptyComposeRule()

    @BindValue
    @field:ApiBaseUrl
    @JvmField
    val baseUrl: String = backend.baseUrl

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private var scenario: ActivityScenario<MainActivity>? = null

    private fun openAndFillForm() {
        backend.respondJson("/albums", FakeBackendData.albums)
        backend.respondJson("/collectors", FakeBackendData.collectors)
        scenario = ActivityScenario.launch(MainActivity::class.java)
        composeTestRule.enterAsCollector()
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("album_list"), timeoutMillis = TIMEOUT)
        composeTestRule.onNodeWithTag("create_album_button").performClick()
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("album_create_form"), timeoutMillis = TIMEOUT)
        composeTestRule.fillAlbumForm(NAME)
    }

    private fun submitAndWaitForError() {
        composeTestRule.onNodeWithTag("album_create_submit").performScrollTo().performClick()
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("album_create_error"), timeoutMillis = TIMEOUT)
    }

    @After
    fun closeApp() {
        scenario?.close()
    }

    @Test
    fun aBadRequestShowsTheMessageKeepsTheDataAndRetryWorks() {
        openAndFillForm()
        backend.respondStatus("/albums", 400, "\"genre\" must be one of [Classical, Salsa, Rock, Folk]")

        submitAndWaitForError()

        composeTestRule.onNodeWithText(context.getString(R.string.album_create_rejected)).performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("album_name_input").performScrollTo().assertTextContains(NAME)

        backend.respondJson("/albums", FakeBackendData.album)
        composeTestRule.onNodeWithTag("album_create_submit").performScrollTo().performClick()
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("album_created_dialog"), timeoutMillis = TIMEOUT)
    }

    @Test
    fun aPreconditionFailureShowsTheRejection() {
        openAndFillForm()
        backend.respondStatus("/albums", 412, "Rejected")

        submitAndWaitForError()

        composeTestRule.onNodeWithText(context.getString(R.string.album_create_rejected)).performScrollTo().assertIsDisplayed()
    }

    @Test
    fun aServerErrorShowsTheGenericMessageAndKeepsTheData() {
        openAndFillForm()
        backend.respondStatus("/albums", 500)

        submitAndWaitForError()

        composeTestRule.onNodeWithText(context.getString(R.string.error_loading)).performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("album_name_input").performScrollTo().assertTextContains(NAME)
    }

    private companion object {
        const val TIMEOUT = 15_000L
        const val NAME = "E2E rejected album"
    }
}
