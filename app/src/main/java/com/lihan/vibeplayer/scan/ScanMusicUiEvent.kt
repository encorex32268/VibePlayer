package com.lihan.vibeplayer.scan

sealed interface ScanMusicUiEvent {
    data class OnShowResultSize(val itemSize: Int): ScanMusicUiEvent
}