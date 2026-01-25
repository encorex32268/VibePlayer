package com.lihan.vibeplayer.music_list.presentation.playlistdetial

import com.lihan.vibeplayer.music_list.presentation.model.AudioUi
import com.lihan.vibeplayer.music_list.presentation.model.PlaylistUi

data class PlaylistDetailState(
    val audios: List<AudioUi> = emptyList(),
    val playlistUi: PlaylistUi?=null
)
