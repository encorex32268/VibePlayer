@file:OptIn(FlowPreview::class)

package com.lihan.vibeplayer.music_list.presentation.addsong

import androidx.compose.foundation.text.input.clearText
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lihan.vibeplayer.music_list.domain.MusicListRepository
import com.lihan.vibeplayer.music_list.domain.Playlist
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
    private val repository: MusicListRepository
) : ViewModel() {

    private var hasInitialLoadedData = false
    private var playlistTitle = ""

    private var originAudioUi: List<AudioUi> = emptyList()

    private val _uiEvent = Channel<AddSongsUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _state = MutableStateFlow(AddSongsState())
    val state = _state
        .onStart {
            if (!hasInitialLoadedData) {
                loadAudios()
                observeSearchTextField()
                hasInitialLoadedData = true
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
            is AddSongsAction.OnSaveTitleName -> onSaveTitleName(action.title)
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

                val isSelectAll = if (newAudios.isEmpty()) false else { newAudios.all { it.isSelected }  }

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

        val selectCount = originAudioUi.filter { audioUi ->
            audioUi.isSelected
        }.size

        val isSelectAll = if (currentAudios.isEmpty()) false else currentAudios.all { it.isSelected }

        _state.update {
            it.copy(
                isSelectAll = isSelectAll,
                audioUis = currentAudios,
                selectedCount = selectCount
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

        val selectCount = originAudioUi.filter { audioUi ->
            audioUi.isSelected
        }.size

        val isSelectAll = if (newAudios.isEmpty()) false else allSelected

        _state.update {
            it.copy(
                audioUis = newAudios,
                isSelectAll = isSelectAll,
                selectedCount = selectCount
            )
        }
    }

    private fun onSaveTitleName(title: String) {
        playlistTitle = title
    }

    private fun loadAudios() {
        repository
            .getAllAudios()
            .onEach { audios ->
                val hasImageAudios = coroutineScope {
                    audios.map { audio ->
                        async{
                            val audioUi = audio.toUi()
                            val albumImage = repository.getAlbumArtImage(audioUi.album)
                            audioUi.copy(albumImage = albumImage)
                        }
                    }
                }.awaitAll()

                originAudioUi = hasImageAudios

                val isSelectAll = if (originAudioUi.isEmpty()) false else {
                    originAudioUi.all { it.isSelected }
                }

                _state.update { state ->
                    state.copy(
                        audioUis = hasImageAudios,
                        isSelectAll = isSelectAll
                    )
                }

                println("LoadAudios ${originAudioUi.all { it.isSelected }}")

            }.launchIn(viewModelScope)
    }


    private fun onCloseClick() {
        state.value.searchTextField.clearText()
    }

    private fun onOKClick() {
        viewModelScope.launch {
            val selectedAudios = state.value.audioUis
                .filter { it.isSelected }
                .map { it.id.toString() }

            repository.createPlaylist(
                playlist = Playlist(
                    title = playlistTitle,
                    audioIds = selectedAudios
                )
            )

            _uiEvent.send(
                AddSongsUiEvent.OnPlaylistSaved
            )
        }
    }
}