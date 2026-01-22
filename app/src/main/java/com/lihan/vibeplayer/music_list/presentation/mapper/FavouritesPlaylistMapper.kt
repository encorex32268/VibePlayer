package com.lihan.vibeplayer.music_list.presentation.mapper

import com.lihan.vibeplayer.music_list.domain.FavouritesPlaylist
import com.lihan.vibeplayer.music_list.presentation.model.FavouritesPlaylistUi

fun FavouritesPlaylist.toUi(): FavouritesPlaylistUi {
    return FavouritesPlaylistUi(
        id = id,
        title = title,
        count = audioIds.size
    )
}