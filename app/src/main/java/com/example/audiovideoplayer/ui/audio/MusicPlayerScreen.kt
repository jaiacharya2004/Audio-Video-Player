package com.example.audiovideoplayer.ui.audio

import androidx.compose.foundation.Image
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
import kotlinx.coroutines.launch

@Composable
fun MusicPlayerScreen(navController: NavController, audioViewModel: AudioViewModel, index: Int) {
    val isPlaying by audioViewModel.isPlaying.collectAsState()
    val repeatMode by audioViewModel.repeatMode.collectAsState()
    val currentSongIndex by audioViewModel.currentSongIndex.collectAsState()
    val audioList by audioViewModel.audioList.collectAsState()
    val currentPosition by audioViewModel.currentPosition.collectAsState()
    val duration by audioViewModel.duration.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()



    // ✅ Ensure valid index before playing
    LaunchedEffect(index, audioList) {
        if (audioList.isNotEmpty() && index in audioList.indices) {
            audioViewModel.playSong(index)
        }
    }

    val progress = remember(currentPosition, duration) {
        if (duration > 0) currentPosition.toFloat() / duration.toFloat() else 0f
    }

    val currentSong = audioList.getOrNull(currentSongIndex) ?: return

    val musicImages = listOf(
        R.drawable.image_1, R.drawable.image_2, R.drawable.image_3,
        R.drawable.image_4, R.drawable.image_5, R.drawable.image_6,
        R.drawable.image_7, R.drawable.image_8
    )
    val imageRes = remember(currentSongIndex) { musicImages[currentSongIndex % musicImages.size] }

    Column(
        modifier = Modifier.fillMaxSize().padding(top = 36.dp, start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 🔹 Top Bar with Back Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // 🔹 Album Art
        Card(
            modifier = Modifier
                .size(350.dp)
                .padding(top = 80.dp, bottom = 15.dp, start = 16.dp, end = 16.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = "Album Art",
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 🔹 Title and Artist (Left-aligned)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = currentSong.title,
                style = MaterialTheme.typography.headlineSmall, // Bold Title
                modifier = Modifier.padding(bottom = 4.dp)
            )
            currentSong.artist?.let {
                Text(
                    text = it.ifEmpty { "Unknown Artist" },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 🔹 Seek Bar
        Slider(
            value = progress,
            onValueChange = { newValue ->
                val seekPosition = (newValue * duration).toLong()
                audioViewModel.seekTo(seekPosition)
            },
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 🔹 Music Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 🔂 Repeat Button (Toggles Repeat Mode)
            IconButton(
                onClick = {
                    val newModeText = audioViewModel.toggleRepeatMode() // ✅ This should be a String
                    scope.launch {
                        snackbarHostState.currentSnackbarData?.dismiss() // ✅ Dismiss any existing Snackbar
                        snackbarHostState.showSnackbar(message = newModeText) // ✅ Show new Snackbar instantly
                    }
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.loop_svgrepo_com),
                    contentDescription = "Repeat",
                    tint = when (repeatMode) {
                        AudioViewModel.RepeatMode.REPEAT_ONE, AudioViewModel.RepeatMode.REPEAT_ALL -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    }
                )
            }





            IconButton(onClick = { audioViewModel.playPrevious() }, enabled = currentSongIndex > 0) {
                Icon(painter = painterResource(id = R.drawable.previous_svgrepo_com), contentDescription = "Previous")
            }
            IconButton(onClick = { audioViewModel.togglePlayPause() }) {
                Icon(
                    painter = painterResource(if (isPlaying) R.drawable.pause_svgrepo_com else R.drawable.play_svgrepo_com),
                    contentDescription = if (isPlaying) "Pause" else "Play"
                )
            }
            IconButton(onClick = { audioViewModel.playNext() }, enabled = currentSongIndex < audioList.size - 1) {
                Icon(painter = painterResource(id = R.drawable.next_svgrepo_com__1_), contentDescription = "Next")
            }
        }

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            SnackbarHost(hostState = snackbarHostState)
        }
    }
}
