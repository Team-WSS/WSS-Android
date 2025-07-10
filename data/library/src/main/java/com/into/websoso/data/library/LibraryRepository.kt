package com.into.websoso.data.library

import NovelRemoteMediator
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.into.websoso.core.database.entity.InDatabaseNovelEntity
import com.into.websoso.data.library.datasource.LibraryLocalDataSource
import com.into.websoso.data.library.datasource.LibraryRemoteDataSource
import com.into.websoso.data.library.datasource.MyLibraryFilterLocalDataSource
import com.into.websoso.data.library.model.LibraryFilterParams
import com.into.websoso.data.library.model.NovelEntity
import com.into.websoso.data.library.model.toData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LibraryRepository
    @Inject
    constructor(
        private val libraryRemoteDataSource: LibraryRemoteDataSource,
        private val libraryLocalDataSource: LibraryLocalDataSource,
        private val myLibraryFilterLocalDataSource: MyLibraryFilterLocalDataSource,
    ) {
        val myLibraryFilter = myLibraryFilterLocalDataSource.myLibraryFilterFlow

        @OptIn(ExperimentalPagingApi::class)
        fun getUserLibrary(
            userId: Long,
            lastUserNovelId: Long,
            size: Int,
            sortCriteria: String,
            isInterest: Boolean?,
            readStatuses: List<String>?,
            attractivePoints: List<String>?,
            novelRating: Float?,
            query: String?,
        ): Flow<PagingData<NovelEntity>> =
            Pager(
                config = PagingConfig(pageSize = NETWORK_PAGE_SIZE),
                remoteMediator = NovelRemoteMediator(
                    userId,
                    libraryRemoteDataSource,
                    libraryLocalDataSource,
                ),
            ) {
                libraryLocalDataSource.selectAllNovels()
            }.flow.map { it.map(InDatabaseNovelEntity::toData) }

        suspend fun updateMyLibraryFilter(
            userId: Long = 184,
            lastUserNovelId: Long = 0,
            size: Int = 60,
            sortCriteria: String = "",
            isInterest: Boolean? = null,
            readStatuses: List<String>,
            attractivePoints: List<String>,
            novelRating: Float? = null,
            query: String? = null,
        ) {
            myLibraryFilterLocalDataSource.updateMyLibraryFilter(
                LibraryFilterParams(
                    sortCriteria = sortCriteria,
                    isInterest = isInterest,
                    readStatuses = readStatuses.toList(),
                    attractivePoints = attractivePoints.toList(),
                    novelRating = novelRating,
                ),
            )
        }

        // 1. 클릭 리스너로 뷰모델 상태 업데이트
        // 2. 확인 누르면 datastore 업데이트
        // 3. 객체 직렬화 및 저장
        // 4. datastore를 읽고, 캐싱(가능하면), 널이 아니라면, 해당 쿼리문으로 룸 업데이트
        // 5. 룸에서 데이터 읽고 UI
        // 6. 이미지 캐싱
        // 7. 다른 뷰에서 룸 동기화
        // 8. 증분 API
        companion object {
            private const val NETWORK_PAGE_SIZE = 10
        }
    }
