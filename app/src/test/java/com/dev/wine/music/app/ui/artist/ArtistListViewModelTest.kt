package com.dev.wine.music.app.ui.artist

import com.dev.wine.music.app.domain.model.AppError
import com.dev.wine.music.app.domain.model.ArtistType
import com.dev.wine.music.app.domain.usecase.GetArtistsUseCase
import com.dev.wine.music.app.fakes.FakeArtistRepository
import com.dev.wine.music.app.fakes.TestData
import com.dev.wine.music.app.ui.common.ErrorType
import com.dev.wine.music.app.ui.common.UiState
import com.dev.wine.music.app.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ArtistListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeArtistRepository(
        artists = listOf(
            TestData.artist(id = 100, name = "Rubén Blades Bellido de Luna"),
            TestData.artist(id = 102, name = "Freddie Mercury"),
            TestData.artist(id = 101, name = "Queen", type = ArtistType.BAND, memberIds = listOf(102)),
        ),
    )

    /** uiState uses stateIn(WhileSubscribed): it only computes while someone collects it. */
    private fun TestScope.createViewModel() = ArtistListViewModel(GetArtistsUseCase(repository)).also { viewModel ->
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
    }

    private fun ArtistListViewModel.shownNames(): List<String> =
        (uiState.value as UiState.Success).data.artists.map { it.name }

    @Test
    fun `emits Success with musicians and bands sorted by name`() = runTest {
        val viewModel = createViewModel()

        assertEquals(listOf("Freddie Mercury", "Queen", "Rubén Blades Bellido de Luna"), viewModel.shownNames())
        assertEquals(3, (viewModel.uiState.value as UiState.Success).data.totalCount)
    }

    @Test
    fun `each musician shows their band or none`() = runTest {
        val artists = (createViewModel().uiState.value as UiState.Success).data.artists

        assertEquals("Queen", artists.first { it.id == 102 }.bandName)
        assertNull(artists.first { it.id == 100 }.bandName)
    }

    @Test
    fun `emits a network error when artists fail to load`() = runTest {
        repository.shouldFail = true
        repository.error = AppError.Network()

        val state = createViewModel().uiState.value as UiState.Error

        assertEquals(ErrorType.NETWORK, state.type)
    }

    @Test
    fun `retry after an error loads the artists`() = runTest {
        repository.shouldFail = true
        val viewModel = createViewModel()

        repository.shouldFail = false
        viewModel.loadArtists()

        assertTrue(viewModel.uiState.value is UiState.Success)
    }

    @Test
    fun `the band filter keeps only bands`() = runTest {
        val viewModel = createViewModel()

        viewModel.onTypeSelect(ArtistType.BAND)

        assertEquals(listOf("Queen"), viewModel.shownNames())
        assertEquals(ArtistType.BAND, viewModel.selectedType.value)
    }

    @Test
    fun `the musician filter keeps only musicians`() = runTest {
        val viewModel = createViewModel()

        viewModel.onTypeSelect(ArtistType.MUSICIAN)

        assertEquals(listOf("Freddie Mercury", "Rubén Blades Bellido de Luna"), viewModel.shownNames())
    }

    @Test
    fun `selecting all shows every artist again`() = runTest {
        val viewModel = createViewModel()
        viewModel.onTypeSelect(ArtistType.BAND)

        viewModel.onTypeSelect(null)

        assertEquals(3, viewModel.shownNames().size)
    }

    @Test
    fun `a filter without matches keeps the total so the screen shows no results`() = runTest {
        repository.artists = listOf(TestData.artist(id = 100))
        val viewModel = createViewModel()

        viewModel.onTypeSelect(ArtistType.BAND)

        val catalog = (viewModel.uiState.value as UiState.Success).data
        assertTrue(catalog.artists.isEmpty())
        assertEquals(1, catalog.totalCount)
    }

    @Test
    fun `filtering does not call the repository again`() = runTest {
        val viewModel = createViewModel()
        repository.shouldFail = true

        viewModel.onTypeSelect(ArtistType.MUSICIAN)

        assertTrue(viewModel.uiState.value is UiState.Success)
    }
}
