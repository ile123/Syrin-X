package com.ile.syrin_x.ui.navigation

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ile.syrin_x.domain.usecase.auth.IsLoggedInUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthenticationNavigationViewModel @Inject constructor(
    private val isLoggedInUseCase: IsLoggedInUseCase
) : ViewModel() {

    private var _isLoggedInState = mutableStateOf(false)
    val isLoggedInState = _isLoggedInState

    init {
        isLoggedIn()
    }

    private fun isLoggedIn() = viewModelScope.launch {
        isLoggedInUseCase.invoke().collect {
            _isLoggedInState.value = it
        }
    }

}