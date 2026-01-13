package com.lihan.vibeplayer.music_list.presentation.search

import androidx.compose.foundation.text.input.TextFieldState
import com.lihan.vibeplayer.music_list.presentation.model.AudioUi

data class SearchState(
    val textFieldState: TextFieldState = TextFieldState(),
    val searchedAudios: List<AudioUi> = emptyList(),
    val isSearching: Boolean = false
)