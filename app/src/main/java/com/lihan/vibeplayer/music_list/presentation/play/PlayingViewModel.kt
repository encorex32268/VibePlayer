package com.lihan.vibeplayer.music_list.presentation.play

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lihan.vibeplayer.music_list.domain.AudioRepository
import com.lihan.vibeplayer.music_list.presentation.MusicListState
import com.lihan.vibeplayer.music_list.presentation.mapper.toUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlayingViewModel(
    private val audioRepository: AudioRepository
): ViewModel(){

    private var hasInitialLoadedData = false

    private val _state = MutableStateFlow(PlayingState())
    val state = _state
        .onStart {
            if (!hasInitialLoadedData){
                loadAudios()
                hasInitialLoadedData = true
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            PlayingState()
        )


    fun onAction(action: PlayingAction){
        when(action){
            PlayingAction.OnBackClick -> Unit
            PlayingAction.OnPlayClick -> {
                _state.update { it.copy(
                    isPlaying = !it.isPlaying
                ) }
            }
            is PlayingAction.OnSetCurrentAudio -> onSetCurrentAudio(action.id)
            PlayingAction.OnSkipNextClick -> TODO()
            PlayingAction.OnSkipPreviousClick -> TODO()
        }
    }

    private fun loadAudios(){
        val audios = audioRepository.getAudios().map { it.toUi() }
        _state.update { it.copy(
            audios = audios
        ) }
    }

    private fun onSetCurrentAudio(id: Long){
        val currentAudios = state.value.audios
        val findAudio = currentAudios.find { audio ->
            audio.id == id
        }
        _state.update { it.copy(
            currentAudio = findAudio
        ) }

        findAudio?.let { audio ->
            loadAlbumArt(audio.album)
        }
    }

    private fun loadAlbumArt(album: android.net.Uri?){
        viewModelScope.launch {
            if (album != null){
                val data = audioRepository.getAlbumArt(album)
                _state.update { it.copy(imageByteArray = data) }
            }
        }
    }

}