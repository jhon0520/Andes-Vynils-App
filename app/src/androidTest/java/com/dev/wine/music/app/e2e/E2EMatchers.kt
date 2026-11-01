package com.dev.wine.music.app.e2e

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
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
