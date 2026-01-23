package com.lihan.vibeplayer.music_list.presentation

import androidx.compose.foundation.text.input.TextFieldState
import com.lihan.vibeplayer.core.presentation.util.UiText
import com.lihan.vibeplayer.music_list.domain.FavouritesPlaylist
import com.lihan.vibeplayer.music_list.presentation.model.AudioUi
import com.lihan.vibeplayer.music_list.presentation.model.PlaylistUi
import com.lihan.vibeplayer.music_list.presentation.model.RepeatModeStatus

data class MusicListState(
    val isScanning: Boolean = true,
    val isEnabledShuffle: Boolean = false,
    val isExpandPlayer: Boolean = false,
    val audios: List<AudioUi> = emptyList(),
    val playingAudioUi: AudioUi?=null,
    val isPlaying: Boolean = false,
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
    val progress: Float = 0f,
    val modeStatusBanner: UiText?=null,
    val repeatModeStatus: RepeatModeStatus = RepeatModeStatus.Off,
    val playlists: List<PlaylistUi> = emptyList(),
    val favouritesPlaylists: PlaylistUi?=null,
    val isShowCreatePlaylistBottomSheet: Boolean = false,
    val createPlaylistTextFieldState: TextFieldState = TextFieldState(),
    val isCreateButtonEnabled: Boolean = false,
    val renamePlaylistTextFieldState: TextFieldState = TextFieldState(),
    val isRenameButtonEnabled: Boolean = false,
    val selectActionSheetPlaylistUi: PlaylistUi?=null,
    val isShowActionSheet: Boolean = false,
    val isShowRenameBottomSheet: Boolean = false,
    val isShowDeleteBottomSheet: Boolean = false,
)