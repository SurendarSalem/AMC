package com.amc.amcapp.util

import android.Manifest
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Photo
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.amc.amcapp.R
import com.amc.amcapp.model.Actions
import com.amc.amcapp.model.NotifyState
import com.amc.amcapp.util.ImageUtils.loadBitmapFromUrl
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun AppImagePicker(
    imageUrl: String = "", onImageReturned: (
        bitmap: Bitmap?
    ) -> Unit, onErrorReturned: (error: NotifyState) -> Unit, isEditEnabled: Boolean = false
) {

    val context = LocalContext.current
    var cameraImageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var previewBitmap by rememberSaveable { mutableStateOf<Bitmap?>(null) }
    var cameraImagePath by rememberSaveable { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(imageUrl) {
        if (imageUrl.isNotEmpty()) {
            scope.launch {
                previewBitmap = loadBitmapFromUrl(context, imageUrl)
                onImageReturned(previewBitmap)
            }
        }

    }

    val requestCameraPermissionLauncher = requestPermissionLauncher { granted ->
        if (!granted) onErrorReturned(
            NotifyState.ShowToast(
                "Camera permission denied", actions = Actions.OPEN_PERMISSION
            )
        )
    }

    val requestReadGalleryPermissionLauncher = requestPermissionLauncher { granted ->
        if (!granted) onErrorReturned(
            NotifyState.ShowToast(
                "Gallery permission denied", actions = Actions.OPEN_PERMISSION
            )
        )
    }

    val cameraLauncher = cameraLauncher(cameraImagePath) { bitmap ->
        previewBitmap = bitmap
        onImageReturned(previewBitmap)
        cameraImagePath = null
    }

    val galleryLauncher = galleryLauncher(context) { bitmap ->
        previewBitmap = bitmap
        onImageReturned(previewBitmap)
    }


    val cameraClicked: () -> Unit = {
        if (!PermissionHelper().hasCameraPermission(context)) {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        } else {
            val file = File.createTempFile("camera_image", ".jpg", context.cacheDir)
            cameraImagePath = file.absolutePath
            cameraImageUri = FileProvider.getUriForFile(
                context, "${context.packageName}.fileprovider", file
            )
            cameraImageUri?.let {
                cameraLauncher.launch(it)
            }
        }
    }


    val onGalleryClicked: () -> Unit = {
        if (!PermissionHelper().hasReadStoragePermission(context)) {
            requestReadGalleryPermissionLauncher.launch(PermissionHelper().readGalleryPermission)
        } else {
            galleryLauncher.launch("image/*")
        }
    }

    Box(
        modifier = Modifier
            .border(
                1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp)
            )
            .clip(RoundedCornerShape(12.dp))
            .aspectRatio(1.5f)
    ) {
        if (previewBitmap != null) {
            // Directly render picked bitmap (no Coil, no flicker)
            Image(
                bitmap = previewBitmap!!.asImageBitmap(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            // Fallback: load from URL if provided
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(imageUrl).crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                error = painterResource(id = R.drawable.error_placeholder),
                placeholder = painterResource(id = R.drawable.error_placeholder),
                modifier = Modifier.fillMaxWidth()
            )
        }
        if (isEditEnabled) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    imageVector = Icons.Rounded.CameraAlt,
                    contentDescription = "Camera Icon",
                    modifier = Modifier
                        .size(48.dp)
                        .alpha(0.5f)
                        .align(Alignment.BottomStart)
                        .background(
                            MaterialTheme.colorScheme.primary,
                            shape = MaterialTheme.shapes.extraSmall
                        )
                        .padding(8.dp)
                        .clickable {
                            cameraClicked()
                        },
                    tint = Color.White
                )

                Icon(
                    imageVector = Icons.Rounded.Photo,
                    contentDescription = "Camera Icon",
                    modifier = Modifier
                        .size(48.dp)
                        .alpha(0.5f)
                        .align(Alignment.BottomEnd)
                        .background(
                            MaterialTheme.colorScheme.primary,
                            shape = MaterialTheme.shapes.extraSmall
                        )
                        .padding(8.dp)
                        .clickable {
                            onGalleryClicked()
                        },
                    tint = Color.White,
                )

                previewBitmap?.let {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = "Camera Icon",
                        modifier = Modifier
                            .size(48.dp)
                            .alpha(0.5f)
                            .align(Alignment.TopEnd)
                            .background(
                                MaterialTheme.colorScheme.primary,
                                shape = MaterialTheme.shapes.extraSmall
                            )
                            .padding(8.dp)
                            .clickable {
                                previewBitmap = null
                            },
                        tint = Color.White,
                    )
                }
            }
        }
    }
}