package com.into.websoso.ui.collection

import android.content.Intent
import java.net.URI
import java.net.URLDecoder

internal object CollectionDeepLink {
    const val PENDING_COLLECTION_ID = "pendingCollectionId"

    fun parseCollectionId(
        link: String?,
        expectedScheme: String,
    ): Long? =
        runCatching {
            val uri = URI(link ?: return null)
            if (uri.scheme != expectedScheme || uri.rawAuthority != "kakaolink") return null
            if (!uri.rawPath.isNullOrEmpty() && uri.rawPath != "/") return null
            if (uri.rawFragment != null) return null

            val values = uri.rawQuery
                ?.split('&')
                ?.map { it.split('=', limit = 2) }
                ?.filter { URLDecoder.decode(it[0], "UTF-8") == "collectionId" }
                ?: return null
            val value = values.singleOrNull()?.getOrNull(1) ?: return null
            val decoded = URLDecoder.decode(value, "UTF-8")
            if (decoded.isEmpty() || decoded.any { it !in '0'..'9' }) return null
            decoded.toLongOrNull()?.takeIf { it > 0L }
        }.getOrNull()

    fun forward(
        source: Intent,
        destination: Intent,
    ): Intent =
        destination.apply {
            source.getLongExtra(PENDING_COLLECTION_ID, 0L).takeIf { it > 0L }?.let {
                putExtra(PENDING_COLLECTION_ID, it)
            }
        }
}
