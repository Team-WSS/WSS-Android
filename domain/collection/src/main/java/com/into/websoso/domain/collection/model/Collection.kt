package com.into.websoso.domain.collection.model

data class Collection(
    val id: Long,
    val name: String,
    val description: String?,
    val isPublic: Boolean,
    val novelCount: Int,
    val likeCount: Int?,
    val representativeNovel: CollectionNovel,
    val recentNovels: List<CollectionNovel>,
)

data class CollectionNovel(
    val id: Long,
    val title: String,
    val imageUrl: String,
    val author: String,
)

data class CollectionOwner(
    val id: Long,
    val nickname: String,
    val avatarImageUrl: String,
)

data class CollectionDetail(
    val id: Long,
    val name: String,
    val description: String?,
    val isPublic: Boolean,
    val isMine: Boolean,
    val owner: CollectionOwner,
    val representativeNovelId: Long,
    val novelCount: Int,
    val likeCount: Int,
    val isLiked: Boolean,
    val novels: List<CollectionNovel>,
)

data class CollectionPage(
    val totalCount: Int,
    val hasNext: Boolean,
    val nextCursor: String?,
    val collections: List<Collection>,
)

data class SaveCollection(
    val name: String,
    val description: String,
    val isPublic: Boolean,
    val novelIds: List<Long>,
    val representativeNovelId: Long,
) {
    init {
        require(name.isNotBlank() && name.length <= NAME_MAX_LENGTH)
        require(description.length <= DESCRIPTION_MAX_LENGTH)
        require(novelIds.size in MIN_NOVEL_COUNT..MAX_NOVEL_COUNT)
        require(novelIds.distinct().size == novelIds.size)
        require(representativeNovelId in novelIds)
    }

    private companion object {
        const val NAME_MAX_LENGTH = 20
        const val DESCRIPTION_MAX_LENGTH = 60
        const val MIN_NOVEL_COUNT = 1
        const val MAX_NOVEL_COUNT = 100
    }
}

enum class CollectionSortCriteria {
    RECENT,
    OLD,
}
