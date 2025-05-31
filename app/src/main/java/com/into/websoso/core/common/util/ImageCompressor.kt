package com.into.websoso.core.common.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageCompressor
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        suspend fun compressUris(
            uris: List<Uri>,
            size: Double = DEFAULT_MAX_IMAGE_SIZE,
        ): List<Uri> =
            withContext(Dispatchers.IO) {
                uris.mapNotNull { uri ->
                    runCatching {
                        val inputStream = context.contentResolver.openInputStream(uri)
                        val bitmap = inputStream?.use { BitmapFactory.decodeStream(it) } ?: return@runCatching null

                        var quality = INITIAL_QUALITY
                        val outputStream = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)

                        while (outputStream.size() > (size * MB) && quality > QUALITY_DECREMENT_STEP) {
                            quality -= QUALITY_DECREMENT_STEP
                            outputStream.reset()
                            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                        }

                        val compressedFile = File.createTempFile("compressed_", ".jpg", context.cacheDir)
                        FileOutputStream(compressedFile).use {
                            it.write(outputStream.toByteArray())
                        }

                        outputStream.close()
                        Uri.fromFile(compressedFile)
                    }.onFailure {
                        it.printStackTrace()
                    }.getOrNull()
                }
            }

        companion object {
            private const val INITIAL_QUALITY: Int = 100
            private const val QUALITY_DECREMENT_STEP: Int = 5
            private const val DEFAULT_MAX_IMAGE_SIZE: Double = 0.25
            private const val MB: Double = (1024 * 1024).toDouble()
        }
    }
