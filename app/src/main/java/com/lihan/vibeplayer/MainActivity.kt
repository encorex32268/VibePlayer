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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.lihan.vibeplayer.core.navigation.Route
import com.lihan.vibeplayer.music_list.data.Audio
import com.lihan.vibeplayer.music_list.data.DefaultAudioRepository
import com.lihan.vibeplayer.music_list.presentation.PermissionScreenRoot
import com.lihan.vibeplayer.music_list.presentation.components.toTimeString
import com.lihan.vibeplayer.ui.theme.SurfaceBG
import com.lihan.vibeplayer.ui.theme.VibePlayerTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
                            val context = LocalContext.current
                            val audioRepository = DefaultAudioRepository(context)
                            var audios = remember {
                                mutableStateListOf<Audio>()
                            }
                            LaunchedEffect(Unit) {
                                scope.launch(Dispatchers.IO){
                                    audios.addAll(
                                        audioRepository.getAudios()
                                    )
                                    println("Found ${audios.size} audio files")

                                }
                            }
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(SurfaceBG)
                                    .padding(16.dp)
                            ) {
                                items(audios){ audio ->
                                    audio.album?.let {
                                        AsyncImage(
                                            modifier = Modifier.size(200.dp),
                                            model = audio.album,
                                            contentDescription = audio.songTitle
                                        )

                                    }
                                    Text(
                                        text = audio.songTitle
                                    )
                                    Text(
                                        text = audio.artisName
                                    )
                                    Text(
                                        text = audio.duration.toTimeString()
                                    )
                                    Text(
                                        text = audio.album.toString()
                                    )

                                }
                            }

//                            MusicListScreen(
//                                modifier = Modifier.fillMaxSize()
//                            )
                        }


                    }

                }

            }
        }
    }
}



