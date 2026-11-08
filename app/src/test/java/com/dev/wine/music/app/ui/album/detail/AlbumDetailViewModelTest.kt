package com.dev.wine.music.app.ui.album.detail

import androidx.lifecycle.SavedStateHandle
import com.dev.wine.music.app.domain.model.AppError
import com.dev.wine.music.app.domain.model.UserSession
import com.dev.wine.music.app.domain.usecase.GetAlbumDetailUseCase
import com.dev.wine.music.app.domain.usecase.ObserveSessionUseCase
import com.dev.wine.music.app.fakes.FakeAlbumRepository
import com.dev.wine.music.app.fakes.FakeCollectorRepository
import com.dev.wine.music.app.fakes.FakeSessionRepository
import com.dev.wine.music.app.fakes.TestData
import com.dev.wine.music.app.ui.common.ErrorType
import com.dev.wine.music.app.ui.common.UiState
import com.dev.wine.music.app.util.MainDispatcherRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class AlbumDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val albumRepository = FakeAlbumRepository(
        albums = listOf(
            TestData.album(id = 1),
            TestData.album(id = 2, name = "Poeta del pueblo", comments = listOf(TestData.comment(id = 100))),
        ),
    )

    private val collectorRepository = FakeCollectorRepository(
        collectors = listOf(TestData.collector(name = "Manolo Bellon", commentIds = listOf(100))),
    )

    private val sessionRepository = FakeSessionRepository()

    private fun createViewModel(albumId: Int = 2) = AlbumDetailViewModel(
        SavedStateHandle(mapOf(AlbumDetailViewModel.ALBUM_ID_KEY to albumId)),
        GetAlbumDetailUseCase(albumRepository, collectorRepository),
        ObserveSessionUseCase(sessionRepository),
    )

    @Test
    fun `session follows the chosen profile so only a collector can add tracks`() {
        val viewModel = createViewModel()

        sessionRepository.selectVisitor()
        assertEquals(UserSession.Visitor, viewModel.session.value)

        sessionRepository.selectCollector(TestData.collector(id = 100, name = "Manolo Bellon"))
        assertTrue(viewModel.session.value is UserSession.Collector)
    }

    @Test
    fun `loads the album whose id comes from navigation`() {
        val state = createViewModel().uiState.value

        assertTrue(state is UiState.Success)
        assertEquals("Poeta del pueblo", (state as UiState.Success).data.name)
    }

    @Test
    fun `shows each review with its author`() {
        val album = (createViewModel().uiState.value as UiState.Success).data

        assertEquals("Manolo Bellon", album.comments.single().author)
    }

    @Test
    fun `emits a network error when the album cannot load`() {
        albumRepository.shouldFail = true
        albumRepository.error = AppError.Network()

        val state = createViewModel().uiState.value as UiState.Error

        assertEquals(ErrorType.NETWORK, state.type)
    }

    @Test
    fun `a missing album shows NOT_FOUND`() {
        albumRepository.shouldFail = true
        albumRepository.error = AppError.NotFound("The album with the given id was not found")

        val state = createViewModel().uiState.value as UiState.Error

        assertEquals(ErrorType.NOT_FOUND, state.type)
    }

    @Test
    fun `retry after an error loads the album`() {
        albumRepository.shouldFail = true
        val viewModel = createViewModel()

        albumRepository.shouldFail = false
        viewModel.loadAlbum()

        assertTrue(viewModel.uiState.value is UiState.Success)
    }
}
