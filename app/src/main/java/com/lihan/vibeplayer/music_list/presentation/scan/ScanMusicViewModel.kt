package com.lihan.vibeplayer.music_list.presentation.scan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lihan.vibeplayer.core.domain.util.kb
import com.lihan.vibeplayer.music_list.domain.AudioRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ScanMusicViewModel(
    private val audioRepository: AudioRepository
): ViewModel() {

    private var hasInitialLoadedData = false

    private val _uiEvent = Channel<ScanMusicUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _state = MutableStateFlow(ScanMusicState())
    val state = _state.onStart {
        if (!hasInitialLoadedData){

            hasInitialLoadedData = true
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        ScanMusicState()
    )


    fun onAction(action: ScanMusicAction){
        when(action){
            ScanMusicAction.OnBackClick -> Unit
            ScanMusicAction.OnScanClick -> onScanClick()
            is ScanMusicAction.OnSecondSelect -> {
                _state.update { it.copy(
                    secondSelect = action.second
                ) }
            }
            is ScanMusicAction.OnSizeSelect -> {
                _state.update { it.copy(
                    sizeSelect = action.size
                ) }
            }
        }
    }

    private fun onScanClick(){
        viewModelScope.launch {

            _state.update { it.copy(
                isScanning = true
            ) }

            delay(3000L)

            val currentState = state.value

            val duration = currentState.secondSelect.replace("s","").toLongOrNull()?:0L
            val size = currentState.sizeSelect.replace("KB","").toInt().kb

            val audios = audioRepository.getAudios(
                duration = duration * 1_000,
                size = size
            )

            _state.update { it.copy(
                isScanning = false
            ) }

            _uiEvent.trySend(
                ScanMusicUiEvent.OnShowResultSize(
                    itemSize = audios.size
                )
            )

        }
    }
}


