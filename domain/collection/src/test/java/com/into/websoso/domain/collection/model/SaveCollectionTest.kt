package com.into.websoso.domain.collection.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class SaveCollectionTest {
    @Test
    fun `작품은 1개부터 100개까지 중복 없이 저장한다`() {
        listOf(emptyList(), List(101) { it.toLong() }, listOf(1L, 1L)).forEach { ids ->
            assertThrows(IllegalArgumentException::class.java) {
                SaveCollection("컬렉션", "", true, ids, 1L)
            }
        }
        listOf(listOf(1L), (1L..100L).toList()).forEach { ids ->
            assertEquals(ids, SaveCollection("컬렉션", "", true, ids, 1L).novelIds)
        }
    }

    @Test
    fun `대표 작품 변경은 등록 순서를 바꾸지 않는다`() {
        val collection = SaveCollection("컬렉션", "", false, listOf(3L, 1L, 2L), 3L)
        val updated = collection.copy(representativeNovelId = 1L)
        assertEquals(listOf(3L, 1L, 2L), updated.novelIds)
        assertEquals(false, updated.isPublic)
    }

    @Test
    fun `이름과 설명 길이를 검증한다`() {
        listOf("", "   ", "가".repeat(21)).forEach { name ->
            assertThrows(IllegalArgumentException::class.java) {
                SaveCollection(name, "", true, listOf(1L), 1L)
            }
        }
        assertThrows(IllegalArgumentException::class.java) {
            SaveCollection("컬렉션", "가".repeat(61), true, listOf(1L), 1L)
        }
        assertEquals(60, SaveCollection("가".repeat(20), "가".repeat(60), true, listOf(1L), 1L).description.length)
    }

    @Test
    fun `대표 작품이 작품 목록에 없으면 생성할 수 없다`() {
        assertThrows(IllegalArgumentException::class.java) {
            SaveCollection(
                name = "컬렉션",
                description = "",
                isPublic = true,
                novelIds = listOf(1L),
                representativeNovelId = 2L,
            )
        }
    }
}
