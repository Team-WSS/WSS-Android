package com.into.websoso.feature.collection

import androidx.paging.PagingData
import com.into.websoso.domain.collection.CollectionRepository
import com.into.websoso.domain.collection.model.Collection
import com.into.websoso.domain.collection.model.CollectionDetail
import com.into.websoso.domain.collection.model.CollectionOwner
import com.into.websoso.domain.collection.model.CollectionPage
import com.into.websoso.domain.collection.model.CollectionSortCriteria
import com.into.websoso.domain.collection.model.PublicCollection
import com.into.websoso.domain.collection.model.SaveCollection
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeCollectionViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `조회 중에 다시 갱신하면 이전 응답 대신 새 응답을 반영한다`() =
        runTest(dispatcher) {
            val staleResponse = CompletableDeferred<List<PublicCollection>>()
            val freshResponse = CompletableDeferred<List<PublicCollection>>()
            val repository = FakeCollectionRepository(ArrayDeque(listOf(staleResponse, freshResponse)))
            val viewModel = HomeCollectionViewModel(repository)

            viewModel.refresh()
            advanceUntilIdle()
            // 상세 화면에서 컬렉션을 비공개로 바꾸고 돌아와 다시 갱신한다.
            viewModel.refresh()
            advanceUntilIdle()
            staleResponse.complete(listOf(publicCollection(id = 1L), publicCollection(id = 2L)))
            freshResponse.complete(listOf(publicCollection(id = 2L)))
            advanceUntilIdle()

            assertEquals(2, repository.previewRequestCount)
            assertEquals(listOf(2L), viewModel.collections.value.map { it.id })
        }

    @Test
    fun `갱신에 실패하면 이전 목록을 유지한다`() =
        runTest(dispatcher) {
            val first = CompletableDeferred(listOf(publicCollection(id = 1L)))
            val failed = CompletableDeferred<List<PublicCollection>>().apply {
                completeExceptionally(IllegalStateException())
            }
            val viewModel = HomeCollectionViewModel(FakeCollectionRepository(ArrayDeque(listOf(first, failed))))

            viewModel.refresh()
            advanceUntilIdle()
            viewModel.refresh()
            advanceUntilIdle()

            assertEquals(listOf(1L), viewModel.collections.value.map { it.id })
        }

    private fun publicCollection(id: Long) =
        PublicCollection(
            id = id,
            name = "컬렉션 $id",
            description = null,
            novelCount = 0,
            owner = CollectionOwner(id = 1L, nickname = "웹소소", avatarImageUrl = ""),
            recentNovels = emptyList(),
        )

    private class FakeCollectionRepository(
        private val previewResponses: ArrayDeque<CompletableDeferred<List<PublicCollection>>>,
    ) : CollectionRepository {
        var previewRequestCount = 0
            private set

        override suspend fun getPublicCollectionPreview(): List<PublicCollection> {
            previewRequestCount++
            return previewResponses.removeFirst().await()
        }

        override fun getMyCollections(): Flow<PagingData<Collection>> = error("unused")

        override fun getUserCollections(userId: Long): Flow<PagingData<Collection>> = error("unused")

        override fun getLikedCollections(): Flow<PagingData<Collection>> = error("unused")

        override fun getPublicCollections(): Flow<PagingData<PublicCollection>> = error("unused")

        override suspend fun getMyCollectionPreview(): CollectionPage = error("unused")

        override suspend fun getUserCollectionPreview(userId: Long): CollectionPage = error("unused")

        override suspend fun getCollection(
            collectionId: Long,
            sortCriteria: CollectionSortCriteria,
        ): CollectionDetail = error("unused")

        override suspend fun createCollection(collection: SaveCollection): Long = error("unused")

        override suspend fun updateCollection(
            collectionId: Long,
            collection: SaveCollection,
        ) = error("unused")

        override suspend fun deleteCollection(collectionId: Long) = error("unused")

        override suspend fun updateLike(
            collectionId: Long,
            isLiked: Boolean,
        ) = error("unused")
    }
}
