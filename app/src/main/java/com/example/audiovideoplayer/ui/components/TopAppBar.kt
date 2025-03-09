package com.example.audiovideoplayer.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.audiovideoplayer.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarComponent(navController: NavController, title: String) {
    CenterAlignedTopAppBar(
        title = {
            Text(text = title, color = Color.White)
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color(0xFF121212), // Dark Gray background
            titleContentColor = Color.White, // White text color
            actionIconContentColor = Color(0xFFB0B0B0), // Light Gray for icons
            navigationIconContentColor = Color.White // White for logo
        ),
        navigationIcon = {
            val appLogo: Painter = painterResource(id = R.drawable.ic_launcher_foreground) // Replace with your actual logo
            Image(
                painter = appLogo,
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(40.dp)
                    .padding(8.dp)
            )
        },
        actions = {
            IconButton(onClick = { /* Handle search action */ }) {
                Icon(
                    Icons.Filled.Search,
                    contentDescription = "Search",
                    tint = Color.White // Ensures clear visibility
                )
            }
            IconButton(onClick = { navController.navigate("settings") }) {
                Icon(
                    Icons.Filled.Settings,
                    contentDescription = "Settings",
                    tint = Color.White
                )
            }
        }
    )
}
