@file:OptIn(FlowPreview::class)

package com.lihan.vibeplayer.music_list.presentation.search

import androidx.compose.foundation.text.input.clearText
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lihan.vibeplayer.music_list.domain.MusicListRepository
import com.lihan.vibeplayer.music_list.presentation.mapper.toUi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel(
    private val repository: MusicListRepository
) : ViewModel() {
    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(SearchState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                observerTextFieldState()
                hasLoadedInitialData = true
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            SearchState()
        )


    fun onAction(action: SearchAction) {
        when (action) {
            SearchAction.OnCancelClick -> Unit
            SearchAction.OnCloseClick -> {
                state.value.textFieldState.clearText()
                _state.update {
                    it.copy(
                        searchedAudios = emptyList(),
                        isSearching = false
                    )
                }
            }
        }
    }

    private fun observerTextFieldState() {
        viewModelScope.launch {
            snapshotFlow {
                state.value.textFieldState.text.toString()
            }
                .onEach { text ->
                    if (text.trim().isNotEmpty()) {
                        _state.update { it.copy(isSearching = true) }
                    }else{
                        _state.update { it.copy(
                            searchedAudios = emptyList()
                        ) }
                    }
                }
                .filter { it.trim().isNotEmpty() }
                .debounce(500L)
                .onEach { text ->
                    val result = repository
                        .getAudiosByTitle(text)
                        .map { audio ->
                            async {
                                val audioUi = audio.toUi()
                                val albumImage = repository.getAlbumArtImage(audioUi.album)
                                audioUi.copy(albumImage = albumImage)
                            }
                        }.awaitAll()

                    _state.update {
                        it.copy(
                            searchedAudios = result,
                            isSearching = false
                        )
                    }
                }.launchIn(this)
        }
    }
}