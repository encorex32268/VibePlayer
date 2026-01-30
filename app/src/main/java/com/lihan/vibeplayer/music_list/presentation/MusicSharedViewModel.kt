@file:OptIn(ExperimentalCoroutinesApi::class)

package com.lihan.vibeplayer.music_list.presentation

import android.net.Uri
import androidx.compose.foundation.text.input.clearText
import androidx.compose.runtime.snapshotFlow
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import com.lihan.vibeplayer.R
import com.lihan.vibeplayer.core.presentation.util.UiText
import com.lihan.vibeplayer.music_list.domain.Audio
import com.lihan.vibeplayer.music_list.domain.ExoPlayerManager
import com.lihan.vibeplayer.music_list.domain.MusicListRepository
import com.lihan.vibeplayer.music_list.domain.Playlist
import com.lihan.vibeplayer.music_list.presentation.mapper.toDomain
import com.lihan.vibeplayer.music_list.presentation.mapper.toUi
import com.lihan.vibeplayer.music_list.presentation.model.AudioUi
import com.lihan.vibeplayer.music_list.presentation.model.PlaylistCardStyle
import com.lihan.vibeplayer.music_list.presentation.model.PlaylistUi
import com.lihan.vibeplayer.music_list.presentation.model.RepeatModeStatus
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.collections.map

