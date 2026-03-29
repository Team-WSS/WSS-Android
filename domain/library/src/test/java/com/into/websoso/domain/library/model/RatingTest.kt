package com.into.websoso.domain.library.model

import org.junit.Assert.assertEquals
import org.junit.Test

class RatingTest {
    @Test
    fun `정확한 값은 대응되는 평점으로 변환된다`() {
        // when
        val default = Rating.from(0.0f)
        val one = Rating.from(1.0f)
        val threePointFive = Rating.from(3.5f)
        val fourPointEight = Rating.from(4.8f)
        val five = Rating.from(5.0f)

        // then
        assertEquals(Rating.DEFAULT, default)
        assertEquals(Rating.ONE, one)
        assertEquals(Rating.THREE_POINT_FIVE, threePointFive)
        assertEquals(Rating.FOUR_POINT_EIGHT, fourPointEight)
        assertEquals(Rating.FIVE, five)
    }

    @Test
    fun `근사값도 가까운 평점으로 변환된다`() {
        // when
        val four = Rating.from(4.00001f)
        val fourPointEight = Rating.from(4.79999f)

        // then
        assertEquals(Rating.FOUR, four)
        assertEquals(Rating.FOUR_POINT_EIGHT, fourPointEight)
    }

    @Test
    fun `매칭되지 않는 값은 기본 평점으로 처리된다`() {
// when
        val negative = Rating.from(-1.0f)
        val unmatched = Rating.from(4.7f)
        val overflow = Rating.from(100.0f)

        // then
        assertEquals(Rating.DEFAULT, negative)
        assertEquals(Rating.DEFAULT, unmatched)
        assertEquals(Rating.DEFAULT, overflow)
    }
}
