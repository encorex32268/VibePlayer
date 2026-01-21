package com.lihan.vibeplayer.core.data.mapper

import com.lihan.vibeplayer.core.data.local.FavouritesPlaylistEntity
import com.lihan.vibeplayer.music_list.domain.FavouritesPlaylist

fun FavouritesPlaylistEntity.toDomain(): FavouritesPlaylist {
    return FavouritesPlaylist(
        id = id?:-1,
        title = title,
        audioIds = audioIds,
    )
}

fun FavouritesPlaylist.toData(): FavouritesPlaylistEntity {
    return FavouritesPlaylistEntity(
        id = id,
        title = title,
        audioIds = audioIds
    )
}