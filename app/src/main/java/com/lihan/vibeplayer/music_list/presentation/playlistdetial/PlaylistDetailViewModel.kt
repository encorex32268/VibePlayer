package com.lihan.vibeplayer.music_list.presentation.playlistdetial

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PlaylistDetailViewModel: ViewModel() {

    private val _state = MutableStateFlow(PlaylistDetailState())
    val state = _state.asStateFlow()



    fun onAction(action: PlaylistDetailAction){
        when(action){
            PlaylistDetailAction.OnBackClick -> TODO()
        }
    }
}