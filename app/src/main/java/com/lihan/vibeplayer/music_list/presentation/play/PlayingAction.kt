package com.lihan.vibeplayer.music_list.presentation.play

import android.content.Context

sealed interface PlayingAction {
    data class OnSetupPlayer(val id: Long, val context: Context): PlayingAction
    data object OnBackClick: PlayingAction
    data object OnPlayClick: PlayingAction
    data object OnSkipNextClick: PlayingAction
    data object OnSkipPreviousClick: PlayingAction
}