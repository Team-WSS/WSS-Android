package com.into.websoso.data.collection

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.into.websoso.data.account.AccountRepository
import com.into.websoso.domain.collection.CollectionRepository
import com.into.websoso.domain.collection.model.Collection
import com.into.websoso.domain.collection.model.CollectionDetail
import com.into.websoso.domain.collection.model.CollectionPage
import com.into.websoso.domain.collection.model.CollectionSortCriteria
import com.into.websoso.domain.collection.model.SaveCollection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

internal class DefaultCollectionRepository
    @Inject
    constructor(
        private val api: CollectionApi,
        private val accountRepository: AccountRepository,
    ) : CollectionRepository {
        override fun getMyCollections(): Flow<PagingData<Collection>> =
            createPager { cursor, size ->
                getUserCollectionPage(
                    userId = accountRepository.userIdFlow.first { it != 0L },
                    cursor = cursor,
                    size = size,
                )
            }

        override fun getUserCollections(userId: Long): Flow<PagingData<Collection>> =
            createPager { cursor, size -> getUserCollectionPage(userId, cursor, size) }

        override fun getLikedCollections(): Flow<PagingData<Collection>> =
            createPager { cursor, size ->
                api.getLikedCollections(cursor, size).toDomain()
            }

        override suspend fun getMyCollectionPreview(): CollectionPage =
            getUserCollectionPage(
                userId = accountRepository.userIdFlow.first { it != 0L },
                cursor = null,
                size = PREVIEW_SIZE,
            )

        override suspend fun getUserCollectionPreview(userId: Long): CollectionPage =
            getUserCollectionPage(userId = userId, cursor = null, size = PREVIEW_SIZE)

        override suspend fun getCollection(
            collectionId: Long,
            sortCriteria: CollectionSortCriteria,
        ): CollectionDetail = api.getCollection(collectionId, sortCriteria.name).toDomain()

        override suspend fun createCollection(collection: SaveCollection): Long = api.createCollection(collection.toRequest()).collectionId

        override suspend fun updateCollection(
            collectionId: Long,
            collection: SaveCollection,
        ) {
            api.updateCollection(collectionId, collection.toRequest())
        }

        override suspend fun deleteCollection(collectionId: Long) {
            api.deleteCollection(collectionId)
        }

        override suspend fun updateLike(
            collectionId: Long,
            isLiked: Boolean,
        ) {
            if (isLiked) api.likeCollection(collectionId) else api.unlikeCollection(collectionId)
        }

        private fun createPager(getPage: suspend (cursor: String?, size: Int) -> CollectionPage): Flow<PagingData<Collection>> =
            Pager(
                config = PagingConfig(
                    pageSize = PAGE_SIZE,
                    enablePlaceholders = false,
                ),
                pagingSourceFactory = { CollectionPagingSource(getPage) },
            ).flow

        private suspend fun getUserCollectionPage(
            userId: Long,
            cursor: String?,
            size: Int,
        ): CollectionPage = api.getUserCollections(userId, cursor, size).toDomain()

        private companion object {
            const val PAGE_SIZE = 10
            const val PREVIEW_SIZE = 3
        }
    }
