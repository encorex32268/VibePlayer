package com.lihan.vibeplayer.music_list.presentation.addsong

import com.lihan.vibeplayer.music_list.presentation.model.AudioUi

sealed interface AddSongsAction {
    data object OnBackClick: AddSongsAction
    data object OnCloseClick: AddSongsAction
    data class OnAudioSelected(val audioUi: AudioUi): AddSongsAction
    data object OnAllSelectedClick: AddSongsAction
    data object OnOKClick: AddSongsAction
}