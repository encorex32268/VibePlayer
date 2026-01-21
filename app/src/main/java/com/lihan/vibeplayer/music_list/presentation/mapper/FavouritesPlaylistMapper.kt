package com.lihan.vibeplayer.music_list.presentation.mapper

import com.lihan.vibeplayer.music_list.domain.FavouritesPlaylist
import com.lihan.vibeplayer.music_list.domain.Playlist
import com.lihan.vibeplayer.music_list.presentation.model.FavouritesPlaylistUi
import com.lihan.vibeplayer.music_list.presentation.model.PlaylistUi

fun FavouritesPlaylist.toUi(): FavouritesPlaylistUi {
    return FavouritesPlaylistUi(
        id = id,
        title = title,
        count = audioIds.size
    )
}