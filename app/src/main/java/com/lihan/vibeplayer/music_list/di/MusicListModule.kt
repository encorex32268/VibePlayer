package com.lihan.vibeplayer.music_list.di

import com.lihan.vibeplayer.music_list.data.DefaultAudioRepository
import com.lihan.vibeplayer.music_list.domain.AudioRepository
import com.lihan.vibeplayer.music_list.presentation.MusicListViewModel
import com.lihan.vibeplayer.music_list.presentation.play.PlayingViewModel
import com.lihan.vibeplayer.music_list.presentation.scan.ScanMusicViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val musicListModule = module {

    single{
        DefaultAudioRepository(
            context = androidContext()
        )
    }.bind<AudioRepository>()

    viewModelOf(::MusicListViewModel)
    viewModelOf(::ScanMusicViewModel)
    viewModelOf(::PlayingViewModel)

}