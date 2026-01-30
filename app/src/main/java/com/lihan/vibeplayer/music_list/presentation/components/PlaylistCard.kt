package com.lihan.vibeplayer.music_list.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lihan.vibeplayer.R
import com.lihan.vibeplayer.core.presentation.components.CircleIconButton
import com.lihan.vibeplayer.music_list.presentation.model.PlaylistCardStyle
import com.lihan.vibeplayer.ui.theme.TextPrimary
import com.lihan.vibeplayer.ui.theme.TextSecondary
import com.lihan.vibeplayer.ui.theme.VibePlayerTheme

@Composable
fun PlaylistCard(
    title: String,
    playlistCardStyle: PlaylistCardStyle,
    modifier: Modifier = Modifier,
    onItemClick: (() -> Unit)?=null,
    onMenuDotsClick: (() -> Unit)?=null,
    imageCacheKey: String? = null,
    count: Int?=null
) {
    var isImageLoadingAndError by remember(imageCacheKey){ mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = {
                onItemClick?.invoke()
            }),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        when(playlistCardStyle){
            PlaylistCardStyle.Create -> {
                CardCreateIcon()
            }
            PlaylistCardStyle.Favourites -> {
                CardFavouriteIcon()
            }
            PlaylistCardStyle.NoCover -> {
                CardNoCoverIcon()
            }
            is PlaylistCardStyle.HasCover -> {
                if (isImageLoadingAndError){
                    CardNoCoverIcon()
                }else{
                    AudioAsyncImage(
                        model = (playlistCardStyle).imageModel,
                        cacheKey = imageCacheKey?:"",
                        contentDescription = title,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape),
                        onError = {
                            isImageLoadingAndError = true
                        }
                    )

                }
            }
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary
            )
            if (count != null){
                Text(
                    text = stringResource(R.string.playlist_song_count,count),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Normal,
                    color = TextSecondary
                )
            }
        }
        if (onMenuDotsClick!= null){
            CircleIconButton (
                isRemoveBackground = true,
                icon = ImageVector.vectorResource(R.drawable.menu_dots),
                onClick = onMenuDotsClick
            )
        }

    }

}


@Preview(showBackground = true, backgroundColor = 0xFF0A131D)
@Composable
private fun PlaylistCardPreview() {
    VibePlayerTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            PlaylistCard(
                title = "Friday Chill",
                count = null,
                onMenuDotsClick = null,
                playlistCardStyle = PlaylistCardStyle.Create,
                imageCacheKey = ""
            )
            PlaylistCard(
                title = "Friday Chill",
                count = 222,
                onMenuDotsClick = {},
                playlistCardStyle = PlaylistCardStyle.Favourites,
                imageCacheKey = ""
            )
            PlaylistCard(
                title = "Friday Chill",
                count = 0,
                onMenuDotsClick = null,
                playlistCardStyle = PlaylistCardStyle.NoCover,
                imageCacheKey = ""
            )
            PlaylistCard(
                title = "Friday Chill",
                count = 0,
                onMenuDotsClick = null,
                playlistCardStyle = PlaylistCardStyle.HasCover(null,true),
                imageCacheKey = ""
            )
        }
    }
}