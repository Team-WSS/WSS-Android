package com.into.websoso.data.library.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.into.websoso.data.account.AccountRepository
import com.into.websoso.data.filter.FilterRepository
import com.into.websoso.data.filter.model.DEFAULT_RATING_MAX
import com.into.websoso.data.filter.model.DEFAULT_RATING_MIN
import com.into.websoso.data.filter.model.LibraryFilter
import com.into.websoso.data.library.LibraryRepository
import com.into.websoso.data.library.LibraryRepository.Companion.PAGE_SIZE
import com.into.websoso.data.library.datasource.LibraryRemoteDataSource
import com.into.websoso.data.library.model.LibraryKeywordEntity
import com.into.websoso.data.library.model.NovelEntity
import com.into.websoso.data.library.paging.LibraryPagingSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class MyLibraryRepository
    @Inject
    constructor(
        private val filterRepository: FilterRepository,
        private val accountRepository: AccountRepository,
        private val libraryRemoteDataSource: LibraryRemoteDataSource,
    ) : LibraryRepository {
        private var _novelTotalCount: MutableStateFlow<Long> = MutableStateFlow(0)
        override val novelTotalCount: Flow<Long> = _novelTotalCount.asStateFlow()

        /**
         * Room 캐싱 없이 서버와 직접 통신하며, 필터 변경 시 스트림을 재생성합니다.
         */
        @OptIn(ExperimentalCoroutinesApi::class)
        override fun getLibraryFlow(): Flow<PagingData<NovelEntity>> =
            filterRepository.filterFlow
                .flatMapLatest(::createLibraryFlow)

        override fun getUnfilteredLibraryFlow(): Flow<PagingData<NovelEntity>> = createLibraryFlow(LibraryFilter())

        override suspend fun getRegisteredKeywords(): List<LibraryKeywordEntity> {
            val userId = accountRepository.userIdFlow.first { it != 0L }
            return libraryRemoteDataSource.getUserNovelKeywords(userId = userId)
        }

        private suspend fun getUserNovels(
            cursor: String?,
            libraryFilter: LibraryFilter,
        ) = runCatching {
            val (ratingMin, ratingMax) = libraryFilter.toRatingRangeParams()

            libraryRemoteDataSource.getUserNovels(
                userId = accountRepository.userId,
                cursor = cursor,
                size = PAGE_SIZE,
                sortType = libraryFilter.sortCriteria,
                isInterest = if (!libraryFilter.isInterested) null else true,
                readStatuses = libraryFilter.readStatuses.ifEmpty { null },
                genres = libraryFilter.genres.ifEmpty { null },
                isComplete = libraryFilter.isComplete,
                ratingMin = ratingMin,
                ratingMax = ratingMax,
                unratedOnly = if (libraryFilter.isRatingless) true else null,
                attractivePoints = libraryFilter.attractivePoints.ifEmpty { null },
                keywords = libraryFilter.keywords.ifEmpty { null },
            )
        }

        private fun createLibraryFlow(libraryFilter: LibraryFilter): Flow<PagingData<NovelEntity>> =
            Pager(
                config = PagingConfig(
                    pageSize = PAGE_SIZE,
                    enablePlaceholders = false,
                ),
                pagingSourceFactory = {
                    LibraryPagingSource(
                        getNovels = { cursor ->
                            getUserNovels(cursor, libraryFilter).also { result ->
                                _novelTotalCount.update {
                                    result.getOrNull()?.userNovelCount ?: 0
                                }
                            }
                        },
                    )
                },
            ).flow

        private fun LibraryFilter.toRatingRangeParams(): Pair<Float?, Float?> =
            when {
                isRatingless -> null to null
                ratingMin > DEFAULT_RATING_MIN || ratingMax < DEFAULT_RATING_MAX -> ratingMin to ratingMax
                else -> null to null
            }
    }
