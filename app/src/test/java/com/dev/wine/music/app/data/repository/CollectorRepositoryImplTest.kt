package com.dev.wine.music.app.data.repository

import com.dev.wine.music.app.data.remote.api.CollectorApi
import com.dev.wine.music.app.data.remote.dto.CollectorDto
import com.dev.wine.music.app.domain.model.AppError
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
        override suspend fun getCollectors(): List<CollectorDto> = error?.let { throw it } ?: collectors
    }

    @Test
    fun `maps the collectors from the api`() = runTest {
        val repository = CollectorRepositoryImpl(FakeCollectorApi(collectors = listOf(CollectorDto(id = 100, name = "Manolo Bellon"))))

        assertEquals("Manolo Bellon", repository.getCollectors().single().name)
    }

    @Test
    fun `no connection becomes Network`() = runTest {
        val repository = CollectorRepositoryImpl(FakeCollectorApi(error = IOException("timeout")))

        assertTrue(runCatching { repository.getCollectors() }.exceptionOrNull() is AppError.Network)
    }
}
