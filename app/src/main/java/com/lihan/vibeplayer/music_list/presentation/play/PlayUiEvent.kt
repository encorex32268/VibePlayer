package com.lihan.vibeplayer.music_list.presentation.play

sealed interface PlayUiEvent {
    data object OnAudioNotFound: PlayUiEvent
}