package com.lihan.vibeplayer.core.di

import androidx.room.Room
import com.lihan.vibeplayer.core.data.local.AudioDao
import com.lihan.vibeplayer.core.data.local.FavouritesPlaylistDao
import com.lihan.vibeplayer.core.data.local.LocalDataSource
import com.lihan.vibeplayer.core.data.local.PlaylistDao
import com.lihan.vibeplayer.core.data.local.VibePlayerRoomDatabase
import com.lihan.vibeplayer.core.domain.LocalDataRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreModule = module {

    single {
        Room
            .databaseBuilder(
                androidContext(),
                VibePlayerRoomDatabase::class.java,
                "vibe_player.db")
            .build()
    }

    single { get<VibePlayerRoomDatabase>().audioDao }.bind<AudioDao>()
    single { get<VibePlayerRoomDatabase>().playlistDao }.bind<PlaylistDao>()
    single { get<VibePlayerRoomDatabase>().favouritesPlaylistDao }.bind<FavouritesPlaylistDao>()

    singleOf(::LocalDataSource).bind<LocalDataRepository>()
}