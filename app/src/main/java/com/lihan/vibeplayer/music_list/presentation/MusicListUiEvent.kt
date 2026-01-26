package com.lihan.vibeplayer.music_list.presentation


sealed interface MusicListUiEvent {
    data class OnNavigateToAddSongs(val title: String): MusicListUiEvent
    data class OnNavigateToPlaylistDetail(val id: Int): MusicListUiEvent
}