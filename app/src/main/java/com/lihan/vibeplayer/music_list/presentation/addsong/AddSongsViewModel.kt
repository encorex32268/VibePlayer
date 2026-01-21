@file:OptIn(FlowPreview::class)

package com.lihan.vibeplayer.music_list.presentation.addsong

import androidx.compose.foundation.text.input.clearText
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lihan.vibeplayer.core.domain.LocalDataRepository
import com.lihan.vibeplayer.music_list.domain.AudioRepository
import com.lihan.vibeplayer.music_list.domain.Playlist
import com.lihan.vibeplayer.music_list.presentation.mapper.toUi
import com.lihan.vibeplayer.music_list.presentation.model.AudioUi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
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
    private val audioRepository: AudioRepository,
    private val localDataRepository: LocalDataRepository
) : ViewModel() {

    private var hasInitialLoadedData = false
    private var playlistTitle = ""
    private var originalAudios: List<AudioUi> = emptyList()

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
                val newAudios = if(text.trim().isEmpty()){
                    originalAudios
                }else{
                    originalAudios.filter { it.songTitle.contains(text) }
                }

                _state.update { state -> state.copy(
                    audioUis = newAudios
                ) }
            }
            .launchIn(viewModelScope)
    }

    private fun loadAudios() {
        viewModelScope.launch{
            val audios = audioRepository
                .getAllAudiosFlow()
                .first()

            val hasImageAudios = audios.map { audio ->
                async {
                    val audioUi = audio.toUi()
                    val albumImage = audioRepository.getAlbumArt(audioUi.album)
                    audioUi.copy(albumImage = albumImage)
                }
            }.awaitAll()

            originalAudios = hasImageAudios

            _state.update { state ->
                state.copy(
                    audioUis = originalAudios
                )
            }
        }
    }

    private fun onSaveTitleName(title: String) {
        playlistTitle = title
    }

    private fun onAllSelectedClick() {
        val newSelectAll = !state.value.isSelectAll
        val newAudio = state.value.audioUis.map {
            it.copy(
                isSelected = newSelectAll
            )
        }
        originalAudios = newAudio
        _state.update {
            it.copy(
                isSelectAll = newSelectAll,
                audioUis = newAudio
            )
        }
    }

    private fun onCloseClick() {
        state.value.searchTextField.clearText()
    }

    private fun onOKClick() {
        viewModelScope.launch {
            val selectedAudios = state.value.audioUis
                .filter { it.isSelected }
                .map { it.id.toString() }

            localDataRepository.createPlaylist(
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

        originalAudios = newAudioUis

        val allSelected = newAudioUis.all { it.isSelected }

        _state.update {
            it.copy(
                audioUis = newAudioUis,
                isSelectAll = allSelected
            )
        }
    }


}