package com.dev.wine.music.app.e2e

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
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
 * HU01 error paths: the real app against [FakeBackendRule], so the backend can fail on purpose.
 * Covers ViewModel → use case → repository → Retrofit → error screen → "Reintentar".
 */
@OptIn(ExperimentalTestApi::class)
@HiltAndroidTest
@UninstallModules(ApiConfigModule::class)
@RunWith(AndroidJUnit4::class)
class HU01AlbumCatalogErrorE2ETest {

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

    private fun launchApp() {
        scenario = ActivityScenario.launch(MainActivity::class.java)
        composeTestRule.enterAsVisitor()
    }

    @After
    fun closeApp() {
        scenario?.close()
    }

    @Test
    fun serverErrorShowsTheErrorAndRetryLoadsTheCatalog() {
        backend.respondStatus("/albums", 500)
        launchApp()

        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("error_view"), timeoutMillis = TIMEOUT)

        backend.respondJson("/albums", FakeBackendData.albums)
        composeTestRule.onNodeWithTag("retry_button").performClick()

        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("album_list"), timeoutMillis = TIMEOUT)
        composeTestRule.onNodeWithText(FakeBackendData.ALBUM_NAME).assertIsDisplayed()
    }

    @Test
    fun networkErrorShowsTheNoConnectionMessage() {
        backend.respondNetworkError("/albums")
        launchApp()

        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("error_view"), timeoutMillis = TIMEOUT)
        composeTestRule.onNodeWithText(context.getString(R.string.error_network)).assertIsDisplayed()
        composeTestRule.onNodeWithTag("retry_button").assertIsDisplayed()
    }

    @Test
    fun emptyCatalogShowsTheEmptyView() {
        backend.respondJson("/albums", FakeBackendData.EMPTY_LIST)
        launchApp()

        composeTestRule.waitUntilAtLeastOneExists(hasTestTag("empty_view"), timeoutMillis = TIMEOUT)
    }

    private companion object {
        const val TIMEOUT = 15_000L
    }
}
