@file:OptIn(ExperimentalPermissionsApi::class)

package com.lihan.vibeplayer

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.lihan.vibeplayer.core.navigation.Route
import com.lihan.vibeplayer.core.navigation.withoutBottomBarRoutes
import com.lihan.vibeplayer.music_list.presentation.MusicListScreenRoot
import com.lihan.vibeplayer.music_list.presentation.MusicSharedAction
import com.lihan.vibeplayer.music_list.presentation.MusicSharedViewModel
import com.lihan.vibeplayer.music_list.presentation.addsong.AddSongsScreenRoot
import com.lihan.vibeplayer.music_list.presentation.components.PlayerBottomBar
import com.lihan.vibeplayer.music_list.presentation.playlistdetial.PlaylistDetailScreenRoot
import com.lihan.vibeplayer.music_list.presentation.scan.ScanMusicScreenRoot
import com.lihan.vibeplayer.music_list.presentation.search.SearchScreenRoot
import com.lihan.vibeplayer.permission.PermissionScreenRoot
import com.lihan.vibeplayer.ui.theme.SurfaceBG
import com.lihan.vibeplayer.ui.theme.VibePlayerTheme
import org.koin.compose.viewmodel.koinActivityViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VibePlayerTheme {
                val navController = rememberNavController()
                val audioPermissionState =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        rememberPermissionState(android.Manifest.permission.READ_MEDIA_AUDIO)
                    } else {
                        rememberPermissionState(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                val startDestination = if (audioPermissionState.status.isGranted) {
                    Route.MusicList
                } else {
                    Route.Permission
                }
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                val isHideBottomBar = withoutBottomBarRoutes.any { currentDestination?.hasRoute(it) == true }

                val musicSharedViewModel = koinActivityViewModel<MusicSharedViewModel>()
                val sharedState by musicSharedViewModel.state.collectAsStateWithLifecycle()

                Scaffold(
                    containerColor = SurfaceBG,
                    bottomBar = {
                        if (sharedState.playingAudioUi != null && !isHideBottomBar){
                            AnimatedVisibility(
                                visible = true,
                                enter = slideInVertically(
                                    initialOffsetY = { fullHeight -> fullHeight }
                                ) + fadeIn(),
                                exit = slideOutVertically(
                                    targetOffsetY = { fullHeight -> fullHeight }
                                ) + fadeOut()
                            ) {
                                PlayerBottomBar(
                                    audioUi = sharedState.playingAudioUi!!,
                                    modeStatusBanner = sharedState.modeStatusBanner,
                                    repeatModeStatus = sharedState.repeatModeStatus,
                                    isPlaying = sharedState.isPlaying,
                                    isEnabledShuffle = sharedState.isEnabledShuffle,
                                    isExpandPlayer = sharedState.isExpandPlayer,
                                    duration = sharedState.duration,
                                    currentPosition = sharedState.currentPosition,
                                    onPlayClick = {
                                        musicSharedViewModel.onAction(MusicSharedAction.OnPlayClick)
                                    },
                                    onSkipNextClick = {
                                        musicSharedViewModel.onAction(MusicSharedAction.OnSkipNextClick)
                                    },
                                    onSkipPreviousClick = {
                                        musicSharedViewModel.onAction(MusicSharedAction.OnSkipPreviousClick)
                                    },
                                    onSeek = {
                                        musicSharedViewModel.onAction(MusicSharedAction.OnSeek(it))
                                    },
                                    onRepeatClick = {
                                        musicSharedViewModel.onAction(MusicSharedAction.OnRepeatClick)
                                    },
                                    onShuffleClick = {
                                        musicSharedViewModel.onAction(MusicSharedAction.OnShuffleClick)
                                    },
                                    onExpandClick = {
                                        musicSharedViewModel.onAction(MusicSharedAction.OnExpandClick)
                                    },
                                    onCollapseClick = {
                                        musicSharedViewModel.onAction(MusicSharedAction.OnCollapseClick)
                                    },
                                    onHideModeChangedBanner = {
                                        musicSharedViewModel.onAction(MusicSharedAction.OnHideModeChangedBanner)
                                    },
                                    onFavouriteClick = {
                                        musicSharedViewModel.onAction(MusicSharedAction.OnFavouriteClick)
                                    },
                                    onPlaylistClick = {}
                                )
                            }
                        }
                    }
                ) {
                    NavHost(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(it),
                        navController = navController,
                        startDestination = startDestination
                    ) {
                        composable<Route.Permission> {
                            PermissionScreenRoot(
                                audioPermissionState = audioPermissionState,
                                onNavigateToSetting = {
                                    val intent = Intent(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                        Uri.fromParts("package", this@MainActivity.packageName, null)
                                    )
                                    this@MainActivity.startActivity(intent)
                                }
                            )

                        }

                        composable<Route.MusicList> {
                            MusicListScreenRoot(
                                musicSharedViewModel = musicSharedViewModel,
                                onNavigateToScan = {
                                    navController.navigate(Route.ScanMusic)
                                },
                                onNavigateToSearch = {
                                    navController.navigate(Route.Search)
                                },
                                onNavigateToAddSongs = { title ->
                                    navController.navigate(Route.AddSongs(title))
                                },
                                onNavigateToPlaylistDetail = { playlistId ->
                                    navController.navigate(Route.PlaylistDetail(playlistId))
                                },
                                onFunctionPlayClick = {
                                    musicSharedViewModel.onAction(MusicSharedAction.OnFunctionPlayClick(emptyList()))
                                },
                                onFunctionShuffleClick = {
                                    musicSharedViewModel.onAction(MusicSharedAction.OnFunctionShuffleClick(emptyList()))
                                },
                                onSongClick = { audioUi ->
                                    musicSharedViewModel.onAction(MusicSharedAction.OnSongClick(audioUi))
                                }

                            )
                        }

                        composable<Route.ScanMusic> {
                            ScanMusicScreenRoot(
                                onBack = {
                                    navController.navigateUp()
                                }
                            )
                        }

                        composable<Route.Search>{
                            SearchScreenRoot(
                                onBack = {
                                    navController.navigateUp()
                                }
                            )
                        }

                        composable<Route.AddSongs>{ entry ->
                            val route = entry.toRoute<Route.AddSongs>()
                            AddSongsScreenRoot(
                                title = route.title?:"",
                                playlistId = route.id,
                                onBack = {
                                    navController.navigateUp()
                                }
                            )
                        }

                        composable<Route.PlaylistDetail>{ entry ->
                            val routeId = entry.toRoute<Route.PlaylistDetail>().id
                            PlaylistDetailScreenRoot(
                                musicSharedViewModel = musicSharedViewModel,
                                playlistId = routeId,
                                onBack = {
                                    navController.navigateUp()
                                },
                                onNavigateToAddSongs = { playlistId,playlistTitle ->
                                    navController.navigate(
                                        Route.AddSongs(
                                            id = playlistId,
                                            title = playlistTitle
                                        )
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
