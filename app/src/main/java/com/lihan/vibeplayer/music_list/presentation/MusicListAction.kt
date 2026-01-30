package com.lihan.vibeplayer.music_list.presentation

import com.lihan.vibeplayer.music_list.presentation.model.AudioUi
import com.lihan.vibeplayer.music_list.presentation.model.PlaylistUi

sealed interface MusicListAction {
    data object OnScanAgainClick: MusicListAction
    data object OnScanClick: MusicListAction
    data object OnSearchClick: MusicListAction
    data object OnFunctionPlayClick: MusicListAction
    data object OnFunctionShuffleClick: MusicListAction
    data class OnSongClick(val audioUi: AudioUi): MusicListAction
    data object OnCreatePlaylistCancelClick: MusicListAction
    data object OnCreatePlaylistAddClick: MusicListAction
    data object OnNavigateToAddSongs: MusicListAction
    data object OnNavigateToPlaylistDetail: MusicListAction

    data class OnMenuDotsClick(
        val playlistUi: PlaylistUi
    ): MusicListAction
    data class OnFavouritesMenuDotsClick(val favouritesPlaylistsCount: Int): MusicListAction
    data object OnActionSheetDismiss: MusicListAction

    data class OnUpdatePlaylistCover(val uriString: String): MusicListAction

    data class OnDeleteAction(val action: DeleteAction): MusicListAction
    data class OnRenameAction(val action: RenameAction): MusicListAction

}

sealed interface DeleteAction {
    data object OnDeleteActionClick: DeleteAction
    data object OnConfirmClick: DeleteAction
    data object OnCancelClick: DeleteAction
}

sealed interface RenameAction {
    data object OnRenameActionClick: RenameAction
    data object OnConfirmClick: RenameAction
    data object OnCancelClick: RenameAction
}