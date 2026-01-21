package com.lihan.vibeplayer

import android.app.Application
import android.content.Context
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.disk.DiskCache
import coil3.key.Keyer
import coil3.memory.MemoryCache
import coil3.request.Options
import coil3.request.crossfade
import coil3.util.DebugLogger
import com.lihan.vibeplayer.core.di.coreModule
import com.lihan.vibeplayer.music_list.di.musicListModule
import okio.Path.Companion.toPath
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class VibePlayerApp : Application(), SingletonImageLoader.Factory{
    override fun onCreate() {
        super.onCreate()
        startKoin{
            androidLogger(level = Level.DEBUG)
            androidContext(this@VibePlayerApp)
            modules(
                coreModule,
                musicListModule
            )
        }
    }
    override fun newImageLoader(context: Context): ImageLoader {
        return ImageLoader
            .Builder(context)
            .crossfade(true)
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(context, 0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("image_cache").absolutePath.toPath())
                    .maxSizePercent(0.02)
                    .build()
            }
            .components {
                add(ByteArrayKeyer())
            }
            .build()
    }

}


class ByteArrayKeyer : Keyer<ByteArray> {

    override fun key(data: ByteArray, options: Options): String? {
        return data.joinToString(",") { it.toString() }
    }
}