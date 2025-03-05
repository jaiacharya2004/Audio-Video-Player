package com.example.audiovideoplayer.ui.audio

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.audiovideoplayer.R
import com.example.audiovideoplayer.ui.components.BottomNavigationBar
import com.example.audiovideoplayer.ui.components.TopAppBarComponent
import com.example.audiovideoplayer.viewmodel.AudioViewModel
import android.util.Log
import androidx.compose.foundation.background

@Composable
fun AudioScreen(navController: NavController) {
    val viewModel: AudioViewModel = viewModel()
    val audioList by viewModel.audioList

    LaunchedEffect(Unit) {
        viewModel.loadAudio()
        Log.d("AudioScreen", "Audio List after loadAudio: ${audioList.size}")
    }

    Scaffold(
        topBar = { TopAppBarComponent(navController, title = "Audio") },
        bottomBar = { BottomNavigationBar(navController, currentRoute = "audio") }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(text = "Music List", fontSize = 20.sp, fontWeight = FontWeight.Bold)

            Log.d("AudioScreen", "Audio List before LazyColumn: ${audioList.size}")

            if (audioList.isEmpty()) {
                Text("No audio files found.")
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f) // Let LazyColumn take up remaining space
                ) {
                    items(audioList) { audio ->
                        Log.d("AudioScreen", "Audio Item: ${audio.title}")
                        AudioItem(audio)
                    }
                }
            }
        }
    }
}

@Composable
fun AudioItem(audio: AudioModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { /* Handle Click - Play Music */ },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.music_svgrepo_com__1_),
            contentDescription = "Music Icon",
            modifier = Modifier.size(40.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column {
            Text(text = audio.title, fontWeight = FontWeight.Bold)
            Text(text = audio.artist ?: "Unknown Artist", fontSize = 12.sp, color = Color.Gray)
        }
    }
}