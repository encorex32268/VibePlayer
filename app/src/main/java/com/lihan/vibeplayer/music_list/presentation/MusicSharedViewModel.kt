package com.lihan.vibeplayer.music_list.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import com.lihan.vibeplayer.R
import com.lihan.vibeplayer.core.presentation.util.UiText
import com.lihan.vibeplayer.music_list.domain.ExoPlayerManager
import com.lihan.vibeplayer.music_list.presentation.model.AudioUi
import com.lihan.vibeplayer.music_list.presentation.model.RepeatModeStatus
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MusicSharedViewModel(
    private val exoPlayerManager: ExoPlayerManager
) : ViewModel() {

    private var hasInitialLoadedData = false

    private var progressJob: Job? = null

    private var exoPlayerListener: Player.Listener? = null

    private val _state = MutableStateFlow(MusicSharedState())
    val state = _state
        .onStart {
            if (!hasInitialLoadedData) {
                observePlayer()
                hasInitialLoadedData = true
            }
        }
        .onEach {
            println("MusicSharedViewModel ${it.playingAudioUi?.songTitle}")
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            MusicSharedState()
        )

    init {
        println("MusicSharedViewModel is Creadted")
    }

    fun onAction(action: MusicSharedAction) {
        when (action) {
            MusicSharedAction.OnCollapseClick -> {
                _state.update {
                    it.copy(
                        isExpandPlayer = false
                    )
                }
            }

            MusicSharedAction.OnExpandClick -> {
                _state.update {
                    it.copy(
                        isExpandPlayer = true
                    )
                }
            }

            MusicSharedAction.OnHideModeChangedBanner -> {
                _state.update {
                    it.copy(
                        modeStatusBanner = null
                    )
                }
            }
            MusicSharedAction.OnShuffleClick -> exoPlayerManager.shuffleEnabled()
            MusicSharedAction.OnSkipNextClick -> exoPlayerManager.skipNext()
            MusicSharedAction.OnSkipPreviousClick -> exoPlayerManager.skipPrevious()
            MusicSharedAction.OnFunctionPlayClick -> exoPlayerManager.quickPlay()
            MusicSharedAction.OnFunctionShuffleClick -> {
                exoPlayerManager.quickShuffledPlay()
                _state.update { it.copy(
                    isExpandPlayer = true
                )}
            }
            MusicSharedAction.OnPlayClick -> onPlayClick()
            MusicSharedAction.OnRepeatClick -> onRepeatClick()
            is MusicSharedAction.OnSeek -> onSeek(action.duration)
            is MusicSharedAction.OnSongClick -> onSongClick(action.audioUi)
        }
    }

    private fun onPlayClick() {
        val isPlaying = state.value.isPlaying
        if (isPlaying) {
            exoPlayerManager.pause()
        } else {
            exoPlayerManager.play()
        }
    }

    private fun onSeek(duration: Long) {
        exoPlayerManager.seekTo(duration)
        _state.update {
            it.copy(
                currentPosition = duration
            )
        }
    }


    private fun onRepeatClick() {
        val currentRepeatMode = exoPlayerManager.getRepeatMode()
        val newRepeatMode = when (currentRepeatMode) {
            Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
            Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
            Player.REPEAT_MODE_ONE -> Player.REPEAT_MODE_OFF
            else -> Player.REPEAT_MODE_OFF
        }
        exoPlayerManager.setRepeatMode(newRepeatMode)
    }

    private fun onSongClick(audioUi: AudioUi) {
        _state.update {
            it.copy(
                playingAudioUi = audioUi
            )
        }

        val currentMediaItems = exoPlayerManager.getAllMediaItems()
        val mediaItem = currentMediaItems.find { it.mediaId == audioUi.id.toString() }
        if (mediaItem == null){
            return
        }

        val index = currentMediaItems.indexOf(mediaItem)
        if (index == -1){
            //Not found
            return
        }
        exoPlayerManager.playSongByIndex(index)
    }

    private fun observePlayer() {
        exoPlayerListener = object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _state.update {
                    it.copy(
                        isPlaying = isPlaying
                    )
                }
                if (isPlaying) {
                    startProgressTimer()
                } else {
                    progressJob?.cancel()
                }
            }

            override fun onEvents(player: Player, events: Player.Events) {

                val currentMediaItem = exoPlayerManager.getCurrentMediaItem()
                val currentId = currentMediaItem?.mediaId

                if (currentId != null && currentId != state.value.playingAudioUi?.id?.toString() && state.value.playingAudioUi != null) {
                    val currentAudio =
                        state.value.playingQueue.find { it.id.toString() == currentId }
                    _state.update {
                        it.copy(
                            playingAudioUi = currentAudio
                        )
                    }
                }
            }

            override fun onTimelineChanged(timeline: Timeline, reason: Int) {
                updateShuffledList()
            }

            override fun onRepeatModeChanged(repeatMode: Int) {
                val repeatModeStatus = when (repeatMode) {
                    Player.REPEAT_MODE_OFF -> RepeatModeStatus.Off
                    Player.REPEAT_MODE_ALL -> RepeatModeStatus.All
                    Player.REPEAT_MODE_ONE -> RepeatModeStatus.One
                    else -> RepeatModeStatus.Off
                }

                _state.update {
                    it.copy(
                        modeStatusBanner = repeatModeStatus.toUiText(),
                        repeatModeStatus = repeatModeStatus
                    )
                }
            }

            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                updateShuffledList()
                _state.update {
                    it.copy(
                        isEnabledShuffle = shuffleModeEnabled,
                        modeStatusBanner = when (shuffleModeEnabled) {
                            true -> UiText.StringResource(R.string.main_shuffle_enabled)
                            false -> UiText.StringResource(R.string.main_shuffle_off)
                        }
                    )
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_READY -> {
                        val currentPosition = exoPlayerManager.currentPosition
                        val duration = exoPlayerManager.duration

                        _state.update {
                            it.copy(
                                currentPosition = currentPosition,
                                duration = duration,
                            )
                        }
                    }

                    Player.STATE_ENDED -> {
                        progressJob?.cancel()
                    }

                    else -> Unit
                }

            }
        }
        exoPlayerListener?.let {
            exoPlayerManager.addListener(it)
        }
    }

    private fun startProgressTimer() {
        progressJob?.cancel()
        progressJob = viewModelScope.launch {
            while (isActive) {
                val pos = exoPlayerManager.currentPosition
                _state.update { it.copy(currentPosition = pos) }
                delay(500L)
            }
        }
    }

    private fun updateShuffledList() {
        val items = exoPlayerManager.getPlayingMediaItems()

        val audioMap = if (state.value.playingQueue.isEmpty()) {
            hashMapOf()
        } else {
            state.value.playingQueue.associateBy { it.id.toString() }
        }

        val newAudioUis = items.mapNotNull { mediaItem ->
            audioMap[mediaItem.mediaId]
        }.distinctBy { it.id }

        _state.update {
            it.copy(
                playingQueue = newAudioUis
            )
        }

    }

    override fun onCleared() {
        super.onCleared()
        println("MusicSharedViewModel is onCleared")
        progressJob?.cancel()
        exoPlayerListener?.let {
            exoPlayerManager.removeListener(it)
        }
    }
}