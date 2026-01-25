package com.lihan.vibeplayer.music_list.presentation.model

sealed interface PlaylistCardStyle{
    data object Favourites: PlaylistCardStyle
    data object NoCover: PlaylistCardStyle
    class HasCover(val imageModel: Any?): PlaylistCardStyle
}
