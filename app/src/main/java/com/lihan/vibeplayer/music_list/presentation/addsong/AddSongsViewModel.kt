package com.lihan.vibeplayer.music_list.presentation.addsong

import androidx.compose.foundation.text.input.clearText
import androidx.lifecycle.ViewModel
import com.lihan.vibeplayer.music_list.presentation.model.AudioUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AddSongsViewModel() : ViewModel() {

    private val _state = MutableStateFlow(AddSongsState())
    val state = _state.asStateFlow()

    fun onAction(action: AddSongsAction) {
        when (action) {
            AddSongsAction.OnAllSelectedClick -> onAllSelectedClick()
            is AddSongsAction.OnAudioSelected -> onAudioSelected(action.audioUi)
            AddSongsAction.OnBackClick -> Unit
            AddSongsAction.OnCloseClick -> onCloseClick()
            AddSongsAction.OnOKClick -> onOKClick()
        }
    }

    private fun onAllSelectedClick() {
        val newSelectAll = !state.value.isSelectAll
        val newAudio = state.value.audioUis.map {
            it.copy(
                isSelected = newSelectAll
            )
        }
        _state.update {
            it.copy(
                isSelectAll = newSelectAll,
                audioUis = newAudio,
                selectedAudioUis = newAudio.filter { audioUi ->
                    audioUi.isSelected
                }
            )
        }
    }

    private fun onCloseClick() {
        state.value.searchTextField.clearText()
    }

    private fun onOKClick() {
        //TODO: Save To Room and send event navigate back ?
    }

    private fun onAudioSelected(audioUi: AudioUi) {
        val newAudioUis = state.value.audioUis.map { currentAudio ->
            if (currentAudio.id == audioUi.id) {
                currentAudio.copy(
                    isSelected = !currentAudio.isSelected
                )
            } else {
                currentAudio
            }
        }
        _state.update {
            it.copy(
                audioUis = newAudioUis,
                selectedAudioUis = newAudioUis.filter { currentAudio ->
                    currentAudio.isSelected
                }
            )
        }
    }


}