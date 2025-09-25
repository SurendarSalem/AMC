import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import java.io.File

// A dummy function to simulate a Firebase upload.
// You would replace this with your actual Firebase Storage upload logic.
suspend fun uploadImageToFirebase(uri: Uri, path: String): String {
    // This is a placeholder. Your actual implementation would involve
    // uploading the file to Firebase Storage and getting the download URL.
    // For now, we'll just return a mock URL.
    return "https://fake-firebase-url.com/$path"
}

@Composable
fun FullImageUploaderScreen() {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageUrl by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // State to hold the camera file's Uri
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher for picking from gallery
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUri = it
        }
    }

    // Launcher for taking a photo
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            tempCameraUri?.let { imageUri = it }
        }
    }

    // Permission launcher for the camera
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted, now launch the camera
            val uri = getTempImageUri(context)
            tempCameraUri = uri
            cameraLauncher.launch(uri)
        } else {
            // Permission denied, handle it gracefully
            // You might want to show a Toast or a Snackbar
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AsyncImage(
            model = imageUri ?: imageUrl ?: "https://via.placeholder.com/150",
            contentDescription = "Uploaded or selected image",
            modifier = Modifier.size(200.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { galleryLauncher.launch("image/*") }) {
            Text("Pick from Gallery")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            // First request the CAMERA permission
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }) {
            Text("Take Photo")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Example with a hardcoded URL
        Button(onClick = { imageUrl = "https://picsum.photos/200/300" }) {
            Text("Load from URL")
        }

        if (imageUri != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                coroutineScope.launch {
                    try {
                        val downloadUrl = uploadImageToFirebase(
                            uri = imageUri!!,
                            path = "images/${System.currentTimeMillis()}.jpg"
                        )
                        imageUrl = downloadUrl

                        // Clean up the temporary file if it was from the camera
                        val file = tempCameraUri?.path?.let { File(it) }
                        file?.delete()

                        imageUri = null
                        tempCameraUri = null
                        // Optional: show a confirmation message
                    } catch (e: Exception) {
                        // Handle upload error (e.g., show a Toast or log the error)
                    }
                }
            }) {
                Text("Upload to Firebase")
            }
        }
    }
}