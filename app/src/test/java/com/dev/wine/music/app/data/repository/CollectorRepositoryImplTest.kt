package com.dev.wine.music.app.data.repository

import com.dev.wine.music.app.data.cache.RepositoryCache
import com.dev.wine.music.app.data.remote.api.CollectorApi
import com.dev.wine.music.app.data.remote.dto.CollectorDto
import com.dev.wine.music.app.domain.model.AppError
import com.dev.wine.music.app.fakes.testDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class CollectorRepositoryImplTest {

    private class FakeCollectorApi(
        private val collectors: List<CollectorDto> = emptyList(),
        private val error: Exception? = null,
    ) : CollectorApi {
        var calls = 0
            private set

        override suspend fun getCollectors(): List<CollectorDto> {
            calls++
            return error?.let { throw it } ?: collectors
        }
    }

    @Test
    fun `maps the collectors from the api`() = runTest {
        val repository = CollectorRepositoryImpl(FakeCollectorApi(collectors = listOf(CollectorDto(id = 100, name = "Manolo Bellon"))), RepositoryCache(), testDispatcher)

        assertEquals("Manolo Bellon", repository.getCollectors().single().name)
    }

    @Test
    fun `no connection becomes Network`() = runTest {
        val repository = CollectorRepositoryImpl(FakeCollectorApi(error = IOException("timeout")), RepositoryCache(), testDispatcher)

        assertTrue(runCatching { repository.getCollectors() }.exceptionOrNull() is AppError.Network)
    }

    @Test
    fun `the second time the collectors come from the cache without calling the api`() = runTest {
        val api = FakeCollectorApi(collectors = listOf(CollectorDto(id = 100, name = "Manolo Bellon")))
        val repository = CollectorRepositoryImpl(api, RepositoryCache(), testDispatcher)

        repository.getCollectors()
        repository.getCollectors()

        assertEquals(1, api.calls)
    }
}
