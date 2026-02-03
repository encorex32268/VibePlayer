package com.lihan.vibeplayer.music_list.presentation.mapper

import com.lihan.vibeplayer.music_list.domain.PlaylistAudios
import com.lihan.vibeplayer.music_list.presentation.model.PlaylistAudiosUi


fun PlaylistAudios.toUi(): PlaylistAudiosUi{
    return PlaylistAudiosUi(
        playlist = this.playlist.toUi(audiosCount = audios.size),
        audios = audios.map { it.toUi() },
        crossRefs = crossRefs.map { it.toUi() }
    )
}