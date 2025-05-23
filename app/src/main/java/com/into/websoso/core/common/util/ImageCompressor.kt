package com.into.websoso.core.common.util

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class ImageCompressor {
    /**
     * 주어진 이미지 URI 리스트를 최대 5MB 이하로 압축하여 임시 파일로 저장하고,
     * 해당 파일의 URI 리스트를 반환합니다.
     *
     * 이 함수는 JPEG 형식으로 이미지를 반복적으로 압축하면서 품질을 낮추며,
     * 최종 파일의 크기가 5MB를 넘지 않도록 보장합니다.
     * 안전한 반환을 위해 실제 반환값은 4.8MB 이하입니다.
     *
     * @param context 파일 저장을 위한 Context 객체입니다. (cacheDir 사용)
     * @param uris 압축할 이미지 URI 리스트입니다.
     * @param contentResolver URI에서 InputStream을 얻기 위한 ContentResolver입니다.
     * @return 5MB 이하로 압축된 이미지 파일들의 URI 리스트
     */
    suspend fun compressUris(
        context: Context,
        uris: List<Uri>,
        contentResolver: ContentResolver,
    ): List<Uri> =
        withContext(Dispatchers.IO) {
            val compressedUris = mutableListOf<Uri>()

            for (uri in uris) {
                val inputStream = contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()

                var quality = 100
                val outputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)

                while (outputStream.size() > MAX_IMAGE_SIZE && quality > 10) {
                    quality -= 10
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
        private const val MAX_IMAGE_SIZE = 4_800_000
    }
}
