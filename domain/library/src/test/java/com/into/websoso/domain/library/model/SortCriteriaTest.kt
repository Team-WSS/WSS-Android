package com.into.websoso.domain.library.model

import org.junit.Assert.assertEquals
import org.junit.Test

class SortCriteriaTest {
    @Test
    fun `정렬 키가 일치하면 해당 정렬 기준을 반환한다`() {
        // when
        val recent = SortCriteria.from("RECENT")
        val old = SortCriteria.from("OLD")

        // then
        assertEquals(SortCriteria.RECENT, recent)
        assertEquals(SortCriteria.OLD, old)
    }

    @Test
    fun `정렬 키가 일치하지 않으면 최신순으로 처리된다`() {
        // when
        val unknown = SortCriteria.from("UNKNOWN")
        val empty = SortCriteria.from("")
        val lowerCase = SortCriteria.from("recent")

        // then
        assertEquals(SortCriteria.RECENT, unknown)
        assertEquals(SortCriteria.RECENT, empty)
        assertEquals(SortCriteria.RECENT, lowerCase)
    }
}
