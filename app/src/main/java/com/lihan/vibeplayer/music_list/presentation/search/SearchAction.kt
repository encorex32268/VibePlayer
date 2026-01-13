package com.lihan.vibeplayer.music_list.presentation.search

sealed interface SearchAction {
    data object OnCancelClick: SearchAction
    data object OnCloseClick: SearchAction
}