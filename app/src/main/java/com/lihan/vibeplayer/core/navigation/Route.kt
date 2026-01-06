package com.lihan.vibeplayer.core.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {

    @Serializable
    data object Permission: Route

    @Serializable
    data object MusicList: Route

    @Serializable
    data object ScanMusic: Route

    @Serializable
    data class PlayMusic(val id: Long): Route
}