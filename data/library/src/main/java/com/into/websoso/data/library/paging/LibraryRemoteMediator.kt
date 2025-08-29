package com.into.websoso.data.library.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.LoadType.APPEND
import androidx.paging.LoadType.PREPEND
import androidx.paging.LoadType.REFRESH
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.paging.RemoteMediator.MediatorResult.Error
import androidx.paging.RemoteMediator.MediatorResult.Success
import com.into.websoso.data.library.model.NovelEntity
import com.into.websoso.data.library.model.UserNovelsEntity

@OptIn(ExperimentalPagingApi::class)
class LibraryRemoteMediator(
    private val getNovels: suspend (lastUserNovelId: Long) -> Result<UserNovelsEntity>,
    private val getLastNovel: suspend () -> NovelEntity?,
    private val deleteAllNovels: suspend () -> Unit,
    private val insertNovels: suspend (List<NovelEntity>) -> Unit,
) : RemoteMediator<Int, NovelEntity>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, NovelEntity>,
    ): MediatorResult {
        val lastUserNovelId = when (loadType) {
            REFRESH -> {
                deleteAllNovels()
                DEFAULT_LAST_USER_NOVEL_ID
            }

            APPEND -> {
                val lastNovel = getLastNovel()
                lastNovel?.userNovelId
            }

            PREPEND -> null
        } ?: return Success(true)

        return getNovels(lastUserNovelId).fold(
            onSuccess = { result ->
                insertNovels(result.userNovels)
                Success(endOfPaginationReached = !result.isLoadable)
            },
            onFailure = { throwable ->
                Error(throwable)
            },
        )
    }

    companion object {
        private const val DEFAULT_LAST_USER_NOVEL_ID = 0L
    }
}
