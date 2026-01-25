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
import com.lihan.vibeplayer.music_list.presentation.components.SongListContent
import com.lihan.vibeplayer.music_list.presentation.model.AudioUi
import com.lihan.vibeplayer.ui.theme.SurfaceOutline

@Composable
fun SongsScreen(
    state: MusicListState,
    listState: LazyListState,
    onAction: (MusicListAction) -> Unit,
    onSongClick: (AudioUi) -> Unit,
    onFunctionShuffleClick: () -> Unit,
    onFunctionPlayClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when {
        state.isScanning -> {
            ScanningView(
                modifier = modifier.fillMaxSize()
            )
        }

        state.audios.isEmpty() && !state.isScanning -> {
            EmptyView(
                onScanAgainClick = {
                    onAction(MusicListAction.OnScanAgainClick)
                },
                modifier = modifier.fillMaxSize()
            )
        }

        else -> {
            SongListContent(
                modifier = modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                listState = listState,
                audios = state.audios,
                onFunctionShuffleClick = onFunctionShuffleClick,
                onFunctionPlayClick = onFunctionPlayClick,
                onSongClick = onSongClick
            )
        }
    }

}


@Preview(showBackground = true)
@Composable
private fun SongsScreenPreview() {

}