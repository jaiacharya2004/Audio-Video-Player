package com.example.audiovideoplayer.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import com.example.audiovideoplayer.R

// Data class for Bottom Navigation items
data class BottomNavItem(val route: String, val iconRes: Int, val label: String)

@Composable
fun BottomNavigationBar(navController: NavController, currentRoute: String, modifier: Modifier = Modifier) {
    val items = listOf(
        BottomNavItem("audio", R.drawable.headphone_svgrepo_com, "Audio"),
        BottomNavItem("video", R.drawable.video_player_play_button_svgrepo_com, "Video"),
        BottomNavItem("download", R.drawable.download_svgrepo_com, "Downloads"),
        BottomNavItem("web", R.drawable.web_round_svgrepo_com, "Web")
    )

    NavigationBar(
        modifier = modifier.fillMaxWidth().padding(bottom = 0.dp), // Ensures it's at the bottom
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(top = 6.dp) // Added top padding for spacing
                    ) {
                        Image(
                            painter = painterResource(id = item.iconRes),
                            contentDescription = item.label,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item.label,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                },
                label = { Text("") }, // Hide default label
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route)
                    }
                }
            )
        }
    }
}