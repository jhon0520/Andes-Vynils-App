package com.dev.wine.music.app.ui.onboarding

import com.dev.wine.music.app.domain.model.UserSession
import com.dev.wine.music.app.domain.usecase.SelectVisitorUseCase
import com.dev.wine.music.app.fakes.FakeSessionRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RoleSelectionViewModelTest {

    private val sessionRepository = FakeSessionRepository()
    private val viewModel = RoleSelectionViewModel(SelectVisitorUseCase(sessionRepository))

    @Test
    fun `no session is created until a role is chosen`() {
        assertNull(sessionRepository.session.value)
    }

    @Test
    fun `choosing visitor starts a visitor session`() {
        viewModel.onVisitorSelect()

        assertEquals(UserSession.Visitor, sessionRepository.session.value)
    }
}
