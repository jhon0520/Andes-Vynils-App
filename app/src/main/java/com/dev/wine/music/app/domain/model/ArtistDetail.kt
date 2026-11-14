package com.dev.wine.music.app.domain.model

/**
 * Everything the artist detail shows. [date] is the birth date of a musician or the creation
 * date of a band. A band has [members]; a musician has at most one [band].
 */
data class ArtistDetail(
    val artist: Artist,
    val date: String,
    val members: List<Artist> = emptyList(),
    val band: Artist? = null,
    val albums: List<Album> = emptyList(),
    val prizes: List<ArtistPrize> = emptyList(),
) {
    /** [date] as dd/mm/yyyy. */
    val day: String get() = isoDateToDay(date)
}

/** A prize given to the artist. [name] and [organization] are empty until the use case fills them. */
data class ArtistPrize(
    val id: Int,
    val premiationDate: String,
    val name: String = "",
    val organization: String = "",
) {
    /** [premiationDate] as dd/mm/yyyy. */
    val premiationDay: String get() = isoDateToDay(premiationDate)
}
