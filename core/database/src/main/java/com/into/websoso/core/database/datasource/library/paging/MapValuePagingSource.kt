package com.into.websoso.core.database.datasource.library.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import javax.inject.Inject
import javax.inject.Singleton

internal fun <K : Any, From : Any, To : Any> PagingSource<K, From>.mapValue(mapper: suspend (From) -> To): PagingSource<K, To> =
    MapValuePagingSource(this, mapper)

@Singleton
internal class MapValuePagingSource<Key : Any, From : Any, To : Any>
    @Inject
    constructor(
        private val targetSource: PagingSource<Key, From>,
        private val mapper: suspend (From) -> To,
    ) : PagingSource<Key, To>() {
        override suspend fun load(params: LoadParams<Key>): LoadResult<Key, To> =
            when (val result = targetSource.load(params)) {
                is LoadResult.Page -> LoadResult.Page(
                    data = result.data.map { mapper(it) },
                    prevKey = result.prevKey,
                    nextKey = result.nextKey,
                    itemsBefore = result.itemsBefore,
                    itemsAfter = result.itemsAfter,
                )

                is LoadResult.Error -> LoadResult.Error(result.throwable)
                is LoadResult.Invalid -> LoadResult.Invalid()
            }

        override val jumpingSupported: Boolean get() = targetSource.jumpingSupported

        override fun getRefreshKey(state: PagingState<Key, To>): Key? = null
    }
