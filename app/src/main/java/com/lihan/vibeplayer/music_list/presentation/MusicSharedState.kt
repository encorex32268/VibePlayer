package com.lihan.vibeplayer.music_list.presentation

import androidx.compose.foundation.text.input.TextFieldState
import com.lihan.vibeplayer.core.presentation.util.UiText
import com.lihan.vibeplayer.music_list.presentation.model.AudioUi
import com.lihan.vibeplayer.music_list.presentation.model.PlaylistUi
import com.lihan.vibeplayer.music_list.presentation.model.RepeatModeStatus


data class MusicSharedState(
    val playingQueue: List<AudioUi> = emptyList(),
    val playlists: List<PlaylistUi> = emptyList(),
    val audios: List<AudioUi> = emptyList(),
    val isPlaying: Boolean = false,
    val isScanning: Boolean = false,
    val isEnabledShuffle: Boolean = false,
    val isExpandPlayer: Boolean = false,
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
    val progress: Float = 0f,
    val repeatModeStatus: RepeatModeStatus = RepeatModeStatus.Off,
    val playingAudioUi: AudioUi?=null,
    val modeStatusBanner: UiText?=null,
    val isShowAddToPlaylistSheet: Boolean = false,
    val createPlaylistTextFieldState: TextFieldState = TextFieldState(),
    val isCreateButtonEnabled: Boolean = false,
    val isShowCreatePlaylistSheet: Boolean = false,

)
