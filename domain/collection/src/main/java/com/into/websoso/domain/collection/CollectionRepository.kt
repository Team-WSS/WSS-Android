package com.into.websoso.domain.collection

import androidx.paging.PagingData
import com.into.websoso.domain.collection.model.Collection
import com.into.websoso.domain.collection.model.CollectionDetail
import com.into.websoso.domain.collection.model.CollectionPage
import com.into.websoso.domain.collection.model.CollectionSortCriteria
import com.into.websoso.domain.collection.model.SaveCollection
import kotlinx.coroutines.flow.Flow

interface CollectionRepository {
    fun getMyCollections(): Flow<PagingData<Collection>>

    fun getUserCollections(userId: Long): Flow<PagingData<Collection>>

    fun getLikedCollections(): Flow<PagingData<Collection>>

    suspend fun getMyCollectionPreview(): CollectionPage

    suspend fun getUserCollectionPreview(userId: Long): CollectionPage

    suspend fun getCollection(
        collectionId: Long,
        sortCriteria: CollectionSortCriteria = CollectionSortCriteria.RECENT,
    ): CollectionDetail

    suspend fun createCollection(collection: SaveCollection): Long

    suspend fun updateCollection(
        collectionId: Long,
        collection: SaveCollection,
    )

    suspend fun deleteCollection(collectionId: Long)

    suspend fun updateLike(
        collectionId: Long,
        isLiked: Boolean,
    )
}
