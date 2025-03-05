package com.example.audiovideoplayer.ui.navigation


import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.audiovideoplayer.ui.audio.AudioScreen
import com.example.audiovideoplayer.ui.video.VideoScreen
import com.example.audiovideoplayer.ui.downloads.DownloadScreen
import com.example.audiovideoplayer.ui.web.WebScreen
import com.example.audiovideoplayer.ui.settings.SettingScreen
import com.example.audiovideoplayer.viewmodel.AudioViewModel

@Composable
fun NavGraph(navController: NavHostController, modifier: Modifier = Modifier) {

    NavHost(
        navController = navController,
        startDestination = "audio",
        modifier = modifier
    ) {
        composable("audio") { AudioScreen(navController) }
        composable("video") { VideoScreen(navController) }
        composable("download") { DownloadScreen(navController) }
        composable("web") { WebScreen(navController) }
        composable("settings") { SettingScreen(navController) }
    }
}
