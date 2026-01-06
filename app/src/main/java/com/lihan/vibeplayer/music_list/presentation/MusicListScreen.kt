package com.lihan.vibeplayer.music_list.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lihan.vibeplayer.music_list.presentation.components.EmptyView
import com.lihan.vibeplayer.music_list.presentation.components.MusicListScreenTopBar
import com.lihan.vibeplayer.music_list.presentation.components.ScanningView
import com.lihan.vibeplayer.music_list.presentation.components.SongCard
import com.lihan.vibeplayer.ui.design_system.surface.VPSurface
import com.lihan.vibeplayer.ui.theme.SurfaceBG
import com.lihan.vibeplayer.ui.theme.VibePlayerTheme

@Composable
fun MusicListScreenRoot(
    onNavigateToPlay: (Long) -> Unit,
    onNavigateToScan: () -> Unit,
    viewModel: MusicListViewModel
){
    val state by viewModel.state.collectAsStateWithLifecycle()

    MusicListScreen(
        state = state,
        onAction = { action ->
            when(action){
                MusicListAction.OnScanClick -> onNavigateToScan()
                is MusicListAction.OnAudioClick -> {
                    if (action.id == null){
                        return@MusicListScreen
                    }
                    onNavigateToPlay(action.id)
                }
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
                }
            )
            when{
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
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentPadding = PaddingValues(8.dp)
                    ) {
                        items(state.audios){ audioUi ->
                            SongCard(
                                audioUi = audioUi,
                                modifier = Modifier.fillMaxWidth(),
                                onAudioClick = {
                                    onAction(
                                        MusicListAction.OnAudioClick(it.id)
                                    )
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
            state = MusicListState(),
            onAction = {

            }
        )
    }
}