package com.into.websoso.domain.library.model

import org.junit.Assert.assertEquals
import org.junit.Test

class RatingTest {

    @Test
    fun `정확한 값은 대응되는 평점으로 변환된다`() {
        assertEquals(Rating.DEFAULT, Rating.from(0.0f))
        assertEquals(Rating.ONE, Rating.from(1.0f))
        assertEquals(Rating.THREE_POINT_FIVE, Rating.from(3.5f))
        assertEquals(Rating.FOUR_POINT_EIGHT, Rating.from(4.8f))
        assertEquals(Rating.FIVE, Rating.from(5.0f))
    }

    @Test
    fun `근사값도 가까운 평점으로 변환된다`() {
        assertEquals(Rating.FOUR, Rating.from(4.00001f))
        assertEquals(Rating.FOUR_POINT_EIGHT, Rating.from(4.79999f))
    }

    @Test
    fun `매칭되지 않는 값은 기본 평점으로 처리된다`() {
        assertEquals(Rating.DEFAULT, Rating.from(-1.0f))
        assertEquals(Rating.DEFAULT, Rating.from(4.7f))
        assertEquals(Rating.DEFAULT, Rating.from(100.0f))
    }
}
