package com.lihan.vibeplayer.music_list.di

import androidx.media3.exoplayer.ExoPlayer
import com.lihan.vibeplayer.music_list.data.DefaultAudioRepository
import com.lihan.vibeplayer.music_list.domain.AudioRepository
import com.lihan.vibeplayer.music_list.domain.ExoPlayerFactory
import com.lihan.vibeplayer.music_list.presentation.MusicListViewModel
import com.lihan.vibeplayer.music_list.presentation.addsong.AddSongsViewModel
import com.lihan.vibeplayer.music_list.presentation.scan.ScanMusicViewModel
import com.lihan.vibeplayer.music_list.presentation.search.SearchViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val musicListModule = module {

    single{
        DefaultAudioRepository(
            context = androidContext(),
            localDataRepository = get()
        )
    }.bind<AudioRepository>()

    single {
        ExoPlayerFactory.build(
            context = androidApplication()
        )
    }.bind<ExoPlayer>()

    viewModelOf(::MusicListViewModel)
    viewModelOf(::ScanMusicViewModel)
    viewModelOf(::SearchViewModel)
    viewModelOf(::AddSongsViewModel)

}