package com.lihan.vibeplayer.music_list.presentation.playlistdetial

sealed interface PlaylistDetailAction {
    data object OnBackClick: PlaylistDetailAction
    data object OnAddClick: PlaylistDetailAction
}