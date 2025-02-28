package com.ile.syrin_x.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthResult
import com.ile.syrin_x.domain.core.Response
import com.ile.syrin_x.domain.usecase.auth.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private var _registerFlow = MutableSharedFlow<Response<AuthResult>>()
    val registerFlow = _registerFlow

    fun register(email: String, password: String) = viewModelScope.launch {
        registerUseCase.invoke(email, password).collect {
            _registerFlow.emit(it)
        }
    }

}