package com.lihan.vibeplayer.music_list.data.mapper

import android.net.Uri
import com.lihan.vibeplayer.core.database.AudioEntity
import com.lihan.vibeplayer.music_list.domain.Audio
import androidx.core.net.toUri

fun AudioEntity.toDomain(): Audio{
    return Audio(
        id = id?.toLong()?:-1,
        album = this.albumUri.toUri(),
        songTitle = songTitle,
        artisName = artisName,
        duration = duration,
        size = size,
        isFavourite = isFavourite
    )
}

fun Audio.toData(): AudioEntity {
    return AudioEntity(
        id = id.toInt(),
        albumUri = album.toString(),
        songTitle = songTitle,
        artisName = artisName,
        duration = duration,
        size = size,
        isFavourite = isFavourite
    )
}