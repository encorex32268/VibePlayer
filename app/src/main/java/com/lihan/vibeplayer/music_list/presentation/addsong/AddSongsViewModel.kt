@file:OptIn(FlowPreview::class)

package com.lihan.vibeplayer.music_list.presentation.addsong

import androidx.compose.foundation.text.input.clearText
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lihan.vibeplayer.music_list.domain.MusicListRepository
import com.lihan.vibeplayer.music_list.presentation.mapper.toUi
import com.lihan.vibeplayer.music_list.presentation.model.AudioUi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddSongsViewModel(
    private val title: String,
    private val playlistId: Int?=null,
    private val repository: MusicListRepository
) : ViewModel() {

    private var hasLoadedInitialData = false

    private var originAudioUi: List<AudioUi> = emptyList()

    private val _uiEvent = Channel<AddSongsUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _state = MutableStateFlow(AddSongsState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                loadAudios()
                observeSearchTextField()
                hasLoadedInitialData = true
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            AddSongsState()
        )

    fun onAction(action: AddSongsAction) {
        when (action) {
            AddSongsAction.OnAllSelectedClick -> onAllSelectedClick()
            is AddSongsAction.OnAudioSelected -> onAudioSelected(action.audioUi)
            AddSongsAction.OnBackClick -> Unit
            AddSongsAction.OnCloseClick -> onCloseClick()
            AddSongsAction.OnOKClick -> onOKClick()
        }
    }

    private fun observeSearchTextField() {
        snapshotFlow { _state.value.searchTextField.text.toString() }
            .debounce(500L)
            .onEach { text ->

                val newAudios = if (text.isEmpty()) {
                    originAudioUi
                } else {
                    originAudioUi.filter { it.songTitle.contains(text) }
                }

                val isSelectAll = if (newAudios.isEmpty()) false else {
                    newAudios.all { it.isSelected }
                }

                _state.update { state ->
                    state.copy(
                        audioUis = newAudios,
                        isSelectAll = isSelectAll
                    )
                }
            }
            .launchIn(viewModelScope)
    }


    private fun onAllSelectedClick() {

        val newSelectAll = !state.value.isSelectAll

        val currentAudios = state.value.audioUis.map {
            it.copy(
                isSelected = newSelectAll
            )
        }

        //Map All currentAudioList Id
        //update audio's isSelected
        val currentAudioIds = currentAudios.map { it.id }
        originAudioUi = originAudioUi.map { audioUi ->
            if (audioUi.id in currentAudioIds) {
                audioUi.copy(
                    isSelected = newSelectAll
                )
            } else {
                audioUi
            }
        }

        val isSelectAll =
            if (currentAudios.isEmpty()) false else currentAudios.all { it.isSelected }

        val currentSelectAudios = state.value.selectedAudios.toMutableList()
        if (newSelectAll){
            currentAudios.forEach {
                val idString = it.id.toString()
                if (idString !in currentSelectAudios){
                    currentSelectAudios.add(idString)
                }
            }
        }else{
            currentSelectAudios.clear()
        }


        _state.update {
            it.copy(
                isSelectAll = isSelectAll,
                audioUis = currentAudios,
                selectedAudios = currentSelectAudios.toList()
            )
        }
    }


    private fun onAudioSelected(audioUi: AudioUi) {
        val newAudios = state.value.audioUis.map { currentAudio ->
            if (currentAudio.id == audioUi.id) {
                currentAudio.copy(
                    isSelected = !currentAudio.isSelected
                )
            } else {
                currentAudio
            }
        }

        originAudioUi = originAudioUi.map { currentAudio ->
            if (currentAudio.id == audioUi.id) {
                currentAudio.copy(
                    isSelected = !currentAudio.isSelected
                )
            } else {
                currentAudio
            }
        }

        val allSelected = newAudios.all { it.isSelected }

        val currentSelectAudios = state.value.selectedAudios.toMutableList()
        val idString = audioUi.id.toString()
        if (idString in currentSelectAudios){
            currentSelectAudios.removeIf { it == idString }
        }else{
            currentSelectAudios.add(idString)
        }

        val isSelectAll = if (newAudios.isEmpty()) false else allSelected

        _state.update {
            it.copy(
                audioUis = newAudios,
                isSelectAll = isSelectAll,
                selectedAudios = currentSelectAudios.toList()
            )
        }
    }

    private fun loadAudios() {
        viewModelScope.launch {

            val playlistDeferred = async { playlistId?.let { repository.getPlaylistAudiosById(it).first() } }
            val allAudiosDeferred = async { repository.getAllAudios().first() }

            val playlist = playlistDeferred.await()
            val allAudios = allAudiosDeferred.await()

            val playlistAudioIds = playlist?.audios?.map { it.id.toString() }?.toSet()?:emptySet()

            val processedAudios = coroutineScope {
                allAudios.map { audio ->
                    async {
                        val audioIdStr = audio.id.toString()
                        audio.toUi().copy(
                            albumImage = repository.getAlbumArtImage(audio.album),
                            isSelected = playlistAudioIds.contains(audioIdStr)
                        )
                    }
                }.awaitAll()
            }



            originAudioUi = processedAudios


            _state.update { state ->
                state.copy(
                    audioUis = processedAudios,
                    isSelectAll = processedAudios.isNotEmpty() && processedAudios.all { it.isSelected },
                    selectedAudios = playlistAudioIds.toList()
                )
            }
        }
    }


    private fun onCloseClick() {
        state.value.searchTextField.clearText()
    }

    private fun onOKClick() {
        viewModelScope.launch {
            val selectedAudios = state.value.selectedAudios

            repository.createPlaylistWithAudios(
                id = playlistId,
                title = title,
                audios = selectedAudios,
                coverUri = null
            )

            _uiEvent.send(
                AddSongsUiEvent.OnPlaylistSaved
            )
        }
    }
}