@file:OptIn(ExperimentalCoroutinesApi::class)

package com.lihan.vibeplayer.music_list.presentation.playlistdetial

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lihan.vibeplayer.music_list.data.OfflineMusicListRepository
import com.lihan.vibeplayer.music_list.domain.FavouritesPlaylist
import com.lihan.vibeplayer.music_list.domain.Playlist
import com.lihan.vibeplayer.music_list.presentation.mapper.toUi
import com.lihan.vibeplayer.music_list.presentation.model.PlaylistCardStyle
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class PlaylistDetailViewModel(
    private val id: Int,
    private val repository: OfflineMusicListRepository
) : ViewModel() {

    private var hasInitialLoadedData = false

    private val _state = MutableStateFlow(PlaylistDetailState())
    val state = _state.onStart {
        if (!hasInitialLoadedData) {
            initPlaylistUi()
            hasInitialLoadedData = true
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        PlaylistDetailState()
    )

    //TODO: Need Fix
    private fun initPlaylistUi() {
//        val isFavouritesPlaylistId = id == -1
//
//        viewModelScope.launch {
//            val playlist = repository.getPlaylistById(id).firstOrNull()
//
//            var currentPlaylistUi = when (playlist) {
//                is FavouritesPlaylist -> playlist.toUi()
//                is Playlist -> playlist.toUi(coverStyle = PlaylistCardStyle.NoCover)
//                else -> null
//            }
//
//            if (currentPlaylistUi == null){
//                return@launch
//            }
//
//            currentPlaylistUi.let { playlistUi ->
//                repository
//                    .getAudiosByIds(ids = playlistUi.audioIds.mapNotNull { it.toIntOrNull() })
//                    .onEach { audios ->
//                        val audioUiList = audios.map { it.toUi() }.map { audioUi ->
//                            val albumImage = repository.getAlbumArtImage(audioUi.album)
//                            audioUi.copy(albumImage = albumImage)
//                        }
//
//                        val firstAudio = audios.firstOrNull()
//                        val coverStyle = when {
//                            playlistUi.coverImageUriString != null -> {
//                                PlaylistCardStyle.HasCover(playlistUi.coverImageUriString.toUri())
//                            }
//
//                            firstAudio?.album != null && firstAudio.album != Uri.EMPTY -> {
//                                PlaylistCardStyle.HasCover(repository.getAlbumArtImage(firstAudio.album))
//                            }
//                            playlistUi.style == PlaylistCardStyle.Favourites -> {
//                                PlaylistCardStyle.Favourites
//                            }
//                            else -> PlaylistCardStyle.NoCover
//                        }
//                        currentPlaylistUi = playlistUi.copy(
//                            style = coverStyle
//                        )
//
//                        val coverImageString = playlistUi.coverImageUriString
//                        val coverImagePair =
//                            when {
//                                !coverImageString.isNullOrEmpty() -> coverImageString.toUri() to coverImageString
//                                currentPlaylistUi.audioIds.isNotEmpty() -> (currentPlaylistUi.style as PlaylistCardStyle.HasCover).imageModel to "${currentPlaylistUi.id}_${currentPlaylistUi.audioIds.first()}"
//                                else -> null to ""
//                            }
//
//                        _state.update {
//                            it.copy(
//                                audios = audioUiList,
//                                playlistUi = currentPlaylistUi,
//                                coverImagePair = coverImagePair
//                            )
//                        }
//
//                    }
//                    .launchIn(this)
//
//            }
//        }
    }
}