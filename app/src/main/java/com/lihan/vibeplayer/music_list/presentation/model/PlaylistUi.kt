package com.lihan.vibeplayer.music_list.presentation.model

data class PlaylistUi(
    val id: Int,
    val title: String = "",
    val count: Int = 0,
    val audioIds: List<String> = emptyList(),
    val style: PlaylistCardStyle = PlaylistCardStyle.NoCover,
    val coverImageUriString: String?=null
)