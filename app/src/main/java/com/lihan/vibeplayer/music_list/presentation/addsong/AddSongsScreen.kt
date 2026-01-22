@file:OptIn(ExperimentalMaterial3Api::class)

package com.lihan.vibeplayer.music_list.presentation.addsong

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lihan.vibeplayer.R
import com.lihan.vibeplayer.core.presentation.ObserveEvent
import com.lihan.vibeplayer.core.presentation.components.CircleIconButton
import com.lihan.vibeplayer.music_list.presentation.components.SearchBar
import com.lihan.vibeplayer.music_list.presentation.components.SongCard
import com.lihan.vibeplayer.music_list.presentation.model.AudioUi
import com.lihan.vibeplayer.ui.design_system.buttons.VPButton
import com.lihan.vibeplayer.ui.design_system.buttons.VPRadioButton
import com.lihan.vibeplayer.ui.theme.TextPrimary
import com.lihan.vibeplayer.ui.theme.VibePlayerTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AddSongsScreenRoot(
    onBack: () -> Unit,
    title: String,
    viewModel: AddSongsViewModel = koinViewModel()
){
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(title) {
        viewModel.onAction(
            AddSongsAction.OnSaveTitleName(title)
        )
    }

    ObserveEvent(viewModel.uiEvent) { uiEvent ->
        when(uiEvent){
            AddSongsUiEvent.OnPlaylistSaved -> onBack()
        }
    }

    AddSongsScreen(
        state = state,
        onAction = { action ->
            when(action){
                AddSongsAction.OnBackClick -> onBack()
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )

}

@Composable
fun AddSongsScreen(
    state: AddSongsState,
    onAction: (AddSongsAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box {
            Column(
                modifier = Modifier.fillMaxSize()
            ){
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                        .padding(top = 10.dp)
                ){
                    CircleIconButton(
                        modifier = Modifier.align(Alignment.CenterStart),
                        icon = ImageVector.vectorResource(R.drawable.arrow_left),
                        onClick = {
                            onAction(AddSongsAction.OnBackClick)
                        }
                    )
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = if (state.selectedCount == 0){
                            stringResource(R.string.add_songs_title)
                        }else{
                            stringResource(R.string.add_songs_selected,state.selectedCount)
                        },
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = TextPrimary
                    )
                }
                SearchBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp,horizontal = 16.dp),
                    textFieldState = state.searchTextField,
                    onCloseClick = {
                        onAction(
                            AddSongsAction.OnCloseClick
                        )
                    }
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    VPRadioButton(
                        selected = state.isSelectAll,
                        onClick = {
                            onAction(
                                AddSongsAction.OnAllSelectedClick
                            )
                        }
                    )
                    Text(
                        text = stringResource(R.string.add_songs_select_all),
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )
                }
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                ) {
                    items(
                        items = state.audioUis,
                        key = { it.id }
                    ){ audioUi ->
                        SongCard(
                            audioUi = audioUi,
                            isSelectable = true,
                            onAudioClick = {
                                onAction(AddSongsAction.OnAudioSelected(audioUi))
                            },
                            onSelect = { audioUi ->
                                onAction(AddSongsAction.OnAudioSelected(audioUi))
                            }
                        )

                    }
                }

            }
            if(state.selectedCount > 0){
                VPButton(
                    modifier = Modifier
                        .widthIn(max = 480.dp)
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(
                            vertical = 8.dp,
                            horizontal = 16.dp
                        ),
                    text = stringResource(R.string.ok),
                    onClick = {
                        onAction(
                            AddSongsAction.OnOKClick
                        )
                    }
                )
            }
        }

    }

}


@Preview(showBackground = true, backgroundColor = 0xFF0A131D )
@Composable
private fun AddSongsScreenPreview() {
    VibePlayerTheme {
        AddSongsScreen(
            state = AddSongsState(
                audioUis = (0..20).map {
                    AudioUi(
                        id = it.toLong(),
                        album = Uri.EMPTY,
                        songTitle = "Song-${it}",
                        artisName = "Artis-${it}",
                        duration = it.toLong() * 10000,
                        isSelected = it % 3 == 0
                    )
                }
            ),
            onAction = {}
        )
    }
}