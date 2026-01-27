package com.lihan.vibeplayer.music_list.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import com.lihan.vibeplayer.R
import com.lihan.vibeplayer.core.presentation.util.UiText
import com.lihan.vibeplayer.music_list.domain.ExoPlayerManager
import com.lihan.vibeplayer.music_list.domain.MusicListRepository
import com.lihan.vibeplayer.music_list.presentation.mapper.toDomain
import com.lihan.vibeplayer.music_list.presentation.mapper.toUi
import com.lihan.vibeplayer.music_list.presentation.model.AudioUi
import com.lihan.vibeplayer.music_list.presentation.model.RepeatModeStatus
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MusicSharedViewModel(
    private val exoPlayerManager: ExoPlayerManager,
    private val repository: MusicListRepository

) : ViewModel() {

    private var progressJob: Job? = null

    private var exoPlayerListener: Player.Listener? = null

    private val _state = MutableStateFlow(MusicSharedState())
    val state = _state.asStateFlow()


    init {
        loadAudios()
        observePlayer()
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
            is MusicSharedAction.OnFunctionPlayClick -> {
                exoPlayerManager.quickPlay(action.audios.map { it.toDomain() })
            }

            is MusicSharedAction.OnFunctionShuffleClick -> {
                exoPlayerManager.quickShuffledPlay(action.audios.map { it.toDomain() })
                _state.update {
                    it.copy(
                        isExpandPlayer = true
                    )
                }
            }

            MusicSharedAction.OnPlayClick -> onPlayClick()
            MusicSharedAction.OnRepeatClick -> onRepeatClick()
            is MusicSharedAction.OnSeek -> onSeek(action.duration)
            is MusicSharedAction.OnSongClick -> onSongClick(action.audioUi)
            MusicSharedAction.OnScanAgainClick -> loadAudios()
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
        if (mediaItem == null) {
            return
        }

        val index = currentMediaItems.indexOf(mediaItem)
        if (index == -1) {
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


            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                val newId = mediaItem?.mediaId ?: return

                if (newId != state.value.playingAudioUi?.id?.toString()) {
                    val currentAudio = state.value.audios.find { it.id.toString() == newId }

                    currentAudio?.let { audio ->
                        _state.update { it.copy(playingAudioUi = audio) }
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

    private fun loadAudios() {
        _state.update { it.copy(isScanning = true) }
        repository
            .getAllAudiosAndSync()
            .onEach { audios ->

                val hasImageAudios = coroutineScope {
                    audios.map { audio ->
                        async {
                            val audioUi = audio.toUi()
                            val albumImage = repository.getAlbumArtImage(audioUi.album)
                            audioUi.copy(albumImage = albumImage)
                        }
                    }
                }
                exoPlayerManager.setInitMediaItems(audios)

                delay(300L)

                _state.update { state ->
                    state.copy(
                        audios = hasImageAudios.awaitAll(),
                        isScanning = false
                    )
                }
            }.launchIn(viewModelScope)

    }

    private fun updateShuffledList() {
        val items = exoPlayerManager.getPlayingMediaItems()

        val audioMap = if (state.value.audios.isEmpty()) {
            hashMapOf()
        } else {
            state.value.audios.associateBy { it.id.toString() }
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
        progressJob?.cancel()
        exoPlayerListener?.let {
            exoPlayerManager.removeListener(it)
        }
    }
}