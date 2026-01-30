@file:OptIn(ExperimentalCoroutinesApi::class)

package com.lihan.vibeplayer.music_list.presentation.playlistdetial

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lihan.vibeplayer.core.domain.FAVOURITES_ID
import com.lihan.vibeplayer.music_list.data.OfflineMusicListRepository
import com.lihan.vibeplayer.music_list.domain.Audio
import com.lihan.vibeplayer.music_list.presentation.mapper.toUi
import com.lihan.vibeplayer.music_list.presentation.model.AudioUi
import com.lihan.vibeplayer.music_list.presentation.model.PlaylistCardStyle
import com.lihan.vibeplayer.music_list.presentation.model.PlaylistUi
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

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

        val isFavouritesPlaylistId = id == FAVOURITES_ID

        if(isFavouritesPlaylistId){
            repository
                .getFavouriteAudios()
                .onEach {audios ->
                    val hasImageAudios = toDeferredAlbumArtImages(audios)

                    _state.update { it.copy(
                        playlistUi = PlaylistUi(
                            id = FAVOURITES_ID,
                            style = PlaylistCardStyle.Favourites
                        ),
                        audios = hasImageAudios.awaitAll()
                    ) }
                }.launchIn(viewModelScope)

        }else{
            repository
                .getPlaylistAudiosById(id)
                .filterNotNull()
                .onEach { playlistAudio ->
                    val audios = playlistAudio.audios
                    var playlistUi = playlistAudio.playlist.toUi(audios.size)

                    val firstAudio = audios.firstOrNull()
                    val coverStyle = when{
                        playlistUi.coverImageUriString != null -> {
                            PlaylistCardStyle.HasCover(
                                imageModel = playlistUi.coverImageUriString.toUri(),
                                isUploadedImage = true
                            )
                        }
                        firstAudio!=null && firstAudio.album != Uri.EMPTY -> {
                            val image = repository.getAlbumArtImage(firstAudio.album)
                            PlaylistCardStyle.HasCover(
                                imageModel = image,
                                isUploadedImage = false
                            )
                        }
                        else -> playlistUi.style
                    }
                    val hasImageAudios = toDeferredAlbumArtImages(audios)

                    playlistUi = playlistUi.copy(
                        style = coverStyle
                    )

                    _state.update { it.copy(
                        playlistUi = playlistUi,
                        audios = hasImageAudios.awaitAll()
                    ) }


                }.launchIn(viewModelScope)

        }
    }


    private suspend fun toDeferredAlbumArtImages(audios: List<Audio>): List<Deferred<AudioUi>>{
        return coroutineScope {
            audios.map { audio ->
                async {
                    val audioUi = audio.toUi()
                    val albumImage = repository.getAlbumArtImage(audioUi.album)
                    audioUi.copy(albumImage = albumImage)
                }
            }
        }
    }
}