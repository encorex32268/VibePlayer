package com.lihan.vibeplayer.music_list.presentation

import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.foundation.text.input.clearText
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.lihan.vibeplayer.R
import com.lihan.vibeplayer.core.presentation.util.UiText
import com.lihan.vibeplayer.music_list.domain.ExoPlayerManager
import com.lihan.vibeplayer.music_list.domain.MusicListRepository
import com.lihan.vibeplayer.music_list.presentation.mapper.toUi
import com.lihan.vibeplayer.music_list.presentation.model.AudioUi
import com.lihan.vibeplayer.music_list.presentation.model.PlaylistCardStyle
import com.lihan.vibeplayer.music_list.presentation.model.RepeatModeStatus
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MusicListViewModel(
    private val repository: MusicListRepository,
    private val exoPlayerManager: ExoPlayerManager
) : ViewModel() {

    private var hasInitialLoadedData = false

    private var progressJob: Job? = null

    private val _state = MutableStateFlow(MusicListState())
    val state = _state
        .onStart {
            if (!hasInitialLoadedData) {
                loadAudios()
                loadPlaylists()
                observePlayer()
                observeCreatePlaylistTextField()
                hasInitialLoadedData = true
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            MusicListState()
        )

    fun onAction(action: MusicListAction) {
        when (action) {
            MusicListAction.OnScanAgainClick -> {
                viewModelScope.launch{ loadAudios() }
            }
            MusicListAction.OnFunctionShuffleClick -> onFunctionShuffleClick()
            is MusicListAction.OnFunctionPlayClick -> onFunctionPlayClick()
            MusicListAction.OnPlayClick -> onPlayClick()
            MusicListAction.OnSkipNextClick -> onSkipNextClick()
            MusicListAction.OnSkipPreviousClick -> onSkipPreviousClick()
            is MusicListAction.OnSeek -> onSeekTo(action.position)
            MusicListAction.OnRepeatClick -> onRepeatModeClick()
            MusicListAction.OnShuffleClick -> onShuffleClick()
            is MusicListAction.OnSongClick -> onSongClick(action.audioUi)
            MusicListAction.OnExpandClick -> onExpandClick()
            MusicListAction.OnCollapseClick -> onCollapseClick()
            MusicListAction.OnHideModeChangedBanner -> onHideModeChangedBanner()
            MusicListAction.OnCreatePlaylistAddClick -> onCreatePlaylistAddClick()
            MusicListAction.OnNavigateToAddSongs,
            MusicListAction.OnCreatePlaylistCancelClick -> onCreatePlaylistCancel()

            else -> Unit
        }
    }


    private fun onCreatePlaylistAddClick() {
        _state.update {
            it.copy(
                isCreatePlaylistSheetShow = true
            )
        }
    }

    private fun onCreatePlaylistCancel() {
        state.value.createPlaylistTextFieldState.clearText()
        _state.update {
            it.copy(
                isCreatePlaylistSheetShow = false
            )
        }
    }


    private fun onHideModeChangedBanner() {
        _state.update {
            it.copy(
                modeStatusBanner = null
            )
        }
    }

    private fun onExpandClick() {
        _state.update {
            it.copy(
                isExpandPlayer = true
            )
        }
    }

    private fun onCollapseClick() {
        _state.update {
            it.copy(
                isExpandPlayer = false
            )
        }
    }

    private fun onSongClick(audioUi: AudioUi) {
        _state.update {
            it.copy(
                playingAudioUi = audioUi
            )
        }

        val currentMediaItems = exoPlayerManager.getAllMediaItems()
        val index = currentMediaItems.indexOf(
            currentMediaItems.find { it.mediaId == audioUi.id.toString() }
        )
        exoPlayerManager.playSongByIndex(index)
    }

    private fun onShuffleClick() {
        exoPlayerManager.shuffleEnabled()
    }


    private fun onRepeatModeClick() {
        val currentRepeatMode = exoPlayerManager.getRepeatMode()
        val newRepeatMode = when (currentRepeatMode) {
            Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
            Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
            Player.REPEAT_MODE_ONE -> Player.REPEAT_MODE_OFF
            else -> Player.REPEAT_MODE_OFF
        }
        exoPlayerManager.setRepeatMode(newRepeatMode)
    }

    private fun onSeekTo(position: Long) {
        exoPlayerManager.seekTo(position)
        _state.update {
            it.copy(
                currentPosition = position
            )
        }
    }

    private fun onSkipPreviousClick() {
        exoPlayerManager.skipPrevious()
    }

    private fun onSkipNextClick() {
        exoPlayerManager.skipNext()
    }

    private fun onPlayClick() {
        val isPlaying = state.value.isPlaying
        if (isPlaying) {
            exoPlayerManager.pause()
        } else {
            exoPlayerManager.play()
        }
    }

    private fun onFunctionPlayClick() {
        exoPlayerManager.quickPlay()
    }

    @OptIn(UnstableApi::class)
    private fun onFunctionShuffleClick() {
        exoPlayerManager.quickShuffledPlay()
        _state.update {
            it.copy(
                isExpandPlayer = true
            )
        }
    }

    private fun loadAudios() {
        _state.update { it.copy(isScanning = true) }
        repository
            .getAllAudios()
            .onEach { audios ->
                val hasImageAudios = coroutineScope {
                    audios.map { audio ->
                        async{
                            val audioUi = audio.toUi()
                            val albumImage = repository.getAlbumArtImage(audioUi.album)
                            audioUi.copy(albumImage = albumImage)
                        }
                    }
                }
                val mediaItems = audios.map { audioUi ->
                    MediaItem.Builder()
                        .setMediaId(audioUi.id.toString())
                        .setUri(audioUi.album)
                        .build()
                }
                exoPlayerManager.setInitMediaItems(mediaItems)

                delay(300L)

                _state.update { state ->
                    state.copy(
                        audios = hasImageAudios.awaitAll(),
                        isScanning = false
                    )
                }
            }.launchIn(viewModelScope)

    }


    private fun loadPlaylists() {

        combine(
            flow = repository.getFavouritesPlaylist(),
            flow2 = repository.getAllAudios(),
            flow3 = repository.getAllPlaylist()
        ){ favouritesPlaylist , audios , playlists ->

            val playlists = playlists.map { playlist ->
                val firstPlaylistSongId = playlist.audioIds.first()
                val findAudio =
                    audios.find { audio -> audio.id.toString() == firstPlaylistSongId }

                val coverStyle = if (findAudio == null || findAudio.album == Uri.EMPTY) {
                    PlaylistCardStyle.NoCover
                } else {
                    val image = repository.getAlbumArtImage(findAudio.album)
                    if (image == null) {
                        PlaylistCardStyle.NoCover
                    } else {
                        PlaylistCardStyle.HasCover(
                            byteArray = repository.getAlbumArtImage(findAudio.album)
                        )
                    }
                }
                playlist.toUi(coverStyle)
            }
            _state.update { state ->
                state.copy(
                    favouritesPlaylists = favouritesPlaylist,
                    playlists = playlists
                )
            }

        }.launchIn(viewModelScope)


    }


    private fun observeCreatePlaylistTextField() {
        snapshotFlow {
            _state.value.createPlaylistTextFieldState.text.toString()
        }.onEach { text ->
            println("observeCreatePlaylistTextField ${text}")
            _state.update {
                it.copy(
                    isCreateButtonEnabled = text.isNotEmpty() &&  text.length <= 40
                )
            }
            println("observeCreatePlaylistTextField ${text.isNotEmpty() &&  text.length <= 40}")
        }.launchIn(viewModelScope)
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

    private fun observePlayer() {
        exoPlayerManager.addListener(
            object : Player.Listener {
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
                        val currentAudio = state.value.audios.find { it.id.toString() == currentId }
                        _state.update { it.copy(
                            playingAudioUi = currentAudio
                        ) }
                    }
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

                override fun onTimelineChanged(timeline: Timeline, reason: Int) {
                    updateShuffledList()
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
        )
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
        }

        _state.update {
            it.copy(
                audios = newAudioUis
            )
        }

    }

}