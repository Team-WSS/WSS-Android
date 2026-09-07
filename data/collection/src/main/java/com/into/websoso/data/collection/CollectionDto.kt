package com.into.websoso.data.collection

import com.into.websoso.domain.collection.model.Collection
import com.into.websoso.domain.collection.model.CollectionDetail
import com.into.websoso.domain.collection.model.CollectionNovel
import com.into.websoso.domain.collection.model.CollectionOwner
import com.into.websoso.domain.collection.model.CollectionPage
import com.into.websoso.domain.collection.model.SaveCollection
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class SaveCollectionRequestDto(
    @SerialName("name") val name: String,
    @SerialName("description") val description: String,
    @SerialName("isPublic") val isPublic: Boolean,
    @SerialName("novelIds") val novelIds: List<Long>,
    @SerialName("representativeNovelId") val representativeNovelId: Long,
)

internal fun SaveCollection.toRequest() =
    SaveCollectionRequestDto(
        name = name,
        description = description,
        isPublic = isPublic,
        novelIds = novelIds,
        representativeNovelId = representativeNovelId,
    )

@Serializable
internal data class CreateCollectionResponseDto(
    @SerialName("collectionId") val collectionId: Long,
)

@Serializable
internal data class CollectionNovelDto(
    @SerialName("novelId") val novelId: Long,
    @SerialName("title") val title: String,
    @SerialName("novelImage") val novelImage: String,
    @SerialName("author") val author: String,
) {
    fun toDomain() =
        CollectionNovel(
            id = novelId,
            title = title,
            imageUrl = novelImage,
            author = author,
        )
}

@Serializable
internal data class CollectionResponseDto(
    @SerialName("collectionId") val collectionId: Long,
    @SerialName("collectionName") val collectionName: String,
    @SerialName("collectionDescription") val collectionDescription: String? = null,
    @SerialName("isPublic") val isPublic: Boolean,
    @SerialName("novelCount") val novelCount: Int,
    @SerialName("likeCount") val likeCount: Int? = null,
    @SerialName("representativeNovel") val representativeNovel: CollectionNovelDto,
    @SerialName("recentNovels") val recentNovels: List<CollectionNovelDto>,
) {
    fun toDomain() =
        Collection(
            id = collectionId,
            name = collectionName,
            description = collectionDescription,
            isPublic = isPublic,
            novelCount = novelCount,
            likeCount = likeCount,
            representativeNovel = representativeNovel.toDomain(),
            recentNovels = recentNovels.map(CollectionNovelDto::toDomain),
        )
}

@Serializable
internal data class CollectionPageResponseDto(
    @SerialName("collectionsCount") val collectionsCount: Int,
    @SerialName("hasNext") val hasNext: Boolean,
    @SerialName("nextCursor") val nextCursor: String? = null,
    @SerialName("collections") val collections: List<CollectionResponseDto>,
) {
    fun toDomain() =
        CollectionPage(
            totalCount = collectionsCount,
            hasNext = hasNext,
            nextCursor = nextCursor,
            collections = collections.map(CollectionResponseDto::toDomain),
        )
}

@Serializable
internal data class CollectionOwnerDto(
    @SerialName("userId") val userId: Long,
    @SerialName("nickname") val nickname: String,
    @SerialName("avatarImage") val avatarImage: String,
) {
    fun toDomain() =
        CollectionOwner(
            id = userId,
            nickname = nickname,
            avatarImageUrl = avatarImage,
        )
}

@Serializable
internal data class CollectionDetailResponseDto(
    @SerialName("collectionId") val collectionId: Long,
    @SerialName("collectionName") val collectionName: String,
    @SerialName("collectionDescription") val collectionDescription: String? = null,
    @SerialName("isPublic") val isPublic: Boolean,
    @SerialName("isMyCollection") val isMyCollection: Boolean,
    @SerialName("owner") val owner: CollectionOwnerDto,
    @SerialName("representativeNovelId") val representativeNovelId: Long,
    @SerialName("novelCount") val novelCount: Int,
    @SerialName("likeCount") val likeCount: Int,
    @SerialName("isLiked") val isLiked: Boolean,
    @SerialName("novels") val novels: List<CollectionNovelDto>,
) {
    fun toDomain() =
        CollectionDetail(
            id = collectionId,
            name = collectionName,
            description = collectionDescription,
            isPublic = isPublic,
            isMine = isMyCollection,
            owner = owner.toDomain(),
            representativeNovelId = representativeNovelId,
            novelCount = novelCount,
            likeCount = likeCount,
            isLiked = isLiked,
            novels = novels.map(CollectionNovelDto::toDomain),
        )
}
