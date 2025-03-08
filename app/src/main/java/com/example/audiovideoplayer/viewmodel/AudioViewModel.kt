package com.example.audiovideoplayer.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audiovideoplayer.ui.audio.AudioModel
import com.example.audiovideoplayer.ui.audio.AudioRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.delay

class AudioViewModel(application: Application) : AndroidViewModel(application) {

    val exoPlayer = ExoPlayer.Builder(application).build()
    private val audioRepository = AudioRepository(application)

    private val _audioList = MutableStateFlow<List<AudioModel>>(emptyList())
    val audioList: StateFlow<List<AudioModel>> = _audioList.asStateFlow()

    private val _currentSongIndex = MutableStateFlow(-1)
    val currentSongIndex: StateFlow<Int> = _currentSongIndex.asStateFlow()

    private val _currentSong = MutableStateFlow<AudioModel?>(null)
    val currentSong: StateFlow<AudioModel?> = _currentSong.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration.asStateFlow()

    private val playerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_ENDED -> {
                    _isPlaying.value = false
                    _currentPosition.value = 0L
                }
                Player.STATE_READY -> {
                    _duration.value = exoPlayer.duration
                }
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _isPlaying.value = isPlaying
        }

        override fun onPositionDiscontinuity(
            oldPosition: Player.PositionInfo,
            newPosition: Player.PositionInfo,
            reason: Int
        ) {
            super.onPositionDiscontinuity(oldPosition, newPosition, reason)
            viewModelScope.launch(Dispatchers.IO) {
                withContext(Dispatchers.Main) {
                    _currentPosition.value = exoPlayer.currentPosition
                }
            }
        }
    }

    init {
        loadAudioList()
        exoPlayer.addListener(playerListener)

        // Start coroutine to update progress
        viewModelScope.launch {
            while (true) {
                _currentPosition.value = exoPlayer.currentPosition
                delay(1000) // Update every second
            }
        }
    }


    fun loadAudioList() {
        viewModelScope.launch(Dispatchers.IO) {
            val audioList = audioRepository.getAudioFiles()
            withContext(Dispatchers.Main) {
                _audioList.value = audioList
            }
        }
    }

    fun playSong(index: Int) {
        viewModelScope.launch(Dispatchers.Main) { // Ensure UI update happens on Main thread
            if (index in _audioList.value.indices) {
                exoPlayer.stop()
                exoPlayer.clearMediaItems()

                val mediaItem = MediaItem.fromUri(Uri.parse(_audioList.value[index].path))
                exoPlayer.setMediaItem(mediaItem)

                _currentSongIndex.value = index
                _currentSong.value = _audioList.value[index]

                exoPlayer.prepare()
                exoPlayer.play() // Ensure playback starts
            }
        }
    }


    fun togglePlayPause() {
        if (_isPlaying.value) {
            exoPlayer.pause()
        } else {
            exoPlayer.play()
        }
    }

    fun seekTo(positionMs: Long) {
        viewModelScope.launch(Dispatchers.Main) {
            exoPlayer.seekTo(positionMs)
        }
    }

    fun playNext() {
        val currentIndex = _currentSongIndex.value
        if (currentIndex < _audioList.value.size - 1) {
            playSong(currentIndex + 1)
        } else {
            playSong(0) //loop back to start.
        }
    }

    fun playPrevious() {
        val currentIndex = _currentSongIndex.value
        if (currentIndex > 0) {
            playSong(currentIndex - 1)
        } else {
            playSong(_audioList.value.size - 1) //loop to end.
        }
    }

    override fun onCleared() {
        super.onCleared()
        exoPlayer.release()
    }
}