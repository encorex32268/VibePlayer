package com.lihan.vibeplayer.music_list.di

import androidx.media3.exoplayer.ExoPlayer
import com.lihan.vibeplayer.music_list.data.DefaultExoPlayerManager
import com.lihan.vibeplayer.music_list.data.OfflineMusicListRepository
import com.lihan.vibeplayer.music_list.domain.ExoPlayerManager
import com.lihan.vibeplayer.music_list.domain.MusicListRepository
import com.lihan.vibeplayer.music_list.presentation.MusicListViewModel
import com.lihan.vibeplayer.music_list.presentation.MusicSharedViewModel
import com.lihan.vibeplayer.music_list.presentation.addsong.AddSongsViewModel
import com.lihan.vibeplayer.music_list.presentation.playlistdetial.PlaylistDetailViewModel
import com.lihan.vibeplayer.music_list.presentation.scan.ScanMusicViewModel
import com.lihan.vibeplayer.music_list.presentation.search.SearchViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val musicListModule = module {

    single {
        OfflineMusicListRepository(
            context = androidContext(),
            db = get()
        )
    }.bind<MusicListRepository>()

    single {
        DefaultExoPlayerManager(
            context = androidApplication()
        )
    }.bind<ExoPlayerManager>()

    viewModelOf(::MusicListViewModel)
    viewModelOf(::SearchViewModel)
    viewModelOf(::ScanMusicViewModel)

    viewModel { (title: String, playlistId: Int?) ->
        AddSongsViewModel(
            title = title,
            playlistId = playlistId,
            repository = get()
        )
    }

    viewModel{
        PlaylistDetailViewModel(
            id = get(),
            repository = get()
        )
    }

    viewModelOf(::MusicSharedViewModel)


}