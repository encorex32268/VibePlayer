package com.lihan.vibeplayer.core.di

import androidx.room.Room
import com.lihan.vibeplayer.core.data.local.VibePlayerRoomDatabase
import org.koin.android.ext.koin.androidContext
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
}