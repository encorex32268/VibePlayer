package com.lihan.vibeplayer.core.data.mapper

import android.net.Uri
import com.lihan.vibeplayer.core.data.local.AudioEntity
import com.lihan.vibeplayer.music_list.domain.Audio

fun AudioEntity.toDomain(): Audio{
    return Audio(
        id = id?.toLong()?:-1,
        album = Uri.parse(this.albumUri),
        songTitle = songTitle,
        artisName = artisName,
        duration = duration
    )
}

fun Audio.toData(): AudioEntity{
    return AudioEntity(
        id = id.toInt(),
        albumUri = album.toString(),
        songTitle = songTitle,
        artisName = artisName,
        duration = duration
    )
}