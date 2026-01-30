package com.lihan.vibeplayer.music_list.presentation

import androidx.compose.foundation.text.input.TextFieldState
import com.lihan.vibeplayer.music_list.presentation.model.PlaylistUi

data class MusicListState(
    val favouritesPlaylistsCount: Int = 0,
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