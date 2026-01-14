package com.lihan.vibeplayer.music_list.presentation.components

import android.media.MediaMetadataRetriever
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lihan.vibeplayer.music_list.presentation.model.AudioUi
import com.lihan.vibeplayer.ui.theme.VibePlayerTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun PlayerBottomBar(
    playingAudioUi: AudioUi?,
    isPlaying: Boolean,
    playbackProgress: () -> Float,
    onPlayClick: () -> Unit,
    onSkipNextClick: () -> Unit,
    onSkipPreviousClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val screenHeight = LocalConfiguration.current.screenHeightDp
    val density = LocalDensity.current

    val screenHeightDp = with(density) { (screenHeight * density.density).toDp() }

    var isExpand by remember { mutableStateOf(false) }

    var imageByteArray by remember {
        mutableStateOf<ByteArray?>(null)
    }

    // 使用 BackHandler 處理返回鍵縮回播放器
    BackHandler(enabled = isExpand) {
        isExpand = false
    }

    LaunchedEffect(playingAudioUi?.album) {
        if (playingAudioUi?.album != null) {
            val data = withContext(Dispatchers.IO) {
                val retriever = MediaMetadataRetriever()
                try {
                    retriever.setDataSource(context, playingAudioUi.album)
                    retriever.embeddedPicture
                } catch (e: Exception) {
                    null
                } finally {
                    retriever.release()
                }
            }
            imageByteArray = data
        }
    }

    AnimatedContent(
        targetState = isExpand,
        transitionSpec = {
            ContentTransform(
                targetContentEnter = slideInVertically(animationSpec = tween(300)),
                initialContentExit = slideOutVertically(animationSpec = tween(300)),
                sizeTransform = SizeTransform(sizeAnimationSpec = { _, _ -> tween(300) })
            )
        },
        label = "PlayerAnimation"
    ) { targetIsExpand ->
        if (playingAudioUi!=null){
            if (targetIsExpand) {
                FullScreenPlayer(
                    isPlaying = isPlaying,
                    audioUi = playingAudioUi,
                    playbackProgress = playbackProgress,
                    onPlayClick =  onPlayClick,
                    onSkipNextClick = onSkipNextClick,
                    onSkipPreviousClick = onSkipPreviousClick,
                    onCollapseClick = {
                        isExpand = false
                    },
                    albumImage = imageByteArray
                )
            } else {
                MiniPlayer(
                    audioUi = playingAudioUi,
                    isPlaying = isPlaying,
                    playbackProgress = playbackProgress,
                    albumImage = imageByteArray,
                    onPlayClick = onPlayClick,
                    onSkipNextClick = onSkipNextClick,
                    onMiniPlayerClick = {
                        isExpand = true
                    }
                )
            }

        }
    }

}


@Preview
@Composable
private fun PlayerBottomBarPreview() {
    VibePlayerTheme {
        PlayerBottomBar(
            playingAudioUi = AudioUi(
                songTitle = "505",
                duration = 1000,
                artisName = "Artis Name"
            ),
            isPlaying = false,
            playbackProgress = { 0.5f},
            onPlayClick = {},
            onSkipPreviousClick = {},
            onSkipNextClick = {}
        )
    }

}