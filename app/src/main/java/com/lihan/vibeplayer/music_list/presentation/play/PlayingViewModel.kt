package com.lihan.vibeplayer.music_list.presentation.play

import android.content.Context
import androidx.compose.ui.util.fastFlatMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.lihan.vibeplayer.music_list.domain.AudioRepository
import com.lihan.vibeplayer.music_list.presentation.mapper.toUi
import com.lihan.vibeplayer.music_list.presentation.model.AudioUi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlayingViewModel(
    private val audioRepository: AudioRepository
) : ViewModel() {

    private var hasInitialLoadedData = false

    private val _uiEvent = Channel<PlayUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _exoPlayer = MutableStateFlow<ExoPlayer?>(null)
    val exoPlayer = _exoPlayer.asStateFlow()

    private val _state = MutableStateFlow(PlayingState())
    val state = _state
        .onStart {
            if (!hasInitialLoadedData) {

                hasInitialLoadedData = true
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            PlayingState()
        )


    fun onAction(action: PlayingAction) {
        when (action) {
            PlayingAction.OnBackClick -> Unit
            PlayingAction.OnPlayClick -> onPlayClick()
            is PlayingAction.OnSetupPlayer -> onSetupPlayer(action.id, action.context)
            PlayingAction.OnSkipNextClick -> onSkipNextClick()
            PlayingAction.OnSkipPreviousClick -> onSkipPreviousClick()
            else -> Unit
        }
    }

    private fun onSetupPlayer(id: Long, context: Context) {
        //get all audios
        val audios = audioRepository.getAudios().map { it.toUi() }
        //find current audio
        val findAudio = audios.find { audio ->
            audio.id == id
        } ?: run {
            viewModelScope.launch {
                _uiEvent.send(PlayUiEvent.OnAudioNotFound)
            }
            return
        }

        _state.update {
            it.copy(
                audios = audios,
                currentAudio = findAudio
            )
        }

        loadAlbumArt(findAudio.album)

        initializePlayer(
            context = context,
            audioUis = audios,
            currentAudioUi = findAudio
        )

    }

    private fun loadAlbumArt(album: android.net.Uri?) {
        viewModelScope.launch {
            if (album != null) {
                val data = audioRepository.getAlbumArt(album)
                _state.update { it.copy(imageByteArray = data) }
            }
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


        val exoPlayer = ExoPlayer
            .Builder(context)
            .build()
            .apply {
                setMediaItems(mediaItems,currentAudioUiIndex,0L)
                prepare()
            }

        _exoPlayer.update { exoPlayer }


    }


    private fun onPlayClick() {
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

    private fun onSkipPreviousClick(){
        exoPlayer.value?.let { exoPlayer ->
            if (exoPlayer.hasPreviousMediaItem()){
                val nextIndex = exoPlayer.previousMediaItemIndex
                val currentAudioUis = state.value.audios
                val findAudio = currentAudioUis[nextIndex]

                _state.update { it.copy(
                    currentAudio = findAudio
                ) }
                loadAlbumArt(findAudio.album)
                exoPlayer.seekToPreviousMediaItem()

            }
        }
    }

    private fun onSkipNextClick(){
        exoPlayer.value?.let { exoPlayer ->
            if (exoPlayer.hasNextMediaItem()){
                val nextIndex = exoPlayer.nextMediaItemIndex
                val currentAudioUis = state.value.audios
                val findAudio = currentAudioUis[nextIndex]

                _state.update { it.copy(
                    currentAudio = findAudio
                ) }
                loadAlbumArt(findAudio.album)
                exoPlayer.seekToNextMediaItem()

            }
        }
    }

    private fun releaseExoplayer() {
        exoPlayer.value?.release()
        _exoPlayer.update { null }
    }

    override fun onCleared() {
        super.onCleared()
        releaseExoplayer()
    }


}