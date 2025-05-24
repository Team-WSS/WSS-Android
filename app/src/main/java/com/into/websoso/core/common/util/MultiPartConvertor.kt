package com.into.websoso.core.common.util

import android.content.Context
import android.net.Uri
import com.into.websoso.data.remote.request.FeedRequestDto
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.InputStream
import javax.inject.Inject

class MultiPartConvertor
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        fun formatToMultipart(feedRequestDto: FeedRequestDto): MultipartBody.Part {
            val json = Json.encodeToString(feedRequestDto)
            val requestBody = json.toRequestBody("application/json".toMediaType())
            return MultipartBody.Part.createFormData("feed", "feed.json", requestBody)
        }

        fun formatToMultipart(uri: Uri): MultipartBody.Part {
            val inputStream: InputStream = requireNotNull(context.contentResolver.openInputStream(uri))

            val bytes = inputStream.readBytes()
            val fileName = uri.lastPathSegment ?: "image.jpg"

            val requestBody = bytes.toRequestBody("image/*".toMediaType())
            return MultipartBody.Part.createFormData("images", fileName, requestBody)
        }
    }
