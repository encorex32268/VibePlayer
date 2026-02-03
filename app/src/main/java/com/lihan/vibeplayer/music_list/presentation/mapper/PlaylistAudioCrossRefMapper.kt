package com.lihan.vibeplayer.music_list.presentation.mapper

import com.lihan.vibeplayer.music_list.domain.PlaylistAudioCrossRef
import com.lihan.vibeplayer.music_list.presentation.model.PlaylistAudioUiCrossRef

fun PlaylistAudioCrossRef.toUi(): PlaylistAudioUiCrossRef {
    return PlaylistAudioUiCrossRef(
        playlistId = playlistId,
        audioId = audioId,
        order = order
    )
}