package com.into.websoso.domain.library.model

import com.into.websoso.domain.library.model.ReadStatuses.Companion.toReadStatuses
import org.junit.Assert.*
import org.junit.Test

class ReadStatusesTest {

    @Test
    fun `기본 상태에서는 아무 상태도 선택되지 않는다`() {
        val statuses = ReadStatuses()

        assertFalse(statuses.isSelected)
        assertTrue(statuses.selectedKeys.isEmpty())
        assertTrue(statuses.selectedLabels.isEmpty())

        for (status in ReadStatus.entries) {
            assertFalse(statuses[status])
        }
    }

    @Test
    fun `읽기 상태를 선택하면 해당 상태가 선택된다`() {
        val statuses = ReadStatuses()
            .set(ReadStatus.WATCHING)

        assertTrue(statuses[ReadStatus.WATCHING])
        assertTrue(statuses.isSelected)
        assertEquals(listOf("WATCHING"), statuses.selectedKeys)
    }

    @Test
    fun `같은 읽기 상태를 다시 선택하면 선택이 해제된다`() {
        val statuses = ReadStatuses()
            .set(ReadStatus.WATCHING)
            .set(ReadStatus.WATCHING)

        assertFalse(statuses[ReadStatus.WATCHING])
        assertFalse(statuses.isSelected)
    }

    @Test
    fun `문자열 키 목록으로 읽기 상태를 생성할 수 있다`() {
        val keys = listOf("WATCHING", "QUIT", "UNKNOWN")

        val statuses = keys.toReadStatuses()

        assertTrue(statuses[ReadStatus.WATCHING])
        assertTrue(statuses[ReadStatus.QUIT])
        assertFalse(statuses[ReadStatus.WATCHED])
    }
}
