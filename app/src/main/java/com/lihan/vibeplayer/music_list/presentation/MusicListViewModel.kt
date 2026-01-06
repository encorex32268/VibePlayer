package com.lihan.vibeplayer.music_list.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lihan.vibeplayer.music_list.domain.AudioRepository
import com.lihan.vibeplayer.music_list.presentation.mapper.toUi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MusicListViewModel(
    private val audioRepository: AudioRepository
): ViewModel(){

    private var hasInitialLoadedData = false

    private val _state = MutableStateFlow(MusicListState())
    val state = _state
        .onStart {
            if (!hasInitialLoadedData){
                loadAudios()
                hasInitialLoadedData = true
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            MusicListState()
        )


    fun onAction(action: MusicListAction){
        when(action){
            MusicListAction.OnScanAgainClick -> loadAudios()
            else -> Unit
        }
    }

    private fun loadAudios(){
        viewModelScope.launch {
            _state.update { it.copy(
                isScanning = true
            ) }
            delay(1000)

            _state.update { it.copy(
                audios = audioRepository
                    .getAudios()
                    .map { audio -> audio.toUi() },
                isScanning = false
            ) }


        }
    }



}