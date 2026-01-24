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
import com.lihan.vibeplayer.R
import com.lihan.vibeplayer.core.presentation.util.UiText
import com.lihan.vibeplayer.music_list.domain.ExoPlayerManager
import com.lihan.vibeplayer.music_list.domain.FavouritesPlaylist
import com.lihan.vibeplayer.music_list.domain.MusicListRepository
import com.lihan.vibeplayer.music_list.presentation.mapper.toDomain
import com.lihan.vibeplayer.music_list.presentation.mapper.toUi
import com.lihan.vibeplayer.music_list.presentation.model.AudioUi
import com.lihan.vibeplayer.music_list.presentation.model.PlaylistCardStyle
import com.lihan.vibeplayer.music_list.presentation.model.PlaylistUi
import com.lihan.vibeplayer.music_list.presentation.model.RepeatModeStatus
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
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

    private val _uiEvent = Channel<MusicListUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _state = MutableStateFlow(MusicListState())
    val state = _state
        .onStart {
            if (!hasInitialLoadedData) {
                checkFavouritesPlaylist()
                loadAudios()
                loadPlaylists()
                observePlayer()
                observeCreatePlaylistTextField()
                observeRenameTextField()
                hasInitialLoadedData = true
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            MusicListState()
        )

    private fun checkFavouritesPlaylist() {
        viewModelScope.launch {
            repository.checkAndCreateDefaultPlaylist()
        }
    }

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
            MusicListAction.OnNavigateToAddSongs -> onNavigateToAddSongs()
            MusicListAction.OnCreatePlaylistCancelClick -> onCreatePlaylistCancel()
            is MusicListAction.OnMenuDotsClick -> onMenuDotsClick(action.playlistUi)
            MusicListAction.OnFavouritesMenuDotsClick -> onFavouritesMenuDotsClick()
            MusicListAction.OnActionSheetDismiss -> onActionSheetDismiss()
            MusicListAction.OnChangeCoverClick -> {
                _state.update { it.copy(
                    isShowActionSheet = false,

                ) }
            }
            MusicListAction.OnPlayPlaylistClick -> TODO()
            is MusicListAction.OnDeleteAction -> onDeleteAction(action.action)
            is MusicListAction.OnRenameAction -> onRenameAction(action.action)

            else -> Unit
        }
    }

    private fun onNavigateToAddSongs() {

        viewModelScope.launch {
            val title = state.value.createPlaylistTextFieldState.text.toString()
            state.value.createPlaylistTextFieldState.clearText()
            _state.update { it.copy(
                isShowCreatePlaylistBottomSheet = false
            ) }

            //wait for hide bottom sheet
            delay(300L)
            _uiEvent.send(
                MusicListUiEvent.OnNavigateToAddSongs(title)
            )
        }
    }

    private fun onRenameAction(action: RenameAction){
        when(action){
            RenameAction.OnRenameActionClick -> {
                _state.update { it.copy(
                    isShowActionSheet = false,
                    isShowRenameBottomSheet = true,
                ) }
                val currentPlaylistTitle = state.value.selectActionSheetPlaylistUi?.title
                state.value.renamePlaylistTextFieldState.edit {
                    this.replace(0,this.length,currentPlaylistTitle?:"")
                }
            }
            RenameAction.OnCancelClick -> {
                _state.update { it.copy(
                    isShowActionSheet = true,
                    isShowRenameBottomSheet = false
                ) }
            }
            RenameAction.OnConfirmClick -> {
                viewModelScope.launch {
                    val currentPlaylist = state.value.selectActionSheetPlaylistUi ?: return@launch
                    repository.upsertPlaylist(
                        playlist = currentPlaylist.copy(
                            title = state.value.renamePlaylistTextFieldState.text.toString()
                        ).toDomain()
                    )

                    _state.update { it.copy(
                        isShowActionSheet = false,
                        isShowRenameBottomSheet = false
                    ) }
                }

            }

        }
    }

    private fun onDeleteAction(action: DeleteAction){
        when(action){
            DeleteAction.OnDeleteActionClick -> {
                _state.update { it.copy(
                    isShowActionSheet = false,
                    isShowDeleteBottomSheet = true
                ) }
            }
            DeleteAction.OnCancelClick -> {
                _state.update { it.copy(
                    isShowActionSheet = true,
                    isShowDeleteBottomSheet = false
                ) }
            }
            DeleteAction.OnConfirmClick -> {
                viewModelScope.launch {
                    val currentPlaylist = state.value.selectActionSheetPlaylistUi
                    currentPlaylist?.let {
                        repository.deletePlaylist(currentPlaylist.toDomain())
                    }

                    _state.update { it.copy(
                        isShowActionSheet = false,
                        isShowDeleteBottomSheet = false,
                        selectActionSheetPlaylistUi = null
                    ) }
                }
            }

        }
    }

    private fun onActionSheetDismiss(){
        _state.update { it.copy(
            selectActionSheetPlaylistUi = null,
            isShowActionSheet = false
        ) }
    }

    private fun onFavouritesMenuDotsClick(){
        val favouritesPlaylistUi = state.value.favouritesPlaylists
        _state.update { it.copy(
            selectActionSheetPlaylistUi = favouritesPlaylistUi
        ) }
    }

    private fun onMenuDotsClick(playlistUi: PlaylistUi){
        _state.update { it.copy(
            selectActionSheetPlaylistUi = playlistUi,
            isShowActionSheet = true
        ) }
    }

    private fun onCreatePlaylistAddClick() {
        _state.update {
            it.copy(
                isShowCreatePlaylistBottomSheet = true
            )
        }
    }

    private fun onCreatePlaylistCancel() {
        state.value.createPlaylistTextFieldState.clearText()
        _state.update {
            it.copy(
                isShowCreatePlaylistBottomSheet = false
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

            val audioMap = audios.associateBy { it.id.toString() }

            val playlists = playlists.map { playlist ->
                val firstAudioId = playlist.audioIds.first()
                val firstAudio = firstAudioId.let { audioMap[it] }

                val coverStyle = if (firstAudio != null && firstAudio.album != Uri.EMPTY) {
                    repository.getAlbumArtImage(firstAudio.album)?.let { image ->
                        PlaylistCardStyle.HasCover(image)
                    } ?: PlaylistCardStyle.NoCover
                } else {
                    PlaylistCardStyle.NoCover
                }

                playlist.toUi(coverStyle)
            }

            val favouritesPlaylistUi = favouritesPlaylist?.toUi()

            favouritesPlaylistUi to playlists
        }.onEach { (favouritesPlaylistUi , playlists) ->
            _state.update { state ->
                state.copy(
                    favouritesPlaylists = favouritesPlaylistUi,
                    playlists = playlists
                )
            }
        }.launchIn(viewModelScope)


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

    private fun observeRenameTextField() {
        snapshotFlow {
            _state.value.renamePlaylistTextFieldState.text.toString()
        }.onEach { text ->
            val currentPlaylistTitle = state.value.selectActionSheetPlaylistUi?.title
            val isChanged = text != currentPlaylistTitle
            _state.update {
                it.copy(
                    isRenameButtonEnabled = isChanged &&  text.length <= 40 && text.isNotEmpty()
                )
            }
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