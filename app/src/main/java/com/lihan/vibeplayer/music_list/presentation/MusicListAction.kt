package com.lihan.vibeplayer.music_list.presentation

import com.lihan.vibeplayer.music_list.presentation.model.AudioUi
import com.lihan.vibeplayer.music_list.presentation.model.PlaylistUi

sealed interface MusicListAction {
    data object OnScanAgainClick: MusicListAction
    data object OnScanClick: MusicListAction
    data object OnFunctionPlayClick: MusicListAction
    data object OnSearchClick: MusicListAction
    data object OnFunctionShuffleClick: MusicListAction
    data object OnPlayClick: MusicListAction
    data object OnSkipNextClick: MusicListAction
    data object OnSkipPreviousClick: MusicListAction
    data class OnSeek(val position: Long): MusicListAction
    data object OnRepeatClick: MusicListAction
    data object OnShuffleClick: MusicListAction
    data class OnSongClick(val audioUi: AudioUi): MusicListAction

    data object OnExpandClick: MusicListAction
    data object OnCollapseClick: MusicListAction
    data object OnHideModeChangedBanner: MusicListAction
    data object OnCreatePlaylistCancelClick: MusicListAction
    data object OnCreatePlaylistAddClick: MusicListAction
    data object OnNavigateToAddSongs: MusicListAction
    data class OnNavigateToPlaylistDetail(val id: Int): MusicListAction

    data class OnMenuDotsClick(
        val playlistUi: PlaylistUi
    ): MusicListAction
    data object OnFavouritesMenuDotsClick: MusicListAction
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