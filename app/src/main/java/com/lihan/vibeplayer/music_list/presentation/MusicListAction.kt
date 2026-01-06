package com.lihan.vibeplayer.music_list.presentation

sealed interface MusicListAction {
    data object OnScanAgainClick: MusicListAction
    data object OnScanClick: MusicListAction
    data class OnAudioClick(val id: Long?): MusicListAction
}