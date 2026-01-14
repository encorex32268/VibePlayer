package com.lihan.vibeplayer.music_list.domain

import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.flow.Flow

interface ExoPlayerService {
    val player: ExoPlayer
    val isPlaying: Flow<Boolean>
    val playbackProgress: Flow<Float>

    fun setPlayList(mediaItems: List<MediaItem>)
    fun playByIndex(index: Int)
    fun play()
    fun pause()
    fun seekToNextMediaItem()
    fun seekToPreviousMediaItem()
    fun release()
}