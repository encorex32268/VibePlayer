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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.lihan.vibeplayer.core.navigation.Route
import com.lihan.vibeplayer.music_list.data.DefaultAudioRepository
import com.lihan.vibeplayer.music_list.presentation.MusicListScreenRoot
import com.lihan.vibeplayer.music_list.presentation.MusicListViewModel
import com.lihan.vibeplayer.music_list.presentation.PermissionScreenRoot
import com.lihan.vibeplayer.ui.theme.VibePlayerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VibePlayerTheme {
                val scope = rememberCoroutineScope()
                val navController = rememberNavController()
                val context = LocalContext.current
                val audioPermissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    rememberPermissionState(android.Manifest.permission.READ_MEDIA_AUDIO)
                } else {
                    rememberPermissionState(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                }
                Scaffold{
                    NavHost(
                        modifier = Modifier.fillMaxSize().padding(it),
                        navController = navController,
                        startDestination = if (audioPermissionState.status.isGranted){
                            Route.MusicList
                        }else{
                            Route.Permission
                        },

                    ){
                        composable<Route.Permission>{

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

                        composable<Route.MusicList>{
                            val audioRepository = DefaultAudioRepository(this@MainActivity)
                            val viewModel = MusicListViewModel(audioRepository)
                            MusicListScreenRoot(
                                viewModel = viewModel
                            )

                        }


                    }

                }

            }
        }
    }
}



