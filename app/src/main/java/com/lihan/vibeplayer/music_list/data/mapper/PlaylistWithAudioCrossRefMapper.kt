package com.lihan.vibeplayer.music_list.data.mapper

import com.lihan.vibeplayer.core.database.PlaylistAudioCrossRef
import com.lihan.vibeplayer.music_list.domain.PlaylistAudio

fun PlaylistAudioCrossRef.toDomain(): PlaylistAudio {
    return PlaylistAudio(
        playlistId = playlistId,
        audioId = audioId
    )
}