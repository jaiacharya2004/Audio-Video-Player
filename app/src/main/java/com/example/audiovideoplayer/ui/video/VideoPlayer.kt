package com.example.audiovideoplayer.ui.video



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
fun VideoScreen(navController: NavController) {
    Scaffold(
        topBar = { TopAppBarComponent(navController, title = "Video") },
        bottomBar = { BottomNavigationBar(navController, currentRoute = "video") }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Hello Video", fontSize = 24.sp)
        }
    }

}
