package com.lihan.vibeplayer.music_list.presentation.play

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.lihan.vibeplayer.core.presentation.ObserveEvent
import com.lihan.vibeplayer.core.presentation.components.CircleIconButton
import com.lihan.vibeplayer.music_list.presentation.model.AudioUi
import com.lihan.vibeplayer.ui.theme.SurfaceOutline
import com.lihan.vibeplayer.ui.theme.TextPrimary
import com.lihan.vibeplayer.ui.theme.TextSecondary
import com.lihan.vibeplayer.ui.theme.VibePlayerTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import org.koin.androidx.compose.koinViewModel

@Composable
fun PlayingScreenRoot(
    audioId: Long,
    onBack: () -> Unit,
    viewModel: PlayingViewModel = koinViewModel()
){
    val state by viewModel.state.collectAsStateWithLifecycle()
    val exoPlayer by viewModel.exoPlayer.collectAsStateWithLifecycle()
    val context = LocalContext.current

    ObserveEvent(viewModel.uiEvent) { event ->
        when(event){
            PlayUiEvent.OnAudioNotFound -> {
                //TODO Error Handle
            }
        }
    }

    LaunchedEffect(audioId) {
        viewModel.onAction(PlayingAction.OnSetupPlayer(audioId , context))
    }

    PlayingScreen(
        state = state,
        exoPlayer = exoPlayer,
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
    exoPlayer: ExoPlayer?,
    onAction: (PlayingAction) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentPosition by remember {
        mutableLongStateOf(0L)
    }
    var currentDuration by remember {
        mutableLongStateOf(0L)
    }
    val progress by remember {
        derivedStateOf {
            if (currentDuration > 0) currentPosition.toFloat() / currentDuration else 0f
        }
    }
    LaunchedEffect(exoPlayer) {
        if (exoPlayer!=null){
            while (isActive){
                if (exoPlayer.isPlaying){
                    currentPosition = exoPlayer.currentPosition
                    //Change Song
                    if (exoPlayer.duration != currentDuration && exoPlayer.duration > 0) {
                        currentDuration = exoPlayer.duration
                    }
                }
                delay(500L)
            }
        }
    }







}

@Preview
@Composable
private fun PlayingScreenPreview() {
    VibePlayerTheme {
        val context = LocalContext.current
        PlayingScreen(
            state = PlayingState(
                isPlaying = false,
                currentAudio = AudioUi(
                    songTitle = "This is song's title.",
                    artisName = "ArtisName",
                    duration = 10_000
                )
            ),
            onAction = {},
            exoPlayer = null

        )
    }
}