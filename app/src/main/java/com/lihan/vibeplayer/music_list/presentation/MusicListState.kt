package com.lihan.vibeplayer.music_list.presentation

import com.lihan.vibeplayer.core.presentation.util.UiText
import com.lihan.vibeplayer.music_list.presentation.model.AudioUi
import com.lihan.vibeplayer.music_list.presentation.model.RepeatModeStatus

data class MusicListState(
    val isScanning: Boolean = true,
    val isEnabledShuffle: Boolean = false,
    val isExpandPlayer: Boolean = false,
    val audios: List<AudioUi> = emptyList(),
    val playingAudioUi: AudioUi?=null,
    val isPlaying: Boolean = false,
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
    val progress: Float = 0f,
    val modeStatusBanner: UiText?=null,
    val repeatModeStatus: RepeatModeStatus = RepeatModeStatus.Off
)
