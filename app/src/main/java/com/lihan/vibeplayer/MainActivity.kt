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
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.lihan.vibeplayer.core.navigation.Route
import com.lihan.vibeplayer.music_list.presentation.MusicListScreenRoot
import com.lihan.vibeplayer.music_list.presentation.PermissionScreenRoot
import com.lihan.vibeplayer.music_list.presentation.play.PlayingScreenRoot
import com.lihan.vibeplayer.music_list.presentation.scan.ScanMusicScreenRoot
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

                Scaffold(
                    containerColor = SurfaceBG
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
                                onNavigateToScan = {
                                    navController.navigate(Route.ScanMusic)
                                },
                                onNavigateToPlay = { playId ->
                                    navController.navigate(Route.PlayMusic(playId))
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

                        composable<Route.PlayMusic> { entry ->
                            val audioId = entry.toRoute<Route.PlayMusic>().id
                            PlayingScreenRoot(
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




