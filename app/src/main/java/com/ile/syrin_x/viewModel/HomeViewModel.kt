package com.ile.syrin_x.viewModel

import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.AuthResult
import com.ile.syrin_x.data.model.deezer.MusicGenre
import com.ile.syrin_x.domain.core.Response
import com.ile.syrin_x.domain.usecase.musicsource.deezer.GetAllMusicGenresUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllMusicGenresUseCase: GetAllMusicGenresUseCase
) : ViewModel() {

    private val _categories = mutableListOf<MusicGenre>()
    val categories: List<MusicGenre> get() = _categories

    private val _homeFlow = MutableSharedFlow<Response<Any>>(replay = 1)
    val homeFlow: SharedFlow<Response<Any>> = _homeFlow

    init {
        getAllMusicGenres()
    }

    private fun getAllMusicGenres() = viewModelScope.launch {
        _categories.clear()
        getAllMusicGenresUseCase().collect { response ->
            when (response) {
                is Response.Error -> {
                    Log.d("HomeViewModel", response.message)
                }
                is Response.Loading -> { }
                is Response.Success<*> -> {
                    val musicGenreData = response.data as? List<MusicGenre>
                    musicGenreData?.let {
                        _categories.addAll(it)
                    }
                }
            }

            _homeFlow.emit(response)
        }
    }
}
