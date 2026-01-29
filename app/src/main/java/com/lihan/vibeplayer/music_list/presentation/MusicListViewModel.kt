package com.lihan.vibeplayer.music_list.presentation

import android.net.Uri
import androidx.compose.foundation.text.input.clearText
import androidx.compose.runtime.snapshotFlow
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lihan.vibeplayer.core.domain.FAVOURITES_ID
import com.lihan.vibeplayer.music_list.domain.MusicListRepository
import com.lihan.vibeplayer.music_list.presentation.mapper.toDomain
import com.lihan.vibeplayer.music_list.presentation.mapper.toUi
import com.lihan.vibeplayer.music_list.presentation.model.PlaylistCardStyle
import com.lihan.vibeplayer.music_list.presentation.model.PlaylistUi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
                loadPlaylists()
                observeFavouritePlaylist()
                observeCreatePlaylistTextField()
                observeRenameTextField()
                hasInitialLoadedData = true
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            MusicListState()
        )


    fun onAction(action: MusicListAction) {
        when (action) {
            MusicListAction.OnCreatePlaylistAddClick -> onCreatePlaylistAddClick()
            MusicListAction.OnNavigateToAddSongs -> onNavigateToAddSongs()
            is MusicListAction.OnNavigateToPlaylistDetail -> onNavigateToPlaylistDetail()
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

    private fun onNavigateToPlaylistDetail(){
        viewModelScope.launch {
            val currentPlaylistUi = state.value.selectActionSheetPlaylistUi?:return@launch

            val id = if (currentPlaylistUi.style == PlaylistCardStyle.Favourites){
                FAVOURITES_ID
            }else{
                currentPlaylistUi.id
            }

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
        _state.update { it.copy(
            selectActionSheetPlaylistUi = PlaylistUi(
                id = FAVOURITES_ID,
                style = PlaylistCardStyle.Favourites,
                count = it.favouritesPlaylistsCount
            ),
            isShowActionSheet = true
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


    private fun loadPlaylists() {
        repository
            .getPlaylistAudios()
            .onEach { playlistAudios ->

                val playlistUis = playlistAudios.map { playlistAudio ->
                    val audios = playlistAudio.audios
                    val playlistUi = playlistAudio.playlist.toUi(audios.size)

                    val firstAudio = audios.first()
                    val coverStyle = when{
                        playlistUi.coverImageUriString != null -> {
                            PlaylistCardStyle.HasCover(
                                imageModel = playlistUi.coverImageUriString.toUri(),
                                isUploadedImage = true
                            )
                        }
                        firstAudio.album != Uri.EMPTY -> {
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



