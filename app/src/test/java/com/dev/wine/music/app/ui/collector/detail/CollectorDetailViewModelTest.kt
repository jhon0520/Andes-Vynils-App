package com.dev.wine.music.app.ui.collector.detail

import androidx.lifecycle.SavedStateHandle
import com.dev.wine.music.app.domain.model.AppError
import com.dev.wine.music.app.domain.usecase.GetCollectorDetailUseCase
import com.dev.wine.music.app.fakes.FakeAlbumRepository
import com.dev.wine.music.app.fakes.FakeCollectorRepository
import com.dev.wine.music.app.fakes.TestData
import com.dev.wine.music.app.fakes.testDispatcher
import com.dev.wine.music.app.ui.common.ErrorType
import com.dev.wine.music.app.ui.common.UiState
import com.dev.wine.music.app.util.MainDispatcherRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class CollectorDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeCollectorRepository().apply {
        details = listOf(
            TestData.collectorDetail(collector = TestData.collector(id = 1, name = "Manolo Bellon")),
            TestData.collectorDetail(collector = TestData.collector(id = 2, name = "Jaime Monsalve")),
        )
    }

    private fun createViewModel(collectorId: Int = 2) = CollectorDetailViewModel(
        SavedStateHandle(mapOf(CollectorDetailViewModel.COLLECTOR_ID_KEY to collectorId)),
        GetCollectorDetailUseCase(repository, FakeAlbumRepository(), testDispatcher),
    )

    @Test
    fun `loads the collector whose id comes from navigation`() {
        val state = createViewModel().uiState.value

        assertTrue(state is UiState.Success)
        assertEquals("Jaime Monsalve", (state as UiState.Success).data.collector.name)
    }

    @Test
    fun `emits a network error when the collector cannot load`() {
        repository.shouldFail = true
        repository.error = AppError.Network()

        val state = createViewModel().uiState.value as UiState.Error

        assertEquals(ErrorType.NETWORK, state.type)
    }

    @Test
    fun `a missing collector shows NOT_FOUND`() {
        repository.shouldFail = true
        repository.error = AppError.NotFound("The collector with the given id was not found")

        val state = createViewModel().uiState.value as UiState.Error

        assertEquals(ErrorType.NOT_FOUND, state.type)
    }

    @Test
    fun `retrying after an error loads the collector`() {
        repository.shouldFail = true
        val viewModel = createViewModel()
        assertTrue(viewModel.uiState.value is UiState.Error)

        repository.shouldFail = false
        viewModel.loadCollector()

        assertTrue(viewModel.uiState.value is UiState.Success)
    }
}
