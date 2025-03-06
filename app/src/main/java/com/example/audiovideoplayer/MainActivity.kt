package com.example.audiovideoplayer


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.audiovideoplayer.ui.components.BottomNavigationBar
import com.example.audiovideoplayer.ui.navigation.NavGraph
import com.example.audiovideoplayer.viewmodel.AudioViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val audioViewModel: AudioViewModel = viewModel()

            NavGraph(navController = navController, audioViewModel = audioViewModel )
        }
    }
}