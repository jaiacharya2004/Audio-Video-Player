package com.example.audiovideoplayer.ui.audio

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 🔹 Top Bar with Back Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back",tint = Color.White )
            }
        }

        Spacer(modifier = Modifier.height(58.dp))

        // 🔹 Album Art
        Card(
            modifier = Modifier
                .size(300.dp)
                .padding(top = 16.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = "Album Art",
                contentScale = ContentScale.Crop, // Ensures image fills the card while maintaining aspect ratio
                modifier = Modifier.size(300.dp) // Keeps image size fixed
            )
        }


        Spacer(modifier = Modifier.height(24.dp))

        // 🔹 Title and Artist (Center-aligned)
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = if (currentSong.title.length > 20) currentSong.title.take(20) + "..." else currentSong.title, color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            currentSong.artist?.let {
                Text(
                    text = it.ifEmpty { "Unknown Artist" } ,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
//                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 🔹 Seek Bar with Time Labels
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = formatTime(currentPosition), style = MaterialTheme.typography.bodySmall, color = Color.White)
                Text(text = formatTime(duration), style = MaterialTheme.typography.bodySmall, color = Color.White)
            }

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
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 🔹 Music Controls (Aligned Center)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 🔂 Repeat Button
            IconButton(
                onClick = {
                    val newModeText = audioViewModel.toggleRepeatMode()
                    scope.launch {
                        snackbarHostState.currentSnackbarData?.dismiss()
                        snackbarHostState.showSnackbar(message = newModeText)
                    }
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.loop_svgrepo_com),
                    contentDescription = "Repeat",
                    tint = when (repeatMode) {
                        AudioViewModel.RepeatMode.REPEAT_ONE, AudioViewModel.RepeatMode.REPEAT_ALL -> MaterialTheme.colorScheme.primary
                        else ->
                            Color.White
                    }
                )
            }

            // ⏪ Previous Button
            IconButton(
                onClick = { audioViewModel.playPrevious() },
                enabled = currentSongIndex > 0,
                colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.previous_svgrepo_com),
                    contentDescription = "Previous"
                )
            }

            // ▶️ Play/Pause Button
            IconButton(
                onClick = { audioViewModel.togglePlayPause() },
                colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
            ) {
                Icon(
                    painter = painterResource(if (isPlaying) R.drawable.pause_svgrepo_com else R.drawable.play_svgrepo_com),
                    contentDescription = if (isPlaying) "Pause" else "Play"
                )
            }

            // ⏩ Next Button
            IconButton(
                onClick = { audioViewModel.playNext() },
                enabled = currentSongIndex < audioList.size - 1,
                colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.next_svgrepo_com__1_),
                    contentDescription = "Next"
                )
            }
        }


        Spacer(modifier = Modifier.height(16.dp))

        // 🔹 Snackbar Host (For Repeat Mode Messages)
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            SnackbarHost(hostState = snackbarHostState)
        }
    }
}

// 🔹 Convert milliseconds to "mm:ss" format
fun formatTime(ms: Long): String {
    val minutes = (ms / 1000) / 60
    val seconds = (ms / 1000) % 60
    return "%02d:%02d".format(minutes, seconds)
}
