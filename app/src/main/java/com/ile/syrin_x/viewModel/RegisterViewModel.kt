package com.ile.syrin_x.viewModel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthResult
import com.google.firebase.database.FirebaseDatabase
import com.ile.syrin_x.domain.core.Response
import com.ile.syrin_x.domain.usecase.auth.RegisterUseCase
import com.ile.syrin_x.domain.usecase.user.ChangeUserProfileUseCase
import com.ile.syrin_x.domain.usecase.user.SaveUserUseCase
import com.ile.syrin_x.domain.usecase.user.UploadProfileImageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
    private val saveUserUseCase: SaveUserUseCase,
    private val uploadProfileImageUseCase: UploadProfileImageUseCase
) : ViewModel() {

    private val _registerFlow = MutableSharedFlow<Response<AuthResult>>()
    val registerFlow = _registerFlow

    fun register(
        userName: String,
        fullName: String,
        email: String,
        password: String,
        imageUri: Uri?,
        context: Context
    ) = viewModelScope.launch {
        registerUseCase(email, password).collect { response ->
            when (response) {
                is Response.Success -> {
                    val uid = response.data.user?.uid ?: return@collect

                    saveUserUseCase(uid, userName, fullName, email, null)

                    if (imageUri != null) {
                        uploadProfileImageUseCase(uid, imageUri, context)
                    }
                }

                is Response.Error -> {
                    Log.e("Register", response.message)
                }

                is Response.Loading -> { }
            }

            _registerFlow.emit(response)
        }
    }
}
