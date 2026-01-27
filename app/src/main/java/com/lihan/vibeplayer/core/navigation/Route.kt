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
    data class AddSongs(
        val title: String?=null,
        val id: Int?=null
    ): Route

    @Serializable
    data class PlaylistDetail(val id: Int): Route
}

val withoutBottomBarRoutes = listOf(
    Route.Permission::class,
    Route.ScanMusic::class,
    Route.AddSongs::class,
    Route.Search::class
)