package com.lihan.vibeplayer.music_list.presentation.playlistdetial

sealed interface PlaylistDetailUiEvent {
    data class OnNavigateToAddSongs(val id: Int): PlaylistDetailUiEvent
}