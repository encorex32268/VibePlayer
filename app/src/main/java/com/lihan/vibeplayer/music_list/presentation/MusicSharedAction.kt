package com.lihan.vibeplayer.music_list.presentation

import com.lihan.vibeplayer.music_list.presentation.model.AudioUi
import com.lihan.vibeplayer.music_list.presentation.model.PlaylistUi

sealed interface MusicSharedAction {
    data object OnScanAgainClick: MusicSharedAction
    data object OnPlayClick: MusicSharedAction
    data object OnSkipNextClick: MusicSharedAction
    data object OnSkipPreviousClick: MusicSharedAction
    data class OnSeek(val duration: Long): MusicSharedAction
    data object OnRepeatClick: MusicSharedAction
    data object OnShuffleClick: MusicSharedAction
    data object OnExpandClick: MusicSharedAction
    data object OnCollapseClick: MusicSharedAction
    data object OnHideModeChangedBanner: MusicSharedAction
    data class OnFunctionPlayClick(val audios: List<AudioUi>): MusicSharedAction
    data class OnFunctionShuffleClick(val audios: List<AudioUi>): MusicSharedAction
    data class OnSongClick(val audioUi: AudioUi): MusicSharedAction
    data object OnFavouriteClick: MusicSharedAction
    data object OnPlaylistClick: MusicSharedAction
    data object OnCreatePlaylistCancelClick: MusicSharedAction
    data object OnCreatePlaylistConfirmClick: MusicSharedAction
    data object OnCreatePlaylistClick: MusicSharedAction
    data object OnFavouritesClick: MusicSharedAction
    data class OnPlaylistItemClick(val playlistUi: PlaylistUi): MusicSharedAction
    data object OnDismissAddToPlaylistSheet: MusicSharedAction
}
