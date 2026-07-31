package com.into.websoso.feature.collection.model

internal data class CollectionLibraryNovelUiModel(
    val novelId: Long,
    val title: String,
    val imageUrl: String,
    val readStatus: CollectionLibraryReadStatus?,
    val isInterested: Boolean,
    val ratingStars: List<CollectionLibraryRatingStar>,
    val dateText: String?,
)

internal enum class CollectionLibraryReadStatus {
    WATCHING,
    WATCHED,
    QUIT,
}

internal enum class CollectionLibraryRatingStar {
    FULL,
    HALF,
    EMPTY,
}
