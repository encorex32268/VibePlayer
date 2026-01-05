package com.lihan.vibeplayer.music_list.presentation.mapper

import com.lihan.vibeplayer.music_list.domain.Audio
import com.lihan.vibeplayer.music_list.presentation.model.AudioUi

fun Audio.toUi(): AudioUi {
    return AudioUi(
        id = id,
        album = album,
        songTitle = songTitle,
        artisName = artisName,
        duration = duration
    )
}