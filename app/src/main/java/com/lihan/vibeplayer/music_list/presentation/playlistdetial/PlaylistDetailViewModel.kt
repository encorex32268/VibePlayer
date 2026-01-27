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
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

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

            }.onEach { (audios, playlistUi) ->
                val coverImageString = playlistUi.coverImageUriString
                val coverImagePair = if (coverImageString.isNullOrEmpty()){
                    if (audios.isEmpty()){
                        null to ""
                    }else{
                        val firstAudio = audios.first()

                        (playlistUi.style as PlaylistCardStyle.HasCover).imageModel to firstAudio.id.toString()
                    }
                }else{
                    coverImageString.toUri() to coverImageString
                }

                _state.update {
                    it.copy(
                        audios = audios,
                        playlistUi = playlistUi,
                        coverImagePair = coverImagePair
                    )
                }
            }
            .launchIn(viewModelScope)


    }
}