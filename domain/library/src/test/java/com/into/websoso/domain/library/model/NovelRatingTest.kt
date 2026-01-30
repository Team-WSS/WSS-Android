package com.into.websoso.domain.library.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NovelRatingTest {
    @Test
    fun `기본 평점은 선택되지 않은 상태이다`() {
        val rating = NovelRating()

        assertEquals(Rating.DEFAULT, rating.rating)
        assertFalse(rating.isSelected)
    }

    @Test
    fun `평점을 설정하면 선택 상태가 된다`() {
        val rating = NovelRating.from(4.0f)

        assertTrue(rating.isSelected)
        assertEquals(Rating.FOUR, rating.rating)
    }

    @Test
    fun `같은 평점을 다시 설정하면 기본 상태로 돌아간다`() {
        val rating = NovelRating
            .from(3.0f)
            .set(Rating.THREE)

        assertEquals(Rating.DEFAULT, rating.rating)
        assertFalse(rating.isSelected)
    }

    @Test
    fun `평점은 근사값 비교로 판단된다`() {
        val rating = NovelRating.from(4.0f)

        assertTrue(rating.isCloseTo(Rating.from(4.00001f)))
    }
}
