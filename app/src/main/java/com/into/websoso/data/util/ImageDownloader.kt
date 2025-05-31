package com.into.websoso.data.util

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageDownloader
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        suspend fun formatImageToUri(url: String): Result<Uri> =
            withContext(Dispatchers.IO) {
                runCatching {
                    val connection = URL(url).openConnection()
                    connection.connect()

                    connection.getInputStream().use { inputStream ->
                        val file = File.createTempFile("image_", ".jpg", context.cacheDir)
                        FileOutputStream(file).use { output -> inputStream.copyTo(output) }
                        Uri.fromFile(file)
                    }
                }
            }
    }
