package com.lihan.vibeplayer.music_list.presentation.playlistdetial

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lihan.vibeplayer.music_list.data.OfflineMusicListRepository
import com.lihan.vibeplayer.music_list.presentation.mapper.toUi
import com.lihan.vibeplayer.music_list.presentation.model.AudioUi
import com.lihan.vibeplayer.music_list.presentation.model.PlaylistCardStyle
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlaylistDetailViewModel(
    private val id: Int,
    private val repository: OfflineMusicListRepository
): ViewModel() {

    private var hasInitialLoadedData = false

    private val _state = MutableStateFlow(PlaylistDetailState())
    val state = _state.onStart {
        if (!hasInitialLoadedData){
            initPlaylistUi()
            hasInitialLoadedData = true
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        PlaylistDetailState()
    )



    fun onAction(action: PlaylistDetailAction){
        when(action){
            PlaylistDetailAction.OnBackClick -> Unit
        }
    }


    private fun initPlaylistUi(){
        viewModelScope.launch {
            _state.update { it.copy(
                isLoading = true
            ) }
            val isFavouritesPlaylistId = id == -1

            val playlistUi = if (isFavouritesPlaylistId){
                repository.getFavouritesPlaylist().firstOrNull()?.toUi()
            }else{
                repository.getPlaylistById(id).firstOrNull()?.toUi(PlaylistCardStyle.NoCover)
            }

            if (playlistUi == null){
                return@launch
            }
            val audios = repository.getAllAudios().firstOrNull()?:emptyList()
            if (audios.isEmpty()){
                return@launch
            }

            val firstAudio = audios.first()
            val coverStyle = when{
                playlistUi.coverImageUriString != null -> {
                    PlaylistCardStyle.HasCover(
                        Uri.parse(playlistUi.coverImageUriString)
                    )
                }
                firstAudio.album != Uri.EMPTY -> {
                    PlaylistCardStyle.HasCover(
                        repository.getAlbumArtImage(firstAudio.album)
                    )
                }
                else -> {
                    PlaylistCardStyle.NoCover
                }
            }

            val allAudios = audios.associateBy { it.id.toString() }
            val playlistAudios = playlistUi.audioIds.mapNotNull {
                allAudios[it]
            }
            val hasImageAudios = coroutineScope {
                playlistAudios.map { audio ->
                    async{
                        val audioUi = audio.toUi()
                        val albumImage = repository.getAlbumArtImage(audioUi.album)
                        audioUi.copy(albumImage = albumImage)
                    }
                }
            }

            _state.update { it.copy(
                audios = hasImageAudios.awaitAll(),
                playlistUi = playlistUi.copy(
                    style = coverStyle
                ),
                isLoading = false
            ) }
        }

    }
}