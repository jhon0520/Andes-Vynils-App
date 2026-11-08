package com.dev.wine.music.app.data.repository

import com.dev.wine.music.app.domain.model.UserSession
import com.dev.wine.music.app.fakes.TestData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class InMemorySessionRepositoryTest {

    private val repository = InMemorySessionRepository()

    @Test
    fun `starts without a session`() {
        assertNull(repository.session.value)
    }

    @Test
    fun `selecting visitor stores a visitor session`() {
        repository.selectVisitor()

        assertEquals(UserSession.Visitor, repository.session.value)
    }

    @Test
    fun `selecting a collector keeps its id, name and email`() {
        repository.selectCollector(TestData.collector(id = 7, name = "Jaime Monsalve", email = "jm@rtvc.com.co"))

        assertEquals(UserSession.Collector(7, "Jaime Monsalve", "jm@rtvc.com.co"), repository.session.value)
    }

    @Test
    fun `a new choice replaces the previous one and clear removes it`() {
        repository.selectCollector(TestData.collector())
        repository.selectVisitor()
        assertEquals(UserSession.Visitor, repository.session.value)

        repository.clear()

        assertNull(repository.session.value)
    }
}
