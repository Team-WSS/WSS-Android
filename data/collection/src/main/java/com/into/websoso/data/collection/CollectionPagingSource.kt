package com.into.websoso.data.collection

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.into.websoso.domain.collection.model.Collection
import com.into.websoso.domain.collection.model.CollectionPage
import kotlinx.coroutines.CancellationException

internal class CollectionPagingSource(
    private val getPage: suspend (cursor: String?, size: Int) -> CollectionPage,
) : PagingSource<String, Collection>() {
    override suspend fun load(params: LoadParams<String>): LoadResult<String, Collection> =
        runCatching {
            getPage(params.key, params.loadSize)
        }.fold(
            onSuccess = { page ->
                LoadResult.Page(
                    data = page.collections,
                    prevKey = null,
                    nextKey = page.nextCursor.takeIf { page.hasNext },
                )
            },
            onFailure = { throwable ->
                if (throwable is CancellationException) throw throwable
                LoadResult.Error(throwable)
            },
        )

    override fun getRefreshKey(state: PagingState<String, Collection>): String? = null
}
