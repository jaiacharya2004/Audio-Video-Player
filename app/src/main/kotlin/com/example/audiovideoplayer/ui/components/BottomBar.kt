package com.example.audiovideoplayer.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
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
        modifier = modifier.fillMaxWidth().padding(bottom = 0.dp),
        containerColor = Color.Black // Set bottom bar background to black
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route
            val iconTint = if (isSelected) Color.White else Color.Gray // White when selected, Gray when not
            val textColor = if (isSelected) Color.White else Color.Gray

            NavigationBarItem(
                icon = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(top = 6.dp)
                    ) {
                        Image(
                            painter = painterResource(id = item.iconRes),
                            contentDescription = item.label,
                            modifier = Modifier.size(24.dp),
                            colorFilter = ColorFilter.tint(iconTint) // Apply tint color to the icon
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item.label,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            color = textColor // Apply text color based on selection
                        )
                    }
                },
                label = { Text("") }, // Hide default label
                selected = isSelected,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White, // Ensures selected icon is white
                    unselectedIconColor = Color.Gray, // Ensures unselected icon is gray
                    indicatorColor = Color.Transparent // Removes highlight effect
                ),
                onClick = {
                    if (!isSelected) {
                        navController.navigate(item.route)
                    }
                }
            )
        }
    }
}
