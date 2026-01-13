package com.lihan.vibeplayer.music_list.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.lihan.vibeplayer.music_list.domain.AudioRepository
import com.lihan.vibeplayer.music_list.presentation.mapper.toUi
import com.lihan.vibeplayer.music_list.presentation.model.AudioUi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MusicListViewModel(
    private val audioRepository: AudioRepository
): ViewModel(){

    private var hasInitialLoadedData = false

    private val _exoPlayer = MutableStateFlow<ExoPlayer?>(null)
    val exoPlayer = _exoPlayer.asStateFlow()

    private val _state = MutableStateFlow(MusicListState())
    val state = _state
        .onStart {
            if (!hasInitialLoadedData){
                loadAudios()
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
            is MusicListAction.OnAudioUiClick -> onAudioClick(action.audioUi,action.context)
            MusicListAction.OnPlayClick -> onPlayClick()
            MusicListAction.OnSkipNextClick -> onSkipNextClick()
            else -> Unit
        }
    }

    private fun onSkipNextClick(){
        exoPlayer.value?.let { exoPlayer ->
            if (exoPlayer.hasNextMediaItem()){
                val nextIndex = exoPlayer.nextMediaItemIndex
                val currentAudioUis = state.value.audios
                val findAudio = currentAudioUis[nextIndex]
                _state.update { it.copy(
                    playingAudioUi = findAudio
                ) }
                exoPlayer.seekToNextMediaItem()
            }
        }
    }

    private fun onPlayClick(){
        exoPlayer.value?.let { exoPlayer ->
            val isPlaying = state.value.isPlaying
            if (isPlaying) {
                exoPlayer.pause()
            } else {
                exoPlayer.play()
            }
            _state.update {
                it.copy(
                    isPlaying = !it.isPlaying
                )
            }
        }
    }

    private fun onAudioClick(audioUi: AudioUi, context: Context){
        _state.update { it.copy(
            playingAudioUi = audioUi
        ) }
        initializePlayer(
            context = context,
            audioUis = state.value.audios,
            currentAudioUi = audioUi
        )
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

            _state.update { it.copy(
                audios = audios,
                isScanning = false
            ) }


        }
    }


    private fun initializePlayer(
        context: Context,
        audioUis: List<AudioUi>,
        currentAudioUi: AudioUi
    ) {
        var currentAudioUiIndex = 0
        val mediaItems = audioUis
            .filter { it.album != null }
            .mapIndexed{ index , audioUi ->
                if (audioUi.id == currentAudioUi.id){
                    currentAudioUiIndex = index
                }
                MediaItem.fromUri(audioUi.album!!)

            }
        if (exoPlayer.value == null){
            val exoPlayer = ExoPlayer
                .Builder(context)
                .build()
                .apply {
                    setMediaItems(mediaItems,currentAudioUiIndex,0L)
                    prepare()
                }
            _exoPlayer.update { exoPlayer }
        }else{
            exoPlayer.value?.setMediaItems(mediaItems,currentAudioUiIndex,0L)
            exoPlayer.value?.prepare()
        }




    }


}