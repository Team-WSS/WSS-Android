package com.into.websoso.feature.collection.model

import com.into.websoso.domain.collection.model.CollectionDetail

data class CollectionShareContent(
    val collectionId: Long,
    val title: String,
    val nickname: String,
    val imageUrls: List<String>,
) {
    companion object {
        internal fun from(collection: CollectionDetail): CollectionShareContent? {
            if (!collection.isPublic || collection.id <= 0L || collection.novelCount <= 0) return null
            val representative = collection.novels.firstOrNull { it.id == collection.representativeNovelId } ?: return null
            val images = (listOf(representative) + collection.novels)
                .distinctBy { it.id }
                .take(collection.novelCount.coerceAtMost(3))
                .map { it.imageUrl }
            if (images.size != collection.novelCount.coerceAtMost(3) || images.any(String::isBlank)) return null
            return CollectionShareContent(collection.id, collection.name, collection.owner.nickname, images)
        }
    }
}
