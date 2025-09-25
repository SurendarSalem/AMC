package com.amc.amcapp.util.image

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionRequest(
    permission: String,
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit
) {
    val permissionState = rememberPermissionState(permission)
    LaunchedEffect(permissionState) {
        if (permissionState.status.isGranted) {
            onPermissionGranted()
        } else {
            permissionState.launchPermissionRequest()
        }
    }
}