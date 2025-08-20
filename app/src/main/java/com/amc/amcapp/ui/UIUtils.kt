package com.amc.amcapp.ui

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.rounded.BrowseGallery
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.amc.amcapp.R
import com.amc.amcapp.ui.theme.Dimens
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ImagePicker(
    bitmap: Bitmap?,
    imageUrl: String?,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var aspectRatio by remember { mutableFloatStateOf(1f) }

    Box(
        modifier = Modifier
            .border(
                1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp)
            )
            .clip(RoundedCornerShape(12.dp))
            .aspectRatio(1.5f)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(bitmap).build(),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            error = painterResource(id = R.drawable.error_placeholder),
            placeholder = painterResource(id = R.drawable.error_placeholder),
            modifier = Modifier.fillMaxWidth()
        )
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
                        MaterialTheme.colorScheme.primary, shape = MaterialTheme.shapes.extraSmall
                    )
                    .padding(8.dp)
                    .clickable { onCameraClick() },
                tint = Color.White
            )

            Icon(
                imageVector = Icons.Rounded.BrowseGallery,
                contentDescription = "Camera Icon",
                modifier = Modifier
                    .size(48.dp)
                    .alpha(0.5f)
                    .align(Alignment.BottomEnd)
                    .background(
                        MaterialTheme.colorScheme.primary, shape = MaterialTheme.shapes.extraSmall
                    )
                    .padding(8.dp)
                    .clickable { onGalleryClick() },
                tint = Color.White,
            )

            bitmap?.let {
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
                        .clickable { onGalleryClick() },
                    tint = Color.White,
                )
            }
        }
    }
}


@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    minLines: Int = 1,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        visualTransformation = visualTransformation,
        minLines = minLines
    )
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun AppLoadingBar(scope: BoxScope) {
    scope.apply {
        CircularProgressIndicator(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp),
            color = MaterialTheme.colorScheme.primary
        )
    }
}


@Composable
fun AppError(errorMessage: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = errorMessage,
            color = MaterialTheme.colorScheme.error,
            fontSize = 16.sp
        )
    }
}

fun showSnackBar(scope: CoroutineScope, snackBarHostState: SnackbarHostState, message: String) {
    scope.launch {
        snackBarHostState.showSnackbar(
            message = message, duration = SnackbarDuration.Short
        )
    }
}

@Composable
fun EmailField(text: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = text,
        onValueChange = onValueChange,
        label = { Text("Email") },
        textStyle = TextStyle(
            fontSize = Dimens.MediumText
        ),
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Email, contentDescription = "Email Icon"
            )
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun PasswordField(text: String, onValueChange: (String) -> Unit) {
    val hideIcon = remember { mutableStateOf(true) }
    OutlinedTextField(
        value = text,
        onValueChange = onValueChange,
        label = { Text("Password") },
        textStyle = TextStyle(
            fontSize = Dimens.MediumText
        ),
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Password, contentDescription = "Password Icon"
            )
        },
        trailingIcon = {
            Icon(
                imageVector = if (hideIcon.value) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                contentDescription = "Password Icon",
                modifier = Modifier.clickable {
                    hideIcon.value = !hideIcon.value
                })
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = if (hideIcon.value) PasswordVisualTransformation() else VisualTransformation.None,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun RoundedTextGradient(text: String) {
    val brush = Brush.horizontalGradient(
        listOf(
            MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary
        )
    )
    Text(
        text = text,
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(brush)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        color = MaterialTheme.colorScheme.onPrimary,
        fontSize = 24.sp
    )
}