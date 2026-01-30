package com.into.websoso.domain.library.model

import org.junit.Assert.assertEquals
import org.junit.Test

class SortCriteriaTest {
    @Test
    fun `정렬 키가 일치하면 해당 정렬 기준을 반환한다`() {
        assertEquals(SortCriteria.RECENT, SortCriteria.from("RECENT"))
        assertEquals(SortCriteria.OLD, SortCriteria.from("OLD"))
    }

    @Test
    fun `정렬 키가 일치하지 않으면 최신순으로 처리된다`() {
        assertEquals(SortCriteria.RECENT, SortCriteria.from("UNKNOWN"))
        assertEquals(SortCriteria.RECENT, SortCriteria.from(""))
        assertEquals(SortCriteria.RECENT, SortCriteria.from("recent"))
    }
}
