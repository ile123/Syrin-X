package com.ile.syrin_x.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthResult
import com.ile.syrin_x.domain.core.Response
import com.ile.syrin_x.domain.usecase.auth.RegisterUseCase
import com.ile.syrin_x.domain.usecase.user.SaveUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
    private val saveUserUseCase: SaveUserUseCase
) : ViewModel() {

    private var _registerFlow = MutableSharedFlow<Response<AuthResult>>()
    val registerFlow = _registerFlow

    fun register(userName: String, fullName: String, email: String, password: String) = viewModelScope.launch {
        registerUseCase.invoke(email, password).collect { response ->
            when (response) {
                is Response.Loading -> {

                }
                is Response.Success -> {
                    saveUserUseCase(response.data.user?.uid.toString(), userName, fullName, email)
                }
                is Response.Error -> {
                    Log.d("User Error:", response.message)
                }
            }
            _registerFlow.emit(response)
        }

    }

}