class MusicSharedViewModel(
    private val exoPlayerManager: ExoPlayerManager,
    private val repository: MusicListRepository

) : ViewModel() {

    private var progressJob: Job? = null
    private var progressFavouriteJob: Job? =null

    private var exoPlayerListener: Player.Listener? = null

    private val _uiEvent = Channel<MusicSharedUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _state = MutableStateFlow(MusicSharedState())
    val state = _state
        .onStart {
            loadAudios()
            loadPlaylists()
            observeFavouritePlaylist()
            observePlayer()
            observeCurrentAudio()
            observeCreatePlaylistTextField()
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            MusicSharedState()
        )


    fun onAction(action: MusicSharedAction) {
        when (action) {
            MusicSharedAction.OnCollapseClick -> {
                _state.update { it.copy(
                    isExpandPlayer = false
                ) }
            }

            MusicSharedAction.OnExpandClick -> {
                _state.update { it.copy(
                    isExpandPlayer = true
                ) }
            }

            MusicSharedAction.OnHideModeChangedBanner -> {
                _state.update { it.copy(
                    modeStatusBanner = null
                ) }
            }
            MusicSharedAction.OnShuffleClick -> exoPlayerManager.shuffleEnabled()
            MusicSharedAction.OnSkipNextClick -> exoPlayerManager.skipNext()
            MusicSharedAction.OnSkipPreviousClick -> exoPlayerManager.skipPrevious()
            is MusicSharedAction.OnFunctionPlayClick -> {
                exoPlayerManager.quickPlay(action.audios.map { it.toDomain() })
            }

            is MusicSharedAction.OnFunctionShuffleClick -> {
                exoPlayerManager.quickShuffledPlay(action.audios.map { it.toDomain() })
                _state.update { it.copy(
                    isExpandPlayer = true
                ) }
            }
            MusicSharedAction.OnPlayClick -> onPlayClick()
            MusicSharedAction.OnRepeatClick -> onRepeatClick()
            is MusicSharedAction.OnSeek -> onSeek(action.duration)
            is MusicSharedAction.OnSongClick -> onSongClick(action.audioUi)
            MusicSharedAction.OnScanAgainClick -> loadAudios()
            MusicSharedAction.OnToggleFavourite -> onToggleFavourite()
            MusicSharedAction.OnPlaylistClick -> onPlaylistClick()
            MusicSharedAction.OnCreatePlaylistCancelClick -> onCreatePlaylistCancelClick()
            MusicSharedAction.OnCreatePlaylistConfirmClick -> onCreatePlaylistConfirmClick()
            MusicSharedAction.OnCreatePlaylistClick -> onCreatePlaylistClick()
            MusicSharedAction.OnFavouritesClick -> onFavouritesClick()
            is MusicSharedAction.OnPlaylistItemClick -> onPlaylistItemClick(action.playlistUi)
            MusicSharedAction.OnDismissAddToPlaylistSheet -> onDismissAddToPlaylistSheet()
        }
    }

    private fun onCreatePlaylistCancelClick(){
        _state.update { it.copy(
            isShowCreatePlaylistSheet = false,
            isShowAddToPlaylistSheet = true
        ) }
    }
    private fun onCreatePlaylistConfirmClick(){
        val currentPlayingAudioUi = state.value.playingAudioUi
        if (currentPlayingAudioUi == null){
            _state.update { it.copy(
                isShowAddToPlaylistSheet = false,
                isShowCreatePlaylistSheet = false
            ) }
            return
        }

        viewModelScope.launch {
            val title = state.value.createPlaylistTextFieldState.text.toString()
            val newPlaylistId = repository.upsertPlaylist(
                Playlist(
                    title = title
                )
            ).toInt()

            repository
                .createPlaylistWithAudios(
                    id = newPlaylistId,
                    title = title,
                    coverUri = null,
                    audios = listOf(currentPlayingAudioUi.id.toString())
                )

            _uiEvent.send(
                MusicSharedUiEvent.OnAddToPlaylistSucceed(title)
            )

            _state.update { it.copy(
                isShowAddToPlaylistSheet = false,
                isShowCreatePlaylistSheet = false
            ) }
            state.value.createPlaylistTextFieldState.clearText()
        }

    }

    private fun onPlaylistItemClick(playlistUi: PlaylistUi){

        val currentPlayingAudioUi = state.value.playingAudioUi
        if (currentPlayingAudioUi == null){
            _state.update { it.copy(
                isShowAddToPlaylistSheet = false,
                isShowCreatePlaylistSheet = false
            ) }
            return
        }
        viewModelScope.launch {
            repository
                .createPlaylistWithAudios(
                    id = playlistUi.id,
                    title = playlistUi.title,
                    coverUri = playlistUi.coverImageUriString,
                    audios = listOf(currentPlayingAudioUi.id.toString())
                )
            _uiEvent.send(
                MusicSharedUiEvent.OnAddToPlaylistSucceed(playlistUi.title)
            )

            _state.update { it.copy(
                isShowAddToPlaylistSheet = false,
                isShowCreatePlaylistSheet = false
            ) }
            state.value.createPlaylistTextFieldState.clearText()

        }

    }

    private fun onFavouritesClick(){
        val currentPlayingAudioUi = state.value.playingAudioUi
        if (currentPlayingAudioUi == null){
            _state.update { it.copy(
                isShowAddToPlaylistSheet = false,
                isShowCreatePlaylistSheet = false
            ) }
            return
        }
        val isFavourite = !currentPlayingAudioUi.isFavourite
        progressFavouriteJob?.cancel()
        progressFavouriteJob = viewModelScope.launch {
            repository
                .updateFavouriteStatus(
                    audioId = currentPlayingAudioUi.id.toInt(),
                    isFavourite = isFavourite,
                    timestamp = if (isFavourite) System.currentTimeMillis() else null
                )
        }
        _state.update { it.copy(
            isShowAddToPlaylistSheet = false,
            isShowCreatePlaylistSheet = false
        ) }

    }

    private fun onCreatePlaylistClick(){
        _state.update { it.copy(
            isShowAddToPlaylistSheet = false,
            isShowCreatePlaylistSheet = true
        ) }
    }

    private fun onPlaylistClick(){
        _state.update { it.copy(
            isShowAddToPlaylistSheet = true
        ) }
    }

    private fun onDismissAddToPlaylistSheet(){
        _state.update { it.copy(
            isShowAddToPlaylistSheet = false,
            isShowCreatePlaylistSheet = false
        ) }
    }


    private fun observeCurrentAudio(){
        state
            .map { it.playingAudioUi }
            .flatMapLatest {
                if (it != null){
                    repository.getAudioById(it.id.toInt())
                }else emptyFlow()
            }
            .filterNotNull()
            .onEach { audioUi ->
                _state.update { it.copy(
                       playingAudioUi = it.playingAudioUi?.copy(
                           isFavourite = audioUi.isFavourite
                       )
                    )
                }

            }.launchIn(viewModelScope)
    }

    private fun onToggleFavourite(){
        val currentPlayingAudioUi = state.value.playingAudioUi ?: return
        val isFavourite = !currentPlayingAudioUi.isFavourite
        progressFavouriteJob?.cancel()
        progressFavouriteJob = viewModelScope.launch {
            repository
                .updateFavouriteStatus(
                    audioId = currentPlayingAudioUi.id.toInt(),
                    isFavourite = isFavourite,
                    timestamp = if (isFavourite) System.currentTimeMillis() else null
                )
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
        viewModelScope.launch {
            _state.update { it.copy(isScanning = true) }
            val allAudios = repository.getAllAudiosAndSync().first()

            val hasImageAudios = toDeferredAlbumArtImages(allAudios)

            exoPlayerManager.setInitMediaItems(allAudios)
            delay(300L)
            _state.update { state ->
                state.copy(
                    audios = hasImageAudios.awaitAll(),
                    isScanning = false
                )
            }
        }

    }

    private fun observeCreatePlaylistTextField() {
        snapshotFlow {
            _state.value.createPlaylistTextFieldState.text.toString()
        }.onEach { text ->
            _state.update {
                it.copy(
                    isCreateButtonEnabled = text.isNotEmpty() &&  text.length <= 40
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

    private fun loadPlaylists() {
        repository
            .getPlaylistAudios()
            .onEach { playlistAudios ->

                val playlistUis = playlistAudios.map { playlistAudio ->
                    //Need to reverse the list so the primary audio's album art is prioritized.
                    val audios = playlistAudio.audios.reversed()
                    val playlistUi = playlistAudio.playlist.toUi(audios.size)

                    val firstAudio = audios.firstOrNull()
                    val coverStyle = when{
                        playlistUi.coverImageUriString != null -> {
                            PlaylistCardStyle.HasCover(
                                imageModel = playlistUi.coverImageUriString.toUri(),
                                isUploadedImage = true
                            )
                        }
                        firstAudio != null && firstAudio.album != Uri.EMPTY -> {
                            val image = repository.getAlbumArtImage(firstAudio.album)
                            PlaylistCardStyle.HasCover(
                                imageModel = image,
                                isUploadedImage = false
                            )
                        }
                        else -> playlistUi.style
                    }

                    playlistUi.copy(
                        style = coverStyle,
                        audioIds = audios.map { it.id.toString() }
                    )
                }

                _state.update { it.copy(
                    playlists = playlistUis
                ) }
            }
            .launchIn(viewModelScope)
    }

    private fun observeFavouritePlaylist(){
        repository
            .getFavouriteCount()
            .onEach {  count ->
                _state.update { it.copy(
                    favouritesPlaylistsCount = count
                ) }
            }
            .launchIn(viewModelScope)
    }

    override fun onCleared() {
        super.onCleared()
        progressJob?.cancel()
        exoPlayerListener?.let {
            exoPlayerManager.removeListener(it)
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