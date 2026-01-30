package com.lihan.vibeplayer.music_list.presentation.model


sealed interface PlaylistCardStyle{
    data object Favourites: PlaylistCardStyle
    data object Create: PlaylistCardStyle
    data object NoCover: PlaylistCardStyle
    data class HasCover(
        val imageModel: Any?,
        val isUploadedImage: Boolean
    ): PlaylistCardStyle
}
