package com.lihan.vibeplayer.music_list.presentation.mapper

import com.lihan.vibeplayer.music_list.domain.FavouritesPlaylist
import com.lihan.vibeplayer.music_list.presentation.model.FavouritesPlaylistUi
import com.lihan.vibeplayer.music_list.presentation.model.PlaylistCardStyle
import com.lihan.vibeplayer.music_list.presentation.model.PlaylistUi

fun FavouritesPlaylist.toUi(): PlaylistUi {
    return PlaylistUi(
        id = id?:0,
        title = title,
        audioIds = audioIds,
        style = PlaylistCardStyle.Favourites
    )
}