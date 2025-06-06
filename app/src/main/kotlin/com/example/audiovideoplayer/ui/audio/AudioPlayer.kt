package com.example.audiovideoplayer.ui.audio

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.audiovideoplayer.ui.components.BottomNavigationBar
import com.example.audiovideoplayer.ui.components.TopAppBarComponent
import com.example.audiovideoplayer.utils.RequestAudioPermission
import com.example.audiovideoplayer.viewmodel.AudioViewModel

@Composable
fun AudioScreen(navController: NavController, audioViewModel: AudioViewModel) {
    val audioList by audioViewModel.audioList.collectAsState()
    val isPlaying by audioViewModel.isPlaying.collectAsState()
    val currentSongIndex by audioViewModel.currentSongIndex.collectAsState()
    val context = LocalContext.current

    var hasPermission by remember { mutableStateOf(false) }

    // These are needed for delete dialog control
    var fileToDelete by remember { mutableStateOf<AudioModel?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    // Request audio permission, update hasPermission
    RequestAudioPermission { granted ->
        hasPermission = granted
    }

    // Load audio only after permission granted
    LaunchedEffect(hasPermission) {
        if (hasPermission) {
            audioViewModel.loadAudioList()
        }
    }

    if (!hasPermission) {
        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color.White)
        }
        return
    }

    Scaffold(
        topBar = { TopAppBarComponent(navController, title = "Audio") },
        bottomBar = {
            Column {
                if (currentSongIndex in audioList.indices) {
                    MiniPlayer(navController, audioViewModel, currentSongIndex)
                }
                BottomNavigationBar(navController, currentRoute = "audio")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1C1C1C))
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(text = "Music List", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)

            if (audioList.isEmpty()) {
                Text("No audio files found.", color = Color.White)
            } else {
                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn {
                    items(audioList) { audio ->
                        AudioItem(
                            audio = audio,
                            navController = navController,
                            audioList = audioList,
                            onDeleteAudio = {
                                fileToDelete = it
                                showDialog = true
                            }
                        )
                    }
                }
            }
        }
    }

    if (showDialog && fileToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Delete Audio") },
            text = { Text("Are you sure you want to delete '${fileToDelete?.title}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        fileToDelete?.let { file ->
                            audioViewModel.deleteAudioFile(
                                context,
                                file.path,
                                onSuccess = {
                                    Toast.makeText(context, "File deleted", Toast.LENGTH_SHORT).show()
                                    audioViewModel.loadAudioList()
                                },
                                onError = { errorMsg ->
                                    Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                        showDialog = false
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

