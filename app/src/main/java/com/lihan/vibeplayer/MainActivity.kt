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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.lihan.vibeplayer.core.navigation.Route
import com.lihan.vibeplayer.music_list.data.DefaultAudioRepository
import com.lihan.vibeplayer.music_list.presentation.MusicListScreenRoot
import com.lihan.vibeplayer.music_list.presentation.MusicListViewModel
import com.lihan.vibeplayer.music_list.presentation.PermissionScreenRoot
import com.lihan.vibeplayer.music_list.presentation.play.PlayingScreenRoot
import com.lihan.vibeplayer.music_list.presentation.play.PlayingViewModel
import com.lihan.vibeplayer.music_list.presentation.scan.ScanMusicScreenRoot
import com.lihan.vibeplayer.music_list.presentation.scan.ScanMusicViewModel
import com.lihan.vibeplayer.ui.theme.SurfaceBG
import com.lihan.vibeplayer.ui.theme.VibePlayerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VibePlayerTheme {
                val navController = rememberNavController()
                val context = LocalContext.current
                val audioPermissionState =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        rememberPermissionState(android.Manifest.permission.READ_MEDIA_AUDIO)
                    } else {
                        rememberPermissionState(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    }

                val audioRepository = DefaultAudioRepository(this@MainActivity)

                Scaffold(
                    containerColor = SurfaceBG
                ) {
                    NavHost(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(it),
                        navController = navController,
                        startDestination = if (audioPermissionState.status.isGranted) {
                            Route.MusicList
                        } else {
                            Route.Permission
                        },

                        ) {
                        composable<Route.Permission> {

                            PermissionScreenRoot(
                                audioPermissionState = audioPermissionState,
                                onNavigateToSetting = {
                                    val intent = Intent(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                        Uri.fromParts("package", context.packageName, null)
                                    )
                                    context.startActivity(intent)
                                }
                            )

                        }

                        composable<Route.MusicList> {
                            val viewModel: MusicListViewModel = viewModel(
                                factory = MusicListViewModelFactory(audioRepository)
                            )
                            MusicListScreenRoot(
                                viewModel = viewModel,
                                onNavigateToScan = {
                                    navController.navigate(Route.ScanMusic)
                                },
                                onNavigateToPlay = {
                                    navController.navigate(Route.PlayMusic(it))
                                }
                            )

                        }

                        composable<Route.ScanMusic> {
                            val viewModel: ScanMusicViewModel = viewModel(
                                factory = ScanMusicViewModelFactory(audioRepository)
                            )
                            ScanMusicScreenRoot(
                                viewModel = viewModel,
                                onBack = {
                                    navController.navigateUp()
                                }
                            )
                        }

                        composable<Route.PlayMusic> { entry ->
                            val audioId = entry.toRoute<Route.PlayMusic>().id

                            val viewModel: PlayingViewModel = viewModel(
                                factory = PlayingViewModelFactory(audioRepository)
                            )
                            PlayingScreenRoot(
                                viewModel = viewModel,
                                audioId = audioId,
                                onBack = {
                                    navController.navigateUp()
                                }
                            )
                        }


                    }

                }

            }
        }
    }
}

class MusicListViewModelFactory(
    private val audioRepository: DefaultAudioRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MusicListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MusicListViewModel(audioRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class ScanMusicViewModelFactory(
    private val audioRepository: DefaultAudioRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScanMusicViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ScanMusicViewModel(audioRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class PlayingViewModelFactory(
    private val audioRepository: DefaultAudioRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlayingViewModel(audioRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}



