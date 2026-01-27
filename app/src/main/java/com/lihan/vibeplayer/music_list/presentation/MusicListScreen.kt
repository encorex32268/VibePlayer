package com.lihan.vibeplayer.music_list.presentation

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lihan.vibeplayer.core.presentation.ObserveEvent
import com.lihan.vibeplayer.music_list.presentation.components.MusicListScreenTopBar
import com.lihan.vibeplayer.music_list.presentation.components.MusicListTabRow
import com.lihan.vibeplayer.music_list.presentation.components.PLAYLIST
import com.lihan.vibeplayer.music_list.presentation.components.SONGS
import com.lihan.vibeplayer.music_list.presentation.model.AudioUi
import com.lihan.vibeplayer.ui.design_system.surface.VPSurface
import com.lihan.vibeplayer.ui.theme.SurfaceBG
import com.lihan.vibeplayer.ui.theme.VibePlayerTheme
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel


@Composable
fun MusicListScreenRoot(
    onNavigateToScan: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onFunctionPlayClick: () -> Unit,
    onFunctionShuffleClick: () -> Unit,
    onNavigateToAddSongs: (String) -> Unit,
    onNavigateToPlaylistDetail: (Int) -> Unit,
    onSongClick: (AudioUi) -> Unit,
    musicSharedViewModel: MusicSharedViewModel,
    viewModel: MusicListViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val sharedState by musicSharedViewModel.state.collectAsStateWithLifecycle()

    ObserveEvent(viewModel.uiEvent) { uiEvent ->
        when (uiEvent) {
            is MusicListUiEvent.OnNavigateToAddSongs -> onNavigateToAddSongs(uiEvent.title)
            is MusicListUiEvent.OnNavigateToPlaylistDetail -> onNavigateToPlaylistDetail(uiEvent.id)
        }

    }

    MusicListScreen(
        state = state,
        sharedState = sharedState,
        onAction = { action ->
            when (action) {
                MusicListAction.OnScanClick -> onNavigateToScan()
                MusicListAction.OnSearchClick -> onNavigateToSearch()
                MusicListAction.OnFunctionPlayClick -> onFunctionPlayClick()
                MusicListAction.OnFunctionShuffleClick -> onFunctionShuffleClick()
                is MusicListAction.OnSongClick -> onSongClick(action.audioUi)
                MusicListAction.OnScanAgainClick -> musicSharedViewModel.onAction(MusicSharedAction.OnScanAgainClick)
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )

}

@Composable
fun MusicListScreen(
    state: MusicListState,
    sharedState: MusicSharedState,
    onAction: (MusicListAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    val horizontalPager = rememberPagerState(
        pageCount = { 2 }
    )
    Scaffold(
        containerColor = SurfaceBG,
    ) { innerPadding -> innerPadding
        VPSurface {
            Column(
                modifier = modifier
                    .fillMaxSize()
            ) {
                MusicListScreenTopBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    onScanClick = {
                        onAction(MusicListAction.OnScanClick)
                    },
                    onSearchClick = {
                        onAction(MusicListAction.OnSearchClick)
                    }
                )
                MusicListTabRow(
                    selectedTabIndex = horizontalPager.currentPage,
                    onTabClick = {
                        scope.launch {
                            horizontalPager.scrollToPage(it)
                        }
                    }
                )

                HorizontalPager(
                    state = horizontalPager,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    when (page) {
                        SONGS -> {
                            SongsPage(
                                modifier = Modifier.fillMaxSize(),
                                state = sharedState,
                                onAction = onAction,
                                onFunctionPlayClick = {
                                    onAction(MusicListAction.OnFunctionPlayClick)
                                },
                                onFunctionShuffleClick = {
                                    onAction(MusicListAction.OnFunctionShuffleClick)
                                },
                                onSongClick = { audioUi ->
                                    onAction(MusicListAction.OnSongClick(audioUi))
                                }
                            )
                        }

                        PLAYLIST -> {
                            PlayListPage(
                                state = state,
                                onCreatePlaylistAddClick = {
                                    onAction(MusicListAction.OnCreatePlaylistAddClick)
                                },
                                onCreatePlaylistCancelClick = {
                                    onAction(MusicListAction.OnCreatePlaylistCancelClick)
                                },
                                onFavouritesMenuDotsClick = {
                                    onAction(MusicListAction.OnFavouritesMenuDotsClick)
                                },
                                onMenuDotsClick = { playlistUi ->
                                    onAction(MusicListAction.OnMenuDotsClick(playlistUi))
                                },
                                onNavigateToAddSongs = {
                                    onAction(MusicListAction.OnNavigateToAddSongs)
                                },
                                onNavigateToPlaylistDetail = { id ->
                                    onAction(MusicListAction.OnNavigateToPlaylistDetail(id))
                                },
                                onUpdatePlaylistCover = { uriString ->
                                    onAction(MusicListAction.OnUpdatePlaylistCover(uriString))
                                },
                                onActionSheetDismiss = {
                                    onAction(MusicListAction.OnActionSheetDismiss)
                                },
                                onRenameAction = { action ->
                                    onAction(MusicListAction.OnRenameAction(action))
                                },
                                onDeleteAction = { action ->
                                    onAction(MusicListAction.OnDeleteAction(action))
                                }

                            )
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
            sharedState = MusicSharedState(
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
            state = MusicListState(

            ),
            onAction = {

            },
        )
    }
}