package com.dev.wine.music.app.ui.collector

import com.dev.wine.music.app.domain.model.Collector
import java.text.Normalizer

// Search and initials shared by the collector list (HU05) and "¿Quién eres?" of the onboarding.

private val COMBINING_MARKS = Regex("\\p{Mn}+")

/** Compiled once: a Regex built inside collectorInitials would be compiled on every row. */
private val WHITESPACE = Regex("\\s+")

/** Lowercase without accents, so "bellon" finds "Bellón". */
internal fun String.normalizedForSearch(): String =
    Normalizer.normalize(this, Normalizer.Form.NFD).replace(COMBINING_MARKS, "").lowercase().trim()

/** Collectors whose name or email contains [query], ignoring case and accents. A blank query keeps everyone. */
internal fun List<Collector>.filterBy(query: String): List<Collector> {
    val needle = query.normalizedForSearch()
    if (needle.isEmpty()) return this
    return filter { it.name.normalizedForSearch().contains(needle) || it.email.normalizedForSearch().contains(needle) }
}

/** "Manolo Bellón" → "MB". A collector without a name shows "··". */
internal fun collectorInitials(name: String): String {
    val initials = name.trim().split(WHITESPACE)
        .filter { it.isNotEmpty() }
        .take(2)
        .joinToString("") { it.first().uppercase() }
    return initials.ifEmpty { "··" }
}
