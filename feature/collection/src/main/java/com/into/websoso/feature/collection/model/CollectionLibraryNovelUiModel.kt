package com.into.websoso.feature.collection.model

import androidx.annotation.StringRes
import com.into.websoso.core.resource.R.string.collection_read_status_quit
import com.into.websoso.core.resource.R.string.collection_read_status_watched
import com.into.websoso.core.resource.R.string.collection_read_status_watching

internal data class CollectionLibraryNovelUiModel(
    val novelId: Long,
    val title: String,
    val imageUrl: String,
    val readStatus: CollectionLibraryReadStatus?,
    val isInterested: Boolean,
    val ratingStars: List<CollectionLibraryRatingStar>,
    val dateText: String?,
)

internal enum class CollectionLibraryReadStatus(
    @get:StringRes val labelRes: Int,
) {
    WATCHING(collection_read_status_watching),
    WATCHED(collection_read_status_watched),
    QUIT(collection_read_status_quit),
}

internal enum class CollectionLibraryRatingStar {
    FULL,
    HALF,
    EMPTY,
}
