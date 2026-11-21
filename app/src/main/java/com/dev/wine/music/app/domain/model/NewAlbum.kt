package com.dev.wine.music.app.domain.model

/** What a collector fills in to create an album. [releaseDate] is an ISO date with offset. */
data class NewAlbum(
    val name: String,
    val cover: String,
    val releaseDate: String,
    val description: String,
    val genre: AlbumGenre,
    val recordLabel: RecordLabel,
)

/** Record labels accepted by the backend; [apiValue] is the exact text it validates. */
enum class RecordLabel(val apiValue: String) {
    SONY_MUSIC("Sony Music"),
    EMI("EMI"),
    DISCOS_FUENTES("Discos Fuentes"),
    ELEKTRA("Elektra"),
    FANIA_RECORDS("Fania Records"),
}
