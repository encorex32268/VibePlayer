package com.lihan.vibeplayer.music_list.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.lihan.vibeplayer.music_list.domain.AudioRepository
import com.lihan.vibeplayer.music_list.presentation.mapper.toUi
import com.lihan.vibeplayer.music_list.presentation.model.AudioUi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MusicListViewModel(
    private val audioRepository: AudioRepository,
    private val exoPlayer: ExoPlayer
): ViewModel(){

    private var hasInitialLoadedData = false

    private val _state = MutableStateFlow(MusicListState())
    val state = _state
        .onStart {
            if (!hasInitialLoadedData){
                loadAudios()
                observerPlayer()
                hasInitialLoadedData = true
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            MusicListState()
        )



    fun onAction(action: MusicListAction){
        when(action){
            MusicListAction.OnScanAgainClick -> loadAudios()
            MusicListAction.OnShuffleClick -> onShuffleClick()
            is MusicListAction.OnAudioUiClick -> onAudioClick(action.audioUi)
            MusicListAction.OnPlayClick -> onPlayClick()
            MusicListAction.OnSkipNextClick -> onSkipNextClick()
            MusicListAction.OnSkipPreviousClick -> onSkipPreviousClick()
            is MusicListAction.OnSeek -> onSeekTo(action.position)
            else -> Unit
        }
    }

    private fun onSeekTo(position: Long){
        exoPlayer.seekTo(position)
        _state.update { it.copy(
            currentPosition = position
        ) }
    }

    private fun onSkipPreviousClick(){
        exoPlayer.seekToPreviousMediaItem()
    }
    private fun onSkipNextClick(){
        exoPlayer.seekToNextMediaItem()
    }

    private fun onPlayClick(){
        val isPlaying = state.value.isPlaying
        if (isPlaying) {
            exoPlayer.pause()
        } else {
            exoPlayer.play()
        }
    }

    private fun onAudioClick(audioUi: AudioUi){
        _state.update { it.copy(
            playingAudioUi = audioUi
        ) }
        val mediaItems = exoPlayer.getAllMediaItems()

        val audioIndex = mediaItems.indexOf(
            mediaItems.find {
                it.mediaId == audioUi.id.toString()
            }
        )

        exoPlayer.setMediaItems(
            exoPlayer.getAllMediaItems(),
            audioIndex,
            0L
        )
        exoPlayer.prepare()
    }

    private fun onShuffleClick(){
        val shuffledAudios = state.value.audios.shuffled()
        _state.update { it.copy(
            audios = shuffledAudios
        ) }
    }

    private fun loadAudios(){
        viewModelScope.launch {
            _state.update { it.copy(
                isScanning = true
            ) }
            delay(1000)

            val audios = audioRepository
                .getAllAudios()
                .map { audio ->
                    async {
                        val audioUi = audio.toUi()
                        val albumImage = audioRepository.getAlbumArt(audioUi.album)
                        audioUi.copy(albumImage = albumImage)
                    }
                }.awaitAll()


            //set player List
            exoPlayer.setMediaItems(
                audios
                    .map {
                        MediaItem.Builder()
                            .setMediaId(it.id.toString())
                            .setUri(it.album)
                            .build()
                    }
            )

            _state.update { it.copy(
                audios = audios,
                isScanning = false
            ) }


        }
    }

    private fun observerPlayer(){
        exoPlayer.addListener(
            object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _state.update { it.copy(
                        isPlaying = isPlaying
                    ) }
                }

                override fun onEvents(player: Player, events: Player.Events) {
                   when{
                       events.contains(Player.EVENT_TRACKS_CHANGED) -> {
                           val currentId = exoPlayer.currentMediaItem?.mediaId
                           if (currentId != null){
                               val currentAudio = state.value.audios.find {
                                   it.id.toString() == currentId
                               }
                               _state.update { it.copy(
                                   playingAudioUi = currentAudio
                               ) }
                           }
                       }
                       events.contains(Player.EVENT_MEDIA_ITEM_TRANSITION) -> {

                      }
                   }
                }

                override fun onRepeatModeChanged(repeatMode: Int) {
                    when(repeatMode){
                        Player.REPEAT_MODE_OFF -> {}
                        Player.REPEAT_MODE_ALL -> {}
                        Player.REPEAT_MODE_ONE -> {}
                    }
                }

                override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {

                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    when(playbackState){
                        Player.STATE_READY -> {
                            val currentPosition = exoPlayer.currentPosition
                            val duration = exoPlayer.duration

                            _state.update { it.copy(
                                currentPosition = currentPosition,
                                duration = duration,
                            ) }
                        }
                        else -> Unit
                    }

                }
            }
        )
        viewModelScope.launch{
            while (isActive){
                if (exoPlayer.isPlaying){
                    val currentPosition = exoPlayer.currentPosition
                    val duration = exoPlayer.duration

                    _state.update { it.copy(
                        currentPosition = currentPosition,
                        duration = duration,
                    ) }

                    delay(500L)
                } else {

                    delay(1000L)
                }
            }
        }
    }

    fun ExoPlayer.getAllMediaItems(): List<MediaItem>{
        val items = mutableListOf<MediaItem>()
        for (i in 0 until this.mediaItemCount){
            items.add(this.getMediaItemAt(i))
        }
        return items
    }

}