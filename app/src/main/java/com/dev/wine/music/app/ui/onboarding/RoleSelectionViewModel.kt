package com.dev.wine.music.app.ui.onboarding

import androidx.lifecycle.ViewModel
import com.dev.wine.music.app.domain.usecase.SelectVisitorUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RoleSelectionViewModel @Inject constructor(
    private val selectVisitor: SelectVisitorUseCase,
) : ViewModel() {

    /** Starts a visitor session. The collector path picks a profile on the next screen. */
    fun onVisitorSelect() {
        selectVisitor()
    }
}
