package com.into.websoso.data.collection

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.into.websoso.domain.collection.model.CollectionPage
import kotlinx.coroutines.CancellationException

internal data class CursorPage<T>(
    val items: List<T>,
    val hasNext: Boolean,
    val nextCursor: String?,
)

internal fun CollectionPage.toCursorPage() = CursorPage(items = collections, hasNext = hasNext, nextCursor = nextCursor)

internal class CollectionPagingSource<T : Any>(
    private val getPage: suspend (cursor: String?, size: Int) -> CursorPage<T>,
) : PagingSource<String, T>() {
    override suspend fun load(params: LoadParams<String>): LoadResult<String, T> =
        runCatching {
            getPage(params.key, params.loadSize)
        }.fold(
            onSuccess = { page ->
                LoadResult.Page(
                    data = page.items,
                    prevKey = null,
                    nextKey = page.nextCursor.takeIf { page.hasNext },
                )
            },
            onFailure = { throwable ->
                if (throwable is CancellationException) throw throwable
                LoadResult.Error(throwable)
            },
        )

    override fun getRefreshKey(state: PagingState<String, T>): String? = null
}
