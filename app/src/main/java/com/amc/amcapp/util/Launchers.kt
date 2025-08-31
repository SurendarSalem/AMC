package com.amc.amcapp.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import com.amc.amcapp.util.ImageUtils.loadBitmapFromFile
import com.amc.amcapp.util.ImageUtils.loadBitmapFromUri
import com.amc.amcapp.util.ImageUtils.resizeBitmap
import java.io.File

@Composable
fun requestPermissionLauncher(onPermissionGranted: (granted: Boolean) -> Unit): ManagedActivityResultLauncher<String, Boolean> {
    return rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        onPermissionGranted(granted)
    }
}

@Composable
fun cameraLauncher(
    cameraImagePath: String?, onImageCaptured: (bitmap: Bitmap) -> Unit
): ManagedActivityResultLauncher<Uri, Boolean> {
    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && cameraImagePath != null) {
            val file = File(cameraImagePath)
            val fixedBitmap = loadBitmapFromFile(file)
            val resizedBitmap = resizeBitmap(fixedBitmap, 300, 300)
            onImageCaptured(resizedBitmap)
            file.delete()
        }
    }
}


@Composable
fun galleryLauncher(
    context: Context,
    onImageSelected: (bitmap: Bitmap) -> Unit
): ManagedActivityResultLauncher<String, Uri?> {
    return rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val fixedBitmap = loadBitmapFromUri(context, it)
            val resizedBitmap = resizeBitmap(fixedBitmap, 300, 300)
            onImageSelected(resizedBitmap)
        }
    }
}

fun openAppSettings(context: Context) {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", context.packageName, null)
    )
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}




