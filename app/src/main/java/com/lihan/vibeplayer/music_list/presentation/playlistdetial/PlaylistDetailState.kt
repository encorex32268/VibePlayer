package com.lihan.vibeplayer.music_list.presentation.playlistdetial

import com.lihan.vibeplayer.music_list.presentation.model.AudioUi
import com.lihan.vibeplayer.music_list.presentation.model.PlaylistUi


typealias ImageModel = Any
typealias CacheKey = String

data class PlaylistDetailState(
    val audios: List<AudioUi> = emptyList(),
    val playlistUi: PlaylistUi?=null,
    val coverImagePair: Pair<ImageModel?,CacheKey>?=null,
    val isLoading: Boolean = false
)
