package com.lihan.vibeplayer.music_list.presentation

import androidx.compose.foundation.text.input.TextFieldState
import com.lihan.vibeplayer.core.presentation.util.UiText
import com.lihan.vibeplayer.music_list.domain.FavouritesPlaylist
import com.lihan.vibeplayer.music_list.presentation.model.AudioUi
import com.lihan.vibeplayer.music_list.presentation.model.PlaylistUi
import com.lihan.vibeplayer.music_list.presentation.model.RepeatModeStatus

data class MusicListState(
    val playlists: List<PlaylistUi> = emptyList(),
    val favouritesPlaylists: PlaylistUi?=null,
    val isShowCreatePlaylistBottomSheet: Boolean = false,
    val createPlaylistTextFieldState: TextFieldState = TextFieldState(),
    val isCreateButtonEnabled: Boolean = false,
    val renamePlaylistTextFieldState: TextFieldState = TextFieldState(),
    val isRenameButtonEnabled: Boolean = false,
    val selectActionSheetPlaylistUi: PlaylistUi?=null,
    val isShowActionSheet: Boolean = false,
    val isShowRenameBottomSheet: Boolean = false,
    val isShowDeleteBottomSheet: Boolean = false,
)