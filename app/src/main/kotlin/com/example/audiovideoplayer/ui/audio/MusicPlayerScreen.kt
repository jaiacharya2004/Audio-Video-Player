package com.example.audiovideoplayer.ui.audio

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.audiovideoplayer.R
import com.example.audiovideoplayer.viewmodel.AudioViewModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


@OptIn(ExperimentalMaterial3Api::class)
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
    val swipeOffset = remember { Animatable(0f) }






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
                .padding(top = 16.dp)
                .offset { IntOffset(swipeOffset.value.roundToInt(), 0) } // 🔹 Smooth real-time movement
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            val threshold = 150f // 🔹 More natural swipe threshold
                            val isNext = swipeOffset.value < -threshold
                            val isPrevious = swipeOffset.value > threshold

                            scope.launch {
                                if (isNext || isPrevious) {
                                    val targetOffset = if (isNext) -200f else 200f

                                    swipeOffset.animateTo(targetOffset, tween(200, easing = FastOutSlowInEasing)) // 🔹 Smooth swipe
                                    audioViewModel.playNextOrPrevious(isNext) // ✅ Change song
                                    swipeOffset.animateTo(0f, tween(200, easing = FastOutSlowInEasing)) // 🔹 Natural reset
                                } else {
                                    swipeOffset.animateTo(0f, tween(200, easing = FastOutSlowInEasing)) // Snap back gently if not enough swipe
                                }
                            }
                        }
                    ) { _, dragAmount ->
                        scope.launch {
                            val newOffset = swipeOffset.value + dragAmount * 0.5f // 🔹 Reduce sensitivity (feels smoother)
                            swipeOffset.snapTo(newOffset.coerceIn(-300f, 300f)) // 🔹 Limit movement
                        }
                    }
                },
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = "Album Art",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(300.dp)
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



            val primaryColor = MaterialTheme.colorScheme.primary


            Slider(
                value = progress,
                onValueChange = { newValue ->
                    val seekPosition = (newValue * duration).toLong()
                    audioViewModel.seekTo(seekPosition)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 0.dp),
                colors = SliderColors(
                    activeTrackColor = Color(0xFF6200EE),
                    thumbColor = Color.White,
                    inactiveTickColor = Color.Gray,
                    disabledThumbColor = Color.DarkGray,
                    disabledActiveTrackColor = Color.Gray,
                    disabledInactiveTrackColor = Color.White,
                    disabledInactiveTickColor = Color.Red,
                    disabledActiveTickColor = Color.Yellow,
                    activeTickColor = Color.Blue,
                    inactiveTrackColor = Color.LightGray
                ),
                thumb = {
                    androidx.compose.foundation.Canvas(
                        modifier = Modifier
                            .size(14.dp) // Smaller circular thumb
                        // Smaller circular thumb

                    ) {
                        drawCircle(color = primaryColor)
                    }
                },
                track = { _ ->
                    androidx.compose.foundation.Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .clip(RoundedCornerShape(5.dp)), // Adjust thickness of the track
                    ) {
                        val width = size.width
                        val height = size.height / 2
                        drawLine(
                            color = primaryColor,
                            start = androidx.compose.ui.geometry.Offset(0f, height),
                            end = androidx.compose.ui.geometry.Offset(progress * width, height),
                            strokeWidth = size.height // Active part of the line
                        )
                        drawLine(
                            color = Color.Gray,
                            start = androidx.compose.ui.geometry.Offset(progress * width, height),
                            end = androidx.compose.ui.geometry.Offset(width, height),
                            strokeWidth = size.height // Inactive part of the line
                        )
                    }
                }
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

