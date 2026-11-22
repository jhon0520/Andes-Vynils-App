package com.dev.wine.music.app.ui.common

import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.dev.wine.music.app.R
import kotlinx.coroutines.delay

/** Speaks a status message with TalkBack without moving its focus (WCAG 4.1.3, status messages). */
fun interface Announcer {
    fun announce(message: String)
}

/** Tests provide a recording [Announcer]; the app uses the one backed by the current view. */
val LocalAnnouncer = staticCompositionLocalOf<Announcer?> { null }

@Composable
fun rememberAnnouncer(): Announcer {
    LocalAnnouncer.current?.let { return it }
    val view = LocalView.current
    return remember(view) { ViewAnnouncer(view) }
}

private class ViewAnnouncer(private val view: View) : Announcer {
    // Compose 1.7 has no API for status messages, and a live region needs a visible text that changes.
    // announceForAccessibility is deprecated on API 33+ but still delivered by TalkBack, down to API 21.
    @Suppress("DEPRECATION")
    override fun announce(message: String) = view.announceForAccessibility(message)
}

/** Wait after the last change, so typing a search announces the final result once. */
private const val ANNOUNCE_DELAY_MS = 700L

/**
 * Announces [message] when [key] changes. [key] is null while there is nothing to announce yet (loading or
 * error), and the first non-null value (the screen as it loads) is not announced either: TalkBack already
 * reads the screen. So only the changes the user causes afterwards (a filter, a search) are spoken.
 */
@Composable
fun AnnounceOnChange(message: String?, key: Any? = message) {
    val announcer = rememberAnnouncer()
    val last = remember { LastKey() }
    LaunchedEffect(key) {
        val previous = last.value
        last.value = key
        if (message == null || previous == null || previous == key) return@LaunchedEffect
        delay(ANNOUNCE_DELAY_MS)
        announcer.announce(message)
    }
}

private class LastKey(var value: Any? = null)

/** "3 resultados", or "0 resultados. Esta funda está vacía" when nothing matches. */
@Composable
fun resultsMessage(count: Int): String =
    if (count == 0) {
        stringResource(R.string.no_results_count) + ". " + stringResource(R.string.no_results_title)
    } else {
        pluralStringResource(R.plurals.results_count, count, count)
    }
