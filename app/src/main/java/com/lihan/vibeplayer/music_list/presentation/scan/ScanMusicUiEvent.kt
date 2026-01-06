package com.lihan.vibeplayer.music_list.presentation.scan

sealed interface ScanMusicUiEvent {
    data class OnShowResultSize(val itemSize: Int): ScanMusicUiEvent
}