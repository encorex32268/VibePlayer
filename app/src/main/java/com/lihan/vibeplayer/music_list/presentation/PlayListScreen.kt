package com.lihan.vibeplayer.music_list.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lihan.vibeplayer.R
import com.lihan.vibeplayer.core.presentation.components.CircleIconButton
import com.lihan.vibeplayer.music_list.presentation.components.CreateNewPlaylist
import com.lihan.vibeplayer.music_list.presentation.components.PlaylistCard
import com.lihan.vibeplayer.music_list.presentation.model.PlaylistCardStyle
import com.lihan.vibeplayer.ui.design_system.buttons.VPOutlineButton
import com.lihan.vibeplayer.ui.theme.TextPrimary
import com.lihan.vibeplayer.ui.theme.TextSecondary
import com.lihan.vibeplayer.ui.theme.VibePlayerTheme


@Composable
fun PlayListScreen(
    state: MusicListState,
    onAction: (MusicListAction) -> Unit,
    modifier: Modifier = Modifier
) {

    val totalPlaylists = remember(state.playlists.size){
        state.playlists.size + 1
    }
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.playlist_count,totalPlaylists),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary
                )
                CircleIconButton(
                    icon = ImageVector.vectorResource(R.drawable.plus),
                    onClick = {
                        onAction(MusicListAction.OnCreatePlaylistAddClick)
                    }
                )
            }
        }
        item {
            PlaylistCard(
                title = stringResource(R.string.playlist_favourites),
                count = if (state.favouritesPlaylists == null || state.favouritesPlaylists.audioIds.isEmpty()) 0  else state.favouritesPlaylists.audioIds.size,
                onMenuDotsClick = {},
                playlistCardStyle = PlaylistCardStyle.Favourites
            )
        }
        item {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.playlist_my_playlist_count,state.playlists.size),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = TextSecondary
            )

        }
        if (state.playlists.isEmpty()){
            item {
                VPOutlineButton(
                    text = stringResource(R.string.playlist_create_playlist),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onAction(MusicListAction.OnCreatePlaylistAddClick)
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.plus),
                            tint = TextPrimary,
                            contentDescription = stringResource(R.string.playlist_create_playlist)
                        )
                    }
                )
            }
        }else{
            items(
                items = state.playlists,
                key = { it.id }
            ){ playlistUi ->
                PlaylistCard(
                    title = playlistUi.title,
                    count = playlistUi.count,
                    playlistCardStyle = playlistUi.style,
                    onMenuDotsClick = {

                    }
                )
            }
        }

    }
    
    if (state.isCreatePlaylistSheetShow){
        CreateNewPlaylist(
            textFieldState = state.createPlaylistTextFieldState,
            isCreateButtonEnabled = state.isCreateButtonEnabled,
            onCancelClick = {
                onAction(MusicListAction.OnCreatePlaylistCancelClick)
            },
            onCreateClick = {
                onAction(MusicListAction.OnNavigateToAddSongs)
            }
        )
    }
}



@Preview
@Composable
private fun PlayListScreenPreview() {
    VibePlayerTheme {
        PlayListScreen(
            state = MusicListState(
                playlists = emptyList()
            ),
            onAction = {}
        )
    }

}