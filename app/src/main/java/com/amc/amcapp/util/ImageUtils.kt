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
import coil.decode.DecodeUtils.calculateInSampleSize
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
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
            maxWidth.toFloat() / bitmap.width, maxHeight.toFloat() / bitmap.height
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

    fun decodeAndCompressImage(
        context: Context,
        uri: Uri,
        reqWidth: Int,
        reqHeight: Int,
        quality: Int
    ): Uri? {
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }

        context.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, options)
        }

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
        options.inJustDecodeBounds = false

        val bitmap = context.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, options)
        } ?: return null

        // Fix rotation using EXIF
        val rotatedBitmap = try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val exif = ExifInterface(inputStream)
                val orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
                val matrix = Matrix()
                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                    ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                    ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                }
                Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            }
        } catch (e: Exception) {
            bitmap
        }

        val file = File(context.cacheDir, "compressed_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { out ->
            rotatedBitmap?.compress(Bitmap.CompressFormat.JPEG, quality, out)
        }

        rotatedBitmap?.recycle()
        bitmap.recycle()

        return Uri.fromFile(file)
    }

    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            while ((halfHeight / inSampleSize) >= reqHeight &&
                (halfWidth / inSampleSize) >= reqWidth
            ) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
}

