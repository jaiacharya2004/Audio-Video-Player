package com.example.audiovideoplayer.ui.web

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.audiovideoplayer.ui.components.BottomNavigationBar
import com.example.audiovideoplayer.ui.components.TopAppBarComponent

@Composable
fun WebScreen(navController: NavController) {
    Scaffold(
        topBar = { TopAppBarComponent(navController, title = "Web") },
        bottomBar = { BottomNavigationBar(navController, currentRoute = "web") }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Hello Web", fontSize = 24.sp)
        }
    }

}
