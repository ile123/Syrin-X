package com.ile.syrin_x.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ile.syrin_x.domain.repository.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val dataStore: DataStoreRepository
) : ViewModel() {

    companion object {
        private const val ONBOARDING_SHOWN_KEY = "onboarding_shown"
    }

    val hasSeenOnboarding: StateFlow<Boolean> =
        dataStore
            .getBooleanFlow(ONBOARDING_SHOWN_KEY)
            .map { it ?: false }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = false
            )

    fun markOnboardingShown() {
        viewModelScope.launch {
            dataStore.putBoolean(ONBOARDING_SHOWN_KEY, true)
        }
    }
}