package com.dev.wine.music.app.ui.album.list

import com.dev.wine.music.app.domain.usecase.GetAlbumsUseCase
import com.dev.wine.music.app.fakes.FakeAlbumRepository
import com.dev.wine.music.app.fakes.TestData
import com.dev.wine.music.app.ui.common.UiState
import com.dev.wine.music.app.util.MainDispatcherRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class AlbumListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeAlbumRepository()

    private fun createViewModel() = AlbumListViewModel(GetAlbumsUseCase(repository))

    @Test
    fun `emits Success with albums when loading works`() {
        repository.albums = listOf(TestData.album())

        val viewModel = createViewModel()

        val state = viewModel.uiState.value
        assertTrue(state is UiState.Success)
        assertEquals("Buscando América", (state as UiState.Success).data.single().name)
    }

    @Test
    fun `emits Error when the repository fails`() {
        repository.shouldFail = true

        val viewModel = createViewModel()

        assertTrue(viewModel.uiState.value is UiState.Error)
    }

    @Test
    fun `retry after an error loads the albums`() {
        repository.shouldFail = true
        val viewModel = createViewModel()

        repository.shouldFail = false
        repository.albums = listOf(TestData.album())
        viewModel.loadAlbums()

        assertTrue(viewModel.uiState.value is UiState.Success)
    }
}
