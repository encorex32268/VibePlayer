package com.lihan.vibeplayer.music_list.presentation

import com.lihan.vibeplayer.core.presentation.util.UiText

sealed interface MusicListUiEvent {
    data class OnRepeatModeChange(val uiText: UiText): MusicListUiEvent
    data class OnShuffleEnabledChange(val uiText: UiText): MusicListUiEvent
    data class OnNavigateToAddSongs(val title: String): MusicListUiEvent
}