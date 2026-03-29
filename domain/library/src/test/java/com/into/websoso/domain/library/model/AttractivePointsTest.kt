package com.into.websoso.domain.library.model

import com.into.websoso.domain.library.model.AttractivePoints.Companion.toAttractivePoints
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AttractivePointsTest {
    @Test
    fun `기본 상태에서는 아무 항목도 선택되지 않는다`() {
        // given
        val points = AttractivePoints()

        // then
        assertFalse(points.isSelected)
        assertTrue(points.selectedAttractivePoints.isEmpty())
        assertTrue(points.selectedLabels.isEmpty())
        assertTrue(points.selectedKeys.isEmpty())

        for (point in AttractivePoint.entries) {
            assertFalse(points[point])
        }
    }

    @Test
    fun `항목을 선택하면 해당 항목만 선택 상태가 된다`() {
        // given
        val points = AttractivePoints()

        // when
        val selected = points.set(AttractivePoint.WORLDVIEW)

        // then
        assertTrue(selected[AttractivePoint.WORLDVIEW])
        assertTrue(selected.isSelected)
        assertEquals(listOf("세계관"), selected.selectedLabels)
        assertEquals(listOf("worldview"), selected.selectedKeys)
    }

    @Test
    fun `같은 항목을 다시 선택하면 선택이 해제된다`() {
        // given
        val points = AttractivePoints()

        // when
        val toggled = points
            .set(AttractivePoint.WORLDVIEW)
            .set(AttractivePoint.WORLDVIEW)

        // then
        assertFalse(toggled[AttractivePoint.WORLDVIEW])
        assertFalse(toggled.isSelected)
    }

    @Test
    fun `문자열 키 목록을 통해 선택 상태를 생성할 수 있다`() {
        // given
        val keys = listOf("worldview", "character", "unknown")

        // when
        val points = keys.toAttractivePoints()

        // then
        assertTrue(points[AttractivePoint.WORLDVIEW])
        assertTrue(points[AttractivePoint.CHARACTER])
        assertFalse(points[AttractivePoint.MATERIAL])
    }
}
