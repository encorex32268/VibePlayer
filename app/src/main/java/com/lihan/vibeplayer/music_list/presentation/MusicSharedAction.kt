package com.lihan.vibeplayer.music_list.presentation

import com.lihan.vibeplayer.music_list.domain.Audio
import com.lihan.vibeplayer.music_list.presentation.model.AudioUi

sealed interface MusicSharedAction {
    data object OnScanAgainClick: MusicSharedAction
    data object OnPlayClick: MusicSharedAction
    data object OnSkipNextClick: MusicSharedAction
    data object OnSkipPreviousClick: MusicSharedAction
    data class OnSeek(val duration: Long): MusicSharedAction
    data object OnRepeatClick: MusicSharedAction
    data object OnShuffleClick: MusicSharedAction
    data object OnExpandClick: MusicSharedAction
    data object OnCollapseClick: MusicSharedAction
    data object OnHideModeChangedBanner: MusicSharedAction
    data class OnFunctionPlayClick(val audios: List<AudioUi>): MusicSharedAction
    data class OnFunctionShuffleClick(val audios: List<AudioUi>): MusicSharedAction
    data class OnSongClick(val audioUi: AudioUi): MusicSharedAction
}
