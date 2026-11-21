package com.dev.wine.music.app.e2e

/** Small JSON answers for [FakeBackendRule], shaped like BackVynils' seed data. */
object FakeBackendData {

    const val ALBUM_ID = 100
    const val ALBUM_NAME = "Buscando América"
    const val ALBUM_LABEL = "Elektra"
    const val MUSICIAN_NAME = "Rubén Blades Bellido de Luna"
    const val BAND_NAME = "Queen"

    private val musician = """
        {"id":100,"name":"$MUSICIAN_NAME","image":"","description":"","birthDate":"1948-07-16T05:00:00.000Z"}
    """.trimIndent()

    private val band = """
        {"id":101,"name":"$BAND_NAME","image":"","description":"","creationDate":"1970-01-01T05:00:00.000Z","musicians":[]}
    """.trimIndent()

    val album = """
        {"id":$ALBUM_ID,"name":"$ALBUM_NAME","cover":"","releaseDate":"1984-08-01T05:00:00.000Z",
         "description":"Primer álbum de la banda de Rubén Blades y Seis del Solar.","genre":"Salsa",
         "recordLabel":"$ALBUM_LABEL","tracks":[{"id":100,"name":"Decisiones","duration":"5:05"}],
         "performers":[$musician],"comments":[]}
    """.trimIndent()

    const val COLLECTOR_ID = 100
    const val COLLECTOR_NAME = "Manolo Bellon"
    const val OTHER_COLLECTOR_NAME = "Jaime Monsalve"

    val collectors = """
        [{"id":$COLLECTOR_ID,"name":"$COLLECTOR_NAME","telephone":"3502457896","email":"manollo@caracol.com.co","comments":[]},
         {"id":101,"name":"$OTHER_COLLECTOR_NAME","telephone":"3012357936","email":"jmonsalve@rtvc.com.co","comments":[]}]
    """.trimIndent()

    const val MUSICIAN_ID = 100
    const val BAND_ID = 101
    const val PRIZE_ID = 100
    const val PRIZE_NAME = "Grammy Award"

    /** GET /musicians/{id}: the prize only brings id and date, as in BackVynils. */
    val musicianDetail = """
        {"id":$MUSICIAN_ID,"name":"$MUSICIAN_NAME","image":"","description":"Cantante panameño.",
         "birthDate":"1948-07-16T05:00:00.000Z",
         "albums":[{"id":$ALBUM_ID,"name":"$ALBUM_NAME","cover":"","releaseDate":"1984-08-01T05:00:00.000Z",
                    "description":"","genre":"Salsa","recordLabel":"$ALBUM_LABEL"}],
         "performerPrizes":[{"id":$PRIZE_ID,"premiationDate":"1978-12-10T05:00:00.000Z"}]}
    """.trimIndent()

    /** GET /bands/{id}: a band with no members, albums or prizes, to check the empty sections. */
    val emptyBandDetail = band

    /** GET /performerprizes: each prize given, with the prize name and organization. */
    val performerPrizes = """
        [{"id":$PRIZE_ID,"premiationDate":"1978-12-10T05:00:00.000Z",
          "prize":{"id":100,"name":"$PRIZE_NAME","description":"","organization":"National Academy of Recording Arts & Sciences"}}]
    """.trimIndent()

    const val NEW_TRACK_NAME = "E2E new track"

    /** POST /albums/{id}/tracks answers with the created track. */
    val newTrack = """{"id":200,"name":"$NEW_TRACK_NAME","duration":"03:30"}"""

    /** The album after adding [newTrack]. */
    val albumWithNewTrack = album.replace(
        """"duration":"5:05"}]""",
        """"duration":"5:05"},$newTrack]""",
    )

    val albums = "[$album]"
    val musicians = "[$musician]"
    val bands = "[$band]"
    const val EMPTY_LIST = "[]"
}
