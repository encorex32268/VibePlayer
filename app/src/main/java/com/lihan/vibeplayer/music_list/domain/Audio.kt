package com.lihan.vibeplayer.music_list.domain

import android.net.Uri

data class Audio(
    val id: Long?=null,
    val album: Uri?=null,
    val songTitle: String,
    val artisName: String,
    val duration: Long
)
