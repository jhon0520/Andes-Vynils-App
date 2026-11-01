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

    val albums = "[$album]"
    val musicians = "[$musician]"
    val bands = "[$band]"
    const val EMPTY_LIST = "[]"
}
