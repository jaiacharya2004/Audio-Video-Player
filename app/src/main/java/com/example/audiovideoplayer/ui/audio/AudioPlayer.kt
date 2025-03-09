package com.example.audiovideoplayer.ui.audio


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
                Text("No audio files found.")
            } else {
                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn {
                    items(audioList) { audio ->
                        AudioItem(audio, navController, audioList)
                    }
                }
            }
        }
    }
}

