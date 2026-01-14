package com.lihan.vibeplayer.music_list.data

import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.lihan.vibeplayer.music_list.domain.ExoPlayerService
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class DefaultExoPlayerService(
    private val context: Context
) : ExoPlayerService {


    private var currentMediaItems = emptyList<MediaItem>()

    override val player: ExoPlayer = ExoPlayer
            .Builder(context)
            .setHandleAudioBecomingNoisy(true)
            .build().apply { prepare() }

    override val isPlaying: Flow<Boolean> = callbackFlow {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                trySend(isPlaying)
            }
        }
        player.addListener(listener)

        awaitClose{
            player.removeListener(listener)
        }
    }

    override val playbackProgress: Flow<Float> = callbackFlow {
        fun emitCurrentProgress(){
            val progress = if (player.duration > 0) player.currentPosition.toFloat() / player.duration else 0f
            trySend(progress.coerceIn(0f, 1f))
        }
        val listener = object : Player.Listener {
            // 捕捉：拖動、換歌
            override fun onPositionDiscontinuity(
                oldPosition: Player.PositionInfo,
                newPosition: Player.PositionInfo,
                reason: Int
            ) {
                emitCurrentProgress()
            }
            // 捕捉：播放狀態切換 (按下暫停或播放時立刻同步一次)
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                emitCurrentProgress()
            }
        }
        player.addListener(listener)

        val job = launch {
            while (isActive) {
                if (player.isPlaying) {
                    emitCurrentProgress()
                    delay(500L)
                } else {
                    delay(2000L)
                }
            }
        }
        awaitClose {
            player.removeListener(listener)
            job.cancel()
        }
    }

    override fun setPlayList(mediaItems: List<MediaItem>) {
        currentMediaItems = mediaItems
        player.setMediaItems(currentMediaItems)
    }

    override fun playByIndex(index: Int) {
        print("ID ${player.currentMediaItem?.mediaId}")
        player.setMediaItems(currentMediaItems, index, 0L)
        player.prepare()
    }

    override fun play() {
        player.play()
    }


    override fun pause() {
        player.pause()

    }

    override fun release() {
        player.release()
    }

    override fun seekToNextMediaItem(){
        player.seekToNextMediaItem()
    }

    override fun seekToPreviousMediaItem(){
        player.seekToPreviousMediaItem()
    }

}