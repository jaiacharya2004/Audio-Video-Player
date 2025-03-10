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

    private val playerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_ENDED -> {
                    _isPlaying.value = false
                    _currentPosition.value = 0L
                    playNext() // Auto-play next song
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
        viewModelScope.launch(Dispatchers.Main) {
            if (index !in _audioList.value.indices) {
                Log.e("AudioViewModel", "Invalid song index: $index")
                return@launch
            }

            val songPath = _audioList.value[index].path
            val file = java.io.File(songPath)

            if (!file.exists()) {
                Log.e("AudioViewModel", "File not found: $songPath")
                return@launch
            }

            val uri = Uri.fromFile(file) // Correctly format the URI

            Log.d("AudioViewModel", "Playing song at index: $index, URI: $uri")

            exoPlayer.stop()
            exoPlayer.clearMediaItems()

            try {
                val mediaItem = MediaItem.fromUri(uri) // Use correctly formatted URI
                exoPlayer.setMediaItem(mediaItem)

                _currentSongIndex.value = index
                _currentSong.value = _audioList.value[index]

                exoPlayer.prepare()
                exoPlayer.play()

            } catch (e: Exception) {
                Log.e("AudioViewModel", "Error playing song: $songPath", e)
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



