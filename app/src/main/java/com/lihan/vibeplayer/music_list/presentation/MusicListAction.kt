package com.lihan.vibeplayer.music_list.presentation

import com.lihan.vibeplayer.music_list.presentation.model.AudioUi

sealed interface MusicListAction {
    data object OnScanAgainClick: MusicListAction
    data object OnScanClick: MusicListAction
    data class OnAudioUiClick(val audioUi: AudioUi): MusicListAction
    data object OnSearchClick: MusicListAction
    data object OnPlayListShuffleClick: MusicListAction
    data object OnPlayClick: MusicListAction
    data object OnSkipNextClick: MusicListAction
    data object OnSkipPreviousClick: MusicListAction
    data class OnSeek(val position: Long): MusicListAction
    data object OnRepeatClick: MusicListAction
    data object OnShuffleClick: MusicListAction
}