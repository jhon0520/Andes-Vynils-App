package com.dev.wine.music.app.data.repository

import com.dev.wine.music.app.data.cache.RepositoryCache
import com.dev.wine.music.app.data.remote.api.ArtistApi
import com.dev.wine.music.app.data.remote.dto.AlbumDto
import com.dev.wine.music.app.data.remote.dto.PerformerDto
import com.dev.wine.music.app.data.remote.dto.PerformerPrizeDto
import com.dev.wine.music.app.data.remote.dto.PrizeDto
import com.dev.wine.music.app.domain.model.ArtistType
import com.dev.wine.music.app.fakes.testDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ArtistRepositoryImplTest {

    private class FakeArtistApi : ArtistApi {
        var calls = 0
            private set
        var bandCalls = 0
            private set
        var detailCalls = 0
            private set
        var prizeCalls = 0
            private set

        private val ruben = PerformerDto(
            id = 1,
            name = "Rubén Blades",
            birthDate = "1948-07-16T05:00:00.000Z",
            albums = listOf(AlbumDto(id = 100, name = "Buscando América")),
            performerPrizes = listOf(PerformerPrizeDto(id = 10, premiationDate = "1978-12-10T05:00:00.000Z")),
        )
        private val queen = PerformerDto(
            id = 2,
            name = "Queen",
            creationDate = "1970-01-01T05:00:00.000Z",
            musicians = listOf(PerformerDto(id = 1, name = "Rubén Blades")),
        )

        override suspend fun getMusicians(): List<PerformerDto> {
            calls++
            return listOf(ruben)
        }

        override suspend fun getBands(): List<PerformerDto> {
            bandCalls++
            return listOf(queen)
        }

        override suspend fun getMusician(musicianId: Int): PerformerDto {
            detailCalls++
            return ruben
        }

        override suspend fun getBand(bandId: Int): PerformerDto {
            detailCalls++
            return queen
        }

        override suspend fun getPerformerPrizes(): List<PerformerPrizeDto> {
            prizeCalls++
            return listOf(
                PerformerPrizeDto(
                    id = 10,
                    premiationDate = "1978-12-10T05:00:00.000Z",
                    prize = PrizeDto(id = 5, name = "Grammy", organization = "Recording Academy"),
                ),
            )
        }
    }

    private val api = FakeArtistApi()
    private val cache = RepositoryCache()
    private val repository = ArtistRepositoryImpl(api, cache, testDispatcher)

    @Test
    fun `merges musicians and bands with their type`() = runTest {
        val artists = repository.getArtists()

        assertEquals(2, artists.size)
        assertEquals(ArtistType.MUSICIAN, artists.first { it.name == "Rubén Blades" }.type)
        assertEquals(ArtistType.BAND, artists.first { it.name == "Queen" }.type)
    }

    @Test
    fun `keeps the members of each band`() = runTest {
        val queen = repository.getArtists().first { it.name == "Queen" }

        assertEquals(listOf(1), queen.memberIds)
    }

    @Test
    fun `the second time the artists come from the cache without calling the api`() = runTest {
        repository.getArtists()
        repository.getArtists()

        assertEquals(1, api.calls)
    }

    @Test
    fun `bands come from the cached artists when the artists tab already loaded them`() = runTest {
        repository.getArtists()
        val callsAfterList = api.bandCalls

        val bands = repository.getBands()

        assertEquals(listOf("Queen"), bands.map { it.name })
        assertEquals(callsAfterList, api.bandCalls)
    }

    @Test
    fun `a musician detail uses the birth date and keeps albums and prize ids`() = runTest {
        val detail = repository.getArtist(1, ArtistType.MUSICIAN)

        assertEquals("16/07/1948", detail.day)
        assertEquals(listOf("Buscando América"), detail.albums.map { it.name })
        assertEquals(listOf(10), detail.prizes.map { it.id })
    }

    @Test
    fun `a band detail uses the creation date and lists its members as musicians`() = runTest {
        val detail = repository.getArtist(2, ArtistType.BAND)

        assertEquals("01/01/1970", detail.day)
        assertEquals(ArtistType.BAND, detail.artist.type)
        assertEquals(listOf(ArtistType.MUSICIAN), detail.members.map { it.type })
    }

    @Test
    fun `performer prizes bring the prize name and organization`() = runTest {
        val prize = repository.getPerformerPrizes().single()

        assertEquals("Grammy", prize.name)
        assertEquals("Recording Academy", prize.organization)
        assertEquals("10/12/1978", prize.premiationDay)
    }

    @Test
    fun `reopening an artist answers from the cache without calling the api`() = runTest {
        repository.getArtist(1, ArtistType.MUSICIAN)
        repository.getArtist(1, ArtistType.MUSICIAN)

        assertEquals(1, api.detailCalls)
    }

    @Test
    fun `a musician and a band with the same id are cached apart`() = runTest {
        val musician = repository.getArtist(1, ArtistType.MUSICIAN)
        val band = repository.getArtist(1, ArtistType.BAND)

        assertEquals(ArtistType.MUSICIAN, musician.artist.type)
        assertEquals(ArtistType.BAND, band.artist.type)
        assertEquals(2, api.detailCalls)
    }

    @Test
    fun `the prize catalog is fetched once`() = runTest {
        repository.getPerformerPrizes()
        repository.getPerformerPrizes()

        assertEquals(1, api.prizeCalls)
    }

    @Test
    fun `after invalidating artists the detail and prizes are fetched again`() = runTest {
        repository.getArtist(1, ArtistType.MUSICIAN)
        repository.getPerformerPrizes()

        cache.invalidateArtists()
        repository.getArtist(1, ArtistType.MUSICIAN)
        repository.getPerformerPrizes()

        assertEquals(2, api.detailCalls)
        assertEquals(2, api.prizeCalls)
    }
}
