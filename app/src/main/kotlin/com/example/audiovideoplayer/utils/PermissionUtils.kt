package com.example.audiovideoplayer.utils

import android.Manifest
import android.os.Build
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.google.accompanist.permissions.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestAudioPermission(onPermissionGranted: (Boolean) -> Unit) {
    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_AUDIO
        Manifest.permission.READ_EXTERNAL_STORAGE
        Manifest.permission.READ_MEDIA_VIDEO

    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    val permissionState = rememberPermissionState(permission = permission)

    LaunchedEffect(permissionState.status) {
        if (permissionState.status.isGranted) {
            onPermissionGranted(true)
        } else {
            permissionState.launchPermissionRequest()
        }
    }

    if (permissionState.status.shouldShowRationale) {
        AlertDialog(
            onDismissRequest = { onPermissionGranted(false) },
            title = { Text("Permission Required") },
            text = { Text("We need access to your media files to show audio.") },
            confirmButton = {
                TextButton(onClick = { permissionState.launchPermissionRequest() }) {
                    Text("Grant")
                }
            },
            dismissButton = {
                TextButton(onClick = { onPermissionGranted(false) }) {
                    Text("Deny")
                }
            }
        )
    }
}

@Composable
fun PermissionRationaleDialog(onRequestPermission: () -> Unit, onDeny: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDeny,
        title = { Text("Permission Required") },
        text = { Text("This app needs access to your audio files to display them.") },
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
