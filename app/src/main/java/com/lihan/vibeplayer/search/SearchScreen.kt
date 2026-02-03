package com.lihan.vibeplayer.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lihan.vibeplayer.R
import com.lihan.vibeplayer.music_list.presentation.components.SearchBar
import com.lihan.vibeplayer.music_list.presentation.components.SongCard
import com.lihan.vibeplayer.ui.design_system.buttons.VPTextButton
import com.lihan.vibeplayer.ui.theme.SurfaceOutline
import com.lihan.vibeplayer.ui.theme.TextDisabled
import com.lihan.vibeplayer.ui.theme.TextSecondary
import com.lihan.vibeplayer.ui.theme.VibePlayerTheme
import org.koin.androidx.compose.koinViewModel


@Composable
fun SearchScreenRoot(
    onBack: () -> Unit,
    viewModel: SearchViewModel = koinViewModel()
){
    val state by viewModel.state.collectAsStateWithLifecycle()

    SearchScreen(
        state = state,
        onAction = { action ->
            when(action){
                SearchAction.OnCancelClick -> onBack()
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )

}

@Composable
fun SearchScreen(
    state: SearchState,
    onAction: (SearchAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 10.dp,horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SearchBar(
                modifier = Modifier.weight(1f),
                textFieldState = state.textFieldState,
                onCloseClick = {
                    onAction(SearchAction.OnCloseClick)
                },
                focusRequester = focusRequester
            )
            VPTextButton(
                text = stringResource(R.string.search_cancel),
                onClick = {
                    onAction(SearchAction.OnCancelClick)
                }
            )
        }
        when{
            state.isSearching -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ){
                    CircularProgressIndicator(
                        color = TextDisabled,
                        trackColor = Color.Transparent,
                        strokeWidth = 2.dp,
                        progress = { 0.5f }
                    )
                }
            }
            state.searchedAudios.isEmpty() && state.textFieldState.text.isNotEmpty() && !state.isSearching -> {
                Text(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    text = stringResource(R.string.search_no_results_found),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .imePadding(),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    itemsIndexed(state.searchedAudios){ index, audioUi ->
                        if (index != 0){
                            HorizontalDivider(
                                color = SurfaceOutline,
                                thickness = 1.dp
                            )
                        }
                        SongCard(
                            audioUi = audioUi,
                            modifier = Modifier.fillMaxWidth(),
                            onAudioClick = {

                            }
                        )
                    }
                }
            }
        }

    }



}


@Preview
@Composable
private fun SearchScreenPreview() {
    VibePlayerTheme {
        SearchScreen(
            state = SearchState(
                textFieldState = TextFieldState("Aimyon"),
                searchedAudios = emptyList(),
                isSearching = true,
//                searchedAudios = (0..10).map {
//                    AudioUi(
//                        songTitle = "Aimyon ${it}",
//                        artisName = "ZA",
//                        duration = it * 1_000 * 20 .toLong()
//                    )
//                }
            ),
            onAction = {}
        )
    }
}