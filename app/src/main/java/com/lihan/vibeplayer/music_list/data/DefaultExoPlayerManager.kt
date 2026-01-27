package com.lihan.vibeplayer.music_list.data

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.lihan.vibeplayer.music_list.domain.Audio
import com.lihan.vibeplayer.music_list.domain.ExoPlayerManager

class DefaultExoPlayerManager(
    private val context: Context
): ExoPlayerManager {

    private var initMediaItems: List<MediaItem> = emptyList()
    private var initAudioItems: List<Audio> = emptyList()

    private val exoPlayer = ExoPlayer
        .Builder(context)
        .setHandleAudioBecomingNoisy(true)
        .build()

    override val currentPosition: Long
        get() = exoPlayer.currentPosition

    override val duration: Long
        get() = exoPlayer.duration

    override fun setInitMediaItems(audios: List<Audio>) {
        val items = audios.map { audio ->
            MediaItem.Builder()
                .setMediaId(audio.id.toString())
                .setUri(audio.album)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(audio.songTitle)
                        .setArtist(audio.artisName)
                        .build()
                )
                .build()
        }
        if (initMediaItems.isEmpty()){
            initMediaItems = items
        }
        if (initAudioItems.isEmpty()){
            initAudioItems = audios
        }
        exoPlayer.apply {
            setMediaItems(items)
        }
    }

    override fun playSongByIndex(index: Int) {
        if (index < 0 || index >= exoPlayer.mediaItemCount) {
            return
        }

        exoPlayer.apply {
            seekTo(index, 0L)
            prepare()
        }
    }

    override fun play() {
        exoPlayer.play()
    }

    override fun quickPlay(audios: List<Audio>) {
        exoPlayer.apply {
            stop()
            if (audios.isEmpty()){
                setMediaItems(initMediaItems)
            }else{
                setInitMediaItems(audios)
            }
            prepare()
            play()
        }
    }

    @OptIn(UnstableApi::class)
    override fun quickShuffledPlay(audios: List<Audio>) {
        exoPlayer.apply {
            stop()
            if (audios.isEmpty()){
                setMediaItems(initMediaItems)
            }else{
                setInitMediaItems(audios)
            }
            shuffleModeEnabled = true
            exoPlayer.seekToDefaultPosition(shuffleOrder.firstIndex)
            prepare()
            play()
        }
    }

    override fun pause() {
        exoPlayer.pause()
    }

    override fun shuffleEnabled() {
        exoPlayer.shuffleModeEnabled = !exoPlayer.shuffleModeEnabled
    }

    override fun getShuffledEnabled(): Boolean {
        return exoPlayer.shuffleModeEnabled
    }

    override fun getShuffledMediaItems(): List<MediaItem> {
        val timeline = exoPlayer.currentTimeline
        if (timeline.isEmpty){
            return emptyList()
        }
        val shuffledList = mutableListOf<MediaItem>()
        var currentIndex = timeline.getFirstWindowIndex(true)
        while (currentIndex != -1) {
            val mediaItem = exoPlayer.getMediaItemAt(currentIndex)
            shuffledList.add(mediaItem)

            currentIndex = timeline.getNextWindowIndex(
                currentIndex,
                Player.REPEAT_MODE_OFF,
                true
            )
        }
        return shuffledList
    }

    override fun getPlayingMediaItems(): List<MediaItem> {
        return if(exoPlayer.shuffleModeEnabled){
            getShuffledMediaItems()
        }else{
            (0 until exoPlayer.mediaItemCount).map {
                exoPlayer.getMediaItemAt(it)
            }
        }
    }

    override fun setRepeatMode(repeatMode: Int) {
        exoPlayer.repeatMode = repeatMode
    }

    override fun getRepeatMode(): Int {
        return exoPlayer.repeatMode
    }

    override fun seekTo(position: Long) {
        exoPlayer.seekTo(position)
    }

    override fun skipPrevious() {
        exoPlayer.seekToPreviousMediaItem()
    }

    override fun skipNext() {
        exoPlayer.seekToNextMediaItem()
    }

    override fun addListener(listener: Player.Listener) {
        exoPlayer.addListener(listener)
    }

    override fun removeListener(listener: Player.Listener) {
        exoPlayer.removeListener(listener)
    }

    override fun getAllMediaItems(): List<MediaItem> {
        val items = mutableListOf<MediaItem>()
        for (i in 0 until exoPlayer.mediaItemCount) {
            items.add(exoPlayer.getMediaItemAt(i))
        }
        return items
    }

    override fun getAllAudios(): List<Audio> {
        return initAudioItems
    }

    override fun getCurrentMediaItem(): MediaItem? {
        return exoPlayer.currentMediaItem
    }
}
