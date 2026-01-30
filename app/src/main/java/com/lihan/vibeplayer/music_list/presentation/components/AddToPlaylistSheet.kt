@file:OptIn(ExperimentalMaterial3Api::class)

package com.lihan.vibeplayer.music_list.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lihan.vibeplayer.R
import com.lihan.vibeplayer.music_list.presentation.model.PlaylistCardStyle
import com.lihan.vibeplayer.music_list.presentation.model.PlaylistUi
import com.lihan.vibeplayer.ui.theme.SurfaceHighest
import com.lihan.vibeplayer.ui.theme.SurfaceOutline
import com.lihan.vibeplayer.ui.theme.VibePlayerTheme

@Composable
fun AddToPlaylistSheet(
    playlists: List<PlaylistUi>,
    favouritesPlaylistsCount: Int,
    onCreatePlaylistClick: () -> Unit,
    onFavouritesClick: () -> Unit,
    onItemClick: (PlaylistUi) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier
            .widthIn(max = 480.dp)
            .fillMaxWidth(),
        containerColor = SurfaceHighest,
        dragHandle = null,
        shape = RoundedCornerShape(
            topStart = 12.dp,
            topEnd = 12.dp
        )
    ) {
        LazyColumn(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item{
                PlaylistCard(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    title = stringResource(R.string.create_playlist),
                    playlistCardStyle = PlaylistCardStyle.Create,
                    onItemClick = onCreatePlaylistClick
                )
                HorizontalDivider(modifier = Modifier.padding(1.dp), color = SurfaceOutline)
            }
            item{
                PlaylistCard(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    title = stringResource(R.string.favourites),
                    playlistCardStyle = PlaylistCardStyle.Favourites,
                    onItemClick = onFavouritesClick,
                    count = favouritesPlaylistsCount
                )
            }
            items(
                items = playlists,
                key = { it.id }
            ){ playlistUi ->
                PlaylistCard(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    title = playlistUi.title,
                    count = playlistUi.count,
                    playlistCardStyle = playlistUi.style,
                    imageCacheKey = when {
                        !playlistUi.coverImageUriString.isNullOrEmpty() -> playlistUi.coverImageUriString
                        playlistUi.audioIds.isNotEmpty() -> "${playlistUi.id}_${playlistUi.audioIds.first()}"
                        else -> playlistUi.id.toString()
                    },onItemClick = {
                        onItemClick(playlistUi)
                    }
                )
                HorizontalDivider(modifier = Modifier.padding(1.dp), color = SurfaceOutline)
            }
        }

    }
}


@Preview
@Composable
private fun AddToPlaylistSheetPreview() {
    VibePlayerTheme {
        AddToPlaylistSheet(
            playlists = (0..10).map {
                PlaylistUi(
                    id = it + 1,
                    title = "Title ${it}",
                    count = it + 1 * 5,
                )
            },
            onItemClick = {},
            onDismiss = {},
            onCreatePlaylistClick = {},
            onFavouritesClick = {},
            favouritesPlaylistsCount = 100
        )
    }
}