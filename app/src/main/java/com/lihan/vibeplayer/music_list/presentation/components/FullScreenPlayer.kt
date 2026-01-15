@file:OptIn(ExperimentalMaterial3Api::class)

package com.lihan.vibeplayer.music_list.presentation.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.lihan.vibeplayer.R
import com.lihan.vibeplayer.core.domain.util.toTimeStringWithoutZero
import com.lihan.vibeplayer.core.presentation.components.CircleIconButton
import com.lihan.vibeplayer.music_list.presentation.model.AudioUi
import com.lihan.vibeplayer.music_list.presentation.model.RepeatModeStatus
import com.lihan.vibeplayer.ui.theme.SurfaceBG
import com.lihan.vibeplayer.ui.theme.SurfaceOutline
import com.lihan.vibeplayer.ui.theme.TextPrimary
import com.lihan.vibeplayer.ui.theme.TextSecondary
import com.lihan.vibeplayer.ui.theme.VibePlayerTheme

@Composable
fun FullScreenPlayer(
    isPlaying: Boolean,
    repeatModeStatus: RepeatModeStatus,
    isEnabledShuffle: Boolean,
    audioUi: AudioUi,
    currentPosition: Long,
    progress: () -> Float,
    onSeek: (Long) -> Unit,
    onCollapseClick: () -> Unit,
    onSkipPreviousClick: () -> Unit,
    onSkipNextClick: () -> Unit,
    onPlayClick: () -> Unit,
    onRepeatClick: () -> Unit,
    onShuffleClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var sliderValue by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

    val displayPosition by remember(isDragging) {
        derivedStateOf {
            if (isDragging) {
                (sliderValue * audioUi.duration).toLong()
            } else {
                currentPosition
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = SurfaceBG)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterStart
        ) {
            CircleIconButton(
                icon = ImageVector.vectorResource(R.drawable.chevron_down),
                onClick = onCollapseClick
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = audioUi.albumImage,
                contentDescription = stringResource(R.string.playing_music_album_image),
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .size(320.dp),
                placeholder = painterResource(R.drawable.song_image_default),
                error = painterResource(R.drawable.song_image_default)
            )

            Spacer(Modifier.height(20.dp))
            Text(
                text = audioUi.songTitle.ifEmpty { stringResource(R.string.playing_music_unknow) },
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                textAlign = TextAlign.Center,
            )
            Text(
                text = audioUi.artisName.ifEmpty { stringResource(R.string.playing_music_unknow) },
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center,
            )
        }
        Box {
            Slider(
                value = if (isDragging) {
                    sliderValue
                } else {
                    progress()
                },
                onValueChange = { newValue ->
                    isDragging = true
                    sliderValue = newValue
                },
                onValueChangeFinished = {
                    isDragging = false
                    onSeek((sliderValue * audioUi.duration).toLong())
                },
                track = { state ->
                    SliderDefaults.Track(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp),
                        sliderState = state,
                        drawStopIndicator = null,
                        thumbTrackGapSize = 0.dp,
                        colors = SliderDefaults.colors(
                            activeTrackColor = TextPrimary,
                            inactiveTrackColor = SurfaceOutline
                        ),
                    )
                },
                thumb = {}
            )
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .alpha(
                        if (isDragging) 1f else 0f
                    )
                    .background(TextPrimary, RoundedCornerShape(100))
                    .padding(horizontal = 4.dp),
            ) {
                Text(
                    text = "${displayPosition.toTimeStringWithoutZero()}/${audioUi.duration.toTimeStringWithoutZero()}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Normal,
                    color = SurfaceBG
                )
            }

        }

        Spacer(modifier = Modifier.height(20.dp))
        PlayerControlSection(
            isPlaying = isPlaying,
            repeatModeStatus = repeatModeStatus,
            isEnabledShuffle = isEnabledShuffle,
            onPlayClick = onPlayClick,
            onSkipNextClick = onSkipNextClick,
            onSkipPreviousClick = onSkipPreviousClick,
            onRepeatClick = onRepeatClick,
            onShuffleClick = onShuffleClick
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun FullScreenPlayerPreview() {
    VibePlayerTheme {
        FullScreenPlayer(
            isPlaying = false,
            audioUi = AudioUi(
                songTitle = "Title",
                artisName = "ArtisName",
                duration = 10000,
                albumImage = null,
                album = Uri.EMPTY,
                id = 0
            ),
            onPlayClick = {},
            onSkipNextClick = {},
            onSkipPreviousClick = {},
            onCollapseClick = {},
            progress = { 0.5f },
            onSeek = {},
            currentPosition = 2000,
            isEnabledShuffle = true,
            repeatModeStatus = RepeatModeStatus.Off,
            onRepeatClick = {},
            onShuffleClick = {}
        )
    }
}