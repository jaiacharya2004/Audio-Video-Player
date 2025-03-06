package com.example.audiovideoplayer.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.audiovideoplayer.ui.audio.AudioScreen
import com.example.audiovideoplayer.ui.audio.MusicPlayerScreen
import com.example.audiovideoplayer.ui.downloads.DownloadScreen
import com.example.audiovideoplayer.ui.settings.SettingScreen
import com.example.audiovideoplayer.ui.video.VideoScreen
import com.example.audiovideoplayer.ui.web.WebScreen
import com.example.audiovideoplayer.viewmodel.AudioViewModel

@Composable
fun NavGraph(navController: NavHostController, audioViewModel: AudioViewModel) {
    NavHost(
        navController = navController,
        startDestination = "audio"
    ) {
        composable("audio") { AudioScreen(navController, audioViewModel) }
        composable("video") { VideoScreen(navController) }
        composable("download") { DownloadScreen(navController) }
        composable("web") { WebScreen(navController) }
        composable("settings") { SettingScreen(navController) }

        composable(
            "music_player/{index}/{encodedSongPath}",
            arguments = listOf(
                navArgument("index") { type = NavType.IntType },
                navArgument("encodedSongPath") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0
            val encodedPath = backStackEntry.arguments?.getString("encodedSongPath") ?: ""
            val songPath = Uri.decode(encodedPath)

            MusicPlayerScreen(navController, audioViewModel, index  )
        }
    }
}
