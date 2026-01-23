package com.lihan.vibeplayer.music_list.presentation.mapper

import com.lihan.vibeplayer.music_list.domain.Playlist
import com.lihan.vibeplayer.music_list.presentation.model.PlaylistCardStyle
import com.lihan.vibeplayer.music_list.presentation.model.PlaylistUi

fun Playlist.toUi(coverStyle: PlaylistCardStyle): PlaylistUi {
    return PlaylistUi(
        id = id?:-1,
        title = title,
        audioIds = audioIds,
        style = coverStyle,
    )
}