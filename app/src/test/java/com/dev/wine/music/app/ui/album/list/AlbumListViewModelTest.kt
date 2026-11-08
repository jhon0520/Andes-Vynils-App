package com.dev.wine.music.app.ui.album.list

import com.dev.wine.music.app.domain.model.AlbumGenre
import com.dev.wine.music.app.domain.model.ArtistType
import com.dev.wine.music.app.domain.model.UserSession
import com.dev.wine.music.app.domain.usecase.GetAlbumsUseCase
import com.dev.wine.music.app.domain.usecase.ObserveSessionUseCase
import com.dev.wine.music.app.fakes.FakeAlbumRepository
import com.dev.wine.music.app.fakes.FakeSessionRepository
import com.dev.wine.music.app.fakes.TestData
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
class AlbumListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val ruben = TestData.artist(id = 100, name = "Rubén Blades Bellido de Luna")
    private val queen = TestData.artist(id = 101, name = "Queen", type = ArtistType.BAND)

    private val repository = FakeAlbumRepository(
        albums = listOf(
            TestData.album(id = 100, name = "Buscando América", genre = "Salsa", performers = listOf(ruben)),
            TestData.album(id = 101, name = "Poeta del pueblo", genre = "Salsa", performers = listOf(ruben)),
            TestData.album(id = 102, name = "A Night at the Opera", genre = "Rock", performers = listOf(queen)),
        ),
    )

    private val sessionRepository = FakeSessionRepository()

    /** uiState uses stateIn(WhileSubscribed): it only computes while someone collects it. */
    private fun TestScope.createViewModel() = AlbumListViewModel(GetAlbumsUseCase(repository), ObserveSessionUseCase(sessionRepository)).also { viewModel ->
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
    }

    private fun AlbumListViewModel.shownNames(): List<String> =
        (uiState.value as UiState.Success).data.albums.map { it.name }

    @Test
    fun `emits Success with every album sorted by name when loading works`() = runTest {
        val viewModel = createViewModel()

        val catalog = (viewModel.uiState.value as UiState.Success).data
        assertEquals(listOf("A Night at the Opera", "Buscando América", "Poeta del pueblo"), catalog.albums.map { it.name })
        assertEquals(3, catalog.totalCount)
    }

    @Test
    fun `emits Error when the repository fails`() = runTest {
        repository.shouldFail = true

        val viewModel = createViewModel()

        assertTrue(viewModel.uiState.value is UiState.Error)
    }

    @Test
    fun `retry after an error loads the albums`() = runTest {
        repository.shouldFail = true
        val viewModel = createViewModel()

        repository.shouldFail = false
        viewModel.loadAlbums()

        assertTrue(viewModel.uiState.value is UiState.Success)
    }

    @Test
    fun `search filters by album name ignoring case`() = runTest {
        val viewModel = createViewModel()

        viewModel.onQueryChange("OPERA")

        assertEquals(listOf("A Night at the Opera"), viewModel.shownNames())
    }

    @Test
    fun `search filters by performer name ignoring accents`() = runTest {
        val viewModel = createViewModel()

        viewModel.onQueryChange("ruben")

        assertEquals(listOf("Buscando América", "Poeta del pueblo"), viewModel.shownNames())
    }

    @Test
    fun `genre filter keeps only albums of that genre`() = runTest {
        val viewModel = createViewModel()

        viewModel.onGenreSelect(AlbumGenre.ROCK)

        assertEquals(listOf("A Night at the Opera"), viewModel.shownNames())
    }

    @Test
    fun `search and genre filter combine`() = runTest {
        val viewModel = createViewModel()

        viewModel.onGenreSelect(AlbumGenre.SALSA)
        viewModel.onQueryChange("poeta")

        assertEquals(listOf("Poeta del pueblo"), viewModel.shownNames())
    }

    @Test
    fun `a search without matches keeps the total so the screen shows no results instead of an empty catalog`() = runTest {
        val viewModel = createViewModel()

        viewModel.onQueryChange("Soda Stereo en vivo")

        val catalog = (viewModel.uiState.value as UiState.Success).data
        assertTrue(catalog.albums.isEmpty())
        assertEquals(3, catalog.totalCount)
    }

    @Test
    fun `filtering does not call the repository again`() = runTest {
        val viewModel = createViewModel()
        repository.shouldFail = true

        viewModel.onQueryChange("queen")
        viewModel.onGenreSelect(AlbumGenre.ROCK)

        assertTrue(viewModel.uiState.value is UiState.Success)
    }

    @Test
    fun `clearFilters shows every album again`() = runTest {
        val viewModel = createViewModel()
        viewModel.onGenreSelect(AlbumGenre.FOLK)
        viewModel.onQueryChange("nada")

        viewModel.clearFilters()

        assertEquals("", viewModel.query.value)
        assertNull(viewModel.selectedGenre.value)
        assertEquals(3, viewModel.shownNames().size)
    }

    @Test
    fun `session follows the chosen profile`() = runTest {
        val viewModel = createViewModel()

        sessionRepository.selectVisitor()
        assertEquals(UserSession.Visitor, viewModel.session.value)

        sessionRepository.selectCollector(TestData.collector(id = 100, name = "Manolo Bellon"))
        assertTrue(viewModel.session.value is UserSession.Collector)
    }
}
