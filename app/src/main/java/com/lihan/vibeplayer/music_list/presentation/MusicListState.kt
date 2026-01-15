package com.lihan.vibeplayer.music_list.presentation

import com.lihan.vibeplayer.music_list.presentation.model.AudioUi

data class MusicListState(
    val isScanning: Boolean = true,
    val isEnabledRepeat: Boolean = false,
    val isEnabledShuffle: Boolean = false,
    val audios: List<AudioUi> = emptyList(),
    val playingAudioUi: AudioUi?=null,
    val isPlaying: Boolean = false,
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
    val progress: Float = 0f,
)
