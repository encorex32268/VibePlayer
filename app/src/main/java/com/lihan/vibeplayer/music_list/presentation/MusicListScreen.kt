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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lihan.vibeplayer.R
import com.lihan.vibeplayer.core.presentation.ObserveEvent
import com.lihan.vibeplayer.music_list.presentation.components.EmptyView
import com.lihan.vibeplayer.music_list.presentation.components.ListFunctionSection
import com.lihan.vibeplayer.music_list.presentation.components.MusicListScreenTopBar
import com.lihan.vibeplayer.music_list.presentation.components.PlayerBottomBar
import com.lihan.vibeplayer.music_list.presentation.components.ScanningView
import com.lihan.vibeplayer.music_list.presentation.components.SongCard
import com.lihan.vibeplayer.music_list.presentation.model.AudioUi
import com.lihan.vibeplayer.ui.design_system.buttons.VPFloatingActionButton
import com.lihan.vibeplayer.ui.design_system.surface.VPSurface
import com.lihan.vibeplayer.ui.theme.SurfaceBG
import com.lihan.vibeplayer.ui.theme.SurfaceOutline
import com.lihan.vibeplayer.ui.theme.VibePlayerTheme
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun MusicListScreenRoot(
    onNavigateToScan: () -> Unit,
    onNavigateToSearch: () -> Unit,
    viewModel: MusicListViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    MusicListScreen(
        state = state,
        onAction = { action ->
            when (action) {
                MusicListAction.OnScanClick -> onNavigateToScan()
                MusicListAction.OnSearchClick -> onNavigateToSearch()
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )

}

@Composable
fun MusicListScreen(
    state: MusicListState,
    onAction: (MusicListAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val isShowFloatingActionButton by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex >= 10
        }
    }
    val density = LocalDensity.current

    var miniPlayerHeight by remember { mutableStateOf(0.dp) }

    Scaffold(
        containerColor = SurfaceBG,
        floatingActionButton = {
            AnimatedVisibility(
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
        bottomBar = {
            if (state.playingAudioUi != null){
                AnimatedVisibility(
                    modifier = Modifier.onSizeChanged{
                        miniPlayerHeight = with(density){ it.height.toDp() }
                    },
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
                        currentPosition = { state.currentPosition },
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
    ) { it ->
        it
        VPSurface {
            Column(
                modifier = modifier
                    .fillMaxSize()
            ) {
                MusicListScreenTopBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp, horizontal = 16.dp),
                    onScanClick = {
                        onAction(MusicListAction.OnScanClick)
                    },
                    onSearchClick = {
                        onAction(MusicListAction.OnSearchClick)
                    }
                )
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
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                        ) {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(8.dp),
                                state = listState
                            ) {
                                item{
                                    ListFunctionSection(
                                        isTablet = false,
                                        songListSize = state.audios.size,
                                        onShuffleClick = {
                                            onAction(MusicListAction.OnFunctionShuffleClick)
                                        },
                                        onPlayClick = {
                                            onAction(
                                                MusicListAction.OnFunctionPlayClick
                                            )
                                        }
                                    )
                                }
                                itemsIndexed(
                                    items = state.audios,
                                    key = { _, audioUi ->
                                        audioUi.id
                                    }
                                ) { index, audioUi ->
                                    if (index != 0) {
                                        HorizontalDivider(
                                            color = SurfaceOutline,
                                            thickness = 1.dp
                                        )
                                    }
                                    SongCard(
                                        audioUi = audioUi,
                                        modifier = Modifier.fillMaxWidth(),
                                        onAudioClick = {
                                            onAction(MusicListAction.OnSongClick(audioUi))
                                        }
                                    )
                                }
                                item {
                                    Spacer(Modifier.height(miniPlayerHeight))
                                }
                            }

                        }
                    }
                }

            }

        }

    }

}

@Preview(showSystemUi = true)
@Composable
private fun MusicListScreenPreview() {
    VibePlayerTheme {
        MusicListScreen(
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
                }
            ),
            onAction = {

            }
        )
    }
}