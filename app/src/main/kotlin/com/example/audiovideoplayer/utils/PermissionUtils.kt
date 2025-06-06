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
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    val permissionState = rememberPermissionState(permission)

    // Monitor permission status changes
    LaunchedEffect(permissionState.status) {
        when (permissionState.status) {
            is PermissionStatus.Granted -> onPermissionGranted(true)
            is PermissionStatus.Denied -> {
                // Notify denied only if user permanently denied (don't ask again) or just denied?
                // Here we just notify false to caller.
                onPermissionGranted(false)
            }
        }
    }

    // Show rationale dialog if needed
    if (permissionState.status is PermissionStatus.Denied && permissionState.status.shouldShowRationale) {
        PermissionRationaleDialog(
            onRequestPermission = { permissionState.launchPermissionRequest() },
            onDeny = { onPermissionGranted(false) }
        )
    } else if (!permissionState.status.isGranted) {
        // Request permission when no rationale and not granted yet
        SideEffect {
            permissionState.launchPermissionRequest()
        }
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
