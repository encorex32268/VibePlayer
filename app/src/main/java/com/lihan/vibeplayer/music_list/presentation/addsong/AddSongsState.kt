package com.lihan.vibeplayer.music_list.presentation.addsong

import androidx.compose.foundation.text.input.TextFieldState
import com.lihan.vibeplayer.music_list.presentation.model.AudioUi

data class AddSongsState(
    val searchTextField: TextFieldState = TextFieldState(),
    val audioUis: List<AudioUi> = emptyList(),
    val selectedAudioUis: List<AudioUi> = emptyList(),
    val isSelectAll: Boolean = false,
)
