package com.lihan.vibeplayer.music_list.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import com.lihan.vibeplayer.music_list.domain.AudioRepository
import com.lihan.vibeplayer.music_list.domain.ExoPlayerService
import com.lihan.vibeplayer.music_list.presentation.mapper.toUi
import com.lihan.vibeplayer.music_list.presentation.model.AudioUi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MusicListViewModel(
    private val audioRepository: AudioRepository,
    private val exoPlayerService: ExoPlayerService
): ViewModel(){

    private var hasInitialLoadedData = false

    private val _state = MutableStateFlow(MusicListState())
    val state = _state
        .onStart {
            if (!hasInitialLoadedData){
                loadAudios()
                observerPlayer()
                hasInitialLoadedData = true
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            MusicListState()
        )



    fun onAction(action: MusicListAction){
        when(action){
            MusicListAction.OnScanAgainClick -> loadAudios()
            MusicListAction.OnShuffleClick -> onShuffleClick()
            is MusicListAction.OnAudioUiClick -> onAudioClick(action.audioUi)
            MusicListAction.OnPlayClick -> onPlayClick()
            MusicListAction.OnSkipNextClick -> onSkipNextClick()
            else -> Unit
        }
    }

    private fun onSkipNextClick(){
        exoPlayerService.seekToNextMediaItem()
    }

    private fun onPlayClick(){
        val isPlaying = state.value.isPlaying
        if (isPlaying) {
            exoPlayerService.pause()
        } else {
            exoPlayerService.play()
        }
    }

    private fun onAudioClick(audioUi: AudioUi){
        _state.update { it.copy(
            playingAudioUi = audioUi
        ) }
        exoPlayerService.playByIndex(0)
    }

    private fun onShuffleClick(){
        val shuffledAudios = state.value.audios.shuffled()
        _state.update { it.copy(
            audios = shuffledAudios
        ) }
    }

    private fun loadAudios(){
        viewModelScope.launch {
            _state.update { it.copy(
                isScanning = true
            ) }
            delay(1000)

            val audios = audioRepository
                .getAllAudios()
                .map { audio -> audio.toUi() }

            //set player List
            exoPlayerService.setPlayList(
                mediaItems = audios
                    .filterNot { it.album == null }
                    .map {
                        MediaItem.Builder()
                            .setUri(it.album)
                            .build()
                    }
            )

            _state.update { it.copy(
                audios = audios,
                isScanning = false
            ) }


        }
    }

    private fun observerPlayer(){
        exoPlayerService.isPlaying
            .onEach { isPlaying ->
                _state.update { it.copy(isPlaying = isPlaying) }
            }.launchIn(viewModelScope)

        exoPlayerService.playbackProgress.onEach { playbackProgress ->
            _state.update { it.copy(playbackProgress = playbackProgress) }
        }.launchIn(viewModelScope)
    }

}