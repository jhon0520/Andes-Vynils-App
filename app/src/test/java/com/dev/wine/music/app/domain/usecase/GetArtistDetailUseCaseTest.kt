package com.dev.wine.music.app.domain.usecase

import com.dev.wine.music.app.domain.model.ArtistType
import com.dev.wine.music.app.fakes.FakeArtistRepository
import com.dev.wine.music.app.fakes.TestData
import com.dev.wine.music.app.fakes.testDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class GetArtistDetailUseCaseTest {

    private val ruben = TestData.artist(id = 1, name = "Rubén Blades")
    private val seis = TestData.artist(id = 2, name = "Seis del Solar", type = ArtistType.BAND, memberIds = listOf(1))

    private val repository = FakeArtistRepository(artists = listOf(ruben, seis)).apply {
        details = listOf(
            TestData.artistDetail(
                artist = ruben,
                albums = listOf(
                    TestData.album(id = 2, name = "Amor y Control", releaseDate = "1992-01-01T05:00:00.000Z"),
                    TestData.album(id = 1, name = "Buscando América", releaseDate = "1984-08-01T05:00:00.000Z"),
                ),
                prizes = listOf(TestData.prize(id = 100, premiationDate = "1978-12-10T05:00:00.000Z")),
            ),
            TestData.artistDetail(artist = seis, date = "1983-01-01T05:00:00.000Z", members = listOf(ruben)),
        )
        prizes = listOf(
            TestData.prize(
                id = 100,
                premiationDate = "1978-12-10T05:00:00.000Z",
                name = "Premio Grammy",
                organization = "Recording Academy",
            ),
        )
    }

    private val useCase = GetArtistDetailUseCase(repository, testDispatcher)

    @Test
    fun `names each prize with the performer prizes catalog`() = runTest {
        val prize = useCase(1, ArtistType.MUSICIAN).prizes.single()

        assertEquals("Premio Grammy", prize.name)
        assertEquals("Recording Academy", prize.organization)
        assertEquals("10/12/1978", prize.premiationDay)
    }

    @Test
    fun `a musician gets the band that lists them as a member`() = runTest {
        val detail = useCase(1, ArtistType.MUSICIAN)

        assertEquals("Seis del Solar", detail.band?.name)
        assertEquals("Seis del Solar", detail.artist.bandName)
    }

    @Test
    fun `the discography goes from the oldest album to the newest`() = runTest {
        val albums = useCase(1, ArtistType.MUSICIAN).albums

        assertEquals(listOf("Buscando América", "Amor y Control"), albums.map { it.name })
    }

    @Test
    fun `a band keeps its members and has no band of its own`() = runTest {
        val detail = useCase(2, ArtistType.BAND)

        assertEquals(listOf("Rubén Blades"), detail.members.map { it.name })
        assertNull(detail.band)
    }

    @Test
    fun `if the prize catalog fails the artist still shows with unnamed prizes`() = runTest {
        repository.prizesShouldFail = true

        val prize = useCase(1, ArtistType.MUSICIAN).prizes.single()

        assertEquals(100, prize.id)
        assertEquals("", prize.name)
    }

    @Test
    fun `if the bands fail the musician still shows without a band`() = runTest {
        repository.bandsShouldFail = true

        assertNull(useCase(1, ArtistType.MUSICIAN).band)
    }
}
