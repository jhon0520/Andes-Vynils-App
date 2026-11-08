package com.dev.wine.music.app.ui.navigation

import androidx.lifecycle.ViewModel
import com.dev.wine.music.app.domain.model.UserSession
import com.dev.wine.music.app.domain.usecase.ObserveSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/** Lets the app shell know whether a profile has been chosen in this process. */
@HiltViewModel
class SessionViewModel @Inject constructor(
    observeSession: ObserveSessionUseCase,
) : ViewModel() {
    val session: StateFlow<UserSession?> = observeSession()
}
