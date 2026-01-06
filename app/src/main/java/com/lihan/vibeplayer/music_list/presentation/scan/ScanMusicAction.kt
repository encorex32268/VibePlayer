package com.lihan.vibeplayer.music_list.presentation.scan

sealed interface ScanMusicAction {
    data class OnSecondSelect(val second: String): ScanMusicAction
    data class OnSizeSelect(val size: String): ScanMusicAction
    data object OnScanClick: ScanMusicAction
    data object OnBackClick: ScanMusicAction
}