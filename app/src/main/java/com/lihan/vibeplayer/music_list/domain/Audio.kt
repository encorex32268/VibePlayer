package com.lihan.vibeplayer.music_list.domain

import android.net.Uri

data class Audio(
    val id: Long,
    val album: Uri,
    val songTitle: String,
    val artisName: String,
    val duration: Long,
    val size: Long,
    val isFavourite: Boolean = false
)
