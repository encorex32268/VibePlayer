@file:OptIn(ExperimentalMaterial3Api::class)

package com.lihan.vibeplayer.music_list.presentation.components

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lihan.vibeplayer.R
import com.lihan.vibeplayer.core.presentation.components.CircleIconButton
import com.lihan.vibeplayer.music_list.presentation.model.PlaylistCardStyle
import com.lihan.vibeplayer.music_list.presentation.model.PlaylistUi
import com.lihan.vibeplayer.ui.theme.SurfaceHighest
import com.lihan.vibeplayer.ui.theme.SurfaceOutline
import com.lihan.vibeplayer.ui.theme.TextPrimary
import com.lihan.vibeplayer.ui.theme.VibePlayerTheme


private const val FavouritesID = -1

@Composable
fun ActionSheet(
    playlistUi: PlaylistUi,
    onPlayClick: () -> Unit,
    onRenameClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onDismiss: () -> Unit,
    onUpdatePlaylistCover: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { imageUri ->
        if (imageUri!=null){
            val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
            context.contentResolver.takePersistableUriPermission(imageUri, flag)
            onUpdatePlaylistCover(imageUri.toString())
        }
    }

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
        Column (
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ){

            PlaylistCard(
                title = if (playlistUi.id == FavouritesID){
                    stringResource(R.string.playlist_favourites)
                }else{
                    playlistUi.title
                },
                count = playlistUi.audioIds.size,
                playlistCardStyle = playlistUi.style,
                imageCacheKey =  when {
                    !playlistUi.coverImageUriString.isNullOrEmpty() -> playlistUi.coverImageUriString
                    playlistUi.audioIds.isNotEmpty() -> "${playlistUi.id}_${playlistUi.audioIds.first()}"
                    else -> playlistUi.id.toString()
                },
            )
            when(playlistUi.style){
                PlaylistCardStyle.Favourites -> {
                    ActionItem(
                        icon = ImageVector.vectorResource(R.drawable.play_outline),
                        onClick = onPlayClick,
                        title = stringResource(R.string.play)
                    )
                }
                else -> {
                    HorizontalDivider(modifier = Modifier.padding(1.dp), color = SurfaceOutline)
                    ActionItem(
                        icon = ImageVector.vectorResource(R.drawable.play_outline),
                        onClick = onPlayClick,
                        title = stringResource(R.string.play)
                    )
                    ActionItem(
                        icon = ImageVector.vectorResource(R.drawable.pen),
                        onClick = onRenameClick,
                        title = stringResource(R.string.rename)
                    )
                    ActionItem(
                        icon = ImageVector.vectorResource(R.drawable.img_edit),
                        onClick = {
                            picker.launch("image/*")
                        },
                        title = stringResource(R.string.change_cover)
                    )
                    ActionItem(
                        icon = ImageVector.vectorResource(R.drawable.delete),
                        onClick = onDeleteClick,
                        title = stringResource(R.string.delete)
                    )
                }
            }
        }

    }

}

@Composable
private fun ActionItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
){
    Row(
        modifier = modifier.fillMaxWidth().clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CircleIconButton(
            icon = icon,
            onClick = onClick
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = TextPrimary
        )
    }

}


@Preview
@Composable
private fun ActionSheetPreview() {
    VibePlayerTheme {
        ActionSheet(
            playlistUi = PlaylistUi(
                id = 1,
                title = "Favourites", count = 1,
                style = PlaylistCardStyle.NoCover
            ),
            onDismiss = {},
            onDeleteClick = {},
            onPlayClick = {},
            onRenameClick = {},
            onUpdatePlaylistCover = {

            }
        )
    }
}