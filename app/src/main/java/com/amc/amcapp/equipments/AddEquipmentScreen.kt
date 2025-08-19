// File: ComposeImageCaptureUpload.kt
package com.amc.amcapp.equipments

import android.Manifest
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import com.amc.amcapp.ui.AppTextField
import com.amc.amcapp.ui.ImagePicker
import com.amc.amcapp.util.ImageUtils
import com.amc.amcapp.util.ImageUtils.loadBitmapFromUri
import com.amc.amcapp.util.ImageUtils.resizeBitmap
import com.amc.amcapp.util.PermissionHelper
import com.amc.amcapp.util.cameraLauncher
import com.amc.amcapp.util.galleryLauncher
import com.amc.amcapp.util.requestPermissionLauncher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.compose.viewmodel.koinViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEquipmentScreen(
    navController: NavHostController,
    modifier: Modifier,
    addEquipmentViewModel: AddEquipmentViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    val complaints = addEquipmentViewModel.complaintsState.collectAsState()

    val equipment = addEquipmentViewModel.equipmentState.collectAsState()
    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }
    var previewBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var cameraImagePath by rememberSaveable { mutableStateOf<String?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val newComplaint = addEquipmentViewModel.newComplaint.collectAsState()

    val requestCameraPermissionLauncher = requestPermissionLauncher { granted ->
        if (!granted) errorMessage = "Camera permission denied"
    }

    val requestReadGalleryPermissionLauncher = requestPermissionLauncher { granted ->
        if (!granted) errorMessage = "Gallery permission denied"
    }

    val cameraLauncher = cameraLauncher(cameraImagePath) { bitmap ->
        previewBitmap = bitmap
        cameraImagePath = null
    }

    val galleryLauncher = galleryLauncher(context) { bitmap ->
        previewBitmap = bitmap
    }

    LaunchedEffect(Unit) {
        addEquipmentViewModel.getAllComplaints()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ImagePicker(bitmap = previewBitmap, imageUrl = null, onCameraClick = {
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
        }, onGalleryClick = {
            if (!PermissionHelper().hasReadStoragePermission(context)) {
                requestReadGalleryPermissionLauncher.launch(PermissionHelper().readGalleryPermission)
            } else {
                galleryLauncher.launch("image/*")
            }
        }, onDeleteClick = { previewBitmap = null })

        previewBitmap?.let {

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = {
                    if (isUploading) return@Button
                    isUploading = true
                    errorMessage = null
                    previewBitmap?.let {
                        scope.launch {
                            try {
                                val bytes = withContext(Dispatchers.Default) {
                                    ImageUtils.bitmapToByteArray(it, quality = 85)
                                }

                                val downloadUrl = addEquipmentViewModel.uploadBytesToFirebase(bytes)

                                errorMessage = "Uploaded successfully"
                                scope.launch {
                                    snackBarHostState.showSnackbar("Uploaded successfully: $downloadUrl")
                                }

                            } catch (e: Exception) {
                                e.printStackTrace()
                                errorMessage = "Upload failed: ${e.message}"
                                scope.launch {
                                    snackBarHostState.showSnackbar("Upload failed: ${e.message}")
                                }

                            } finally {
                                isUploading = false
                            }
                        }
                    }
                }) {
                    Text(if (isUploading) "Uploading..." else "Resize & Upload")
                }

                Button(onClick = { previewBitmap = null }) {
                    Text("Clear")
                }
            }
        }

        errorMessage?.let {
            Text(it, style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(modifier = Modifier.height(16.dp))

        AppTextField(
            value = equipment.value.name,
            onValueChange = { addEquipmentViewModel.onNameChange(it) },
            label = "Equipment Name",
            minLines = 1
        )

        Spacer(modifier = Modifier.height(8.dp))
        AppTextField(
            value = equipment.value.description,
            onValueChange = { addEquipmentViewModel.onDescriptionChange(it) },
            label = "Description",
            minLines = 3
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Complaints", modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(16.dp))
        repeat(complaints.value.size) {
            val complaint = complaints.value[it]
            Text(
                complaint.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.primary, MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.small)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .clickable {
                        addEquipmentViewModel.onComplaintRemoved(complaint)
                    })
            Spacer(
                modifier = Modifier.height(16.dp)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextField(
                value = newComplaint.value.name,
                onValueChange = { addEquipmentViewModel.onComplaintUpdated(it) },
                modifier = Modifier.weight(1f),
                singleLine = true,
                label = { Text("Complaint Name") })

            Button(
                onClick = {
                    scope.launch {
                        if (newComplaint.value.name.isNotBlank()) {
                            addEquipmentViewModel.onComplaintAdded()
                        } else {
                            snackBarHostState.showSnackbar("Please enter a complaint name")
                        }
                    }
                }, modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Text("Add Complaint")
            }
        }
        Spacer(
            modifier = Modifier.height(8.dp)
        )
        Button(
            onClick = {
                scope.launch {
                    if (equipment.value.name.isBlank() || equipment.value.description.isBlank()) {
                        snackBarHostState.showSnackbar("Please fill all fields")
                        return@launch
                    }
                    addEquipmentViewModel.addEquipment(equipment.value)
                    navController.popBackStack()
                }
            }, modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Equipment")
        }
        SnackbarHost(hostState = snackBarHostState)
    }
}
