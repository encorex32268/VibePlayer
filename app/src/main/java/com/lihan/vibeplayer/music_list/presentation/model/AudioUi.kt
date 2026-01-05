package com.lihan.vibeplayer.music_list.presentation.model

import android.net.Uri

data class AudioUi(
    val id: Long?=null,
    val album: Uri?=null,
    val songTitle: String,
    val artisName: String,
    val duration: Long
)
