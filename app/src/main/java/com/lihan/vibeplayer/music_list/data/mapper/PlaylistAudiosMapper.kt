package com.lihan.vibeplayer.music_list.data.mapper

import com.lihan.vibeplayer.core.database.PlaylistAudios

typealias DomainPlaylistAudios = com.lihan.vibeplayer.music_list.domain.PlaylistAudios

fun PlaylistAudios.toDomain(): DomainPlaylistAudios{
    return DomainPlaylistAudios(
        playlist = this.playlist.toDomain(),
        audios = audios.map { it.toDomain() }
    )
}