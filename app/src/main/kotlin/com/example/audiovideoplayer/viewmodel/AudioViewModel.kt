package com.example.audiovideoplayer.viewmodel

import android.app.Activity
import android.app.Application
import android.app.RecoverableSecurityException
import android.content.ContentResolver
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
import android.content.Context
import android.media.MediaScannerConnection
import android.os.Build
import android.provider.MediaStore
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.audiovideoplayer.MainActivity
import com.example.audiovideoplayer.MainActivity.Companion.REQUEST_DELETE_PERMISSION
import java.io.File



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
    private val _repeatMode = MutableStateFlow(RepeatMode.NO_REPEAT)
    val repeatMode: StateFlow<RepeatMode> = _repeatMode.asStateFlow()




    enum class RepeatMode {
        NO_REPEAT, REPEAT_ONE, REPEAT_ALL
    }



//    val audioList = MutableStateFlow<List<AudioModel>>(emptyList())

    private var currentPlayingIndex = mutableIntStateOf(-1)
        private set

    fun setPlayingSong(index: Int) {
        currentPlayingIndex.intValue = index
    }


    private val playerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_ENDED -> {
                    _isPlaying.value = false
                    _currentPosition.value = 0L
                    handlePlaybackCompletion()  // Call the function here ✅
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

        // Set ExoPlayer repeat mode to manual control
        exoPlayer.repeatMode = Player.REPEAT_MODE_OFF

        // Start coroutine to update progress
        viewModelScope.launch {
            while (true) {
                _currentPosition.value = exoPlayer.currentPosition
                delay(200) // Update every second
            }
        }
    }


    private val _isLooping = MutableStateFlow(false)
    val isLooping: StateFlow<Boolean> = _isLooping.asStateFlow()


    private fun handlePlaybackCompletion() {
        when (_repeatMode.value) {
            RepeatMode.REPEAT_ONE -> {
                exoPlayer.seekTo(0)
                exoPlayer.play()
            }
            RepeatMode.REPEAT_ALL -> {
                val nextIndex = (_currentSongIndex.value + 1) % _audioList.value.size
                playSong(nextIndex)
            }
            RepeatMode.NO_REPEAT -> {
                val nextIndex = _currentSongIndex.value + 1
                if (nextIndex < _audioList.value.size) {
                    playSong(nextIndex)
                }
            }
        }
    }



    fun toggleRepeatMode(): String {
        _repeatMode.value = when (_repeatMode.value) {
            RepeatMode.NO_REPEAT -> {
                exoPlayer.repeatMode = Player.REPEAT_MODE_ONE
                RepeatMode.REPEAT_ONE
            }
            RepeatMode.REPEAT_ONE -> {
                exoPlayer.repeatMode = Player.REPEAT_MODE_OFF
                RepeatMode.NO_REPEAT
            }
            else -> RepeatMode.NO_REPEAT
        }
        return getRepeatModeText() // ✅ Return the updated repeat mode as a String
    }

    private fun getRepeatModeText(): String {
        return when (repeatMode.value) {
            RepeatMode.REPEAT_ONE -> "Repeat "
            RepeatMode.REPEAT_ALL -> "Repeat "
            RepeatMode.NO_REPEAT -> "Repeat Off"
            else -> "Unknown Mode"
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
        viewModelScope.launch(Dispatchers.Main) {
            if (index !in _audioList.value.indices) {
                Log.e("AudioViewModel", "playSong called with invalid index: $index")
                return@launch
            }

            val song = _audioList.value[index]
            val uri = Uri.parse(song.path)

            Log.d("AudioViewModel", "Playing song: ${song.title} at $uri")

            _currentSongIndex.value = index
            _currentSong.value = song.copy()

            exoPlayer.stop()
            exoPlayer.clearMediaItems()
            exoPlayer.setMediaItem(MediaItem.fromUri(uri))
            exoPlayer.prepare()
            exoPlayer.play()

            _isPlaying.value = true
        }
    }


    fun togglePlayPause() {
        if (_isPlaying.value) {
            val lastPosition = exoPlayer.currentPosition
            exoPlayer.pause()
            exoPlayer.seekTo(lastPosition) // Prevent overshooting
        } else {
            exoPlayer.play()
        }
    }


    fun playNextOrPrevious(isNext: Boolean) {
        val listSize = _audioList.value.size
        if (listSize == 0) return

        val newIndex = if (isNext) {
            (_currentSongIndex.value + 1) % listSize  // Moves forward, loops back to 0
        } else {
            (_currentSongIndex.value - 1 + listSize) % listSize  // Moves backward, loops to last song
        }

        playSong(newIndex)
    }





    fun seekTo(positionMs: Long) {
        viewModelScope.launch(Dispatchers.Main) {
            exoPlayer.seekTo(positionMs)

            // Prevent flicker by disabling state updates for a moment
            _isPlaying.value = exoPlayer.playWhenReady
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


    private fun deleteFileScopedStorage(context: Context, uri: Uri) {
        try {
            val resolver = context.contentResolver
            val deletedRows = resolver.delete(uri, null, null)

            if (deletedRows > 0) {
                Log.d("AudioViewModel", "File deleted successfully: $uri")
            } else {
                Log.e("AudioViewModel", "Failed to delete file: $uri")
            }
        } catch (e: SecurityException) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {  // API 29+
                if (e is RecoverableSecurityException) {
                    val intentSender = e.userAction.actionIntent.intentSender
                    (context as? Activity)?.startIntentSenderForResult(
                        intentSender, MainActivity.REQUEST_DELETE_PERMISSION, null, 0, 0, 0, null
                    )
                }
            } else {
                Log.e("AudioViewModel", "SecurityException occurred on API < 29", e)
            }
        } catch (e: Exception) {
            Log.e("AudioViewModel", "Error deleting file", e)
        }
    }





    private fun getMediaStoreUri(context: Context, filePath: String): Uri? {
        val contentResolver = context.contentResolver
        val projection = arrayOf(MediaStore.Audio.Media._ID)
        val selection = "${MediaStore.Audio.Media.DATA} = ?"
        val selectionArgs = arrayOf(filePath)

        contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection, selection, selectionArgs, null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
                return Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id.toString())
            }
        }
        return null
    }




    private fun removeDeletedFileFromList(filePath: String) {
        val updatedList = _audioList.value.filter { it.path != filePath }
        _audioList.value = updatedList
    }



    fun deleteAudioFile(
        context: Context,
        filePath: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val file = File(filePath)

        if (file.exists()) {
            // Check if file is in MediaStore
            val uri = getMediaStoreUri(context, filePath)

            if (uri != null) {
                // Use deleteFileScopedStorage for scoped storage deletion
                deleteFileScopedStorage(context, uri)
                onSuccess()
            } else {
                // Try deleting directly (works for app-private files)
                if (file.delete()) {
                    onSuccess()
                } else {
                    onError("File exists but could not be deleted.")
                }
            }
        } else {
            onError("File does not exist.")
        }
    }




}



