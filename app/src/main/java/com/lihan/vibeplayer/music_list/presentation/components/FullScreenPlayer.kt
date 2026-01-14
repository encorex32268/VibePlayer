@file:OptIn(ExperimentalMaterial3Api::class)

package com.lihan.vibeplayer.music_list.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.lihan.vibeplayer.R
import com.lihan.vibeplayer.core.presentation.components.CircleIconButton
import com.lihan.vibeplayer.music_list.presentation.model.AudioUi
import com.lihan.vibeplayer.ui.theme.SurfaceBG
import com.lihan.vibeplayer.ui.theme.SurfaceHigher
import com.lihan.vibeplayer.ui.theme.SurfaceOutline
import com.lihan.vibeplayer.ui.theme.TextPrimary
import com.lihan.vibeplayer.ui.theme.TextSecondary
import com.lihan.vibeplayer.ui.theme.VibePlayerTheme

@Composable
fun FullScreenPlayer(
    isPlaying: Boolean,
    audioUi: AudioUi,
    playbackProgress: () -> Float,
    onCollapseClick: () -> Unit,
    onSkipPreviousClick: () -> Unit,
    onSkipNextClick: () -> Unit,
    onPlayClick: () -> Unit,
    modifier: Modifier = Modifier,
    albumImage: ByteArray?=null
) {

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
        ){
            CircleIconButton(
                icon = ImageVector.vectorResource(R.drawable.chevron_down),
                onClick = onCollapseClick
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            AsyncImage(
                model = albumImage,
                contentDescription = stringResource(R.string.playing_music_album_image),
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .size(320.dp),
                placeholder = painterResource(R.drawable.song_image_default),
                error = painterResource(R.drawable.song_image_default)
            )

            Spacer(Modifier.height(20.dp))
            Text(
                text = audioUi.songTitle.ifEmpty {  stringResource(R.string.playing_music_unknow) },
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary
            )
            Text(
                text = audioUi.artisName.ifEmpty {  stringResource(R.string.playing_music_unknow) },
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
        Slider(
            value = playbackProgress(),
            onValueChange = {},
            track = { state ->
                SliderDefaults.Track(
                    modifier = Modifier.height(6.dp),
                    sliderState = state,
                    drawStopIndicator = null,
                    thumbTrackGapSize = 0.dp,
                    colors = SliderDefaults.colors(
                        activeTrackColor = TextPrimary,
                        inactiveTrackColor = SurfaceOutline
                    ),
                )
            },
            thumb = {
                Row(
                    modifier = Modifier
                        .background(TextPrimary,RoundedCornerShape(100))
                        .padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "2:07 / 4:14",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Normal,
                        color = SurfaceBG
                    )
                }
            }
        )
        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth(),
            progress = playbackProgress,
            color = Color.White,
            trackColor = SurfaceOutline,
            strokeCap = StrokeCap.Round,
            gapSize = 0.dp
        )
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
        ) {
            CircleIconButton(
                icon = ImageVector.vectorResource(R.drawable.skip_previous),
                onClick = onSkipPreviousClick
            )
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clickable{
                        onPlayClick()
                    }
                    .clip(CircleShape)
                    .background(color = Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPlaying){
                        ImageVector.vectorResource(R.drawable.pause)
                    }else{
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
                duration = 20000,
            ),
            onPlayClick = {},
            onSkipNextClick = {},
            onSkipPreviousClick = {},
            onCollapseClick = {},
            playbackProgress = { 0.5f}
        )
    }
}