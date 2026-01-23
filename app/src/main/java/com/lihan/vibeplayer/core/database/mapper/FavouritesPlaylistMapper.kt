package com.lihan.vibeplayer.core.database.mapper

import com.lihan.vibeplayer.core.database.FavouritesPlaylistEntity
import com.lihan.vibeplayer.music_list.domain.FavouritesPlaylist

fun FavouritesPlaylistEntity.toDomain(): FavouritesPlaylist {
    return FavouritesPlaylist(
        id = id,
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