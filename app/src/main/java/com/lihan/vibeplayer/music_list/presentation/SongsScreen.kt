package com.lihan.vibeplayer.music_list.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lihan.vibeplayer.music_list.presentation.components.EmptyView
import com.lihan.vibeplayer.music_list.presentation.components.ListFunctionSection
import com.lihan.vibeplayer.music_list.presentation.components.ScanningView
import com.lihan.vibeplayer.music_list.presentation.components.SongCard
import com.lihan.vibeplayer.ui.theme.SurfaceOutline

@Composable
fun SongsScreen(
    state: MusicListState,
    listState: LazyListState,
    miniPlayerHeight: Dp,
    onAction: (MusicListAction) -> Unit,
) {
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


@Preview(showBackground = true)
@Composable
private fun SongsScreenPreview() {

}