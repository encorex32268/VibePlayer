@file:OptIn(ExperimentalCoroutinesApi::class)

package com.lihan.vibeplayer.music_list.presentation.playlistdetial

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lihan.vibeplayer.music_list.data.OfflineMusicListRepository
import com.lihan.vibeplayer.music_list.domain.FavouritesPlaylist
import com.lihan.vibeplayer.music_list.domain.Playlist
import com.lihan.vibeplayer.music_list.presentation.mapper.toUi
import com.lihan.vibeplayer.music_list.presentation.model.AudioUi
import com.lihan.vibeplayer.music_list.presentation.model.PlaylistCardStyle
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.core.net.toUri
import com.lihan.vibeplayer.music_list.domain.ExoPlayerManager
import com.lihan.vibeplayer.music_list.domain.MusicListRepository
import com.lihan.vibeplayer.music_list.presentation.mapper.toDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class PlaylistDetailViewModel(
    private val id: Int,
    private val repository: OfflineMusicListRepository
) : ViewModel() {

    private var hasInitialLoadedData = false

    private val _uiEvent = Channel<PlaylistDetailUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()


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


    fun onAction(action: PlaylistDetailAction) {
        when (action) {
            PlaylistDetailAction.OnBackClick -> Unit
            PlaylistDetailAction.OnAddSongClick -> {
                //TODO: Play First! next part do this
//                val playlistUi = state.value.playlistUi ?: return
//                viewModelScope.launch {
//                    _uiEvent.send(
//                        PlaylistDetailUiEvent.OnNavigateToAddSongs(playlistUi.id)
//                    )
//                }
            }

            PlaylistDetailAction.OnFunctionPlayClick -> Unit
        }
    }


    private fun initPlaylistUi() {
        val isFavouritesPlaylistId = id == -1
        val playlistFlow = if (isFavouritesPlaylistId) {
            repository.getFavouritesPlaylist()
        } else {
            repository.getPlaylistById(id)
        }

        combine(
            flow = playlistFlow,
            flow2 = repository.getAllAudios()
        ) { playlist, allAudios ->
            val playlistUi = when (playlist) {
                is FavouritesPlaylist -> playlist.toUi()
                is Playlist -> playlist.toUi(coverStyle = PlaylistCardStyle.NoCover)
                else -> null
            }

            if (playlistUi == null) return@combine null

            val audioMap = allAudios.associateBy { it.id.toString() }
            val playlistAudios = playlistUi.audioIds.mapNotNull { audioMap[it] }

            Pair(playlistUi, playlistAudios)

        }
            .filterNotNull()
            .flatMapLatest { (playlistUi, audios) ->
                flow {
                    val audioUiList = audios.map { it.toUi() }.map { audioUi ->
                        val albumImage = repository.getAlbumArtImage(audioUi.album)
                        audioUi.copy(albumImage = albumImage)
                    }

                    val firstAudio = audios.firstOrNull()
                    val coverStyle = when {
                        playlistUi.coverImageUriString != null -> {
                            PlaylistCardStyle.HasCover(playlistUi.coverImageUriString.toUri())
                        }

                        firstAudio?.album != null && firstAudio.album != Uri.EMPTY -> {
                            PlaylistCardStyle.HasCover(repository.getAlbumArtImage(firstAudio.album))
                        }

                        else -> PlaylistCardStyle.NoCover
                    }
                    emit(audioUiList to playlistUi.copy(style = coverStyle))

                }.flowOn(Dispatchers.IO)

            }.onEach { (audios, playlist) ->
                _state.update {
                    it.copy(
                        audios = audios,
                        playlistUi = playlist
                    )
                }
            }
            .launchIn(viewModelScope)


    }
}