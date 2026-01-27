package com.lihan.vibeplayer.music_list.presentation

import com.lihan.vibeplayer.core.presentation.util.UiText
import com.lihan.vibeplayer.music_list.presentation.model.AudioUi
import com.lihan.vibeplayer.music_list.presentation.model.RepeatModeStatus


data class MusicSharedState(
    val playingQueue: List<AudioUi> = emptyList(),
    val isPlaying: Boolean = false,
    val isEnabledShuffle: Boolean = false,
    val isExpandPlayer: Boolean = false,
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
    val progress: Float = 0f,
    val repeatModeStatus: RepeatModeStatus = RepeatModeStatus.Off,
    val playingAudioUi: AudioUi?=null,
    val modeStatusBanner: UiText?=null
)
