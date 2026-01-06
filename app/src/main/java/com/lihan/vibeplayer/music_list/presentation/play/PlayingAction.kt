package com.lihan.vibeplayer.music_list.presentation.play

sealed interface PlayingAction {
    data class OnSetCurrentAudio(val id: Long): PlayingAction
    data object OnBackClick: PlayingAction
    data object OnPlayClick: PlayingAction
    data object OnSkipNextClick: PlayingAction
    data object OnSkipPreviousClick: PlayingAction
}