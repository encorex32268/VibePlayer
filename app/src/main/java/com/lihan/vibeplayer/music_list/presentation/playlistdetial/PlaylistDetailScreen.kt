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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lihan.vibeplayer.R
import com.lihan.vibeplayer.core.presentation.components.CircleIconButton
import com.lihan.vibeplayer.music_list.presentation.MusicSharedAction
import com.lihan.vibeplayer.music_list.presentation.MusicSharedViewModel
import com.lihan.vibeplayer.music_list.presentation.components.AudioAsyncImage
import com.lihan.vibeplayer.music_list.presentation.components.CardFavouriteIcon
import com.lihan.vibeplayer.music_list.presentation.components.CardNoCoverIcon
import com.lihan.vibeplayer.music_list.presentation.components.SongListContent
import com.lihan.vibeplayer.music_list.presentation.model.AudioUi
import com.lihan.vibeplayer.music_list.presentation.model.PlaylistCardStyle
import com.lihan.vibeplayer.music_list.presentation.model.PlaylistUi
import com.lihan.vibeplayer.ui.design_system.buttons.VPOutlineButton
import com.lihan.vibeplayer.ui.theme.TextPrimary
import com.lihan.vibeplayer.ui.theme.TextSecondary
import com.lihan.vibeplayer.ui.theme.VibePlayerTheme
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun PlaylistDetailScreenRoot(
    musicSharedViewModel: MusicSharedViewModel,
    playlistId: Int,
    onBack: () -> Unit,
    onNavigateToAddSongs: (id: Int, title: String) -> Unit,
    viewModel: PlaylistDetailViewModel = koinViewModel {
        parametersOf(playlistId)
    }
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    PlaylistDetailScreen(
        state = state,
        onSharedAction = musicSharedViewModel::onAction,
        onAction = { action ->
            when (action) {
                PlaylistDetailAction.OnBackClick -> onBack()
                PlaylistDetailAction.OnAddClick -> onNavigateToAddSongs(
                    playlistId,
                    state.playlistUi?.title ?: ""
                )
            }
        }
    )


}

@Composable
fun PlaylistDetailScreen(
    state: PlaylistDetailState,
    onSharedAction: (MusicSharedAction) -> Unit,
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
        ) {
            CircleIconButton(
                modifier = Modifier.align(Alignment.CenterStart),
                icon = ImageVector.vectorResource(R.drawable.arrow_left),
                onClick = {
                    onAction(PlaylistDetailAction.OnBackClick)
                }
            )
        }
        Spacer(modifier = Modifier.height(30.dp))

        when (state.playlistUi?.style) {
            PlaylistCardStyle.Favourites -> {
                CardFavouriteIcon(
                    iconSize = 100.dp,
                    backgroundSize = 200.dp
                )
            }
            PlaylistCardStyle.NoCover -> {
                CardNoCoverIcon(
                    backgroundSize = 200.dp,
                    iconSize = 100.dp
                )
            }
            is PlaylistCardStyle.HasCover -> {
                if (isImageLoadingAndError) {
                    CardNoCoverIcon(
                        backgroundSize = 200.dp,
                        iconSize = 100.dp
                    )
                } else {
                    val style = state.playlistUi.style
                    AudioAsyncImage(
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(200.dp),
                        model = style.imageModel,
                        cacheKey = when {
                            style.isUploadedImage -> state.playlistUi.coverImageUriString!!
                            !style.isUploadedImage -> {
                                val firstAudioId = state.playlistAudio?.sortedAudios?.firstOrNull()
                                if (firstAudioId != null) {
                                    "${state.playlistUi.id}_$firstAudioId"
                                } else {
                                    state.playlistUi.id.toString()
                                }
                            }
                            else -> state.playlistUi.id.toString()
                        },
                        onError = {
                            isImageLoadingAndError = true
                        }
                    )

                }

            }
            else -> Unit
        }


        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = if (state.playlistUi?.style is PlaylistCardStyle.Favourites){
                stringResource(R.string.playlist_favourites)
            }else{
                state.playlistUi?.title ?: ""
            },
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(30.dp))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when {
                state.audios.isEmpty() -> {
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
                            onAction(PlaylistDetailAction.OnAddClick)
                        }
                    )
                }

                else -> {
                    SongListContent(
                        modifier = modifier
                            .fillMaxSize(),
                        listState = listState,
                        audios = state.audios,
                        onFunctionShuffleClick = {
                            onSharedAction(MusicSharedAction.OnFunctionShuffleClick(state.audios))
                        },
                        onFunctionPlayClick = {
                            onSharedAction(MusicSharedAction.OnFunctionPlayClick(state.audios))
                        },
                        onSongClick = { audioUi ->
                            onSharedAction(MusicSharedAction.OnSongClick(audioUi))
                        },
                        onAddClick = {
                            onAction(PlaylistDetailAction.OnAddClick)
                        }
                    )
                }
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
                        id = it.toLong(),
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
                    count = 1
                )
            ),
            onAction = {},
            onSharedAction = {}
        )
    }
}