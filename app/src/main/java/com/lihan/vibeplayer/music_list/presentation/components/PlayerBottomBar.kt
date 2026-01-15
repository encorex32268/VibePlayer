package com.lihan.vibeplayer.music_list.presentation.components

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.lihan.vibeplayer.music_list.presentation.model.AudioUi
import com.lihan.vibeplayer.ui.theme.VibePlayerTheme

@Composable
fun PlayerBottomBar(
    isPlaying: Boolean,
    isEnabledRepeat: Boolean,
    isEnabledShuffle: Boolean,
    audioUi: AudioUi,
    currentPosition: () -> Long,
    duration: Long,
    onSeek: (Long) -> Unit,
    onPlayClick: () -> Unit,
    onSkipNextClick: () -> Unit,
    onSkipPreviousClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpand by remember { mutableStateOf(false) }

    BackHandler(enabled = isExpand) {
        isExpand = false
    }

    val progress by remember(currentPosition()) {
        derivedStateOf {
            if (duration > 0) currentPosition().toFloat() / duration.toFloat() else 0f
        }
    }


    AnimatedContent(
        modifier = modifier
            .clickable(
                onClick = {},
                indication = null,
                interactionSource = null
            ),
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
        if (targetIsExpand) {
            FullScreenPlayer(
                isPlaying = isPlaying,
                audioUi = audioUi,
                progress = { progress },
                currentPosition = currentPosition(),
                onPlayClick = onPlayClick,
                onSkipNextClick = onSkipNextClick,
                onSkipPreviousClick = onSkipPreviousClick,
                onCollapseClick = {
                    isExpand = false
                },
                onSeek = onSeek,
                isEnabledRepeat = isEnabledRepeat,
                isEnabledShuffle = isEnabledShuffle
            )

        } else {

            MiniPlayer(
                audioUi = audioUi,
                isPlaying = isPlaying,
                playbackProgress = { progress },
                onPlayClick = onPlayClick,
                onSkipNextClick = onSkipNextClick,
                onMiniPlayerClick = {
                    isExpand = true
                }
            )
        }
    }

}



@Preview
@Composable
private fun PlayerBottomBarPreview() {
    VibePlayerTheme {
        PlayerBottomBar(
            audioUi = AudioUi(
                songTitle = "505",
                artisName = "Artis Name",
                album = Uri.EMPTY,
                albumImage = null,
                duration = 10_000,
                id = 1
            ),
            duration = 1000,
            isPlaying = false,
            currentPosition = {1000},
            onPlayClick = {},
            onSkipPreviousClick = {},
            onSkipNextClick = {},
            onSeek = {},
            isEnabledRepeat = false,
            isEnabledShuffle = false
        )
    }

}