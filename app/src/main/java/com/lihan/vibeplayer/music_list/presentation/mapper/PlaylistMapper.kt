package com.lihan.vibeplayer.music_list.presentation.mapper

import androidx.core.net.toUri
import com.lihan.vibeplayer.music_list.domain.Playlist
import com.lihan.vibeplayer.music_list.presentation.model.PlaylistCardStyle
import com.lihan.vibeplayer.music_list.presentation.model.PlaylistUi

fun Playlist.toUi(
    audiosCount: Int,
): PlaylistUi {
    return PlaylistUi(
        id = id?:-1,
        title = title,
        count = audiosCount,
        coverImageUriString = coverImageUriString
    )
}

fun PlaylistUi.toDomain(): Playlist {
    return Playlist(
        id = id,
        title = title,
        coverImageUriString = coverImageUriString
    )
}