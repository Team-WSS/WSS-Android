import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract

// package com.into.websoso.core.database.datasource.library
//
// import androidx.paging.PagingSource
// import androidx.paging.PagingState
// import com.into.websoso.core.database.entity.InDatabaseNovelEntity
// import com.into.websoso.data.library.model.NovelEntity
//
// internal class NovelEntityPagingSource(
//    private val source: PagingSource<Int, InDatabaseNovelEntity>,
// ) : PagingSource<Int, NovelEntity>() {
//    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NovelEntity> =
//        when (val result = source.load(params)) {
//            is LoadResult.Page -> LoadResult.Page(
//                data = result.data.map(InDatabaseNovelEntity::toData),
//                prevKey = result.prevKey,
//                nextKey = result.nextKey,
//            )
//
//            is LoadResult.Error -> LoadResult.Error(result.throwable)
//            is LoadResult.Invalid -> LoadResult.Invalid()
//        }
//
//    override fun getRefreshKey(state: PagingState<Int, NovelEntity>): Int? {
//        val anchorPosition = state.anchorPosition ?: return null
//        val novel = state.closestItemToPosition(anchorPosition) ?: return null
//        return novel.userNovelId.toInt()
//    }
// }

