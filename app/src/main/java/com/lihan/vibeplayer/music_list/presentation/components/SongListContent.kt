package com.lihan.vibeplayer.music_list.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lihan.vibeplayer.music_list.presentation.model.AudioUi
import com.lihan.vibeplayer.ui.theme.SurfaceOutline

@Composable
fun SongListContent(
    listState: LazyListState,
    audios: List<AudioUi>,
    onSongClick: (AudioUi) -> Unit,
    onFunctionShuffleClick: () -> Unit,
    onFunctionPlayClick: () -> Unit,
    modifier: Modifier = Modifier,
    onAddClick: (() -> Unit)? = null,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            state = listState
        ) {
            item {
                ListFunctionSection(
                    isTablet = false,
                    songListSize = audios.size,
                    onShuffleClick = onFunctionShuffleClick,
                    onPlayClick = onFunctionPlayClick,
                    onAddClick = onAddClick
                )
            }
            itemsIndexed(
                items = audios,
                key = { _ , audioUi ->
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
                        onSongClick(audioUi)
                    }
                )
            }
        }

    }

}

@Preview
@Composable
private fun SongListContentPreview() {

}