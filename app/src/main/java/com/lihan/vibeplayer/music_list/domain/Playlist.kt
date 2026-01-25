package com.lihan.vibeplayer.music_list.domain

data class Playlist(
    val id: Int?=null,
    val title: String,
    val audioIds: List<String>,
    val coverImageUriString: String?=null
)