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
import kotlinx.coroutines.withTimeoutOrNull
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
                    userId = requireUserId(),
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
                userId = requireUserId(),
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

        // accountRepository.userIdFlow는 로그인 직후 별도 화면(MainViewModel.updateUserInfo 등)이
        // 유저 정보를 성공적으로 받아와야만 채워진다. 그 전에 여기서 무기한 대기하면
        // 콜드 스타트 직후 진입 시 화면이 영원히 멈출 수 있어 타임아웃으로 상한을 둔다.
        private suspend fun requireUserId(): Long =
            withTimeoutOrNull(USER_ID_AWAIT_TIMEOUT_MS) {
                accountRepository.userIdFlow.first { it != 0L }
            } ?: error("userId was not resolved within ${USER_ID_AWAIT_TIMEOUT_MS}ms")

        private companion object {
            const val PAGE_SIZE = 10
            const val PREVIEW_SIZE = 3

            // fetchUserInfo()는 MainActivity.onCreate() 시점에 이미 시작되어 있고
            // 정상 응답은 수백 ms 수준이다. 실패하더라도 CollectionPreview에 재시도 버튼이 있어
            // 회복 가능하므로, 네트워크 타임아웃이 아니라 UI 대기 상한으로 5초를 둔다.
            const val USER_ID_AWAIT_TIMEOUT_MS = 5_000L
        }
    }
