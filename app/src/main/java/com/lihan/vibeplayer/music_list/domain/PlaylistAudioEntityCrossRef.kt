package com.lihan.vibeplayer.music_list.domain

data class PlaylistAudioCrossRef(
    val playlistId: Int,
    val audioId: Int,
    val order: Int
)