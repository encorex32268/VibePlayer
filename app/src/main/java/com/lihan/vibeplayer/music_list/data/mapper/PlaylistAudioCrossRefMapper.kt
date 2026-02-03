package com.lihan.vibeplayer.music_list.data.mapper

import com.lihan.vibeplayer.core.database.PlaylistAudioEntityCrossRef
import com.lihan.vibeplayer.music_list.domain.PlaylistAudioCrossRef

fun PlaylistAudioEntityCrossRef.toDomain(): PlaylistAudioCrossRef {
    return PlaylistAudioCrossRef(
        playlistId = playlistId,
        audioId = audioId,
        order = order
    )
}