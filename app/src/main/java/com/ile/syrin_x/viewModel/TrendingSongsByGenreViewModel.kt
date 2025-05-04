package com.ile.syrin_x.viewModel

import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ile.syrin_x.data.model.deezer.MusicGenre
import com.ile.syrin_x.data.model.deezer.TrackByGenre
import com.ile.syrin_x.domain.core.Response
import com.ile.syrin_x.domain.usecase.musicsource.deezer.GetTrendingSongsByGenreUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrendingSongsByGenreViewModel @Inject constructor(
    private val getTrendingSongsByGenreUseCase: GetTrendingSongsByGenreUseCase
): ViewModel() {

    private val _tracks = mutableStateListOf<TrackByGenre>()
    val tracks: List<TrackByGenre> get() = _tracks

    private val _trackByGenreFlow = MutableSharedFlow<Response<Any>>()
    val trackByGenreFlow: MutableSharedFlow<Response<Any>> = _trackByGenreFlow

    val offset = mutableLongStateOf(0)

    fun fetchTrendingSongsByGenre(genreId: Long) = viewModelScope.launch {
        _tracks.clear()
        getTrendingSongsByGenreUseCase(genreId, 200, offset.longValue).collect { response ->
            when(response) {
                is Response.Error -> {
                    Log.d("TrendingSongsByGenreViewModel", response.message)
                }
                is Response.Loading -> {

                }
                is Response.Success<*> -> {
                    val trendingSongsByGenreData = response.data as? List<TrackByGenre>
                    trendingSongsByGenreData?.let {
                        _tracks.addAll(trendingSongsByGenreData)
                    }
                }
            }
            _trackByGenreFlow.emit(response)
        }
    }

    fun fetchMoreTrendingSongsByGenreForInfiniteScroll(genreId: Long) = viewModelScope.launch {
        //Deezer keeps returning the same songs if my offset increments by 1, so I had to bump it up a bit
        offset.longValue += 5
        getTrendingSongsByGenreUseCase(genreId, 200, offset.longValue).collect { response ->
            when(response) {
                is Response.Error -> {
                    Log.d("TrendingSongsByGenreViewModel", response.message)
                }
                is Response.Loading -> {

                }
                is Response.Success<*> -> {
                    val trendingSongsByGenreData = response.data as? List<TrackByGenre>
                    trendingSongsByGenreData?.let {
                        trendingSongsByGenreData.forEach { newTrack ->
                            if(_tracks.find { x -> x.id == newTrack.id } == null) {
                                _tracks.add(newTrack)
                            }
                        }
                    }
                }
            }
            _trackByGenreFlow.emit(response)
        }
    }
}