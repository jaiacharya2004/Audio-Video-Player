package com.example.audiovideoplayer.ui.audio

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.audiovideoplayer.R
import com.example.audiovideoplayer.viewmodel.AudioViewModel
import kotlinx.coroutines.delay

@Composable
fun MiniPlayer(navController: NavController, audioViewModel: AudioViewModel, currentIndex: Int) {
    val songList by audioViewModel.audioList.collectAsState()
    val isPlaying by audioViewModel.isPlaying.collectAsState()
    val currentSong = songList.getOrNull(currentIndex) ?: return

    val musicImages = listOf(
        R.drawable.image_1, R.drawable.image_2, R.drawable.image_3,
        R.drawable.image_4, R.drawable.image_5, R.drawable.image_6,
        R.drawable.image_7, R.drawable.image_8
    )
    val imageRes = remember(currentIndex) { musicImages[currentIndex % musicImages.size] }

    val screenWidth = 300f // Approximate screen width
    val imageWidth = 56f   // Width of image + padding
    val textScrollWidth = screenWidth - imageWidth // Space for text to scroll
    var textOffset by remember { mutableFloatStateOf(textScrollWidth) }

    // 🔄 Auto-scroll effect (text moves smoothly)
    LaunchedEffect(currentSong.title) {
        while (true) {
            withFrameMillis {
                textOffset -= 4f
                if (textOffset < -textScrollWidth) {
                    textOffset = textScrollWidth // Reset text position
                }
            }
        }
    }

    Surface(
        color = Color.DarkGray,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (currentIndex in songList.indices) {
                    val encodedPath = Uri.encode(currentSong.path)

                    // Check if the same song is playing before calling playSong()
                    if (audioViewModel.currentSongIndex.value != currentIndex) {
                        audioViewModel.playSong(currentIndex)
                    }

                    navController.navigate("music_player/$currentIndex/$encodedPath")
                }
            }
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ✅ Song Image (Fixed Position)
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = "Song Image",
                modifier = Modifier
                    .size(48.dp)
                    .padding(end = 8.dp)
            )

            // ✅ Scrolling Song Title (Only hides when inside image, keeps scrolling)
            Box(
                modifier = Modifier
                    .weight(1f) // Takes the available space
                    .height(24.dp)
                    .clipToBounds() // Ensures text outside bounds is hidden
            ) {
                Text(
                    text = currentSong.title,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Clip,
                    modifier = Modifier
                        .offset(x = textOffset.dp)
                        .graphicsLayer {
                            val fadeStart = 10f   // Start fading out near the image
                            val fadeEnd = imageWidth + 10f // Fully invisible at image width

                            // Text fades out smoothly only when entering image area
                            alpha = if (textOffset < fadeEnd && textOffset > fadeStart) {
                                (textOffset - fadeStart) / (fadeEnd - fadeStart)
                            } else if (textOffset <= fadeStart) {
                                0f // Fully hide inside image
                            } else {
                                1f // Fully visible otherwise
                            }
                        }
                )
            }

            // ✅ Play/Pause Button (Fixed Position)
            IconButton(
                onClick = { audioViewModel.togglePlayPause() }
            ) {
                Icon(
                    painter = painterResource(if (isPlaying) R.drawable.pause_svgrepo_com else R.drawable.play_svgrepo_com),
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = Color.White // 🔥 Set icon color to white
                )
            }
        }
    }
}
