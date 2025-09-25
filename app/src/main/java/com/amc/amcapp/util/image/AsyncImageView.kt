import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest

@Composable
fun AsyncImageView(
    modifier: Modifier = Modifier,
    model: Any, // Can be Uri or String (URL)
    contentDescription: String? = null
) {
    val imageRequest = ImageRequest.Builder(LocalContext.current)
        .data(model)
        .crossfade(true)
        .build()

    val painter = rememberAsyncImagePainter(model = imageRequest)

    Image(
        painter = painter,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = ContentScale.Crop
    )
}