package com.lihan.vibeplayer.music_list.presentation.playlistdetial

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
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
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lihan.vibeplayer.R
import com.lihan.vibeplayer.core.presentation.components.CircleIconButton
import com.lihan.vibeplayer.music_list.presentation.components.AudioAsyncImage
import com.lihan.vibeplayer.music_list.presentation.components.PlaylistGradientIcon
import com.lihan.vibeplayer.music_list.presentation.components.SongListContent
import com.lihan.vibeplayer.music_list.presentation.model.AudioUi
import com.lihan.vibeplayer.music_list.presentation.model.PlaylistCardStyle
import com.lihan.vibeplayer.music_list.presentation.model.PlaylistUi
import com.lihan.vibeplayer.music_list.presentation.scan.ScanMusicAction
import com.lihan.vibeplayer.ui.design_system.buttons.VPOutlineButton
import com.lihan.vibeplayer.ui.theme.TextPrimary
import com.lihan.vibeplayer.ui.theme.TextSecondary
import com.lihan.vibeplayer.ui.theme.VibePlayerTheme

@Composable
fun PlaylistDetailScreenRoot(){


}

@Composable
fun PlaylistDetailScreen(
    state: PlaylistDetailState,
    onAction: (PlaylistDetailAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    var isImageLoadingAndError by remember { mutableStateOf(false) }


    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ){
            CircleIconButton(
                modifier = Modifier.align(Alignment.CenterStart),
                icon = ImageVector.vectorResource(R.drawable.arrow_left),
                onClick = {
                    onAction(PlaylistDetailAction.OnBackClick)
                }
            )
        }
        Spacer(modifier = Modifier.height(30.dp))
        if (isImageLoadingAndError){
            PlaylistGradientIcon(
                modifier = Modifier
                    .clip(CircleShape)
                    .size(200.dp),
                iconSize = 100.dp
            )
        }else{
            AudioAsyncImage(
                modifier = Modifier
                    .clip(CircleShape)
                    .size(200.dp),
                cacheKey = "",
                model = null,
                onError = {
                    isImageLoadingAndError = true
                },
                onLoading = {
                    isImageLoadingAndError = true
                }
            )

        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = state.playlistUi?.title?:"",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(30.dp))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            if (state.audios.isEmpty()){
                //Empty
                Text(
                    modifier = Modifier
                        .padding(top = 16.dp, bottom = 8.dp),
                    text = stringResource(R.string.playlist_detail_no_songs_found),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Normal,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
                VPOutlineButton(
                    text = stringResource(R.string.playlist_detail_add_songs),
                    leadingIcon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.plus),
                            contentDescription = stringResource(R.string.playlist_detail_add_songs),
                            tint = TextPrimary
                        )
                    },
                    onClick = {
                        //TODO: Navigate To Add Songs
                    }
                )
            }else{
                SongListContent(
                    modifier = modifier
                        .fillMaxSize(),
                    listState = listState,
                    audios = state.audios,
                    onFunctionShuffleClick = {

                    },
                    onFunctionPlayClick = {

                    },
                    onSongClick = {

                    },
                    onAddClick = {

                    }
                )

            }

        }




    }

}

@Preview
@Composable
private fun PlaylistDetailScreenPreview() {
    VibePlayerTheme {
        PlaylistDetailScreen(
            state = PlaylistDetailState(
                audios = (0..10).map {
                    AudioUi(
                        id =  it.toLong(),
                        album = Uri.EMPTY,
                        songTitle = "Song - ${it}",
                        artisName = "ArtisName ${it}",
                        duration = 1000
                    )
                },
                playlistUi = PlaylistUi(
                    id = 1,
                    title = "My Playlist Test",
                    style = PlaylistCardStyle.NoCover,
                    audioIds = (0..10).map{
                        it.toString()
                    }
                )
            ),
            onAction = {}
        )
    }
}