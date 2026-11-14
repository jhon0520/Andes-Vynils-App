package com.dev.wine.music.app.domain.model

/**
 * An ISO date from the backend as dd/mm/yyyy, e.g. "01/08/1984" from "1984-08-01T05:00:00.000Z".
 * The backend sends midnight in Colombia as 05:00 UTC, so the date part is already the right day.
 */
fun isoDateToDay(isoDate: String): String {
    val parts = isoDate.take(10).split("-")
    return if (parts.size == 3) "${parts[2]}/${parts[1]}/${parts[0]}" else isoDate
}
