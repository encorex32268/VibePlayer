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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lihan.vibeplayer.core.presentation.util.UiText
import com.lihan.vibeplayer.music_list.presentation.model.AudioUi
import com.lihan.vibeplayer.music_list.presentation.model.RepeatModeStatus
import com.lihan.vibeplayer.ui.theme.VibePlayerTheme

@Composable
fun PlayerBottomBar(
    isExpandPlayer: Boolean,
    isPlaying: Boolean,
    isEnabledShuffle: Boolean,
    modeStatusBanner: UiText?,
    repeatModeStatus: RepeatModeStatus,
    audioUi: AudioUi,
    currentPosition: Long,
    duration: Long,
    onSeek: (Long) -> Unit,
    onPlayClick: () -> Unit,
    onSkipNextClick: () -> Unit,
    onSkipPreviousClick: () -> Unit,
    onRepeatClick: () -> Unit,
    onShuffleClick: () -> Unit,
    onCollapseClick: () -> Unit,
    onExpandClick: () -> Unit,
    onHideModeChangedBanner: () -> Unit,
    onFavouriteClick: () -> Unit,
    onPlaylistClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    BackHandler(enabled = isExpandPlayer) {
        onCollapseClick()
    }

    val progress by remember(currentPosition) {
        derivedStateOf {
            if (duration > 0) currentPosition.toFloat() / duration.toFloat() else 0f
        }
    }


    AnimatedContent(
        modifier = modifier
            .clickable(
                onClick = {},
                indication = null,
                interactionSource = null
            ),
        targetState = isExpandPlayer,
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
                isEnabledShuffle = isEnabledShuffle,
                modeStatusBanner = modeStatusBanner,
                repeatModeStatus = repeatModeStatus,
                audioUi = audioUi,
                progress = { progress },
                currentPosition = { currentPosition },
                onPlayClick = onPlayClick,
                onSkipNextClick = onSkipNextClick,
                onSkipPreviousClick = onSkipPreviousClick,
                onCollapseClick = onCollapseClick,
                onSeek = onSeek,
                onRepeatClick = onRepeatClick,
                onShuffleClick = onShuffleClick,
                onHideModeChangedBanner = onHideModeChangedBanner,
                onFavouriteClick = onFavouriteClick,
                onPlaylistClick = onPlaylistClick
            )


        } else {

            MiniPlayer(
                modifier = Modifier
                    .widthIn(max = 480.dp)
                    .fillMaxWidth(),
                audioUi = audioUi,
                isPlaying = isPlaying,
                playbackProgress = { progress },
                onPlayClick = onPlayClick,
                onSkipNextClick = onSkipNextClick,
                onMiniPlayerClick = onExpandClick
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
            modeStatusBanner = UiText.DynamicString("Test"),
            duration = 1000,
            isPlaying = false,
            currentPosition = 1000,
            onPlayClick = {},
            onSkipPreviousClick = {},
            onSkipNextClick = {},
            onSeek = {},
            isEnabledShuffle = false,
            onRepeatClick = {},
            onShuffleClick = {},
            isExpandPlayer = true,
            onExpandClick = {},
            onCollapseClick = {},
            onHideModeChangedBanner = {},
            repeatModeStatus = RepeatModeStatus.Off,
            onPlaylistClick = {},
            onFavouriteClick = {}
        )
    }

}