package com.lihan.vibeplayer.music_list.presentation

import android.net.Uri
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.exoplayer.ExoPlayer
import com.lihan.vibeplayer.R
import com.lihan.vibeplayer.core.presentation.util.UiText
import com.lihan.vibeplayer.music_list.data.OfflineMusicListRepository
import com.lihan.vibeplayer.music_list.domain.MusicListRepository
import com.lihan.vibeplayer.music_list.presentation.mapper.toUi
import com.lihan.vibeplayer.music_list.presentation.model.AudioUi
import com.lihan.vibeplayer.music_list.presentation.model.PlaylistCardStyle
import com.lihan.vibeplayer.music_list.presentation.model.RepeatModeStatus
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okhttp3.Dispatcher

class MusicListViewModel(
    private val exoPlayer: ExoPlayer,
    private val repository: MusicListRepository,
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
                observeSearchTextField()
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
        _state.update {
            it.copy(
                isCreatePlaylistSheetShow = false,
                createPlaylistTextFieldState = TextFieldState()
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
        val currentMediaItems = exoPlayer.getAllMediaItems()
        val index = currentMediaItems.indexOf(
            currentMediaItems.find { it.mediaId == audioUi.id.toString() }
        )
        exoPlayer.setMediaItems(
            exoPlayer.getAllMediaItems(),
            index,
            0L
        )
        exoPlayer.prepare()
    }

    private fun onShuffleClick() {
        exoPlayer.shuffleModeEnabled = !exoPlayer.shuffleModeEnabled
        if (exoPlayer.shuffleModeEnabled) {
            val items = getShuffledMediaItems()
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


    private fun onRepeatModeClick() {
        val currentRepeatMode = exoPlayer.repeatMode
        val newRepeatMode = when (currentRepeatMode) {
            Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
            Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
            Player.REPEAT_MODE_ONE -> Player.REPEAT_MODE_OFF
            else -> Player.REPEAT_MODE_OFF
        }
        exoPlayer.repeatMode = newRepeatMode
    }

    private fun onSeekTo(position: Long) {
        exoPlayer.seekTo(position)
        _state.update {
            it.copy(
                currentPosition = position
            )
        }
    }

    private fun onSkipPreviousClick() {
        exoPlayer.seekToPreviousMediaItem()
    }

    private fun onSkipNextClick() {
        exoPlayer.seekToNextMediaItem()
    }

    private fun onPlayClick() {
        val isPlaying = state.value.isPlaying
        if (isPlaying) {
            exoPlayer.pause()
        } else {
            exoPlayer.play()
        }
    }

    private fun onFunctionPlayClick() {
        exoPlayer.setMediaItems(
            exoPlayer.getAllMediaItems(),
            0,
            0L
        )
        exoPlayer.prepare()
        exoPlayer.play()
    }

    private fun onFunctionShuffleClick() {
        exoPlayer.shuffleModeEnabled = true

        exoPlayer.seekToDefaultPosition(
            exoPlayer.shuffleOrder.firstIndex
        )
        exoPlayer.prepare()
        exoPlayer.play()

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
                exoPlayer.setMediaItems(mediaItems)

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


    private fun observeSearchTextField() {
        snapshotFlow {
            _state.value.createPlaylistTextFieldState.text.toString()
        }.onEach { text ->
            _state.update {
                it.copy(
                    isCreateButtonEnabled = text.isNotEmpty()
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun observePlayer() {
        exoPlayer.addListener(
            object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _state.update {
                        it.copy(
                            isPlaying = isPlaying
                        )
                    }
                    if (!isPlaying) {
                        progressJob?.cancel()
                    }
                    progressJob = viewModelScope.launch {
                        while (isActive) {
                            val currentPosition = exoPlayer.currentPosition
                            val duration = exoPlayer.duration

                            _state.update {
                                it.copy(
                                    currentPosition = currentPosition,
                                    duration = duration,
                                )
                            }
                            delay(300L)
                        }
                    }
                }

                override fun onEvents(player: Player, events: Player.Events) {

                    val currentMediaItem = exoPlayer.currentMediaItem
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
                            val currentPosition = exoPlayer.currentPosition
                            val duration = exoPlayer.duration

                            _state.update {
                                it.copy(
                                    currentPosition = currentPosition,
                                    duration = duration,
                                )
                            }
                        }

                        else -> Unit
                    }

                }
            }
        )
    }


    fun ExoPlayer.getAllMediaItems(): List<MediaItem> {
        val items = mutableListOf<MediaItem>()
        for (i in 0 until this.mediaItemCount) {
            items.add(this.getMediaItemAt(i))
        }
        return items
    }

    private fun updateShuffledList() {
        val items = if (exoPlayer.shuffleModeEnabled) {
            getShuffledMediaItems()
        } else {
            (0 until exoPlayer.mediaItemCount).map { exoPlayer.getMediaItemAt(it) }
        }

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

    private fun getShuffledMediaItems(): List<MediaItem> {
        val timeline = exoPlayer.currentTimeline
        if (timeline.isEmpty) {
            return emptyList()
        }
        val shuffledList = mutableListOf<MediaItem>()

        var currentIndex = timeline.getFirstWindowIndex(true)

        while (currentIndex != -1) {
            val mediaItem = exoPlayer.getMediaItemAt(currentIndex)
            shuffledList.add(mediaItem)

            currentIndex = timeline.getNextWindowIndex(
                currentIndex,
                Player.REPEAT_MODE_OFF,
                true
            )
        }

        return shuffledList
    }

}