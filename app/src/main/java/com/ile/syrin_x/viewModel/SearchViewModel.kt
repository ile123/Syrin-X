package com.ile.syrin_x.viewModel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ile.syrin_x.data.enums.MusicSource
import com.ile.syrin_x.data.model.UnifiedPlaylist
import com.ile.syrin_x.data.model.UnifiedTrack
import com.ile.syrin_x.data.model.UnifiedUser
import com.ile.syrin_x.data.model.entity.FavoriteArtist
import com.ile.syrin_x.data.model.soundcloud.SoundCloudPlaylistResponse
import com.ile.syrin_x.data.model.soundcloud.SoundCloudTrackResponse
import com.ile.syrin_x.data.model.soundcloud.SoundCloudUserResponse
import com.ile.syrin_x.data.model.spotify.SpotifyAlbum
import com.ile.syrin_x.data.model.spotify.SpotifyResponse
import com.ile.syrin_x.domain.core.Response
import com.ile.syrin_x.domain.usecase.auth.GetUserUidUseCase
import com.ile.syrin_x.domain.usecase.musicsource.soundcloud.SearchPlaylistsSoundCloudUseCase
import com.ile.syrin_x.domain.usecase.musicsource.soundcloud.SearchTracksSoundCloudUseCase
import com.ile.syrin_x.domain.usecase.musicsource.soundcloud.SearchUsersSoundCloudUseCase
import com.ile.syrin_x.domain.usecase.musicsource.spotify.SearchAlbumsSpotifyUseCase
import com.ile.syrin_x.domain.usecase.musicsource.spotify.SearchAllSpotifyUseCase
import com.ile.syrin_x.domain.usecase.musicsource.spotify.SearchPlaylistsSpotifyUseCase
import com.ile.syrin_x.domain.usecase.musicsource.spotify.SearchTracksSpotifyUseCase
import com.ile.syrin_x.domain.usecase.user.AddOrRemoveArtistFromFavoritesUseCase
import com.ile.syrin_x.domain.usecase.user.GetAllFavoriteArtistsByUserUseCase
import com.ile.syrin_x.utils.GlobalContext
import com.ile.syrin_x.utils.toSoundCloudUnifiedPlaylists
import com.ile.syrin_x.utils.toSoundCloudUnifiedTracks
import com.ile.syrin_x.utils.toSoundCloudUnifiedUsers
import com.ile.syrin_x.utils.toSpotifyUnifiedPlaylists
import com.ile.syrin_x.utils.toSpotifyUnifiedTracks
import com.ile.syrin_x.utils.toSpotifyUnifiedUsers
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchAllSpotifyUseCase: SearchAllSpotifyUseCase,
    private val searchTracksSpotifyUseCase: SearchTracksSpotifyUseCase,
    private val searchAlbumsSpotifyUseCase: SearchAlbumsSpotifyUseCase,
    private val searchPlaylistsSpotifyUseCase: SearchPlaylistsSpotifyUseCase,
    private val searchUsersSpotifyUseCase: SearchAlbumsSpotifyUseCase,
    private val searchTracksSoundCloudUseCase: SearchTracksSoundCloudUseCase,
    private val searchPlaylistsSoundCloudUseCase: SearchPlaylistsSoundCloudUseCase,
    private val searchUsersSoundCloudUseCase: SearchUsersSoundCloudUseCase,
    private val getUserUidUseCase: GetUserUidUseCase,
    private val getAllFavoriteArtistsByUserUseCase: GetAllFavoriteArtistsByUserUseCase,
    private val addOrRemoveArtistFromFavoritesUseCase: AddOrRemoveArtistFromFavoritesUseCase
): ViewModel() {

    private val _searchFlow = MutableSharedFlow<Response<Any>>()
    val searchFlow = _searchFlow

    val offsets = mutableMapOf(
        "tracks" to 0,
        "albums" to 0,
        "playlists" to 0,
        "users" to 0
    )

    val searchedTracks = mutableStateListOf<UnifiedTrack>()
    val searchedPlaylists = mutableStateListOf<UnifiedPlaylist>()
    val searchedAlbums = mutableStateListOf<SpotifyAlbum>()
    val searchedUsers = mutableStateListOf<UnifiedUser>()
    val favoriteArtists = mutableStateListOf<FavoriteArtist>()

    var searchedMusicSource = MusicSource.NOT_SPECIFIED

    private var searchedKeyword = ""

    fun clearPreviouslyFetchedContent() {
        searchedTracks.clear()
        searchedPlaylists.clear()
        searchedAlbums.clear()
        searchedUsers.clear()
        favoriteArtists.clear()
    }

    fun fetchAllUsersFavoriteArtists() = viewModelScope.launch {
        getUserUidUseCase.invoke().collect { userUid ->
            getAllFavoriteArtistsByUserUseCase(userUid).collect { response ->
                when(response) {
                    is Response.Error -> {
                        Log.d("SearchViewModel", response.message)
                    }
                    is Response.Loading -> {  }
                    is Response.Success<*> -> {
                        val usersFavoriteArtistsData = response.data as? List<FavoriteArtist>
                        usersFavoriteArtistsData?.let {
                            favoriteArtists.addAll(usersFavoriteArtistsData)
                        }
                    }
                }
            }
        }
    }

    fun searchAll(keyword: String, musicSource: String) = viewModelScope.launch {
        clearPreviouslyFetchedContent()
        when (musicSource) {
            "Spotify" -> {
                searchedMusicSource = MusicSource.SPOTIFY
                searchedKeyword = keyword
                searchAllSpotifyUseCase(
                    keyword,
                    GlobalContext.Tokens.spotifyToken
                ).collect { response ->
                    when (response) {
                        is Response.Loading -> {
                        }
                        is Response.Success<*> -> {
                            if (response.data is SpotifyResponse) {
                                val spotifyResponse = response.data
                                searchedTracks.addAll(spotifyResponse.tracks?.items?.toSpotifyUnifiedTracks() ?: emptyList())
                                searchedPlaylists.addAll(spotifyResponse.playlists?.items?.toSpotifyUnifiedPlaylists() ?: emptyList())
                                searchedUsers.addAll(spotifyResponse.artists?.items?.toSpotifyUnifiedUsers() ?: emptyList())
                                spotifyResponse.albums?.items?.let { searchedAlbums.addAll(it) }

                                _searchFlow.emit(Response.Success(true))
                            }
                        }
                        is Response.Error -> {
                            Log.d("SearchViewModel", response.message)
                        }
                    }
                    _searchFlow.emit(response)
                }
            }
            "SoundCloud" -> {
                val accessToken = GlobalContext.Tokens.soundCloudToken
                searchedMusicSource = MusicSource.SOUNDCLOUD
                searchedKeyword = keyword
                combine(
                    searchTracksSoundCloudUseCase(keyword, 50, 0, accessToken),
                    searchUsersSoundCloudUseCase(keyword, 50, 0, accessToken),
                    searchPlaylistsSoundCloudUseCase(keyword, 50, 0, accessToken)
                ) { tracks, users, playlists ->
                    when {
                        tracks is Response.Success<*> &&
                                users is Response.Success<*> &&
                                playlists is Response.Success<*> -> {
                            val trackResponse = tracks.data as? SoundCloudTrackResponse
                            searchedTracks.addAll(trackResponse?.collection?.toSoundCloudUnifiedTracks() ?: emptyList())

                            val userResponse = users.data as? SoundCloudUserResponse
                            searchedUsers.addAll(userResponse?.collection?.toSoundCloudUnifiedUsers() ?: emptyList())

                            val playlistResponse = playlists.data as? SoundCloudPlaylistResponse
                            searchedPlaylists.addAll(playlistResponse?.collection?.toSoundCloudUnifiedPlaylists() ?: emptyList())
                            Response.Success(true)
                        }
                        tracks is Response.Error -> tracks
                        users is Response.Error -> users
                        playlists is Response.Error -> playlists
                        else -> Response.Loading
                    }

                }.collect { response ->
                    _searchFlow.emit(response)
                }
            }
        }
    }

    fun fetchMoreTracksForInfiniteScroll() {
        offsets.merge("tracks", 1) { current, value -> current + value }
        viewModelScope.launch {
            when(searchedMusicSource) {
                MusicSource.SPOTIFY -> {
                    searchTracksSpotifyUseCase(
                        searchedKeyword,
                        30,
                        offsets["tracks"]?.toLong() ?: 0L,
                        GlobalContext.Tokens.spotifyToken).collect { response ->
                        when (response) {
                            is Response.Loading -> {
                            }
                            is Response.Success<*> -> {
                                if (response.data is SpotifyResponse) {
                                    val spotifyResponse = response.data.tracks?.items?.toSpotifyUnifiedTracks() ?: emptyList()
                                    spotifyResponse.forEach {
                                        if (searchedTracks.find { x-> x.id == it.id } == null) {
                                            searchedTracks.add(it)
                                        }
                                    }

                                    _searchFlow.emit(Response.Success(true))
                                }
                            }
                            is Response.Error -> {
                                Log.d("SearchViewModel", response.message)
                            }
                        }
                        _searchFlow.emit(response)
                    }
                }

                MusicSource.SOUNDCLOUD -> {
                    searchTracksSoundCloudUseCase(searchedKeyword,
                        30,
                        offsets["tracks"]?.toLong() ?: 0L,
                        GlobalContext.Tokens.soundCloudToken).collect { response ->
                        when (response) {
                            is Response.Error -> {
                                Log.d("SearchViewModel", response.message)
                            }
                            Response.Loading -> { }
                            is Response.Success<*> -> {
                                val trackResponse = response.data as? SoundCloudTrackResponse
                                val soundCloudTracks = trackResponse?.collection?.toSoundCloudUnifiedTracks() ?: emptyList()
                                soundCloudTracks.forEach {
                                    if(searchedTracks.find { x -> x.id == it.id } == null) {
                                        searchedTracks.add(it)
                                    }
                                }

                                _searchFlow.emit(Response.Success(true))
                            }
                        }
                        _searchFlow.emit(response)
                    }
                }
                MusicSource.NOT_SPECIFIED -> { }
            }
        }
    }

    fun fetchMorePlaylistsForInfiniteScroll() {
        offsets.merge("playlists", 1) { current, value -> current + value }
        viewModelScope.launch {
            when(searchedMusicSource) {
                MusicSource.SPOTIFY -> {
                    searchPlaylistsSpotifyUseCase(
                        searchedKeyword,
                        30,
                        offsets["playlists"]?.toLong() ?: 0L,
                        GlobalContext.Tokens.spotifyToken).collect { response ->
                        when (response) {
                            is Response.Loading -> {
                            }
                            is Response.Success<*> -> {
                                if (response.data is SpotifyResponse) {
                                    val spotifyResponse = response.data.playlists?.items?.toSpotifyUnifiedPlaylists() ?: emptyList()
                                    spotifyResponse.forEach {
                                        if(searchedPlaylists.find { x -> x.id == it.id } == null) {
                                            searchedPlaylists.add(it)
                                        }
                                    }

                                    _searchFlow.emit(Response.Success(true))
                                }
                            }
                            is Response.Error -> {
                                Log.d("SearchViewModel", response.message)
                            }
                        }
                        _searchFlow.emit(response)
                    }
                }

                MusicSource.SOUNDCLOUD -> {
                    searchPlaylistsSoundCloudUseCase(searchedKeyword,
                        30,
                        offsets["playlists"]?.toLong() ?: 0L,
                        GlobalContext.Tokens.soundCloudToken).collect { response ->
                        when (response) {
                            is Response.Error -> {
                                Log.d("SearchViewModel", response.message)
                            }
                            Response.Loading -> { }
                            is Response.Success<*> -> {
                                val playlistResponse = response.data as? SoundCloudPlaylistResponse
                                val soundcloudPlaylists = playlistResponse?.collection?.toSoundCloudUnifiedPlaylists() ?: emptyList()
                                soundcloudPlaylists.forEach {
                                    if(searchedPlaylists.find { x -> x.id == it.id } == null) {
                                        searchedPlaylists.add(it)
                                    }
                                }

                                _searchFlow.emit(Response.Success(true))
                            }
                        }
                        _searchFlow.emit(response)
                    }
                }
                MusicSource.NOT_SPECIFIED -> { }
            }
        }
    }

    fun fetchMoreAlbumsForInfiniteScroll() {
        offsets.merge("albums", 1) { current, value -> current + value }
        viewModelScope.launch {
            searchAlbumsSpotifyUseCase(
                searchedKeyword,
                30,
                offsets["tracks"]?.toLong() ?: 0L,
                GlobalContext.Tokens.spotifyToken).collect { response ->
                when (response) {
                    is Response.Loading -> {
                    }
                    is Response.Success<*> -> {
                        if (response.data is SpotifyResponse) {
                            val spotifyResponse = response.data.albums?.items
                            spotifyResponse?.forEach {
                                if(searchedAlbums.find { x -> x.id == it.id } == null) {
                                    searchedAlbums.add(it)
                                }
                            }

                            _searchFlow.emit(Response.Success(true))
                        }
                    }
                    is Response.Error -> {
                        Log.d("SearchViewModel", response.message)
                    }
                }
                _searchFlow.emit(response)
            }
        }
    }

    fun fetchMoreUsersForInfiniteScroll() {
        offsets.merge("users", 1) { current, value -> current + value }
        viewModelScope.launch {
            when(searchedMusicSource) {
                MusicSource.SPOTIFY -> {
                    searchUsersSpotifyUseCase(
                        searchedKeyword,
                        30,
                        offsets["users"]?.toLong() ?: 0L,
                        GlobalContext.Tokens.spotifyToken).collect { response ->
                        when (response) {
                            is Response.Loading -> {
                            }
                            is Response.Success<*> -> {
                                if (response.data is SpotifyResponse) {
                                    val spotifyResponse = response.data.artists?.items?.toSpotifyUnifiedUsers() ?: emptyList()
                                    spotifyResponse.forEach {
                                        if(searchedUsers.find { x -> x.id == it.id } == null) {
                                            searchedUsers.add(it)
                                        }
                                    }

                                    _searchFlow.emit(Response.Success(true))
                                }
                            }
                            is Response.Error -> {
                                Log.d("SearchViewModel", response.message)
                            }
                        }
                        _searchFlow.emit(response)
                    }
                }

                MusicSource.SOUNDCLOUD -> {
                    searchUsersSoundCloudUseCase(searchedKeyword,
                        30,
                        offsets["users"]?.toLong() ?: 0L,
                        GlobalContext.Tokens.soundCloudToken).collect { response ->
                        when (response) {
                            is Response.Error -> {
                                Log.d("SearchViewModel", response.message)
                            }
                            Response.Loading -> { }
                            is Response.Success<*> -> {
                                val playlistResponse = response.data as? SoundCloudPlaylistResponse
                                val soundCloudPlaylists = playlistResponse?.collection?.toSoundCloudUnifiedPlaylists() ?: emptyList()
                                soundCloudPlaylists.forEach {
                                    if(searchedPlaylists.find { x -> x.id == it.id } == null) {
                                        searchedPlaylists.add(it)
                                    }
                                }

                                _searchFlow.emit(Response.Success(true))
                            }
                        }
                        _searchFlow.emit(response)
                    }
                }
                MusicSource.NOT_SPECIFIED -> { }
            }
        }
    }

    fun toggleArtistFavorite(artist: UnifiedUser) = viewModelScope.launch {
        val userUid = getUserUidUseCase.invoke().first()
        val fav = FavoriteArtist(artist.id, userUid, artist.name.orEmpty())

        val currentlyFavorited = favoriteArtists.any { it.id == fav.id }

        if (currentlyFavorited) {
            favoriteArtists.removeAll { it.id == fav.id }
        } else {
            favoriteArtists.add(fav)
        }

        addOrRemoveArtistFromFavoritesUseCase(fav).collect { response ->
            if (response is Response.Error) {
                if (currentlyFavorited) {
                    favoriteArtists.add(fav)
                } else {
                    favoriteArtists.removeAll { it.id == fav.id }
                }
                Log.e("SearchViewModel", "Toggle favorite failed: ${response.message}")
            }
        }
    }


    fun addOrRemoveArtistFromUserFavorites(artist: UnifiedUser) = viewModelScope.launch {
        val userUid = getUserUidUseCase.invoke().first()
        val fav = FavoriteArtist(artist.id, userUid, artist.name.orEmpty())

        addOrRemoveArtistFromFavoritesUseCase(fav).collect { response ->
            when (response) {
                is Response.Success<*> -> {
                    if (favoriteArtists.any { it.id == fav.id }) {
                        favoriteArtists.removeAll { it.id == fav.id }
                    } else {
                        favoriteArtists.add(fav)
                    }
                }
                is Response.Error -> {
                    Log.e("SearchViewModel", "couldn't toggle favorite: ${response.message}")
                }
                else -> { }
            }
        }
    }
}