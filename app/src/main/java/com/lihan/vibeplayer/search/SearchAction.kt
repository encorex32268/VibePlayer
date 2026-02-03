package com.lihan.vibeplayer.search

sealed interface SearchAction {
    data object OnCancelClick: SearchAction
    data object OnCloseClick: SearchAction
}