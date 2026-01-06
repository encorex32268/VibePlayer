package com.lihan.vibeplayer.music_list.presentation.play

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.exoplayer.ExoPlayer
import coil3.compose.AsyncImage
import com.lihan.vibeplayer.R
import com.lihan.vibeplayer.core.presentation.components.CircleIconButton
import com.lihan.vibeplayer.music_list.presentation.model.AudioUi
import com.lihan.vibeplayer.ui.theme.SurfaceBG
import com.lihan.vibeplayer.ui.theme.TextPrimary
import com.lihan.vibeplayer.ui.theme.TextSecondary
import com.lihan.vibeplayer.ui.theme.VibePlayerTheme

@Composable
fun PlayingScreenRoot(
    audioId: Long,
    onBack: () -> Unit,
    viewModel: PlayingViewModel
){
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current


    LaunchedEffect(audioId) {
        viewModel.onAction(
            PlayingAction.OnSetCurrentAudio(audioId)
        )
    }

    PlayingScreen(
        state = state,
        onAction = { action ->
            when(action){
                PlayingAction.OnBackClick -> onBack()
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@Composable
fun PlayingScreen(
    state: PlayingState,
    onAction: (PlayingAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterStart
        ){
            CircleIconButton(
                icon = ImageVector.vectorResource(R.drawable.arrow_left),
                onClick = {
                    onAction(PlayingAction.OnBackClick)
                }
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            if (state.imageByteArray == null){
                Image(
                    contentDescription = stringResource(R.string.playing_music_album_image),
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .size(320.dp),
                    painter = painterResource(R.drawable.song_image_default)
                )
            }else{
                AsyncImage(
                    model = state.imageByteArray,
                    contentDescription = stringResource(R.string.playing_music_album_image),
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .size(320.dp),
                    placeholder = painterResource(R.drawable.song_image_default),
                    error = painterResource(R.drawable.song_image_default)
                )
            }

            Spacer(Modifier.height(20.dp))
            Text(
                text = state.currentAudio?.songTitle?: stringResource(R.string.playing_music_unknow),
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary
            )
            Text(
                text = state.currentAudio?.artisName?: stringResource(R.string.playing_music_unknow),
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth(),
            progress = { 0.3f },
            color = Color.White,
            trackColor = SurfaceBG,
            strokeCap = StrokeCap.Round
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
                onClick = {
                    onAction(PlayingAction.OnSkipPreviousClick)
                }
            )
            IconButton(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(color = Color.White , shape = CircleShape),
                onClick = {
                    onAction(PlayingAction.OnPlayClick)
                }
            ) {
                Icon(
                    imageVector = if (state.isPlaying){
                        ImageVector.vectorResource(R.drawable.pause)
                    }else{
                        ImageVector.vectorResource(R.drawable.play)
                    },
                    contentDescription = null
                )
            }
            CircleIconButton(
                icon = ImageVector.vectorResource(R.drawable.skip_next),
                onClick = {
                    onAction(PlayingAction.OnSkipNextClick)
                }
            )
        }
    }


}

@Preview
@Composable
private fun PlayingScreenPreview() {
    VibePlayerTheme {
        PlayingScreen(
            state = PlayingState(
                isPlaying = false,
                currentAudio = AudioUi(
                    songTitle = "This is song's title.",
                    artisName = "ArtisName",
                    duration = 10_000
                )
            ),
            onAction = {}
        )
    }
}