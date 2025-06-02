package com.into.websoso.data.mapper

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MultiPartMapper
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        inline fun <reified T> formatToMultipart(
            target: T,
            partName: String = "data",
            fileName: String = "data.json",
        ): MultipartBody.Part {
            val json = Json.encodeToString(target)
            val requestBody = json.toRequestBody("application/json".toMediaType())
            return MultipartBody.Part.createFormData(partName, fileName, requestBody)
        }

        fun formatToMultipart(
            uri: Uri,
            partName: String = "images",
            contentType: String = "image/*",
        ): MultipartBody.Part {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: throw IllegalArgumentException("유효하지 않은 URI: $uri")

            return inputStream.use { stream ->
                val bytes = stream.readBytes()
                val fileName = uri.lastPathSegment ?: "file.jpg"
                val requestBody = bytes.toRequestBody(contentType.toMediaType())
                MultipartBody.Part.createFormData(partName, fileName, requestBody)
            }
        }
    }
