package com.into.websoso.domain.library.model

import com.into.websoso.domain.library.model.ReadStatuses.Companion.toReadStatuses
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ReadStatusesTest {
    @Test
    fun `기본 상태에서는 아무 상태도 선택되지 않는다`() {
        // given
        val statuses = ReadStatuses()

        // then
        assertFalse(statuses.isSelected)
        assertTrue(statuses.selectedKeys.isEmpty())
        assertTrue(statuses.selectedLabels.isEmpty())

        for (status in ReadStatus.entries) {
            assertFalse(statuses[status])
        }
    }

    @Test
    fun `읽기 상태를 선택하면 해당 상태가 선택된다`() {
        // given
        val statuses = ReadStatuses()

        // when
        val selected = statuses.set(ReadStatus.WATCHING)

        // then
        assertTrue(selected[ReadStatus.WATCHING])
        assertTrue(selected.isSelected)
        assertEquals(listOf("WATCHING"), selected.selectedKeys)
    }

    @Test
    fun `같은 읽기 상태를 다시 선택하면 선택이 해제된다`() {
        // given
        val statuses = ReadStatuses()

        // when
        val toggled = statuses
            .set(ReadStatus.WATCHING)
            .set(ReadStatus.WATCHING)

        // then
        assertFalse(toggled[ReadStatus.WATCHING])
        assertFalse(toggled.isSelected)
    }

    @Test
    fun `문자열 키 목록으로 읽기 상태를 생성할 수 있다`() {
        // given
        val keys = listOf("WATCHING", "QUIT", "UNKNOWN")

        // when
        val statuses = keys.toReadStatuses()

        // then
        assertTrue(statuses[ReadStatus.WATCHING])
        assertTrue(statuses[ReadStatus.QUIT])
        assertFalse(statuses[ReadStatus.WATCHED])
    }
}
