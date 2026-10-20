package com.into.websoso.data.collection

import androidx.paging.PagingSource.LoadParams
import androidx.paging.PagingSource.LoadResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CollectionPagingSourceTest {
    @Test
    fun `다음 페이지가 있으면 응답 커서를 다음 키로 쓴다`() =
        runTest {
            var requestedCursor: String? = "unset"
            val source = CollectionPagingSource { cursor, _ ->
                requestedCursor = cursor
                CursorPage(items = listOf(1, 2), hasNext = true, nextCursor = "next")
            }

            val result = source.load(refresh()) as LoadResult.Page

            assertNull(requestedCursor)
            assertEquals(listOf(1, 2), result.data)
            assertEquals("next", result.nextKey)
        }

    @Test
    fun `다음 페이지가 없으면 커서가 있어도 다음 키는 null이다`() =
        runTest {
            val source = CollectionPagingSource { _, _ ->
                CursorPage(items = listOf(1), hasNext = false, nextCursor = "ignored")
            }

            val result = source.load(refresh()) as LoadResult.Page

            assertNull(result.nextKey)
        }

    @Test
    fun `요청이 실패하면 에러 결과를 돌려준다`() =
        runTest {
            val source = CollectionPagingSource<Int> { _, _ -> error("network") }

            val result = source.load(refresh())

            assertTrue(result is LoadResult.Error)
        }

    private fun refresh() = LoadParams.Refresh<String>(key = null, loadSize = 10, placeholdersEnabled = false)
}
