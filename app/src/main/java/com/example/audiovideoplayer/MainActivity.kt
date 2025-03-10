package com.example.audiovideoplayer


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.audiovideoplayer.ui.components.BottomNavigationBar
import com.example.audiovideoplayer.ui.navigation.NavGraph
import com.example.audiovideoplayer.viewmodel.AudioViewModel

class MainActivity : ComponentActivity() {


    companion object {
        const val REQUEST_DELETE_PERMISSION = 1001
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val audioViewModel: AudioViewModel = viewModel()

            NavGraph(navController = navController, audioViewModel = audioViewModel )
        }
    }


    @Deprecated("Use Activity Result API instead")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_DELETE_PERMISSION && resultCode == Activity.RESULT_OK) {
            Log.d("MainActivity", "User granted permission to delete the file.")
        } else {
            Log.e("MainActivity", "File deletion permission denied by user.")
        }
    }

}