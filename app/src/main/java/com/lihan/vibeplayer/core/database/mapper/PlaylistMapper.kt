package com.lihan.vibeplayer.core.database.mapper

import com.lihan.vibeplayer.core.database.PlaylistEntity
import com.lihan.vibeplayer.music_list.domain.Playlist

fun PlaylistEntity.toDomain(): Playlist {
    return Playlist(
        id = id?:-1,
        title = title,
        audioIds = audioIds,
        coverImageUriString = coverImageUriString
    )
}

fun Playlist.toData(): PlaylistEntity {
    return PlaylistEntity(
        id = id,
        title = title,
        audioIds = audioIds,
        coverImageUriString = coverImageUriString
    )
}