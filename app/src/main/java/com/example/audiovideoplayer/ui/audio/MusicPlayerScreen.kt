package com.example.audiovideoplayer.ui.audio

import android.media.MediaPlayer
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.audiovideoplayer.R
import com.example.audiovideoplayer.viewmodel.AudioViewModel
import kotlinx.coroutines.delay

@Composable
fun MusicPlayerScreen(navController: NavController, audioViewModel: AudioViewModel, index: Int) {
    val songList by remember { audioViewModel.audioList } // ✅ Fix collectAsState issue
    var currentIndex by remember { mutableIntStateOf(index) }
    var isPlaying by remember { mutableStateOf(false) }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var progress by remember { mutableFloatStateOf(0f) }

    // Function to play a song
    fun playSong(index: Int) {
        if (index in songList.indices) {
            mediaPlayer?.release() // Release any existing player
            mediaPlayer = MediaPlayer().apply {
                try {
                    setDataSource(songList[index].path)
                    prepare()
                    start()
                    isPlaying = true

                    setOnCompletionListener {
                        if (currentIndex < songList.size - 1) {
                            currentIndex++
                        } else {
                            isPlaying = false
                            progress = 0f
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    isPlaying = false
                }
            }
        }
    }

    // Play the selected song when index changes
    LaunchedEffect(currentIndex) {
        playSong(currentIndex)
    }

    // Dispose media player when screen is destroyed
    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
        }
    }

    // Updates progress bar
    LaunchedEffect(isPlaying, mediaPlayer) {
        while (isPlaying) {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    progress = it.currentPosition.toFloat() / it.duration.toFloat()
                }
            }
            delay(1000)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = if (currentIndex in songList.indices) songList[currentIndex].title else "Unknown Song",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Slider(
            value = progress,
            onValueChange = { newValue ->
                mediaPlayer?.seekTo((newValue * (mediaPlayer?.duration ?: 1)).toInt())
                progress = newValue
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { if (currentIndex > 0) currentIndex-- },
                enabled = currentIndex > 0
            ) {
                Icon(painter = painterResource(id = R.drawable.previous_svgrepo_com), contentDescription = "Previous")
            }

            IconButton(onClick = {
                mediaPlayer?.let {
                    if (isPlaying) it.pause() else it.start()
                    isPlaying = !isPlaying
                }
            }) {
                Icon(
                    painter = painterResource(if (isPlaying) R.drawable.pause_svgrepo_com else R.drawable.play_svgrepo_com),
                    contentDescription = if (isPlaying) "Pause" else "Play"
                )
            }

            IconButton(
                onClick = { if (currentIndex < songList.size - 1) currentIndex++ },
                enabled = currentIndex < songList.size - 1
            ) {
                Icon(painter = painterResource(id = R.drawable.next_svgrepo_com), contentDescription = "Next")
            }
        }
    }

    // ✅ FIX: Mini Player is now correctly checked for null songList
    if (isPlaying && songList.isNotEmpty()) {
        MiniPlayer(navController, audioViewModel, currentIndex)
    }
}

@Composable
fun MiniPlayer(navController: NavController, audioViewModel: AudioViewModel, currentIndex: Int) {
    val songList by remember { audioViewModel.audioList } // ✅ Fix collectAsState issue
    val currentSong = songList.getOrNull(currentIndex) ?: return // ✅ Fix unreachable code

    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate("musicPlayer/$currentIndex") }
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = currentSong.title,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
