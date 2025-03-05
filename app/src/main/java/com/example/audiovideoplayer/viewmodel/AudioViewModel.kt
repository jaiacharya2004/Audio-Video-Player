package com.example.audiovideoplayer.viewmodel

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audiovideoplayer.ui.audio.AudioModel
import com.example.audiovideoplayer.ui.audio.AudioRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AudioViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AudioRepository(application.applicationContext)

    private val _audioList = mutableStateOf<List<AudioModel>>(emptyList())
    val audioList: State<List<AudioModel>> = _audioList

    fun loadAudio() {
        viewModelScope.launch(Dispatchers.IO) {
            // Clear the list before loading new data
            _audioList.value = emptyList()
            val audioFiles = repository.getAudioFiles()
            _audioList.value = audioFiles
        }
    }
}