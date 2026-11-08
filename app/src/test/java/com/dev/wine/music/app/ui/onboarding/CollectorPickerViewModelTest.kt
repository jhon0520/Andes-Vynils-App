package com.dev.wine.music.app.ui.onboarding

import androidx.lifecycle.SavedStateHandle
import com.dev.wine.music.app.domain.model.AppError
import com.dev.wine.music.app.domain.model.UserSession
import com.dev.wine.music.app.domain.usecase.GetCollectorsUseCase
import com.dev.wine.music.app.domain.usecase.SelectCollectorUseCase
import com.dev.wine.music.app.fakes.FakeCollectorRepository
import com.dev.wine.music.app.fakes.FakeSessionRepository
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CollectorPickerViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val collectorRepository = FakeCollectorRepository(
        collectors = listOf(
            TestData.collector(id = 100, name = "Manolo Bellón", email = "manollo@caracol.com.co"),
            TestData.collector(id = 101, name = "Jaime Monsalve", email = "jmonsalve@rtvc.com.co"),
        ),
    )
    private val sessionRepository = FakeSessionRepository()

    private fun TestScope.createViewModel(savedState: SavedStateHandle = SavedStateHandle()) =
        CollectorPickerViewModel(
            GetCollectorsUseCase(collectorRepository),
            SelectCollectorUseCase(sessionRepository),
            savedState,
        ).also { viewModel ->
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
        }

    private fun CollectorPickerViewModel.shownNames(): List<String> =
        (uiState.value.collectors as UiState.Success).data.map { it.name }

    @Test
    fun `loads collectors sorted by name with nothing selected`() = runTest {
        val viewModel = createViewModel()

        assertEquals(listOf("Jaime Monsalve", "Manolo Bellón"), viewModel.shownNames())
        assertEquals(2, viewModel.uiState.value.totalCount)
        assertNull(viewModel.uiState.value.selected)
        assertFalse(viewModel.uiState.value.canContinue)
    }

    @Test
    fun `emits a network error and retry loads the collectors`() = runTest {
        collectorRepository.shouldFail = true
        collectorRepository.error = AppError.Network()
        val viewModel = createViewModel()

        assertEquals(ErrorType.NETWORK, (viewModel.uiState.value.collectors as UiState.Error).type)

        collectorRepository.shouldFail = false
        viewModel.loadCollectors()

        assertTrue(viewModel.uiState.value.collectors is UiState.Success)
    }

    @Test
    fun `filters by name`() = runTest {
        val viewModel = createViewModel()

        viewModel.onQueryChange("Jaime")

        assertEquals(listOf("Jaime Monsalve"), viewModel.shownNames())
        assertEquals(2, viewModel.uiState.value.totalCount)
    }

    @Test
    fun `filters by email`() = runTest {
        val viewModel = createViewModel()

        viewModel.onQueryChange("caracol")

        assertEquals(listOf("Manolo Bellón"), viewModel.shownNames())
    }

    @Test
    fun `filter ignores case and accents`() = runTest {
        val viewModel = createViewModel()

        viewModel.onQueryChange("BELLON")

        assertEquals(listOf("Manolo Bellón"), viewModel.shownNames())
    }

    @Test
    fun `a query without matches shows an empty filtered list`() = runTest {
        val viewModel = createViewModel()

        viewModel.onQueryChange("zzz")

        assertTrue(viewModel.shownNames().isEmpty())
        assertEquals(2, viewModel.uiState.value.totalCount)
    }

    @Test
    fun `selecting a collector enables continue`() = runTest {
        val viewModel = createViewModel()

        viewModel.onCollectorSelect(101)

        assertEquals("Jaime Monsalve", viewModel.uiState.value.selected?.name)
        assertTrue(viewModel.uiState.value.canContinue)
    }

    @Test
    fun `a search that hides the selected collector clears the selection`() = runTest {
        val viewModel = createViewModel()
        viewModel.onCollectorSelect(101)

        viewModel.onQueryChange("caracol")

        assertNull(viewModel.uiState.value.selected)
        assertFalse(viewModel.uiState.value.canContinue)

        // Clearing the search does not bring the old selection back.
        viewModel.onQueryChange("")
        assertNull(viewModel.uiState.value.selected)
    }

    @Test
    fun `a search that still shows the selected collector keeps the selection`() = runTest {
        val viewModel = createViewModel()
        viewModel.onCollectorSelect(101)

        viewModel.onQueryChange("monsalve")

        assertEquals(101, viewModel.uiState.value.selected?.id)
        assertTrue(viewModel.uiState.value.canContinue)
    }

    @Test
    fun `continue without a selection does nothing`() = runTest {
        val viewModel = createViewModel()

        assertFalse(viewModel.onContinue())
        assertNull(sessionRepository.session.value)
    }

    @Test
    fun `continue starts the collector session`() = runTest {
        val viewModel = createViewModel()
        viewModel.onCollectorSelect(100)

        assertTrue(viewModel.onContinue())

        assertEquals(
            UserSession.Collector(100, "Manolo Bellón", "manollo@caracol.com.co"),
            sessionRepository.session.value,
        )
    }

    @Test
    fun `query and selection survive recreation through SavedStateHandle`() = runTest {
        val savedState = SavedStateHandle(mapOf("query" to "jaime", "selectedId" to 101))

        val viewModel = createViewModel(savedState)

        assertEquals("jaime", viewModel.uiState.value.query)
        assertEquals(listOf("Jaime Monsalve"), viewModel.shownNames())
        assertEquals(101, viewModel.uiState.value.selected?.id)
    }

    @Test
    fun `initials use the first two words or dots when there is no name`() {
        assertEquals("MB", collectorInitials("Manolo Bellón"))
        assertEquals("J", collectorInitials("Jaime"))
        assertEquals("··", collectorInitials("  "))
    }
}
