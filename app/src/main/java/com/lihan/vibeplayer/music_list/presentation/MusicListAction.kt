package com.lihan.vibeplayer.music_list.presentation

import android.content.Context
import com.lihan.vibeplayer.music_list.presentation.model.AudioUi

sealed interface MusicListAction {
    data object OnScanAgainClick: MusicListAction
    data object OnScanClick: MusicListAction
    data class OnMiniPlayerClick(val id: Long?): MusicListAction
    data class OnAudioUiClick(val audioUi: AudioUi,val context: Context): MusicListAction
    data object OnSearchClick: MusicListAction
    data object OnShuffleClick: MusicListAction
    data object OnPlayClick: MusicListAction
    data object OnSkipNextClick: MusicListAction
}