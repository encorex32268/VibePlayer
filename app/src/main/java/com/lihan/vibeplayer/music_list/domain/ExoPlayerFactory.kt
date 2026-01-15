package com.lihan.vibeplayer.music_list.domain

import android.content.Context
import androidx.media3.exoplayer.ExoPlayer

object ExoPlayerFactory {
    fun build(context: Context): ExoPlayer{
        return ExoPlayer
            .Builder(context)
            .setHandleAudioBecomingNoisy(true)
            .build()
    }
}