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
        /**
         * 주어진 이미지 URI 리스트를 압축된 리스트로 반환합니다.
         *
         * 이 함수는 JPEG 형식으로 이미지를 반복적으로 압축하면서 품질을 낮추며,
         * 최종 파일의 크기가 전달된 용량을 넘지 않도록 보장합니다.
         *
         * @param uris 압축할 이미지 URI 리스트입니다.
         * @param size 압축 후 이미지 파일의 최대 크기 (기본값: 0.25MB, 단위: MB)
         * @return 압축된 이미지 파일들의 URI 리스트
         */
        suspend fun compressUris(
            uris: List<Uri>,
            size: Double = DEFAULT_MAX_IMAGE_SIZE,
        ): List<Uri> =
            withContext(Dispatchers.IO) {
                val compressedUris = mutableListOf<Uri>()

                for (uri in uris) {
                    val inputStream = context.contentResolver.openInputStream(uri)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    inputStream?.close()

                    var quality = INITIAL_QUALITY
                    val outputStream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)

                    while (outputStream.size() > (size * MB) && quality > QUALITY_DECREMENT_STEP) {
                        quality -= QUALITY_DECREMENT_STEP
                        outputStream.reset()
                        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                    }

                    val compressedFile = File.createTempFile("compressed_", ".jpg", context.cacheDir)
                    FileOutputStream(compressedFile).use { it.write(outputStream.toByteArray()) }

                    compressedUris.add(Uri.fromFile(compressedFile))
                    outputStream.close()
                }

                return@withContext compressedUris
            }

        companion object {
            private const val INITIAL_QUALITY: Int = 100
            private const val QUALITY_DECREMENT_STEP: Int = 5
            private const val DEFAULT_MAX_IMAGE_SIZE: Double = 0.25
            private const val MB: Double = (1024 * 1024).toDouble()
        }
    }
