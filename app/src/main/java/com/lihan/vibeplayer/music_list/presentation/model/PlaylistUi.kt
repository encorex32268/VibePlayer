package com.lihan.vibeplayer.music_list.presentation.model


data class PlaylistUi(
    val id: Int,
    val title: String,
    val audioIds: List<String>,
    val style: PlaylistCardStyle,
    val coverImageUriString: String?=null
)