package com.lihan.vibeplayer

import android.app.Application
import com.lihan.vibeplayer.music_list.di.musicListModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class VibePlayerApp : Application(){
    override fun onCreate() {
        super.onCreate()
        startKoin{
            androidLogger(level = Level.DEBUG)
            androidContext(this@VibePlayerApp)
            modules(
                musicListModule
            )
        }
    }
}