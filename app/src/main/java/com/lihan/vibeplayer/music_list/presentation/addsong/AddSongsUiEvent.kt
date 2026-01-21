package com.lihan.vibeplayer.music_list.presentation.addsong

sealed interface AddSongsUiEvent {
    data object OnPlaylistSaved: AddSongsUiEvent
}