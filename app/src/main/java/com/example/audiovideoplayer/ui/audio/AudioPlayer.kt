package com.example.audiovideoplayer.ui.audio

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.audiovideoplayer.ui.components.BottomNavigationBar
import com.example.audiovideoplayer.ui.components.TopAppBarComponent
import com.example.audiovideoplayer.viewmodel.AudioViewModel

@Composable
fun AudioScreen(navController: NavController, audioViewModel: AudioViewModel) {
    val audioList by audioViewModel.audioList.collectAsState()
    val context = LocalContext.current

    var fileToDelete by remember { mutableStateOf<AudioModel?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        audioViewModel.loadAudioList()
    }

    Scaffold(
        topBar = { TopAppBarComponent(navController, title = "Audio") },
        bottomBar = { BottomNavigationBar(navController, currentRoute = "audio") }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background((Color(0xFF1C1C1C)))
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

    // Delete Confirmation Dialog
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
