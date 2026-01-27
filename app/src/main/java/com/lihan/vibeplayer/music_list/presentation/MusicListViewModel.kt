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
import kotlin.collections.distinctBy
import androidx.core.net.toUri
import com.lihan.vibeplayer.music_list.domain.Audio
import kotlinx.coroutines.CoroutineScope

class MusicListViewModel(
    private val repository: MusicListRepository
) : ViewModel() {

    private var hasInitialLoadedData = false

    private val _uiEvent = Channel<MusicListUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _state = MutableStateFlow(MusicListState())
    val state = _state
        .onStart {
            if (!hasInitialLoadedData) {
                checkFavouritesPlaylist()
                loadPlaylists()
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
            MusicListAction.OnCreatePlaylistAddClick -> onCreatePlaylistAddClick()
            MusicListAction.OnNavigateToAddSongs -> onNavigateToAddSongs()
            is MusicListAction.OnNavigateToPlaylistDetail -> onNavigateToPlaylistDetail(action.id)
            MusicListAction.OnCreatePlaylistCancelClick -> onCreatePlaylistCancel()
            is MusicListAction.OnMenuDotsClick -> onMenuDotsClick(action.playlistUi)
            MusicListAction.OnFavouritesMenuDotsClick -> onFavouritesMenuDotsClick()
            MusicListAction.OnActionSheetDismiss -> onActionSheetDismiss()
            is MusicListAction.OnUpdatePlaylistCover -> onUpdatePlaylistCover(action.uriString)
            is MusicListAction.OnDeleteAction -> onDeleteAction(action.action)
            is MusicListAction.OnRenameAction -> onRenameAction(action.action)
            else -> Unit
        }
    }

    private fun onNavigateToPlaylistDetail(id: Int){
        viewModelScope.launch {
            _state.update { it.copy(
                isShowActionSheet = false,
                selectActionSheetPlaylistUi = null
            ) }
            delay(300L)
            _uiEvent.send(
                MusicListUiEvent.OnNavigateToPlaylistDetail(id)
            )
        }
    }

    private fun onUpdatePlaylistCover(uriString: String) {

        val currentPlaylist = state.value.selectActionSheetPlaylistUi ?: return

        viewModelScope.launch {
            repository.upsertPlaylist(
                playlist = currentPlaylist.copy(
                    coverImageUriString = uriString
                ).toDomain()
            )
            _state.update { it.copy(
                isShowActionSheet = false
            ) }
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
        val newPlaylistUi = if (playlistUi.coverImageUriString.isNullOrEmpty()){
            playlistUi
        }else{
            playlistUi.copy(
                style = PlaylistCardStyle.HasCover(
                    playlistUi.coverImageUriString.toUri()
                )
            )
        }
        _state.update { it.copy(
            selectActionSheetPlaylistUi = newPlaylistUi,
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




    private fun loadPlaylists() {

        combine(
            flow = repository.getFavouritesPlaylist(),
            flow2 = repository.getAllAudios(),
            flow3 = repository.getAllPlaylist()
        ){ favouritesPlaylist , audios , playlists ->

            val distinctAudios = audios.distinctBy { it.id }

            val audioMap = distinctAudios.associateBy { it.id.toString() }

            val playlists = playlists.map { playlist ->
                val firstAudioId = playlist.audioIds.first()
                val firstAudio = firstAudioId.let { audioMap[it] }

                val coverStyle = when{
                    playlist.coverImageUriString != null -> {
                        PlaylistCardStyle.HasCover(
                            playlist.coverImageUriString.toUri()
                        )
                    }
                    firstAudio != null && firstAudio.album != Uri.EMPTY -> {
                        PlaylistCardStyle.HasCover(repository.getAlbumArtImage(firstAudio.album))
                    }
                    else -> {
                        PlaylistCardStyle.NoCover
                    }
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


}
