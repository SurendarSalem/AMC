package com.amc.amcapp.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.provider.MediaStore
import androidx.core.graphics.scale
import androidx.exifinterface.media.ExifInterface
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream

object ImageUtils {

    fun loadBitmapFromFile(file: File): Bitmap {
        val exif = ExifInterface(file.absolutePath)
        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
        val orientation = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL
        )
        return fixImageRotation(bitmap, orientation)
    }

    fun loadBitmapFromUri(context: Context, uri: Uri): Bitmap {
        val inputStream: InputStream = context.contentResolver.openInputStream(uri)!!
        val exif = ExifInterface(inputStream)
        inputStream.close()

        val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        val orientation = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL
        )
        return fixImageRotation(bitmap, orientation)
    }

    fun fixImageRotation(bitmap: Bitmap, orientation: Int): Bitmap {
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    fun resizeBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        val ratio = minOf(
            maxWidth.toFloat() / bitmap.width,
            maxHeight.toFloat() / bitmap.height
        )
        val width = (bitmap.width * ratio).toInt()
        val height = (bitmap.height * ratio).toInt()

        return bitmap.scale(width, height, filter = true)
    }

    suspend fun bitmapToByteArray(bitmap: Bitmap, quality: Int = 90): ByteArray =
        withContext(Dispatchers.IO) {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
            return@withContext stream.toByteArray()
        }

    suspend fun loadBitmapFromUrl(context: Context, url: String): Bitmap? =
        withContext(Dispatchers.IO) {
            val loader = ImageLoader(context)
            val request = ImageRequest.Builder(context).data(url).allowHardware(false).build()
            val result = (loader.execute(request) as? SuccessResult)?.drawable
            return@withContext (result as? BitmapDrawable)?.bitmap
        }
}

