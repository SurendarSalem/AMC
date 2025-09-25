package com.amc.amcapp.util.image

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.amc.amcapp.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import androidx.exifinterface.media.ExifInterface
import com.amc.amcapp.ui.theme.LocalDimens
import com.amc.amcapp.util.ImageUtils.decodeAndCompressImage
import getTempImageUri

@Composable
fun AppImagePicker(
    modifier: Modifier = Modifier,
    imageUri: Uri?,
    imageUrl: String?,
    onImageReturned: (Uri) -> Unit,
    onPermissionDenied: (permission: String) -> Unit,
    maxWidth: Int = 512,
    maxHeight: Int = 512,
    contentScale: ContentScale = ContentScale.None,
    quality: Int = 70,
    shouldUseUrl: Boolean = false,
    shouldUseUri: Boolean = false
) {
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Helper: compress, fix rotation, return Uri, delete temp files
    fun handleCompressedImage(uri: Uri, originalTempFile: File? = null) {
        val compressed = decodeAndCompressImage(context, uri, maxWidth, maxHeight, quality)
        compressed?.let { fileUri ->
            onImageReturned(fileUri)
            scope.launch {
                delay(500) // short delay to allow usage
                File(fileUri.path!!).delete()
                originalTempFile?.delete()
            }
        }
    }

    // Gallery picker
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { handleCompressedImage(it) }
    }

    // Camera picker
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            tempCameraUri?.let { originalUri ->
                val tempFile = File(originalUri.path!!)
                handleCompressedImage(originalUri, tempFile)
            }
        }
    }

    // Camera permission
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val uri = getTempImageUri(context)
            tempCameraUri = uri
            cameraLauncher.launch(uri)
        } else {
            onPermissionDenied(Manifest.permission.CAMERA)
        }
    }

    // Gallery permission
    val galleryPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (isGranted) {
            galleryLauncher.launch("image/*")
        } else {
            onPermissionDenied(permission)
        }
    }

    Column(modifier = modifier) {
        AsyncImage(
            model = ImageRequest.Builder(context).data(
                if (shouldUseUri) {
                    imageUri
                } else if (shouldUseUrl) {
                    imageUrl
                } else R.drawable.error_placeholder
            ).error(R.drawable.error_placeholder).crossfade(true)
                .size(512, 512) // downsample for display
                .build(),
            modifier = Modifier.fillMaxWidth(),
            contentDescription = "Uploaded or selected image",
            contentScale = contentScale
        )
        Spacer(modifier = Modifier.height(LocalDimens.current.spacingMedium.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center // Center all children horizontally
        ) {
            Button(onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) }) {
                Text("Open Camera")
            }

            Spacer(modifier = Modifier.width(LocalDimens.current.spacingMedium.dp)) // spacing between buttons

            Button(onClick = {
                val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    Manifest.permission.READ_MEDIA_IMAGES
                } else {
                    Manifest.permission.READ_EXTERNAL_STORAGE
                }
                galleryPermissionLauncher.launch(permission)
            }) {
                Text("Pick Photo")
            }
        }

    }
}
