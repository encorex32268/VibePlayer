package com.lihan.vibeplayer.music_list.presentation

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lihan.vibeplayer.R
import com.lihan.vibeplayer.music_list.presentation.components.EmptyView
import com.lihan.vibeplayer.music_list.presentation.components.ListFunctionSection
import com.lihan.vibeplayer.music_list.presentation.components.PlayerBottomBar
import com.lihan.vibeplayer.music_list.presentation.components.ScanningView
import com.lihan.vibeplayer.music_list.presentation.components.SongCard
import com.lihan.vibeplayer.music_list.presentation.components.SongListContent
import com.lihan.vibeplayer.music_list.presentation.model.AudioUi
import com.lihan.vibeplayer.ui.design_system.buttons.VPFloatingActionButton
import com.lihan.vibeplayer.ui.theme.SurfaceBG
import com.lihan.vibeplayer.ui.theme.SurfaceOutline
import com.lihan.vibeplayer.ui.theme.VibePlayerTheme
import kotlinx.coroutines.launch

@Composable
fun SongsScreen(
    state: MusicListState,
    onAction: (MusicListAction) -> Unit,
    onSongClick: (AudioUi) -> Unit,
    onFunctionShuffleClick: () -> Unit,
    onFunctionPlayClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()

    val scope = rememberCoroutineScope()

    val isShowFloatingActionButton by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex >= 10
        }
    }

    Scaffold(
        modifier = modifier,
        containerColor = SurfaceBG,
        floatingActionButton = {
            AnimatedVisibility(
                modifier = Modifier.padding(bottom = if (state.playingAudioUi != null) (96).dp else 0.dp),
                visible = isShowFloatingActionButton,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                VPFloatingActionButton(
                    onClick = {
                        scope.launch {
                            listState.animateScrollToItem(0)
                        }
                    },
                    content = {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.arrow_up),
                            tint = Color.White,
                            contentDescription = stringResource(R.string.main_floating_action_button_scroll_to_top)
                        )
                    }
                )
            }
        },
    ) { innerPadding ->
        when {
            state.isScanning -> {
                ScanningView(
                    modifier = Modifier.fillMaxSize()
                )
            }

            state.audios.isEmpty() && !state.isScanning -> {
                EmptyView(
                    onScanAgainClick = {
                        onAction(MusicListAction.OnScanAgainClick)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            else -> {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    SongListContent(
                        modifier = Modifier.fillMaxSize().padding(bottom = if (state.playingAudioUi != null) (96).dp else 0.dp),
                        listState = listState,
                        audios = state.audios,
                        onFunctionShuffleClick = onFunctionShuffleClick,
                        onFunctionPlayClick = onFunctionPlayClick,
                        onSongClick = onSongClick
                    )

                    if (state.playingAudioUi != null){
                        Box(
                            modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter),
                            contentAlignment = Alignment.Center
                        ){
                            AnimatedVisibility(
                                visible = true,
                                enter = slideInVertically(
                                    initialOffsetY = { fullHeight -> fullHeight }
                                ) + fadeIn(),
                                exit = slideOutVertically(
                                    targetOffsetY = { fullHeight -> fullHeight }
                                ) + fadeOut()
                            ) {
                                PlayerBottomBar(
                                    audioUi = state.playingAudioUi,
                                    modeStatusBanner = state.modeStatusBanner,
                                    repeatModeStatus = state.repeatModeStatus,
                                    isPlaying = state.isPlaying,
                                    isEnabledShuffle = state.isEnabledShuffle,
                                    isExpandPlayer = state.isExpandPlayer,
                                    duration = state.duration,
                                    currentPosition = state.currentPosition,
                                    onPlayClick = {
                                        onAction(MusicListAction.OnPlayClick)
                                    },
                                    onSkipNextClick = {
                                        onAction(MusicListAction.OnSkipNextClick)
                                    },
                                    onSkipPreviousClick = {
                                        onAction(MusicListAction.OnSkipPreviousClick)
                                    },
                                    onSeek = {
                                        onAction(MusicListAction.OnSeek(it))
                                    },
                                    onRepeatClick = {
                                        onAction(MusicListAction.OnRepeatClick)
                                    },
                                    onShuffleClick = {
                                        onAction(MusicListAction.OnShuffleClick)
                                    },
                                    onExpandClick = {
                                        onAction(MusicListAction.OnExpandClick)
                                    },
                                    onCollapseClick = {
                                        onAction(MusicListAction.OnCollapseClick)
                                    },
                                    onHideModeChangedBanner = {
                                        onAction(MusicListAction.OnHideModeChangedBanner)
                                    }
                                )
                            }

                        }

                    }
                }
            }
        }

    }

}


@Preview(showBackground = true)
@Composable
private fun SongsScreenPreview() {
    VibePlayerTheme {
        SongsScreen(
            state = MusicListState(
                isScanning = false,
                audios = (0..20).map {
                    AudioUi(
                        id = it.toLong(),
                        album = Uri.EMPTY,
                        songTitle = "Song-${it}",
                        artisName = "Artis-${it}",
                        duration = it.toLong() * 10000
                    )
                },
                playingAudioUi = AudioUi(
                    id = 1L,
                    album = Uri.EMPTY,
                    songTitle = "Song-1",
                    artisName = "Artis-1",
                    duration = 1 * 10000L
                )
            ),
            onSongClick = {},
            onAction = {},
            onFunctionPlayClick = {},
            onFunctionShuffleClick = {}
        )
    }
}