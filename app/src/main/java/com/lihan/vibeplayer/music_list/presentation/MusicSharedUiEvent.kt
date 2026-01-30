package com.lihan.vibeplayer.music_list.presentation

sealed interface MusicSharedUiEvent {
    data class OnAddToPlaylistSucceed(val playlistTitle: String): MusicSharedUiEvent
}