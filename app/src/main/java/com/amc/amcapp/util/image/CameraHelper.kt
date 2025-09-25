import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

fun getTempImageUri(context: Context): Uri {
    // Get the directory specified in file_paths.xml
    val cacheDir = File(context.cacheDir, "images")
    if (!cacheDir.exists()) {
        cacheDir.mkdirs() // Ensure the directory exists
    }

    val tempFile = File.createTempFile(
        "temp_image_",
        ".jpg",
        cacheDir // Use the new subdirectory
    )

    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        tempFile
    )
}