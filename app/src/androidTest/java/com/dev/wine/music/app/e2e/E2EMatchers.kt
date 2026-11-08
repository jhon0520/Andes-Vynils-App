package com.dev.wine.music.app.e2e

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.junit4.ComposeTestRule

/** Matches list items by their tag prefix, for example "album_item_" for every album card. */
fun hasTestTagPrefix(prefix: String): SemanticsMatcher =
    SemanticsMatcher("TestTag starts with '$prefix'") {
        it.config.getOrNull(SemanticsProperties.TestTag)?.startsWith(prefix) == true
    }

/**
 * Waits until the list shows at least one item and every shown item matches [matcher].
 *
 * Use it to check a filter. A lazy list only composes the items on screen, so asserting that an item
 * does not exist passes even when the filter is broken and the item is just off-screen.
 */
fun ComposeTestRule.waitUntilEveryItemMatches(
    itemTagPrefix: String,
    matcher: SemanticsMatcher,
    timeoutMillis: Long,
) {
    waitUntil(timeoutMillis) {
        val items = onAllNodes(hasTestTagPrefix(itemTagPrefix), useUnmergedTree = true).fetchSemanticsNodes()
        items.isNotEmpty() && items.all { matcher.matches(it) }
    }
}

/**
 * The app always opens on "¿Cómo quieres ingresar?". Taps "Visitante" so the test lands on the albums screen.
 * Call it right after the app starts, before waiting for `album_list`.
 */
@OptIn(ExperimentalTestApi::class)
fun ComposeTestRule.enterAsVisitor(timeoutMillis: Long = 15_000L) {
    waitUntilAtLeastOneExists(hasTestTag("role_visitor"), timeoutMillis)
    onNodeWithTag("role_visitor").performClick()
}

/**
 * The first node with exactly this text. The shared backend repeats data (albums with the same
 * name, the same track or review author several times in one album), and `onNodeWithText` fails
 * when more than one node has the text. In a lazy list, scroll to the item first.
 */
fun ComposeTestRule.firstNodeWithText(text: String): SemanticsNodeInteraction =
    onAllNodesWithText(text).onFirst()

/**
 * Enters as the first collector of the picker, so the test lands on the albums screen with a collector profile.
 * Call it right after the app starts, before waiting for `album_list`.
 */
@OptIn(ExperimentalTestApi::class)
fun ComposeTestRule.enterAsCollector(timeoutMillis: Long = 15_000L) {
    waitUntilAtLeastOneExists(hasTestTag("role_collector"), timeoutMillis)
    onNodeWithTag("role_collector").performClick()
    waitUntilAtLeastOneExists(hasTestTagPrefix("collector_row_"), timeoutMillis)
    onAllNodes(hasTestTagPrefix("collector_row_")).onFirst().performClick()
    onNodeWithTag("collector_continue").performClick()
}
