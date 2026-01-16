package com.lihan.vibeplayer.music_list.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ShuffleOrder
import com.lihan.vibeplayer.R
import com.lihan.vibeplayer.core.presentation.util.UiText
import com.lihan.vibeplayer.music_list.domain.AudioRepository
import com.lihan.vibeplayer.music_list.presentation.mapper.toUi
import com.lihan.vibeplayer.music_list.presentation.model.AudioUi
import com.lihan.vibeplayer.music_list.presentation.model.RepeatModeStatus
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MusicListViewModel(
    private val audioRepository: AudioRepository,
    private val exoPlayer: ExoPlayer
): ViewModel(){

    private var hasInitialLoadedData = false

    private val _uiEvent = Channel<MusicListUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

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
            MusicListAction.OnFunctionShuffleClick -> onPlayListShuffleClick()
            is MusicListAction.OnFunctionPlayClick -> onFunctionPlayClick()
            MusicListAction.OnPlayClick -> onPlayClick()
            MusicListAction.OnSkipNextClick -> onSkipNextClick()
            MusicListAction.OnSkipPreviousClick -> onSkipPreviousClick()
            is MusicListAction.OnSeek -> onSeekTo(action.position)
            MusicListAction.OnRepeatClick -> onRepeatModeClick()
            MusicListAction.OnShuffleClick -> onShuffleClick()
            is MusicListAction.OnSongClick -> onSongClick(action.audioUi)
            else -> Unit
        }
    }

    private fun onSongClick(audioUi: AudioUi){
        _state.update { it.copy(
            playingAudioUi = audioUi
        ) }
        val currentMediaItems = exoPlayer.getAllMediaItems()
        val index = currentMediaItems.indexOf(
            currentMediaItems.find { it.mediaId == audioUi.id.toString() }
        )
        exoPlayer.setMediaItems(
            exoPlayer.getAllMediaItems(),
            index,
            0L
        )
        exoPlayer.prepare()
    }

    private fun onShuffleClick(){
        exoPlayer.shuffleModeEnabled = !exoPlayer.shuffleModeEnabled
        if (exoPlayer.shuffleModeEnabled) {
            val totalCount = exoPlayer.mediaItemCount
            if (totalCount > 0) {
                val currentIndex = exoPlayer.currentMediaItemIndex

                val shuffleIndices = (0 until totalCount).filter { it != currentIndex }.shuffled()

                val newOrder = (listOf(currentIndex) + shuffleIndices).toIntArray()

                exoPlayer.shuffleOrder = ShuffleOrder.DefaultShuffleOrder(
                    newOrder,
                    System.currentTimeMillis()
                )
            }
        }
    }



    private fun onRepeatModeClick(){
        val currentRepeatMode = exoPlayer.repeatMode
        val newRepeatMode = when(currentRepeatMode){
            Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
            Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
            Player.REPEAT_MODE_ONE -> Player.REPEAT_MODE_OFF
            else -> Player.REPEAT_MODE_OFF
        }
        exoPlayer.repeatMode = newRepeatMode
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

    private fun onFunctionPlayClick(){
        exoPlayer.setMediaItems(
            exoPlayer.getAllMediaItems(),
            0,
            0L
        )
        exoPlayer.prepare()
        exoPlayer.play()
    }

    private fun onPlayListShuffleClick(){
        val shuffledList = state.value.audios.shuffled()
        _state.update { it.copy(
            audios = shuffledList
        ) }

        val currentMediaItems = exoPlayer.getAllMediaItems().associateBy { it.mediaId }
        val newMediaItems = shuffledList.mapNotNull { audio ->
            currentMediaItems[audio.id.toString()]
        }
        exoPlayer.setMediaItems(
            newMediaItems,
            0,
            0L
        )
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
                    val repeatModeStatus = when(repeatMode){
                        Player.REPEAT_MODE_OFF -> RepeatModeStatus.Off
                        Player.REPEAT_MODE_ALL -> RepeatModeStatus.All
                        Player.REPEAT_MODE_ONE -> RepeatModeStatus.One
                        else -> RepeatModeStatus.Off
                    }

                    viewModelScope.launch {
                        _uiEvent.send(
                            MusicListUiEvent.OnRepeatModeChange(
                                repeatModeStatus.toUiText()
                            )
                        )
                    }

                    _state.update { it.copy(
                        repeatModeStatus = repeatModeStatus
                    ) }
                }

                override fun onTimelineChanged(timeline: Timeline, reason: Int) {
                    updateShuffledList()
                }

                override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                    updateShuffledList()

                    viewModelScope.launch {
                        _uiEvent.send(
                            MusicListUiEvent.OnShuffleEnabledChange(
                                UiText.StringResource(
                                    if (shuffleModeEnabled){
                                        R.string.main_shuffle_enabled
                                    }else{
                                        R.string.main_shuffle_off
                                    }
                                )
                            )
                        )
                    }

                    _state.update { it.copy(
                        isEnabledShuffle = shuffleModeEnabled
                    ) }
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

    private fun updateShuffledList(){
        val items = if (exoPlayer.shuffleModeEnabled) {
            getShuffledMediaItems()
        } else {
            (0 until exoPlayer.mediaItemCount).map { exoPlayer.getMediaItemAt(it) }
        }

        val audioMap = state.value.audios.associateBy { it.id.toString() }

        val newAudioUis = items.mapNotNull { mediaItem ->
            audioMap[mediaItem.mediaId]
        }

        _state.update { it.copy(
            audios = newAudioUis
        ) }
    }

    private fun getShuffledMediaItems(): List<MediaItem> {
        val timeline = exoPlayer.currentTimeline
        if (timeline.isEmpty){
            return emptyList()
        }
        val shuffledList = mutableListOf<MediaItem>()

        var currentIndex = timeline.getFirstWindowIndex(true)

        while (currentIndex != -1) {
            val mediaItem = exoPlayer.getMediaItemAt(currentIndex)
            shuffledList.add(mediaItem)

            currentIndex = timeline.getNextWindowIndex(
                currentIndex,
                Player.REPEAT_MODE_OFF,
                true
            )
        }

        return shuffledList
    }

}