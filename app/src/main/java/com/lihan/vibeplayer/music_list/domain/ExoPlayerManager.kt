package com.lihan.vibeplayer.music_list.domain

import androidx.media3.common.MediaItem
import androidx.media3.common.Player

interface ExoPlayerManager {
    val currentPosition: Long
    val duration: Long


    fun setInitMediaItems(audios: List<Audio>)
    fun getAllMediaItems(): List<MediaItem>
    fun getAllAudios(): List<Audio>

    fun getCurrentMediaItem(): MediaItem?

    fun playSongByIndex(index: Int)
    fun play()
    fun quickPlay(audios: List<Audio> = emptyList())
    fun quickShuffledPlay(audios: List<Audio> = emptyList())
    fun pause()

    fun shuffleEnabled()
    fun getShuffledEnabled(): Boolean
    fun getShuffledMediaItems(): List<MediaItem>
    fun getPlayingMediaItems(): List<MediaItem>

    fun setRepeatMode(repeatMode: Int)
    fun getRepeatMode(): Int

    fun seekTo(position: Long)
    fun skipPrevious()
    fun skipNext()

    fun addListener(listener: Player.Listener)
    fun removeListener(listener: Player.Listener)



}