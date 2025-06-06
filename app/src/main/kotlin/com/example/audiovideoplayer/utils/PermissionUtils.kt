package com.example.audiovideoplayer.utils

import android.Manifest
import android.os.Build
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.google.accompanist.permissions.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestAudioPermission(onPermissionGranted: (Boolean) -> Unit) {
    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        listOf(
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.READ_MEDIA_VIDEO
        )
    } else {
        listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    val permissionState = rememberMultiplePermissionsState(permissions = permissions)

    LaunchedEffect(permissionState.allPermissionsGranted) {
        if (permissionState.allPermissionsGranted) {
            onPermissionGranted(true)
        } else {
            permissionState.launchMultiplePermissionRequest()
        }
    }

    if (permissionState.shouldShowRationale) {
        PermissionRationaleDialog(
            onRequestPermission = { permissionState.launchMultiplePermissionRequest() },
            onDeny = { onPermissionGranted(false) }
        )
    }
}

@Composable
fun PermissionRationaleDialog(onRequestPermission: () -> Unit, onDeny: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDeny,
        title = { Text("Permission Required") },
        text = { Text("This app needs access to your media files to display and play them.") },
        confirmButton = {
            Button(onClick = onRequestPermission) {
                Text("Allow")
            }
        },
        dismissButton = {
            Button(onClick = onDeny) {
                Text("Deny")
            }
        }
    )
}
