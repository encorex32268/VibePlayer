package com.lihan.vibeplayer.music_list.presentation.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.lihan.vibeplayer.R
import com.lihan.vibeplayer.core.presentation.components.CircleIconButton
import com.lihan.vibeplayer.music_list.presentation.model.AudioUi
import com.lihan.vibeplayer.ui.theme.SurfaceHigher
import com.lihan.vibeplayer.ui.theme.SurfaceOnSurface
import com.lihan.vibeplayer.ui.theme.TextPrimary
import com.lihan.vibeplayer.ui.theme.TextSecondary
import com.lihan.vibeplayer.ui.theme.VibePlayerTheme

@Composable
fun MiniPlayer(
    isPlaying: Boolean,
    audioUi: AudioUi,
    playbackProgress: () -> Float,
    onMiniPlayerClick: () -> Unit,
    onPlayClick: () -> Unit,
    onSkipNextClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clickable {
                onMiniPlayerClick()
            }
            .background(
                color = SurfaceHigher,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AsyncImage(
            model = audioUi.albumImage,
            contentDescription = audioUi.songTitle,
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .size(64.dp),
            placeholder = painterResource(R.drawable.song_image_default),
            error = painterResource(R.drawable.song_image_default)
        )

        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = audioUi.songTitle,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = audioUi.artisName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clickable {
                                onPlayClick()
                            }
                            .clip(CircleShape)
                            .background(color = Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isPlaying) {
                                ImageVector.vectorResource(R.drawable.pause)
                            } else {
                                ImageVector.vectorResource(R.drawable.play)
                            },
                            contentDescription = null,
                            tint = Color.Black
                        )
                    }
                    CircleIconButton(
                        icon = ImageVector.vectorResource(R.drawable.skip_next),
                        onClick = onSkipNextClick
                    )
                }

            }
            Spacer(modifier = Modifier.height(16.dp))
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                progress = playbackProgress,
                color = Color.White,
                trackColor = SurfaceOnSurface,
                strokeCap = StrokeCap.Round,
                gapSize = 0.dp,
                drawStopIndicator = {}
            )
        }

    }

}

@Preview
@Composable
private fun MiniPlayerPreview() {
    VibePlayerTheme {
        MiniPlayer(
            audioUi = AudioUi(
                id = 1,
                songTitle = "Song Title",
                artisName = "Artist Name",
                album = Uri.EMPTY,
                duration = 1000
            ),
            isPlaying = true,
            onPlayClick = {},
            onSkipNextClick = {},
            onMiniPlayerClick = {},
            playbackProgress = { 0.5f }
        )
    }
}