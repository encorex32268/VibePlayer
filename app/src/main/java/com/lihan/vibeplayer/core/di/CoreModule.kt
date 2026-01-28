package com.lihan.vibeplayer.core.di

import androidx.room.Room
import com.lihan.vibeplayer.core.database.AudioDao
import com.lihan.vibeplayer.core.database.PlaylistDao
import com.lihan.vibeplayer.core.database.VibePlayerRoomDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.bind
import org.koin.dsl.module

val coreModule = module {

    single {
        Room
            .databaseBuilder(
                androidContext(),
                VibePlayerRoomDatabase::class.java,
                "vibe_player.db")
            .fallbackToDestructiveMigration(true)
            .build()
    }

    single { get<VibePlayerRoomDatabase>().audioDao }.bind<AudioDao>()
    single { get<VibePlayerRoomDatabase>().playlistDao }.bind<PlaylistDao>()

}