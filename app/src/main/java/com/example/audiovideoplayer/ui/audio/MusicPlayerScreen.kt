package com.example.audiovideoplayer.ui.audio

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.audiovideoplayer.R
import com.example.audiovideoplayer.viewmodel.AudioViewModel

@Composable
fun MusicPlayerScreen(navController: NavController, audioViewModel: AudioViewModel, index: Int) {
    val isPlaying by audioViewModel.isPlaying.collectAsState()
    val currentSong by audioViewModel.currentSong.collectAsState()
    val currentSongIndex by audioViewModel.currentSongIndex.collectAsState()
    val audioList by audioViewModel.audioList.collectAsState()
    val currentPosition by audioViewModel.currentPosition.collectAsState()
    val duration by audioViewModel.duration.collectAsState()


    LaunchedEffect(index, audioList) {
        if (audioList.isNotEmpty() && index in audioList.indices) {
            audioViewModel.playSong(index)
        }
    }


    val progress = remember(currentPosition, duration) {
        if (duration > 0) currentPosition.toFloat() / duration.toFloat() else 0f
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = currentSong?.title ?: "Unknown Song",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Slider(
            value = progress,
            onValueChange = { newValue ->
                val seekPosition = (newValue * duration).toLong()
                audioViewModel.seekTo(seekPosition)
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { audioViewModel.playPrevious() }, enabled = currentSongIndex > 0) {
                Icon(painter = painterResource(id = R.drawable.previous_svgrepo_com), contentDescription = "Previous")
            }
            IconButton(onClick = { if (isPlaying) audioViewModel.togglePlayPause() else audioViewModel.togglePlayPause() }) {
                Icon(
                    painter = painterResource(if (isPlaying) R.drawable.pause_svgrepo_com else R.drawable.play_svgrepo_com),
                    contentDescription = if (isPlaying) "Pause" else "Play"
                )
            }
            IconButton(onClick = { audioViewModel.playNext() }, enabled = currentSongIndex < audioList.size - 1) {
                Icon(painter = painterResource(id = R.drawable.next_svgrepo_com__1_), contentDescription = "Next")
            }
        }
    }
    if (isPlaying && audioList.isNotEmpty()) {
        MiniPlayer(navController, audioViewModel, currentSongIndex)
    }
}
