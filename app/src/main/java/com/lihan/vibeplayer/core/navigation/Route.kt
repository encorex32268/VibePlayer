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
    data object Search: Route

    @Serializable
    data class AddSongs(val title: String): Route

    @Serializable
    data class PlaylistDetail(val id: Int): Route
}