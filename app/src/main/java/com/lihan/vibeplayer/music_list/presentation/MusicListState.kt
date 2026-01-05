package com.lihan.vibeplayer.music_list.presentation

import com.lihan.vibeplayer.music_list.presentation.model.AudioUi

data class MusicListState(
    val isScanning: Boolean = true,
    val audios: List<AudioUi> = emptyList()
)
