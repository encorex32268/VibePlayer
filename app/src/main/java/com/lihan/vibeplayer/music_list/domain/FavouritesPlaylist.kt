package com.lihan.vibeplayer.music_list.domain

data class FavouritesPlaylist(
    val id: Int?=null,
    val title: String,
    val audioIds: List<String>,
)